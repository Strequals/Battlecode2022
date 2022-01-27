package parrot5_5;

import battlecode.common.*;

public strictfp class MinerRobot extends Robot {
    
    MapLocation resourceLocation;
    MapLocation tabuLocation;
    static final int TABU_RANGE = 20;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_RESOURCE_THRESHOLD = 0.1;
    public final int FLEE_HEALTH = 10;

    public MinerRobot(RobotController rc) {
        super(rc);
        resourceLocation = null;
    }

    int leadMined;
    MapLocation nearestArchon = null;

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        updateExploration();
        Communications.markExplore(rc, rc.getLocation());
        nearestArchon = Communications.getClosestArchon(rc);

        leadMined = 0;
        
        boolean mined = preview();
        Direction moved = null;
        MapLocation current = rc.getLocation();
        int currentRubble = rc.senseRubble(current);
        if ((moved = tryMove()) != null) {
            int nextRubble = rc.senseRubble(current.add(moved));
            if (nextRubble < currentRubble) {
                mineDir(moved);
                rc.move(moved);
                mineDirAfterMove(moved);
            } else {
                mine();
                rc.move(moved);
            }
            mined = tryMine() || mined;
        } else {
            mine();
        }
        if ((moved == null) && !mined && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }
        Communications.incrementMinerCount(rc);
        Communications.correctIncome(rc, leadMined);
        //rc.setIndicatorString("target: " + resourceLocation + "score : " + locationScore);
    }
    

    RobotInfo[] nearbyRobots;
    int nearbyMiners;
    final int MAX_ENEMY_LEVEL = 3;
    boolean areEnemies;
    int enemyLevel = 0;
    MapLocation fleeFrom;
    final int FLEE_MEMORY_TURNS = 8;
    int fleeMemory = 0;
    int MAX_FLEE_RUBBLE_INCREASE = 20;
    int enemyThreat;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        nearbyMiners = 0;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        areEnemies = false;
        enemyThreat = 0;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {
                if (otherRobot.type == RobotType.MINER) {
                    nearbyMiners++;
                }
            } else {
                if (otherRobot.type.canAttack() && otherRobot.mode.canAct) {
                    // condition may need changing if canAttack also involves healing
                    MapLocation attackedFrom;
                    if (otherRobot.mode == RobotMode.TURRET) {
                        attackedFrom = otherRobot.location;
                    } else {
                        attackedFrom = otherRobot.location.add(otherRobot.location.directionTo(rc.getLocation()));
                    }
                    int dsq = attackedFrom.distanceSquaredTo(rc.getLocation());
                    if (dsq < fleeDistanceSquared) {
                        fleeDistanceSquared = dsq;
                        fleeFrom = attackedFrom;
                        fleeMemory = FLEE_MEMORY_TURNS;
                    }
                    switch (otherRobot.type) {
                        case WATCHTOWER:
                            enemyThreat += 4;
                            break;
                        case SOLDIER:
                            enemyThreat += 3;
                            break;
                        case SAGE:
                            enemyThreat += 2;
                            break;
                    }
                }
                areEnemies = true;
            }
        }

        if (areEnemies) {
            enemyLevel = MAX_ENEMY_LEVEL;
        } else if (enemyLevel > 0) {
            enemyLevel--;
        }

        
        
        processAndBroadcastEnemies(nearbyRobots);
    }

    
    
    /**
     * Tries to mine Au if possible, and then tries to mine Pb if possible.
     **/
    public boolean tryMine() throws GameActionException {
        if (rc.isActionReady()) {
            boolean g = tryMineGold();
            boolean l = false;
            if (rc.isActionReady()) {
                l = tryMineLead();
            }
            return g || l;
        }
        return false;
    }

    public boolean tryMine(Direction d) throws GameActionException {
        if (d == null) return tryMine();
        Direction o = d.opposite();
        if (rc.isActionReady()) {
            boolean g = tryMineGold(o);
            boolean l = false;
            if (rc.isActionReady()) {
                l = tryMineLead(o);
            }
            return g || l;
        }
        return false;
    }

    public boolean tryMineGold() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));

        int maxMines = StrictMath.min(turnsCanMine, rc.senseGold(me));
        switch (maxMines) {
            case 5:
                rc.mineGold(me);
            case 4:
                rc.mineGold(me);
            case 3:
                rc.mineGold(me);
            case 2:
                rc.mineGold(me);
            case 1:
                rc.mineGold(me);
                mined = true;
                turnsCanMine -= maxMines;
        }
        for (Direction d : directions) {
            mineLocation = me.add(d);
            if (rc.onTheMap(mineLocation)) {
                maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
                switch (maxMines) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                        mined = true;
                        turnsCanMine -= maxMines;
                }
            }
        }
        return mined;
    }

    public boolean tryMineGold(Direction d) throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));

        int maxMines;
        
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        Direction left = d.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        maxMines = StrictMath.min(turnsCanMine, rc.senseGold(me));
        switch (maxMines) {
            case 5:
                rc.mineGold(me);
            case 4:
                rc.mineGold(me);
            case 3:
                rc.mineGold(me);
            case 2:
                rc.mineGold(me);
            case 1:
                rc.mineGold(me);
                mined = true;
                turnsCanMine -= maxMines;
        }
        left = left.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        left = left.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            switch (maxMines) {
                case 5:
                    rc.mineGold(mineLocation);
                case 4:
                    rc.mineGold(mineLocation);
                case 3:
                    rc.mineGold(mineLocation);
                case 2:
                    rc.mineGold(mineLocation);
                case 1:
                    rc.mineGold(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
            }
        }
        return mined;
    }

    public static final int PRESERVE_RADIUS = 100;
    
    public boolean tryMineLead() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));

        int leaveLead;
        if (enemyLevel > 0 && nearestArchon != null && me.distanceSquaredTo(nearestArchon) >= PRESERVE_RADIUS) {
            leaveLead = 0;
        } else {
            leaveLead = 1;
        }

        int maxMines = StrictMath.min(turnsCanMine, rc.senseLead(me) - leaveLead);
        switch (maxMines) {
            case 5:
                rc.mineLead(me);
            case 4:
                rc.mineLead(me);
            case 3:
                rc.mineLead(me);
            case 2:
                rc.mineLead(me);
            case 1:
                rc.mineLead(me);
                mined = true;
                turnsCanMine -= maxMines;
                leadMined += maxMines;
        }
        for (Direction d : directions) {
            mineLocation = me.add(d);
            if (rc.onTheMap(mineLocation)) {
                maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
                switch (maxMines) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                        mined = true;
                        turnsCanMine -= maxMines;
                        leadMined += maxMines;
                }
            }
        }
        return mined;
    }

    public boolean tryMineLead(Direction d) throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));

        int leaveLead;
        if (enemyLevel > 0 && nearestArchon != null && me.distanceSquaredTo(nearestArchon) >= PRESERVE_RADIUS) {
            leaveLead = 0;
        } else {
            leaveLead = 1;
        }

        int maxMines;
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        Direction left = d.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        maxMines = StrictMath.min(turnsCanMine, rc.senseLead(me) - leaveLead);
        switch (maxMines) {
            case 5:
                rc.mineLead(me);
            case 4:
                rc.mineLead(me);
            case 3:
                rc.mineLead(me);
            case 2:
                rc.mineLead(me);
            case 1:
                rc.mineLead(me);
                mined = true;
                turnsCanMine -= maxMines;
                leadMined += maxMines;
        }
        left = d.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        left = d.rotateLeft();
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        mineLocation = me.add(left);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        d = d.rotateRight();
        mineLocation = me.add(d);
        if (rc.onTheMap(mineLocation)) {
            maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - leaveLead);
            switch (maxMines) {
                case 5:
                    rc.mineLead(mineLocation);
                case 4:
                    rc.mineLead(mineLocation);
                case 3:
                    rc.mineLead(mineLocation);
                case 2:
                    rc.mineLead(mineLocation);
                case 1:
                    rc.mineLead(mineLocation);
                    mined = true;
                    turnsCanMine -= maxMines;
                    leadMined += maxMines;
            }
        }
        return mined;
    }


    public MapLocation mineFrom() throws GameActionException {
        if (!rc.canSenseLocation(resourceLocation)) {
            return resourceLocation;
        }

        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestRubble = 101;
        MapLocation current = rc.getLocation();
        if (!rc.canSenseRobotAtLocation(resourceLocation) || resourceLocation.equals(current)) {
            best = resourceLocation;
            bestRubble = rc.senseRubble(resourceLocation);
            bestDsq = best.distanceSquaredTo(current);
        }
        MapLocation loc;
        int rubble;
        int dsq;
        for (Direction d : directions) {
            loc = resourceLocation.add(d);
            if (loc.equals(current) || (rc.canSenseLocation(loc) && !rc.canSenseRobotAtLocation(loc))) {
                rubble = rc.senseRubble(loc);
                if (rubble <= bestRubble) {
                    dsq = current.distanceSquaredTo(loc);

                    if (rubble < bestRubble) {
                        bestRubble = rubble;
                        best = loc;
                        bestDsq = dsq;
                    } else if (dsq < bestDsq) {
                        bestRubble = rubble;
                        best = loc;
                        bestDsq = dsq;
                    }
                }
            }
        }
        return best;
    }

    boolean fleeing = false;
    static final double FLEE_SCORE_DECAY = 0.5;
    
    /**
     * Looks for a suitable location to mine and tries to move there.
     **/
    public Direction tryMove() throws GameActionException {
        findResources();
        if (!rc.isMovementReady()) return null;

        MapLocation current = rc.getLocation();

        if (fleeMemory > 0) {
            fleeMemory--;
            if (fleeing || resourceValueSeen < enemyThreat * (FLEE_HEALTH + rc.getHealth())) {
                Direction fleeDir = Navigation.flee(rc, current, fleeFrom);
                locationScore *= FLEE_SCORE_DECAY;
                if (fleeDir != null) {
                    MapLocation fleeLoc = current.add(fleeDir);
                    if (rc.senseRubble(fleeLoc) - rc.senseRubble(rc.getLocation()) < MAX_FLEE_RUBBLE_INCREASE) {
                        return fleeDir;
                    }
                }
                fleeing = true;
            }
        } else {
            fleeFrom = null;
            fleeing = false;
        }

        MapLocation mineFrom = mineFrom();

        if (mineFrom != null) {
            Direction d = null;
            if (rc.getLocation().distanceSquaredTo(mineFrom) > 2) {
                d = Navigation.navigate(rc, current, mineFrom);
            } else if (!rc.getLocation().equals(mineFrom)) {
                d = rc.getLocation().directionTo(mineFrom);
            } else if (nearestArchon != null) {
                int currentRubble = rc.senseRubble(current);
                Direction preferred = nearestArchon.directionTo(current);
                if (rc.getLocation().equals(resourceLocation)) {
                    d = getBiasedDirectionOfLeastRubble(preferred);
                } else {
                    d = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(resourceLocation, 2, preferred);
                }
                MapLocation next = current.add(d);
                if (rc.senseRubble(next) > currentRubble || diff(d, preferred) > 2) {
                    d = null;
                }
            }
            if (d != null && rc.canMove(d)) {
                return d;
            }
        } else {
            Direction d = Navigation.navigate(rc, rc.getLocation(), resourceLocation);
            if (d != null && rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public double getScoutProbability() {
        int roundNum = rc.getRoundNum();
        if (roundNum < 10) {
            return 1;
        } else if (roundNum < 110) {
            return 1 - 0.005 * (roundNum - 10);
        } else {
            return 0.5;
        }
    }

    public static final double VALUE_THRESHOLD = 64;
    public static final double TOO_MANY_MINERS = 2;

    int resourceValueSeen;
    
    /**
     * Looks for a suitable place to mine; if no such place is found, chooses a
     * location at random.
     **/

    public void findResources() throws GameActionException {
        Resource r = previewAllNearbyResources();
        if (r != null) {
            Communications.addResourceData(rc, r.location, r.value);
            resourceValueSeen = r.value;
        } else {
            Communications.addResourceData(rc, rc.getLocation(), 0);
            resourceValueSeen = 0;
        }

        if (resourceLocation != null) {
            if (rc.getLocation().isWithinDistanceSquared(resourceLocation, 2)) {
                if (r == null) {
                    tabuLocation = rc.getLocation();
                }
                resourceLocation = null;
                
            } else if (locationScore < CHANGE_RESOURCE_THRESHOLD) {
                resourceLocation = null;
            }
        }

        if (r != null) {
            if ((r.location.distanceSquaredTo(rc.getLocation()) <= 2
                        || (tabuLocation == null || r.location.distanceSquaredTo(tabuLocation) > TABU_RANGE))
                    && (nearbyMiners < TOO_MANY_MINERS || rng.nextInt(nearbyMiners) < TOO_MANY_MINERS)) {
                    //|| nearbyMiners < TOO_MANY_MINERS) {
                resourceLocation = r.location;
                locationScore = 2;
                return;
            }
        }

        if (resourceLocation == null) {
            r = Communications.readResourceData(rc);
            if (r != null && r.value > VALUE_THRESHOLD
                    && (tabuLocation == null || r.location.distanceSquaredTo(tabuLocation) > TABU_RANGE)) {
                if (r.value > VALUE_THRESHOLD && rng.nextDouble() > getScoutProbability()) {
                    resourceLocation = r.location;
                    locationScore = 2;
                    return;
                }
            }
            
            if (resourceLocation == null) {
                resourceLocation = getExploreLocation();
                locationScore = 1;
            }
        }
    }

    public Resource previewAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = 0;
        MapLocation current = rc.getLocation();
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.senseNearbyLocationsWithGold(rc.getType().visionRadiusSquared)) {
            gold = rc.senseGold(l);
            dsq = current.distanceSquaredTo(l);
            if (dsq <= 2) {
                switch (current.directionTo(l)) {
                    case CENTER:
                        gold -= goldCenter;
                        break;
                    case NORTH:
                        gold -= goldNorth;
                        break;
                    case NORTHEAST:
                        gold -= goldNortheast;
                        break;
                    case EAST:
                        gold -= goldEast;
                        break;
                    case SOUTHEAST:
                        gold -= goldSoutheast;
                        break;
                    case SOUTH:
                        gold -= goldSouth;
                        break;
                    case SOUTHWEST:
                        gold -= goldSouthwest;
                        break;
                    case WEST:
                        gold -= goldWest;
                        break;
                    case NORTHWEST:
                        gold -= goldNorthwest;
                        break;
                }
            }
            totalGold += gold;
            if (dsq < bestDsq
                    || (dsq == bestDsq && 32 * gold > bestScore)) {
                best = l;
                bestDsq = dsq;
                bestScore = gold * 32;
            }
        }

        //if (best == null) {
        for (MapLocation l : rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared)) {
            lead = rc.senseLead(l) - 1;
            dsq = current.distanceSquaredTo(l);
            if (dsq <= 2) {
                switch (current.directionTo(l)) {
                    case CENTER:
                        lead -= leadCenter;
                        break;
                    case NORTH:
                        lead -= leadNorth;
                        break;
                    case NORTHEAST:
                        lead -= leadNortheast;
                        break;
                    case EAST:
                        lead -= leadEast;
                        break;
                    case SOUTHEAST:
                        lead -= leadSoutheast;
                        break;
                    case SOUTH:
                        lead -= leadSouth;
                        break;
                    case SOUTHWEST:
                        lead -= leadSouthwest;
                        break;
                    case WEST:
                        lead -= leadWest;
                        break;
                    case NORTHWEST:
                        lead -= leadNorthwest;
                        break;
                }
            }
            totalLead += lead;
            if (lead > 0 && (dsq < bestDsq
                    || (dsq == bestDsq && lead > bestScore))) {
                best = l;
                bestDsq = dsq;
                bestScore = lead;
            }
        }
        //}
        if (best != null) {
            return new Resource(best, totalLead, totalGold);
        }
        return null;
    }

    /**
     * Returns the nearest MapLocation with either Pb or Au, with ties broken by amount.
     * Returns null if no such location is found.
    **/
    public MapLocation senseNearbyResources() throws GameActionException {
        Resource resource = senseAllNearbyResources();
        if (resource != null) {
            return resource.location;
        }
        return null;
    }
    
    /*public Resource senseAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        int score;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;
        MapLocation current = rc.getLocation();
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), rc.getType().visionRadiusSquared)) {
            lead = rc.senseLead(l);
            gold = rc.senseGold(l);
            score = Resource.heuristic(lead, gold);
            if (score > 0) {
                totalLead += lead;
                dsq = current.distanceSquaredTo(l);
                totalGold += gold;
                if (dsq < bestDsq
                        || (dsq == bestDsq && score > bestScore)) {
                    best = l;
                    bestDsq = dsq;
                    bestScore = score;
                }
            }
        }
        if (best != null) {
            return new Resource(best, totalLead, totalGold);
        }
        return null;
    }*/


    static int leadCenter;
    static int leadNorth;
    static int leadNortheast;
    static int leadEast;
    static int leadSoutheast;
    static int leadSouth;
    static int leadSouthwest;
    static int leadWest;
    static int leadNorthwest;
    static int goldCenter;
    static int goldNorth;
    static int goldNortheast;
    static int goldEast;
    static int goldSoutheast;
    static int goldSouth;
    static int goldSouthwest;
    static int goldWest;
    static int goldNorthwest;
    public void mineDir(Direction dir) throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation mineLocation;
        switch (dir) {
            case CENTER:
                
                break;
            case NORTH:
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                break;
            case NORTHEAST:
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case EAST:
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case SOUTHEAST:
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case SOUTH:
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case SOUTHWEST:
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case WEST:
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                break;
            case NORTHWEST:
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                break;
        }
    }
    public void mineDirAfterMove(Direction dir) throws GameActionException {
        MapLocation current = rc.getLocation().add(dir.opposite());
        MapLocation mineLocation;
        switch (dir) {
            case CENTER:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case NORTH:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case NORTHEAST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                break;
            case EAST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.NORTHEAST);
                switch (goldNortheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNortheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNortheast;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                break;
            case SOUTHEAST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                break;
            case SOUTH:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.EAST);
                switch (goldEast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadEast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadEast;
                mineLocation = current.add(Direction.SOUTHEAST);
                switch (goldSoutheast) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSoutheast) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSoutheast;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                break;
            case SOUTHWEST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                break;
            case WEST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.SOUTH);
                switch (goldSouth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouth;
                mineLocation = current.add(Direction.SOUTHWEST);
                switch (goldSouthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadSouthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadSouthwest;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
            case NORTHWEST:
                mineLocation = current.add(Direction.CENTER);
                switch (goldCenter) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadCenter) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadCenter;
                mineLocation = current.add(Direction.NORTH);
                switch (goldNorth) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorth) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorth;
                mineLocation = current.add(Direction.WEST);
                switch (goldWest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadWest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadWest;
                mineLocation = current.add(Direction.NORTHWEST);
                switch (goldNorthwest) {
                    case 5:
                        rc.mineGold(mineLocation);
                    case 4:
                        rc.mineGold(mineLocation);
                    case 3:
                        rc.mineGold(mineLocation);
                    case 2:
                        rc.mineGold(mineLocation);
                    case 1:
                        rc.mineGold(mineLocation);
                }
                switch (leadNorthwest) {
                    case 5:
                        rc.mineLead(mineLocation);
                    case 4:
                        rc.mineLead(mineLocation);
                    case 3:
                        rc.mineLead(mineLocation);
                    case 2:
                        rc.mineLead(mineLocation);
                    case 1:
                        rc.mineLead(mineLocation);
                }
                leadMined += leadNorthwest;
                break;
        }
    }
    public void mine() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation mineLocation;
        mineLocation = current.add(Direction.CENTER);
        switch (goldCenter) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadCenter) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadCenter;
        mineLocation = current.add(Direction.NORTH);
        switch (goldNorth) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadNorth) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadNorth;
        mineLocation = current.add(Direction.NORTHEAST);
        switch (goldNortheast) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadNortheast) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadNortheast;
        mineLocation = current.add(Direction.EAST);
        switch (goldEast) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadEast) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadEast;
        mineLocation = current.add(Direction.SOUTHEAST);
        switch (goldSoutheast) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadSoutheast) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadSoutheast;
        mineLocation = current.add(Direction.SOUTH);
        switch (goldSouth) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadSouth) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadSouth;
        mineLocation = current.add(Direction.SOUTHWEST);
        switch (goldSouthwest) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadSouthwest) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadSouthwest;
        mineLocation = current.add(Direction.WEST);
        switch (goldWest) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadWest) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadWest;
        mineLocation = current.add(Direction.NORTHWEST);
        switch (goldNorthwest) {
            case 5:
                rc.mineGold(mineLocation);
            case 4:
                rc.mineGold(mineLocation);
            case 3:
                rc.mineGold(mineLocation);
            case 2:
                rc.mineGold(mineLocation);
            case 1:
                rc.mineGold(mineLocation);
        }
        switch (leadNorthwest) {
            case 5:
                rc.mineLead(mineLocation);
            case 4:
                rc.mineLead(mineLocation);
            case 3:
                rc.mineLead(mineLocation);
            case 2:
                rc.mineLead(mineLocation);
            case 1:
                rc.mineLead(mineLocation);
        }
        leadMined += leadNorthwest;
    }
    public boolean preview() throws GameActionException {
        boolean gold = previewGold();
        return previewLead() || gold;
    }
    static int turnsCanMine;
    public boolean previewGold() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));
        mineLocation = me.add(Direction.CENTER);
        if (rc.onTheMap(mineLocation)) {
            goldCenter = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldCenter > 0) {
                mined = true;
                turnsCanMine -= goldCenter;
            } else {
                goldCenter = 0;
            }
        } else {
            goldCenter = 0;
        }
        mineLocation = me.add(Direction.NORTH);
        if (rc.onTheMap(mineLocation)) {
            goldNorth = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldNorth > 0) {
                mined = true;
                turnsCanMine -= goldNorth;
            } else {
                goldNorth = 0;
            }
        } else {
            goldNorth = 0;
        }
        mineLocation = me.add(Direction.NORTHEAST);
        if (rc.onTheMap(mineLocation)) {
            goldNortheast = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldNortheast > 0) {
                mined = true;
                turnsCanMine -= goldNortheast;
            } else {
                goldNortheast = 0;
            }
        } else {
            goldNortheast = 0;
        }
        mineLocation = me.add(Direction.EAST);
        if (rc.onTheMap(mineLocation)) {
            goldEast = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldEast > 0) {
                mined = true;
                turnsCanMine -= goldEast;
            } else {
                goldEast = 0;
            }
        } else {
            goldEast = 0;
        }
        mineLocation = me.add(Direction.SOUTHEAST);
        if (rc.onTheMap(mineLocation)) {
            goldSoutheast = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldSoutheast > 0) {
                mined = true;
                turnsCanMine -= goldSoutheast;
            } else {
                goldSoutheast = 0;
            }
        } else {
            goldSoutheast = 0;
        }
        mineLocation = me.add(Direction.SOUTH);
        if (rc.onTheMap(mineLocation)) {
            goldSouth = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldSouth > 0) {
                mined = true;
                turnsCanMine -= goldSouth;
            } else {
                goldSouth = 0;
            }
        } else {
            goldSouth = 0;
        }
        mineLocation = me.add(Direction.SOUTHWEST);
        if (rc.onTheMap(mineLocation)) {
            goldSouthwest = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldSouthwest > 0) {
                mined = true;
                turnsCanMine -= goldSouthwest;
            } else {
                goldSouthwest = 0;
            }
        } else {
            goldSouthwest = 0;
        }
        mineLocation = me.add(Direction.WEST);
        if (rc.onTheMap(mineLocation)) {
            goldWest = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldWest > 0) {
                mined = true;
                turnsCanMine -= goldWest;
            } else {
                goldWest = 0;
            }
        } else {
            goldWest = 0;
        }
        mineLocation = me.add(Direction.NORTHWEST);
        if (rc.onTheMap(mineLocation)) {
            goldNorthwest = StrictMath.min(turnsCanMine, rc.senseGold(mineLocation));
            if (goldNorthwest > 0) {
                mined = true;
                turnsCanMine -= goldNorthwest;
            } else {
                goldNorthwest = 0;
            }
        } else {
            goldNorthwest = 0;
        }
        return mined;
    }
    public boolean previewLead() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int leaveLead;
        if (enemyLevel > 0 && nearestArchon != null && me.distanceSquaredTo(nearestArchon) >= PRESERVE_RADIUS) {
            leaveLead = 0;
        } else {
            leaveLead = 1;
        }
        mineLocation = me.add(Direction.CENTER);
        if (rc.onTheMap(mineLocation)) {
            leadCenter = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadCenter > 0) {
                mined = true;
                turnsCanMine -= leadCenter;
            } else {
                leadCenter = 0;
            }
        } else {
            leadCenter = 0;
        }
        mineLocation = me.add(Direction.NORTH);
        if (rc.onTheMap(mineLocation)) {
            leadNorth = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadNorth > 0) {
                mined = true;
                turnsCanMine -= leadNorth;
            } else {
                leadNorth = 0;
            }
        } else {
            leadNorth = 0;
        }
        mineLocation = me.add(Direction.NORTHEAST);
        if (rc.onTheMap(mineLocation)) {
            leadNortheast = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadNortheast > 0) {
                mined = true;
                turnsCanMine -= leadNortheast;
            } else {
                leadNortheast = 0;
            }
        } else {
            leadNortheast = 0;
        }
        mineLocation = me.add(Direction.EAST);
        if (rc.onTheMap(mineLocation)) {
            leadEast = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadEast > 0) {
                mined = true;
                turnsCanMine -= leadEast;
            } else {
                leadEast = 0;
            }
        } else {
            leadEast = 0;
        }
        mineLocation = me.add(Direction.SOUTHEAST);
        if (rc.onTheMap(mineLocation)) {
            leadSoutheast = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadSoutheast > 0) {
                mined = true;
                turnsCanMine -= leadSoutheast;
            } else {
                leadSoutheast = 0;
            }
        } else {
            leadSoutheast = 0;
        }
        mineLocation = me.add(Direction.SOUTH);
        if (rc.onTheMap(mineLocation)) {
            leadSouth = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadSouth > 0) {
                mined = true;
                turnsCanMine -= leadSouth;
            } else {
                leadSouth = 0;
            }
        } else {
            leadSouth = 0;
        }
        mineLocation = me.add(Direction.SOUTHWEST);
        if (rc.onTheMap(mineLocation)) {
            leadSouthwest = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadSouthwest > 0) {
                mined = true;
                turnsCanMine -= leadSouthwest;
            } else {
                leadSouthwest = 0;
            }
        } else {
            leadSouthwest = 0;
        }
        mineLocation = me.add(Direction.WEST);
        if (rc.onTheMap(mineLocation)) {
            leadWest = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadWest > 0) {
                mined = true;
                turnsCanMine -= leadWest;
            } else {
                leadWest = 0;
            }
        } else {
            leadWest = 0;
        }
        mineLocation = me.add(Direction.NORTHWEST);
        if (rc.onTheMap(mineLocation)) {
            leadNorthwest = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation)- leaveLead);
            if (leadNorthwest > 0) {
                mined = true;
                turnsCanMine -= leadNorthwest;
            } else {
                leadNorthwest = 0;
            }
        } else {
            leadNorthwest = 0;
        }
        return mined;
    }
}
