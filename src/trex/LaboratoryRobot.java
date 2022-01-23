package trex;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double MIN_LEAD = 0;
    private static int TARGET_RATE = 2;  // will only transmute if rate is this or better
    static final int MAX_IDLE_TURNS = 5;
    static final int MAX_MOVING_TURNS = 20;

    private MapLocation targetCorner;

    public LaboratoryRobot(RobotController rc) {
        super(rc);
        targetCorner = findTargetCorner();
        idleTurns = MAX_IDLE_TURNS;
    }


    private int idleTurns = 0;
    private int movingTurns = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        Communications.incrementLabCount(rc);

        switch (rc.getMode()) {
            case TURRET:
                if (shouldBecomePortable() && rc.canTransform()) {
                    rc.transform();
                } else {
                    tryTransmute();
                }
                break;
            case PORTABLE:
                if (shouldBecomeTurret() && rc.canTransform()) {
                    rc.transform();
                    movingTurns = 0;
                } else {
                    tryMove();
                    movingTurns++;
                }
        }

    }

    public boolean nearCorner() {
        return rc.getLocation().distanceSquaredTo(targetCorner) < 8;
    }

    public boolean nearCorner(MapLocation loc) {
        return loc.distanceSquaredTo(targetCorner) < 3;
    }

    public boolean shouldBecomePortable() throws GameActionException {
        return ((rc.getTransmutationRate() > TARGET_RATE) && idleTurns > MAX_IDLE_TURNS) || !bestRubbleInArea() || fleeFrom != null;
    }

    public boolean shouldBecomeTurret() throws GameActionException {
        return (rc.getTransmutationRate() <= TARGET_RATE) && bestRubbleInArea() && fleeFrom == null;
    }

    public static final int RUBBLE_THRESHOLD = 10;
    
    public boolean bestRubbleInArea() throws GameActionException {
        MapLocation best = null;
        int bestRubble = 101;
        int rubble = 0;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 3)) {
            rubble = rc.senseRubble(check);
            if (rubble < bestRubble) {
                if (!rc.canSenseRobotAtLocation(check) || rc.getLocation().equals(check)) {
                    best = check;
                    bestRubble = rubble;
                }
            }
        }
        return rc.senseRubble(rc.getLocation()) <= bestRubble + RUBBLE_THRESHOLD;
    }

    RobotInfo[] nearbyRobots;
    int friendlies = 0;
    boolean areDangerousEnemies;
    boolean spottedByEnemy = false;
    MapLocation fleeFrom;
    MapLocation nearestAlly;
    public void processNearbyRobots() throws GameActionException {
        Team team = rc.getTeam();
        nearbyRobots = rc.senseNearbyRobots();
        int friendlyCount = 0;
        int minEnemyDistance = 999;
        MapLocation current = rc.getLocation();
        nearestAlly = null;
        fleeFrom = null;
        int nearestAllyDist = Integer.MAX_VALUE;
        int allyDist;

        processAndBroadcastEnemies(nearbyRobots);

        for (RobotInfo otherRobot : nearbyRobots) {
            if(otherRobot.team == team) {
                friendlyCount++;
                allyDist = otherRobot.location.distanceSquaredTo(current);
                if (allyDist < nearestAllyDist) {
                    nearestAlly = otherRobot.location;
                    nearestAllyDist = allyDist;
                }
            }
            else {
                switch (otherRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        areDangerousEnemies = true;
                        int distance = current.distanceSquaredTo(otherRobot.location);
                        if(distance < minEnemyDistance) {
                            minEnemyDistance = distance;
                            fleeFrom = otherRobot.location;
                        }
                }
                if (current.isWithinDistanceSquared(otherRobot.location, otherRobot.type.visionRadiusSquared)) {
                    spottedByEnemy = true;
                }
            }
        }
        friendlies = friendlyCount;
    }

    public static final int WAIT_MINERS = 2;
    public static final int INCREASE_RATE_TURNS = 20;

    public void tryTransmute() throws GameActionException {
        int lead = rc.getTeamLeadAmount(rc.getTeam());
        if (Communications.getPrevMinerCount(rc) < WAIT_MINERS && lead < 75 + TARGET_RATE) {
            //let archon produce some miners
            // idleTurns++;
            return;
        }
        if(rc.canTransmute() && lead >= MIN_LEAD && rc.getTransmutationRate() <= TARGET_RATE) {
            rc.transmute();
            idleTurns = 0;
        }
        else if (rc.getTransmutationRate() > TARGET_RATE){
            idleTurns++;
            if (idleTurns >= INCREASE_RATE_TURNS) {
                TARGET_RATE++;
                idleTurns = 0;
            }
        }
    }

    public MapLocation findGoodLocation() throws GameActionException {
        MapLocation best = null;
        int bestRubble = 101;
        int rubble = 0;
        int bestDistance = 100;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 8)) {
            rubble = rc.senseRubble(check);
            if (rubble < bestRubble) {
                if (!rc.canSenseRobotAtLocation(check) || rc.getLocation().equals(check)) {
                    best = check;
                    bestRubble = rubble;
                }
            }
            else if(rubble == bestRubble) {
                if(check.distanceSquaredTo(targetCorner) < bestDistance) {
                    bestDistance = check.distanceSquaredTo(targetCorner);
                    best = check;
                }
            }
        }
        return best;
    }

    public boolean tryMove() throws GameActionException {
        boolean switchCorners = false;
        if (nearCorner() && rc.getTransmutationRate() > TARGET_RATE) {
            switchCorners = true;
        }


        if(switchCorners) {
            if(rc.getRoundNum() % 2 == 0) {
                if(targetCorner.x == 0) {
                    targetCorner = new MapLocation(rc.getMapWidth() - 1, targetCorner.y);
                }
                else {
                    targetCorner = new MapLocation(0, targetCorner.y);
                }
            }
            else {
                if(targetCorner.y == 0) {
                    targetCorner = new MapLocation(targetCorner.x, rc.getMapHeight() - 1);
                }
                else {
                    targetCorner = new MapLocation(targetCorner.x, 0);
                }
            }
            idleTurns = 0;
            movingTurns = 0;
        }

        rc.setIndicatorString("targetCorner: " + targetCorner);

        if(rc.isMovementReady()) {
            if(fleeFrom != null) {
                Direction d = Navigation.flee(rc, rc.getLocation(), fleeFrom);
                if(d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            if (!nearCorner()) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), targetCorner);
                if(d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            if (nearestAlly != null) {
                Direction d = Navigation.flee(rc, rc.getLocation(), nearestAlly);
                if (d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }

            MapLocation goodLocation = findGoodLocation();
            Direction d = Navigation.navigate(rc, rc.getLocation(), goodLocation);
            if(d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }
}
