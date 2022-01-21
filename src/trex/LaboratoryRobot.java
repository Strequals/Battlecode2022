package trex;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double MIN_LEAD = 0;
    private static final int TARGET_RATE = 3;  // will only transmute if rate is this or better
    private static final int MOVE_THRESHOLD = 6;  // threshold for friendlies before lab attempts to move
    private static final int STOP_THRESHOLD = 5;

    private MapLocation targetCorner;

    public LaboratoryRobot(RobotController rc) {
        super(rc);
        targetCorner = findTargetCorner();
    }



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
        return rc.getLocation().distanceSquaredTo(targetCorner) < 2;
    }

    public boolean shouldBecomePortable() {
        return (friendlies > STOP_THRESHOLD) && !nearCorner();
    }

    public boolean shouldBecomeTurret() {
        return friendlies < STOP_THRESHOLD || nearCorner();
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

    public void tryTransmute() throws GameActionException {
        int lead = rc.getTeamLeadAmount(rc.getTeam());
        if(rc.canTransmute() && lead >= MIN_LEAD && rc.getTransmutationRate() <= TARGET_RATE) {
            rc.transmute();
        }
    }

    public boolean tryMove() throws GameActionException {
        if(rc.isMovementReady()) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetCorner);
            if(rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }
}
