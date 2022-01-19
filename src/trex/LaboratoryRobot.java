package trex;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double MIN_LEAD = 100;
    private static final int TARGET_RATE = 3;  // will only transmute if rate is this or better
    private static final int MOVE_THRESHOLD = 6;  // threshold for friendlies before lab attempts to move

    public LaboratoryRobot(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();

        tryTransmute();
    }
    
    RobotInfo[] nearbyRobots;
    int friendlies = 0;
    boolean areDangerousEnemies;
    MapLocation fleeFrom;
    public void processNearbyRobots() throws GameActionException {
        Team team = rc.getTeam();
        nearbyRobots = rc.senseNearbyRobots();
        int friendlyCount = 0;
        MapLocation myLoc = rc.getLocation();
        int minEnemyDistance = 999;

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
}
