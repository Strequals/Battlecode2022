package parrot_2;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double MIN_LEAD = 0;
    private static final int TARGET_RATE = 3;  // will only transmute if rate is this or better
    private static final int MOVE_THRESHOLD = 6;  // threshold for friendlies before lab attempts to move
    private static final int STOP_THRESHOLD = 5;
    static final int MAX_IDLE_TURNS = 5;

    private MapLocation targetCorner;

    public LaboratoryRobot(RobotController rc) {
        super(rc);
        targetCorner = findTargetCorner();
        idleTurns = MAX_IDLE_TURNS;
    }


    private int idleTurns = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
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
                } else {
                    tryMove();
                }
        }
    }

    public boolean nearCorner() {
        return rc.getLocation().distanceSquaredTo(targetCorner) < 3;
    }

    public boolean nearCorner(MapLocation loc) {
        return loc.distanceSquaredTo(targetCorner) < 2;
    }

    public boolean shouldBecomePortable() throws GameActionException {
        return (friendlies > STOP_THRESHOLD) && !nearCorner() && idleTurns > MAX_IDLE_TURNS || !bestRubbleInArea();
    }

    public boolean shouldBecomeTurret() throws GameActionException {
        return (friendlies < STOP_THRESHOLD || nearCorner()) && bestRubbleInArea();
    }
    
    public boolean bestRubbleInArea() throws GameActionException {
        MapLocation best = null;
        int bestRubble = 101;
        int rubble = 0;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 8)) {
            rubble = rc.senseRubble(check);
            if (rubble < bestRubble) {
                if (!rc.canSenseRobotAtLocation(check) || rc.getLocation().equals(check)) {
                    best = check;
                    bestRubble = rubble;
                }
            }
        }
        return rc.senseRubble(rc.getLocation()) == bestRubble;
    }

    RobotInfo[] nearbyRobots;
    int friendlies = 0;
    boolean areDangerousEnemies;
    boolean spottedByEnemy = false;
    MapLocation fleeFrom;
    public void processNearbyRobots() throws GameActionException {
        Team team = rc.getTeam();
        nearbyRobots = rc.senseNearbyRobots();
        int friendlyCount = 0;
        MapLocation myLoc = rc.getLocation();
        int minEnemyDistance = 999;
        MapLocation current = rc.getLocation();

        processAndBroadcastEnemies(nearbyRobots);

        for (RobotInfo otherRobot : nearbyRobots) {
            if(otherRobot.team == team) {
                friendlyCount++;
            }
            else {
                switch (otherRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        areDangerousEnemies = true;
                        int distance = myLoc.distanceSquaredTo(otherRobot.location);
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

    public void tryTransmute() throws GameActionException {
        int lead = rc.getTeamLeadAmount(rc.getTeam());
        if (Communications.getPrevMinerCount(rc) < WAIT_MINERS && lead < 75 + TARGET_RATE) {
            //let archon produce some miners
            idleTurns++;
            return;
        }
        if(rc.canTransmute() && lead >= MIN_LEAD && rc.getTransmutationRate() <= Math.max(TARGET_RATE, TARGET_RATE * (rc.getTeamLeadAmount(rc.getTeam()) - 750) / 150)) {
            rc.transmute();
            idleTurns = 0;
        }
        else if (rc.getTransmutationRate() > Math.max(TARGET_RATE, TARGET_RATE * (rc.getTeamLeadAmount(rc.getTeam()) - 750) / 150)){
            idleTurns++;
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
        if(rc.isMovementReady()) {
            if(!nearCorner() && !(friendlies < STOP_THRESHOLD)) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), targetCorner);
                if(rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            else {
                MapLocation goodLocation = findGoodLocation();
                Direction d = Navigation.navigate(rc, rc.getLocation(), goodLocation);
                if(rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
        }
        return false;
    }
}
