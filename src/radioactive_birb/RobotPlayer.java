package radioactive_birb;

import battlecode.common.*;
import trex.ArchonRobot;
import trex.BuilderRobot;
import trex.LaboratoryRobot;
import trex.MinerRobot;
import trex.Robot;
import trex.SageRobot;
import trex.SoldierRobot;
import trex.WatchtowerRobot;

public strictfp class RobotPlayer {

    static int turnCount = 0;

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        Robot robot = null;

        switch (rc.getType()) {
            case ARCHON:
                robot = new ArchonRobot(rc); break;
            case MINER:
                robot = new MinerRobot(rc); break;
            case SOLDIER:
                robot = new SoldierRobot(rc); break;
            case LABORATORY:
                robot = new LaboratoryRobot(rc); break;
            case WATCHTOWER:
                robot = new WatchtowerRobot(rc); break;
            case BUILDER:
                robot = new BuilderRobot(rc); break;
            case SAGE:
                robot = new SageRobot(rc); break;
        }

        while (true) {

            turnCount += 1;

            try {
                robot.run();
            } catch (GameActionException e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}