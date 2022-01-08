package egg2;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final RATIO = 0.25;
    private static final MIN_LEAD = 100;

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
        int lead = 	getTeamLeadAmount(rc.getTeam());
        int gold = 	getTeamGoldAmount(rc.getTeam());
        if(rc.canTransmute() && lead >= MIN_LEAD && (gold / lead) <= RATIO) {
            rc.transmute();
        }
    }
}
