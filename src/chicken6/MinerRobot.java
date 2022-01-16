package chicken6;

import battlecode.common.*;

public strictfp class MinerRobot extends Robot {
    
    MapLocation resourceLocation;
    MapLocation tabuLocation;
    static final int TABU_RANGE = 34;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_RESOURCE_THRESHOLD = 0.1;

    public MinerRobot(RobotController rc) {
        super(rc);
        resourceLocation = null;
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        
        boolean mined = tryMine();
        boolean moved = false;
        if (moved = tryMove()) {
            tryMine();
        }
        if (!moved && !mined && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }
        Communications.incrementMinerCount(rc);
    }
    

    RobotInfo[] nearbyRobots;
    int nearbyMiners;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        MapLocation fleeFrom = null;
        nearbyMiners = 0;
        int fleeDistanceSquared = Integer.MAX_VALUE;
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
                    }
                }
            }
        }

        if (fleeFrom != null) {
            tryFlee(fleeFrom);
            resourceLocation = null;
            //locationScore *= SCORE_DECAY;
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
    
    public boolean tryMineLead() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        int turnsCanMine = -StrictMath.floorDiv(rc.getActionCooldownTurns() - 10, (int) ((1.0 + rc.senseRubble(me) / 10.0) * 2.0));

        System.out.println("turnsCanMine: " + turnsCanMine + "\nCooldown turns: " + rc.getActionCooldownTurns() + "\nRubble:" + rc.senseRubble(me));

        int maxMines = StrictMath.min(turnsCanMine, rc.senseLead(me) - 1);
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
        }
        for (Direction d : directions) {
            mineLocation = me.add(d);
            if (rc.onTheMap(mineLocation)) {
                maxMines = StrictMath.min(turnsCanMine, rc.senseLead(mineLocation) - 1);
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
                }
            }
        }
        return mined;
    }
    
    /**
     * Looks for a suitable location to mine and tries to move there.
     **/
    public boolean tryMove() throws GameActionException {
        findResources();
        if (!rc.isMovementReady()) return false;
        if (!rc.getLocation().equals(resourceLocation)) {
            Direction d = null;
            if (rc.getLocation().distanceSquaredTo(resourceLocation) > 2) {
                d = Navigation.navigate(rc, rc.getLocation(), resourceLocation);
            } else {
                d = rc.getLocation().directionTo(resourceLocation);
            }
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        } else {
            // TODO: try moving to lower rubble here
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
    
    /**
     * Looks for a suitable place to mine; if no such place is found, chooses a
     * location at random.
     **/
    public void findResources() throws GameActionException {
        Resource r = senseAllNearbyResources();
        if (r != null) {
            Communications.addResourceData(rc, r.location, r.value);
        } else {
            Communications.addResourceData(rc, rc.getLocation(), 0);
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
                resourceLocation = getRandomLocation();
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
