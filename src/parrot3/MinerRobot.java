package parrot3;

import battlecode.common.*;

public strictfp class MinerRobot extends Robot {
    
    MapLocation resourceLocation;
    MapLocation tabuLocation;
    static final int TABU_RANGE = 34;
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
        
        boolean mined = tryMine();
        boolean moved = false;
        if (moved = tryMove()) {
            tryMine();
        }
        if (!moved && !mined && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }
        Communications.incrementMinerCount(rc);
        Communications.correctIncome(rc, leadMined);
        rc.setIndicatorString("target: " + resourceLocation + "score : " + locationScore);
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

    public MapLocation mineFrom() throws GameActionException {
        if (!rc.canSenseLocation(resourceLocation)) {
            return resourceLocation;
        }

        MapLocation best = null;
        int bestRubble = 101;
        MapLocation current = rc.getLocation();
        if (!rc.canSenseRobotAtLocation(resourceLocation) || resourceLocation.equals(current)) {
            best = resourceLocation;
            bestRubble = rc.senseRubble(resourceLocation);
        }
        MapLocation loc;
        int rubble;
        for (Direction d : directions) {
            loc = resourceLocation.add(d);
            if (loc.equals(current) || (rc.canSenseLocation(loc) && !rc.canSenseRobotAtLocation(loc))) {
                rubble = rc.senseRubble(loc);
                if (rubble < bestRubble) {
                    bestRubble = rubble;
                    best = loc;
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
    public boolean tryMove() throws GameActionException {
        findResources();
        if (!rc.isMovementReady()) return false;

        if (fleeMemory > 0) {
            fleeMemory--;
            if (fleeing || resourceValueSeen < enemyThreat * (FLEE_HEALTH + rc.getHealth())) {
                Direction fleeDir = Navigation.flee(rc, rc.getLocation(), fleeFrom);
                locationScore *= FLEE_SCORE_DECAY;
                if (fleeDir != null) {
                    MapLocation fleeLoc = rc.getLocation().add(fleeDir);
                    if (rc.senseRubble(fleeLoc) - rc.senseRubble(rc.getLocation()) < MAX_FLEE_RUBBLE_INCREASE) {
                        rc.move(fleeDir);
                        return true;
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
                d = Navigation.navigate(rc, rc.getLocation(), mineFrom);
            } else if (!rc.getLocation().equals(mineFrom)) {
                d = rc.getLocation().directionTo(mineFrom);
            }
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        } else {
            Direction d = Navigation.navigate(rc, rc.getLocation(), resourceLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
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
        Resource r = senseAllNearbyResources();
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

        rc.setIndicatorString( " fleemem " + fleeMemory + " fleeFrom " + fleeFrom); 

        
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
}
