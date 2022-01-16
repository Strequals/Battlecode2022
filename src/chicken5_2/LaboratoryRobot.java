package chicken5_2;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double RATIO = 0.25;
    private static final double MIN_LEAD = 100;

    public LaboratoryRobot(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();

        tryTransmute();
    }

    RobotInfo[] nearbyRobots;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        processAndBroadcastEnemies(nearbyRobots);
    }

    public void tryTransmute() throws GameActionException {
        int lead = rc.getTeamLeadAmount(rc.getTeam());
        int gold = rc.getTeamGoldAmount(rc.getTeam());
        if(rc.canTransmute() && lead >= MIN_LEAD && (gold / lead) <= RATIO) {
            rc.transmute();
        }
    }
}
