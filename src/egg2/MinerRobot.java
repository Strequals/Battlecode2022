package egg2;

import battlecode.common.*;

public strictfp class MinerRobot extends Robot {
    
    MapLocation resourceLocation;
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
    }
    

    RobotInfo[] nearbyRobots;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        MapLocation fleeFrom = null;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {

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
        while (rc.canMineGold(me)) {
            rc.mineGold(me);
            mined = true;
        }
        for (Direction d : directions) {
            mineLocation = me.add(d);
            while (rc.canMineGold(mineLocation)) {
                rc.mineGold(mineLocation);
                mined = true;
            }
        }
        return mined;
    }
    
    public boolean tryMineLead() throws GameActionException {
        MapLocation me = rc.getLocation();
        boolean mined = false;
        MapLocation mineLocation;
        while (rc.senseLead(me) > 1) {
            rc.mineLead(me);
            mined = true;
        }
        for (Direction d : directions) {
            mineLocation = me.add(d);
            while (rc.senseLead(mineLocation) > 1) {
                rc.mineLead(mineLocation);
                mined = true;
            }
        }
        return mined;
    }
    
    /**
     * Looks for a suitable location to mine and tries to move there.
     **/
    public boolean tryMove() throws GameActionException {
        findResources();
        rc.setIndicatorString("location: " + resourceLocation + ", score: " + locationScore);
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
        }
        return false;
    }

    public static final double VALUE_THRESHOLD = 10;
    
    /**
     * Looks for a suitable place to mine; if no such place is found, chooses a
     * location at random.
     **/
    public void findResources() throws GameActionException {
        if (resourceLocation != null) {
            if (rc.getLocation().isWithinDistanceSquared(resourceLocation, 2)) {
                resourceLocation = null;
            } else if (locationScore < CHANGE_RESOURCE_THRESHOLD) {
                resourceLocation = null;
            }
        }
        Resource r = senseAllNearbyResources();
        if (r != null) {
            resourceLocation = r.location;
            locationScore = 2;
            Communications.addResourceData(rc, rc.getLocation(), r.value);
            return;
        }
        
        if (resourceLocation == null) {
            r = Communications.readResourceData(rc);
            if (r != null && r.value > VALUE_THRESHOLD) {
                resourceLocation = r.location;
                locationScore = 2;
                return;
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

    public Resource senseAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;
        MapLocation current = rc.getLocation();
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.senseNearbyLocationsWithGold(rc.getType().visionRadiusSquared)) {
            gold = rc.senseGold(l);
            dsq = current.distanceSquaredTo(l);
            totalGold += gold;
            if (dsq < bestDsq
                    || (dsq == bestDsq && gold > bestScore)) {
                best = l;
                bestDsq = dsq;
                bestScore = gold;
            }
        }

        if (best == null) {
            for (MapLocation l : rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared)) {
                lead = rc.senseLead(l) - 1;
                dsq = current.distanceSquaredTo(l);
                totalLead += lead;
                if (lead > 0 && (dsq < bestDsq
                        || (dsq == bestDsq && lead > bestScore))) {
                    best = l;
                    bestDsq = dsq;
                    bestScore = lead;
                }
            }
        }
        if (best != null) {
            return new Resource(best, totalLead, totalGold);
        }
        return null;
    }
}
