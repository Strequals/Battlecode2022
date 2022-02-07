package crow_2;
import battlecode.common.*;
public strictfp class BFSMiner extends BFS {
    static MapLocation l_n3_n2;
    static double v_n3_n2;
    static Direction d_n3_n2;
    static MapLocation l_n3_n1;
    static double v_n3_n1;
    static Direction d_n3_n1;
    static MapLocation l_n3_0;
    static double v_n3_0;
    static Direction d_n3_0;
    static MapLocation l_n3_p1;
    static double v_n3_p1;
    static Direction d_n3_p1;
    static MapLocation l_n3_p2;
    static double v_n3_p2;
    static Direction d_n3_p2;
    static MapLocation l_n2_n3;
    static double v_n2_n3;
    static Direction d_n2_n3;
    static MapLocation l_n2_n2;
    static double v_n2_n2;
    static Direction d_n2_n2;
    static MapLocation l_n2_n1;
    static double v_n2_n1;
    static Direction d_n2_n1;
    static MapLocation l_n2_0;
    static double v_n2_0;
    static Direction d_n2_0;
    static MapLocation l_n2_p1;
    static double v_n2_p1;
    static Direction d_n2_p1;
    static MapLocation l_n2_p2;
    static double v_n2_p2;
    static Direction d_n2_p2;
    static MapLocation l_n2_p3;
    static double v_n2_p3;
    static Direction d_n2_p3;
    static MapLocation l_n1_n3;
    static double v_n1_n3;
    static Direction d_n1_n3;
    static MapLocation l_n1_n2;
    static double v_n1_n2;
    static Direction d_n1_n2;
    static MapLocation l_n1_n1;
    static double v_n1_n1;
    static Direction d_n1_n1;
    static MapLocation l_n1_0;
    static double v_n1_0;
    static Direction d_n1_0;
    static MapLocation l_n1_p1;
    static double v_n1_p1;
    static Direction d_n1_p1;
    static MapLocation l_n1_p2;
    static double v_n1_p2;
    static Direction d_n1_p2;
    static MapLocation l_n1_p3;
    static double v_n1_p3;
    static Direction d_n1_p3;
    static MapLocation l_0_n3;
    static double v_0_n3;
    static Direction d_0_n3;
    static MapLocation l_0_n2;
    static double v_0_n2;
    static Direction d_0_n2;
    static MapLocation l_0_n1;
    static double v_0_n1;
    static Direction d_0_n1;
    static MapLocation l_0_0;
    static double v_0_0;
    static Direction d_0_0;
    static MapLocation l_0_p1;
    static double v_0_p1;
    static Direction d_0_p1;
    static MapLocation l_0_p2;
    static double v_0_p2;
    static Direction d_0_p2;
    static MapLocation l_0_p3;
    static double v_0_p3;
    static Direction d_0_p3;
    static MapLocation l_p1_n3;
    static double v_p1_n3;
    static Direction d_p1_n3;
    static MapLocation l_p1_n2;
    static double v_p1_n2;
    static Direction d_p1_n2;
    static MapLocation l_p1_n1;
    static double v_p1_n1;
    static Direction d_p1_n1;
    static MapLocation l_p1_0;
    static double v_p1_0;
    static Direction d_p1_0;
    static MapLocation l_p1_p1;
    static double v_p1_p1;
    static Direction d_p1_p1;
    static MapLocation l_p1_p2;
    static double v_p1_p2;
    static Direction d_p1_p2;
    static MapLocation l_p1_p3;
    static double v_p1_p3;
    static Direction d_p1_p3;
    static MapLocation l_p2_n3;
    static double v_p2_n3;
    static Direction d_p2_n3;
    static MapLocation l_p2_n2;
    static double v_p2_n2;
    static Direction d_p2_n2;
    static MapLocation l_p2_n1;
    static double v_p2_n1;
    static Direction d_p2_n1;
    static MapLocation l_p2_0;
    static double v_p2_0;
    static Direction d_p2_0;
    static MapLocation l_p2_p1;
    static double v_p2_p1;
    static Direction d_p2_p1;
    static MapLocation l_p2_p2;
    static double v_p2_p2;
    static Direction d_p2_p2;
    static MapLocation l_p2_p3;
    static double v_p2_p3;
    static Direction d_p2_p3;
    static MapLocation l_p3_n2;
    static double v_p3_n2;
    static Direction d_p3_n2;
    static MapLocation l_p3_n1;
    static double v_p3_n1;
    static Direction d_p3_n1;
    static MapLocation l_p3_0;
    static double v_p3_0;
    static Direction d_p3_0;
    static MapLocation l_p3_p1;
    static double v_p3_p1;
    static Direction d_p3_p1;
    static MapLocation l_p3_p2;
    static double v_p3_p2;
    static Direction d_p3_p2;
    public Direction navigateBFS(RobotController rc, MapLocation source, MapLocation target) throws GameActionException {
        l_n1_n1 = source.add(Direction.SOUTHWEST);
        v_n1_n1 = 1000000000;
        l_n1_p1 = source.add(Direction.NORTHWEST);
        v_n1_p1 = 1000000000;
        l_0_n1 = source.add(Direction.SOUTH);
        v_0_n1 = 1000000000;
        l_0_p1 = source.add(Direction.NORTH);
        v_0_p1 = 1000000000;
        l_p1_n1 = source.add(Direction.SOUTHEAST);
        v_p1_n1 = 1000000000;
        l_p1_p1 = source.add(Direction.NORTHEAST);
        v_p1_p1 = 1000000000;
        l_n1_0 = source.add(Direction.WEST);
        v_n1_0 = 1000000000;
        l_p1_0 = source.add(Direction.EAST);
        v_p1_0 = 1000000000;
        l_n2_n2 = l_n1_n1.add(Direction.SOUTHWEST);
        v_n2_n2 = 1000000000;
        l_n2_p2 = l_n1_p1.add(Direction.NORTHWEST);
        v_n2_p2 = 1000000000;
        l_n1_n2 = l_n1_n1.add(Direction.SOUTH);
        v_n1_n2 = 1000000000;
        l_n1_p2 = l_n1_p1.add(Direction.NORTH);
        v_n1_p2 = 1000000000;
        l_0_n2 = l_0_n1.add(Direction.SOUTH);
        v_0_n2 = 1000000000;
        l_0_p2 = l_0_p1.add(Direction.NORTH);
        v_0_p2 = 1000000000;
        l_p1_n2 = l_p1_n1.add(Direction.SOUTH);
        v_p1_n2 = 1000000000;
        l_p1_p2 = l_p1_p1.add(Direction.NORTH);
        v_p1_p2 = 1000000000;
        l_p2_n2 = l_p1_n1.add(Direction.SOUTHEAST);
        v_p2_n2 = 1000000000;
        l_p2_p2 = l_p1_p1.add(Direction.NORTHEAST);
        v_p2_p2 = 1000000000;
        l_n2_n1 = l_n1_n1.add(Direction.WEST);
        v_n2_n1 = 1000000000;
        l_p2_n1 = l_p1_n1.add(Direction.EAST);
        v_p2_n1 = 1000000000;
        l_n2_0 = l_n1_0.add(Direction.WEST);
        v_n2_0 = 1000000000;
        l_p2_0 = l_p1_0.add(Direction.EAST);
        v_p2_0 = 1000000000;
        l_n2_p1 = l_n1_p1.add(Direction.WEST);
        v_n2_p1 = 1000000000;
        l_p2_p1 = l_p1_p1.add(Direction.EAST);
        v_p2_p1 = 1000000000;
        l_n2_n3 = l_n2_n2.add(Direction.SOUTH);
        v_n2_n3 = 1000000000;
        l_n2_p3 = l_n2_p2.add(Direction.NORTH);
        v_n2_p3 = 1000000000;
        l_n1_n3 = l_n1_n2.add(Direction.SOUTH);
        v_n1_n3 = 1000000000;
        l_n1_p3 = l_n1_p2.add(Direction.NORTH);
        v_n1_p3 = 1000000000;
        l_0_n3 = l_0_n2.add(Direction.SOUTH);
        v_0_n3 = 1000000000;
        l_0_p3 = l_0_p2.add(Direction.NORTH);
        v_0_p3 = 1000000000;
        l_p1_n3 = l_p1_n2.add(Direction.SOUTH);
        v_p1_n3 = 1000000000;
        l_p1_p3 = l_p1_p2.add(Direction.NORTH);
        v_p1_p3 = 1000000000;
        l_p2_n3 = l_p2_n2.add(Direction.SOUTH);
        v_p2_n3 = 1000000000;
        l_p2_p3 = l_p2_p2.add(Direction.NORTH);
        v_p2_p3 = 1000000000;
        l_n3_n2 = l_n2_n2.add(Direction.WEST);
        v_n3_n2 = 1000000000;
        l_p3_n2 = l_p2_n2.add(Direction.EAST);
        v_p3_n2 = 1000000000;
        l_n3_n1 = l_n2_n1.add(Direction.WEST);
        v_n3_n1 = 1000000000;
        l_p3_n1 = l_p2_n1.add(Direction.EAST);
        v_p3_n1 = 1000000000;
        l_n3_0 = l_n2_0.add(Direction.WEST);
        v_n3_0 = 1000000000;
        l_p3_0 = l_p2_0.add(Direction.EAST);
        v_p3_0 = 1000000000;
        l_n3_p1 = l_n2_p1.add(Direction.WEST);
        v_n3_p1 = 1000000000;
        l_p3_p1 = l_p2_p1.add(Direction.EAST);
        v_p3_p1 = 1000000000;
        l_n3_p2 = l_n2_p2.add(Direction.WEST);
        v_n3_p2 = 1000000000;
        l_p3_p2 = l_p2_p2.add(Direction.EAST);
        v_p3_p2 = 1000000000;
        if (rc.onTheMap(l_n1_p1) && !rc.canSenseRobotAtLocation(l_n1_p1)) {
            v_n1_p1 = 1.0 + rc.senseRubble(l_n1_p1) / 10.0;
            d_n1_p1 = Direction.NORTHWEST;
        }
        if (rc.onTheMap(l_0_p1) && !rc.canSenseRobotAtLocation(l_0_p1)) {
            v_0_p1 = 1.0 + rc.senseRubble(l_0_p1) / 10.0;
            d_0_p1 = Direction.NORTH;
        }
        if (rc.onTheMap(l_p1_p1) && !rc.canSenseRobotAtLocation(l_p1_p1)) {
            v_p1_p1 = 1.0 + rc.senseRubble(l_p1_p1) / 10.0;
            d_p1_p1 = Direction.NORTHEAST;
        }
        if (rc.onTheMap(l_p1_0) && !rc.canSenseRobotAtLocation(l_p1_0)) {
            v_p1_0 = 1.0 + rc.senseRubble(l_p1_0) / 10.0;
            d_p1_0 = Direction.EAST;
        }
        if (rc.onTheMap(l_p1_n1) && !rc.canSenseRobotAtLocation(l_p1_n1)) {
            v_p1_n1 = 1.0 + rc.senseRubble(l_p1_n1) / 10.0;
            d_p1_n1 = Direction.SOUTHEAST;
        }
        if (rc.onTheMap(l_0_n1) && !rc.canSenseRobotAtLocation(l_0_n1)) {
            v_0_n1 = 1.0 + rc.senseRubble(l_0_n1) / 10.0;
            d_0_n1 = Direction.SOUTH;
        }
        if (rc.onTheMap(l_n1_n1) && !rc.canSenseRobotAtLocation(l_n1_n1)) {
            v_n1_n1 = 1.0 + rc.senseRubble(l_n1_n1) / 10.0;
            d_n1_n1 = Direction.SOUTHWEST;
        }
        if (rc.onTheMap(l_n1_0) && !rc.canSenseRobotAtLocation(l_n1_0)) {
            v_n1_0 = 1.0 + rc.senseRubble(l_n1_0) / 10.0;
            d_n1_0 = Direction.WEST;
        }
        if (rc.onTheMap(l_n2_p2) && !rc.canSenseRobotAtLocation(l_n2_p2)) {
            v_n2_p2 = v_n1_p1 + 1.0 + rc.senseRubble(l_n2_p2) / 10.0;
            d_n2_p2 = d_n1_p1;
        }
        if (rc.onTheMap(l_n1_p2) && !rc.canSenseRobotAtLocation(l_n1_p2)) {
            v_n1_p2 = v_n1_p1;
            d_n1_p2 = d_n1_p1;
            if (v_n1_p2 > v_0_p1) {
                v_n1_p2 = v_0_p1;
                d_n1_p2 = d_0_p1;
            }
            v_n1_p2 += 1.0 + rc.senseRubble(l_n1_p2) / 10.0;
        }
        if (rc.onTheMap(l_0_p2) && !rc.canSenseRobotAtLocation(l_0_p2)) {
            v_0_p2 = v_n1_p1;
            d_0_p2 = d_n1_p1;
            if (v_0_p2 > v_0_p1) {
                v_0_p2 = v_0_p1;
                d_0_p2 = d_0_p1;
            }
            if (v_0_p2 > v_p1_p1) {
                v_0_p2 = v_p1_p1;
                d_0_p2 = d_p1_p1;
            }
            v_0_p2 += 1.0 + rc.senseRubble(l_0_p2) / 10.0;
        }
        if (rc.onTheMap(l_p1_p2) && !rc.canSenseRobotAtLocation(l_p1_p2)) {
            v_p1_p2 = v_0_p1;
            d_p1_p2 = d_0_p1;
            if (v_p1_p2 > v_p1_p1) {
                v_p1_p2 = v_p1_p1;
                d_p1_p2 = d_p1_p1;
            }
            v_p1_p2 += 1.0 + rc.senseRubble(l_p1_p2) / 10.0;
        }
        if (rc.onTheMap(l_p2_p2) && !rc.canSenseRobotAtLocation(l_p2_p2)) {
            v_p2_p2 = v_p1_p1 + 1.0 + rc.senseRubble(l_p2_p2) / 10.0;
            d_p2_p2 = d_p1_p1;
        }
        if (rc.onTheMap(l_p2_p1) && !rc.canSenseRobotAtLocation(l_p2_p1)) {
            v_p2_p1 = v_p1_0;
            d_p2_p1 = d_p1_0;
            if (v_p2_p1 > v_p1_p1) {
                v_p2_p1 = v_p1_p1;
                d_p2_p1 = d_p1_p1;
            }
            v_p2_p1 += 1.0 + rc.senseRubble(l_p2_p1) / 10.0;
        }
        if (rc.onTheMap(l_p2_0) && !rc.canSenseRobotAtLocation(l_p2_0)) {
            v_p2_0 = v_p1_n1;
            d_p2_0 = d_p1_n1;
            if (v_p2_0 > v_p1_0) {
                v_p2_0 = v_p1_0;
                d_p2_0 = d_p1_0;
            }
            if (v_p2_0 > v_p1_p1) {
                v_p2_0 = v_p1_p1;
                d_p2_0 = d_p1_p1;
            }
            v_p2_0 += 1.0 + rc.senseRubble(l_p2_0) / 10.0;
        }
        if (rc.onTheMap(l_p2_n1) && !rc.canSenseRobotAtLocation(l_p2_n1)) {
            v_p2_n1 = v_p1_n1;
            d_p2_n1 = d_p1_n1;
            if (v_p2_n1 > v_p1_0) {
                v_p2_n1 = v_p1_0;
                d_p2_n1 = d_p1_0;
            }
            v_p2_n1 += 1.0 + rc.senseRubble(l_p2_n1) / 10.0;
        }
        if (rc.onTheMap(l_p2_n2) && !rc.canSenseRobotAtLocation(l_p2_n2)) {
            v_p2_n2 = v_p1_n1 + 1.0 + rc.senseRubble(l_p2_n2) / 10.0;
            d_p2_n2 = d_p1_n1;
        }
        if (rc.onTheMap(l_p1_n2) && !rc.canSenseRobotAtLocation(l_p1_n2)) {
            v_p1_n2 = v_0_n1;
            d_p1_n2 = d_0_n1;
            if (v_p1_n2 > v_p1_n1) {
                v_p1_n2 = v_p1_n1;
                d_p1_n2 = d_p1_n1;
            }
            v_p1_n2 += 1.0 + rc.senseRubble(l_p1_n2) / 10.0;
        }
        if (rc.onTheMap(l_0_n2) && !rc.canSenseRobotAtLocation(l_0_n2)) {
            v_0_n2 = v_n1_n1;
            d_0_n2 = d_n1_n1;
            if (v_0_n2 > v_0_n1) {
                v_0_n2 = v_0_n1;
                d_0_n2 = d_0_n1;
            }
            if (v_0_n2 > v_p1_n1) {
                v_0_n2 = v_p1_n1;
                d_0_n2 = d_p1_n1;
            }
            v_0_n2 += 1.0 + rc.senseRubble(l_0_n2) / 10.0;
        }
        if (rc.onTheMap(l_n1_n2) && !rc.canSenseRobotAtLocation(l_n1_n2)) {
            v_n1_n2 = v_n1_n1;
            d_n1_n2 = d_n1_n1;
            if (v_n1_n2 > v_0_n1) {
                v_n1_n2 = v_0_n1;
                d_n1_n2 = d_0_n1;
            }
            v_n1_n2 += 1.0 + rc.senseRubble(l_n1_n2) / 10.0;
        }
        if (rc.onTheMap(l_n2_n2) && !rc.canSenseRobotAtLocation(l_n2_n2)) {
            v_n2_n2 = v_n1_n1 + 1.0 + rc.senseRubble(l_n2_n2) / 10.0;
            d_n2_n2 = d_n1_n1;
        }
        if (rc.onTheMap(l_n2_n1) && !rc.canSenseRobotAtLocation(l_n2_n1)) {
            v_n2_n1 = v_n1_n1;
            d_n2_n1 = d_n1_n1;
            if (v_n2_n1 > v_n1_0) {
                v_n2_n1 = v_n1_0;
                d_n2_n1 = d_n1_0;
            }
            v_n2_n1 += 1.0 + rc.senseRubble(l_n2_n1) / 10.0;
        }
        if (rc.onTheMap(l_n2_0) && !rc.canSenseRobotAtLocation(l_n2_0)) {
            v_n2_0 = v_n1_n1;
            d_n2_0 = d_n1_n1;
            if (v_n2_0 > v_n1_0) {
                v_n2_0 = v_n1_0;
                d_n2_0 = d_n1_0;
            }
            if (v_n2_0 > v_n1_p1) {
                v_n2_0 = v_n1_p1;
                d_n2_0 = d_n1_p1;
            }
            v_n2_0 += 1.0 + rc.senseRubble(l_n2_0) / 10.0;
        }
        if (rc.onTheMap(l_n2_p1) && !rc.canSenseRobotAtLocation(l_n2_p1)) {
            v_n2_p1 = v_n1_0;
            d_n2_p1 = d_n1_0;
            if (v_n2_p1 > v_n1_p1) {
                v_n2_p1 = v_n1_p1;
                d_n2_p1 = d_n1_p1;
            }
            v_n2_p1 += 1.0 + rc.senseRubble(l_n2_p1) / 10.0;
        }
        if (rc.onTheMap(l_n2_p3) && !rc.canSenseRobotAtLocation(l_n2_p3)) {
            v_n2_p3 = v_n2_p2;
            d_n2_p3 = d_n2_p2;
            if (v_n2_p3 > v_n1_p2) {
                v_n2_p3 = v_n1_p2;
                d_n2_p3 = d_n1_p2;
            }
            v_n2_p3 += 1.0 + rc.senseRubble(l_n2_p3) / 10.0;
        }
        if (rc.onTheMap(l_n1_p3) && !rc.canSenseRobotAtLocation(l_n1_p3)) {
            v_n1_p3 = v_n2_p2;
            d_n1_p3 = d_n2_p2;
            if (v_n1_p3 > v_n1_p2) {
                v_n1_p3 = v_n1_p2;
                d_n1_p3 = d_n1_p2;
            }
            if (v_n1_p3 > v_0_p2) {
                v_n1_p3 = v_0_p2;
                d_n1_p3 = d_0_p2;
            }
            v_n1_p3 += 1.0 + rc.senseRubble(l_n1_p3) / 10.0;
        }
        if (rc.onTheMap(l_0_p3) && !rc.canSenseRobotAtLocation(l_0_p3)) {
            v_0_p3 = v_n1_p2;
            d_0_p3 = d_n1_p2;
            if (v_0_p3 > v_0_p2) {
                v_0_p3 = v_0_p2;
                d_0_p3 = d_0_p2;
            }
            if (v_0_p3 > v_p1_p2) {
                v_0_p3 = v_p1_p2;
                d_0_p3 = d_p1_p2;
            }
            v_0_p3 += 1.0 + rc.senseRubble(l_0_p3) / 10.0;
        }
        if (rc.onTheMap(l_p1_p3) && !rc.canSenseRobotAtLocation(l_p1_p3)) {
            v_p1_p3 = v_0_p2;
            d_p1_p3 = d_0_p2;
            if (v_p1_p3 > v_p1_p2) {
                v_p1_p3 = v_p1_p2;
                d_p1_p3 = d_p1_p2;
            }
            if (v_p1_p3 > v_p2_p2) {
                v_p1_p3 = v_p2_p2;
                d_p1_p3 = d_p2_p2;
            }
            v_p1_p3 += 1.0 + rc.senseRubble(l_p1_p3) / 10.0;
        }
        if (rc.onTheMap(l_p2_p3) && !rc.canSenseRobotAtLocation(l_p2_p3)) {
            v_p2_p3 = v_p1_p2;
            d_p2_p3 = d_p1_p2;
            if (v_p2_p3 > v_p2_p2) {
                v_p2_p3 = v_p2_p2;
                d_p2_p3 = d_p2_p2;
            }
            v_p2_p3 += 1.0 + rc.senseRubble(l_p2_p3) / 10.0;
        }
        if (rc.onTheMap(l_p3_p2) && !rc.canSenseRobotAtLocation(l_p3_p2)) {
            v_p3_p2 = v_p2_p1;
            d_p3_p2 = d_p2_p1;
            if (v_p3_p2 > v_p2_p2) {
                v_p3_p2 = v_p2_p2;
                d_p3_p2 = d_p2_p2;
            }
            v_p3_p2 += 1.0 + rc.senseRubble(l_p3_p2) / 10.0;
        }
        if (rc.onTheMap(l_p3_p1) && !rc.canSenseRobotAtLocation(l_p3_p1)) {
            v_p3_p1 = v_p2_0;
            d_p3_p1 = d_p2_0;
            if (v_p3_p1 > v_p2_p1) {
                v_p3_p1 = v_p2_p1;
                d_p3_p1 = d_p2_p1;
            }
            if (v_p3_p1 > v_p2_p2) {
                v_p3_p1 = v_p2_p2;
                d_p3_p1 = d_p2_p2;
            }
            v_p3_p1 += 1.0 + rc.senseRubble(l_p3_p1) / 10.0;
        }
        if (rc.onTheMap(l_p3_0) && !rc.canSenseRobotAtLocation(l_p3_0)) {
            v_p3_0 = v_p2_n1;
            d_p3_0 = d_p2_n1;
            if (v_p3_0 > v_p2_0) {
                v_p3_0 = v_p2_0;
                d_p3_0 = d_p2_0;
            }
            if (v_p3_0 > v_p2_p1) {
                v_p3_0 = v_p2_p1;
                d_p3_0 = d_p2_p1;
            }
            v_p3_0 += 1.0 + rc.senseRubble(l_p3_0) / 10.0;
        }
        if (rc.onTheMap(l_p3_n1) && !rc.canSenseRobotAtLocation(l_p3_n1)) {
            v_p3_n1 = v_p2_n2;
            d_p3_n1 = d_p2_n2;
            if (v_p3_n1 > v_p2_n1) {
                v_p3_n1 = v_p2_n1;
                d_p3_n1 = d_p2_n1;
            }
            if (v_p3_n1 > v_p2_0) {
                v_p3_n1 = v_p2_0;
                d_p3_n1 = d_p2_0;
            }
            v_p3_n1 += 1.0 + rc.senseRubble(l_p3_n1) / 10.0;
        }
        if (rc.onTheMap(l_p3_n2) && !rc.canSenseRobotAtLocation(l_p3_n2)) {
            v_p3_n2 = v_p2_n2;
            d_p3_n2 = d_p2_n2;
            if (v_p3_n2 > v_p2_n1) {
                v_p3_n2 = v_p2_n1;
                d_p3_n2 = d_p2_n1;
            }
            v_p3_n2 += 1.0 + rc.senseRubble(l_p3_n2) / 10.0;
        }
        if (rc.onTheMap(l_p2_n3) && !rc.canSenseRobotAtLocation(l_p2_n3)) {
            v_p2_n3 = v_p1_n2;
            d_p2_n3 = d_p1_n2;
            if (v_p2_n3 > v_p2_n2) {
                v_p2_n3 = v_p2_n2;
                d_p2_n3 = d_p2_n2;
            }
            v_p2_n3 += 1.0 + rc.senseRubble(l_p2_n3) / 10.0;
        }
        if (rc.onTheMap(l_p1_n3) && !rc.canSenseRobotAtLocation(l_p1_n3)) {
            v_p1_n3 = v_0_n2;
            d_p1_n3 = d_0_n2;
            if (v_p1_n3 > v_p1_n2) {
                v_p1_n3 = v_p1_n2;
                d_p1_n3 = d_p1_n2;
            }
            if (v_p1_n3 > v_p2_n2) {
                v_p1_n3 = v_p2_n2;
                d_p1_n3 = d_p2_n2;
            }
            v_p1_n3 += 1.0 + rc.senseRubble(l_p1_n3) / 10.0;
        }
        if (rc.onTheMap(l_0_n3) && !rc.canSenseRobotAtLocation(l_0_n3)) {
            v_0_n3 = v_n1_n2;
            d_0_n3 = d_n1_n2;
            if (v_0_n3 > v_0_n2) {
                v_0_n3 = v_0_n2;
                d_0_n3 = d_0_n2;
            }
            if (v_0_n3 > v_p1_n2) {
                v_0_n3 = v_p1_n2;
                d_0_n3 = d_p1_n2;
            }
            v_0_n3 += 1.0 + rc.senseRubble(l_0_n3) / 10.0;
        }
        if (rc.onTheMap(l_n1_n3) && !rc.canSenseRobotAtLocation(l_n1_n3)) {
            v_n1_n3 = v_n2_n2;
            d_n1_n3 = d_n2_n2;
            if (v_n1_n3 > v_n1_n2) {
                v_n1_n3 = v_n1_n2;
                d_n1_n3 = d_n1_n2;
            }
            if (v_n1_n3 > v_0_n2) {
                v_n1_n3 = v_0_n2;
                d_n1_n3 = d_0_n2;
            }
            v_n1_n3 += 1.0 + rc.senseRubble(l_n1_n3) / 10.0;
        }
        if (rc.onTheMap(l_n2_n3) && !rc.canSenseRobotAtLocation(l_n2_n3)) {
            v_n2_n3 = v_n2_n2;
            d_n2_n3 = d_n2_n2;
            if (v_n2_n3 > v_n1_n2) {
                v_n2_n3 = v_n1_n2;
                d_n2_n3 = d_n1_n2;
            }
            v_n2_n3 += 1.0 + rc.senseRubble(l_n2_n3) / 10.0;
        }
        if (rc.onTheMap(l_n3_n2) && !rc.canSenseRobotAtLocation(l_n3_n2)) {
            v_n3_n2 = v_n2_n2;
            d_n3_n2 = d_n2_n2;
            if (v_n3_n2 > v_n2_n1) {
                v_n3_n2 = v_n2_n1;
                d_n3_n2 = d_n2_n1;
            }
            v_n3_n2 += 1.0 + rc.senseRubble(l_n3_n2) / 10.0;
        }
        if (rc.onTheMap(l_n3_n1) && !rc.canSenseRobotAtLocation(l_n3_n1)) {
            v_n3_n1 = v_n2_n2;
            d_n3_n1 = d_n2_n2;
            if (v_n3_n1 > v_n2_n1) {
                v_n3_n1 = v_n2_n1;
                d_n3_n1 = d_n2_n1;
            }
            if (v_n3_n1 > v_n2_0) {
                v_n3_n1 = v_n2_0;
                d_n3_n1 = d_n2_0;
            }
            v_n3_n1 += 1.0 + rc.senseRubble(l_n3_n1) / 10.0;
        }
        if (rc.onTheMap(l_n3_0) && !rc.canSenseRobotAtLocation(l_n3_0)) {
            v_n3_0 = v_n2_n1;
            d_n3_0 = d_n2_n1;
            if (v_n3_0 > v_n2_0) {
                v_n3_0 = v_n2_0;
                d_n3_0 = d_n2_0;
            }
            if (v_n3_0 > v_n2_p1) {
                v_n3_0 = v_n2_p1;
                d_n3_0 = d_n2_p1;
            }
            v_n3_0 += 1.0 + rc.senseRubble(l_n3_0) / 10.0;
        }
        if (rc.onTheMap(l_n3_p1) && !rc.canSenseRobotAtLocation(l_n3_p1)) {
            v_n3_p1 = v_n2_0;
            d_n3_p1 = d_n2_0;
            if (v_n3_p1 > v_n2_p1) {
                v_n3_p1 = v_n2_p1;
                d_n3_p1 = d_n2_p1;
            }
            if (v_n3_p1 > v_n2_p2) {
                v_n3_p1 = v_n2_p2;
                d_n3_p1 = d_n2_p2;
            }
            v_n3_p1 += 1.0 + rc.senseRubble(l_n3_p1) / 10.0;
        }
        if (rc.onTheMap(l_n3_p2) && !rc.canSenseRobotAtLocation(l_n3_p2)) {
            v_n3_p2 = v_n2_p1;
            d_n3_p2 = d_n2_p1;
            if (v_n3_p2 > v_n2_p2) {
                v_n3_p2 = v_n2_p2;
                d_n3_p2 = d_n2_p2;
            }
            v_n3_p2 += 1.0 + rc.senseRubble(l_n3_p2) / 10.0;
        }
        int dx = target.x - source.x;
        int dy = target.y - source.y;
        switch (dx) {
            case -3:
                switch (dy) {
                    case -2:
                        return d_n3_n2;
                    case -1:
                        return d_n3_n1;
                    case 0:
                        return d_n3_0;
                    case 1:
                        return d_n3_p1;
                    case 2:
                        return d_n3_p2;
                }
                break;
            case -2:
                switch (dy) {
                    case -3:
                        return d_n2_n3;
                    case -2:
                        return d_n2_n2;
                    case -1:
                        return d_n2_n1;
                    case 0:
                        return d_n2_0;
                    case 1:
                        return d_n2_p1;
                    case 2:
                        return d_n2_p2;
                    case 3:
                        return d_n2_p3;
                }
                break;
            case -1:
                switch (dy) {
                    case -3:
                        return d_n1_n3;
                    case -2:
                        return d_n1_n2;
                    case -1:
                        return d_n1_n1;
                    case 0:
                        return d_n1_0;
                    case 1:
                        return d_n1_p1;
                    case 2:
                        return d_n1_p2;
                    case 3:
                        return d_n1_p3;
                }
                break;
            case 0:
                switch (dy) {
                    case -3:
                        return d_0_n3;
                    case -2:
                        return d_0_n2;
                    case -1:
                        return d_0_n1;
                    case 0:
                        return Direction.CENTER;
                    case 1:
                        return d_0_p1;
                    case 2:
                        return d_0_p2;
                    case 3:
                        return d_0_p3;
                }
                break;
            case 1:
                switch (dy) {
                    case -3:
                        return d_p1_n3;
                    case -2:
                        return d_p1_n2;
                    case -1:
                        return d_p1_n1;
                    case 0:
                        return d_p1_0;
                    case 1:
                        return d_p1_p1;
                    case 2:
                        return d_p1_p2;
                    case 3:
                        return d_p1_p3;
                }
                break;
            case 2:
                switch (dy) {
                    case -3:
                        return d_p2_n3;
                    case -2:
                        return d_p2_n2;
                    case -1:
                        return d_p2_n1;
                    case 0:
                        return d_p2_0;
                    case 1:
                        return d_p2_p1;
                    case 2:
                        return d_p2_p2;
                    case 3:
                        return d_p2_p3;
                }
                break;
            case 3:
                switch (dy) {
                    case -2:
                        return d_p3_n2;
                    case -1:
                        return d_p3_n1;
                    case 0:
                        return d_p3_0;
                    case 1:
                        return d_p3_p1;
                    case 2:
                        return d_p3_p2;
                }
                break;
        }
        Direction best = null;
        double bestEstimate = 0;
        double initialDist = Math.sqrt(source.distanceSquaredTo(target));
        if (dx > 0 || dy >= 0) {
            double e_p1_p3 = (initialDist - Math.sqrt(l_p1_p3.distanceSquaredTo(target))) / v_p1_p3;
            if (e_p1_p3 > bestEstimate) {
                bestEstimate = e_p1_p3;
                best = d_p1_p3;
            }
            double e_p2_p2 = (initialDist - Math.sqrt(l_p2_p2.distanceSquaredTo(target))) / v_p2_p2;
            if (e_p2_p2 > bestEstimate) {
                bestEstimate = e_p2_p2;
                best = d_p2_p2;
            }
            double e_p2_p3 = (initialDist - Math.sqrt(l_p2_p3.distanceSquaredTo(target))) / v_p2_p3;
            if (e_p2_p3 > bestEstimate) {
                bestEstimate = e_p2_p3;
                best = d_p2_p3;
            }
            double e_p3_0 = (initialDist - Math.sqrt(l_p3_0.distanceSquaredTo(target))) / v_p3_0;
            if (e_p3_0 > bestEstimate) {
                bestEstimate = e_p3_0;
                best = d_p3_0;
            }
            double e_p3_p1 = (initialDist - Math.sqrt(l_p3_p1.distanceSquaredTo(target))) / v_p3_p1;
            if (e_p3_p1 > bestEstimate) {
                bestEstimate = e_p3_p1;
                best = d_p3_p1;
            }
            double e_p3_p2 = (initialDist - Math.sqrt(l_p3_p2.distanceSquaredTo(target))) / v_p3_p2;
            if (e_p3_p2 > bestEstimate) {
                bestEstimate = e_p3_p2;
                best = d_p3_p2;
            }
        }
        if (dx <= 0 || dy > 0) {
            double e_n3_p1 = (initialDist - Math.sqrt(l_n3_p1.distanceSquaredTo(target))) / v_n3_p1;
            if (e_n3_p1 > bestEstimate) {
                bestEstimate = e_n3_p1;
                best = d_n3_p1;
            }
            double e_n3_p2 = (initialDist - Math.sqrt(l_n3_p2.distanceSquaredTo(target))) / v_n3_p2;
            if (e_n3_p2 > bestEstimate) {
                bestEstimate = e_n3_p2;
                best = d_n3_p2;
            }
            double e_n2_p2 = (initialDist - Math.sqrt(l_n2_p2.distanceSquaredTo(target))) / v_n2_p2;
            if (e_n2_p2 > bestEstimate) {
                bestEstimate = e_n2_p2;
                best = d_n2_p2;
            }
            double e_n2_p3 = (initialDist - Math.sqrt(l_n2_p3.distanceSquaredTo(target))) / v_n2_p3;
            if (e_n2_p3 > bestEstimate) {
                bestEstimate = e_n2_p3;
                best = d_n2_p3;
            }
            double e_n1_p3 = (initialDist - Math.sqrt(l_n1_p3.distanceSquaredTo(target))) / v_n1_p3;
            if (e_n1_p3 > bestEstimate) {
                bestEstimate = e_n1_p3;
                best = d_n1_p3;
            }
            double e_0_p3 = (initialDist - Math.sqrt(l_0_p3.distanceSquaredTo(target))) / v_0_p3;
            if (e_0_p3 > bestEstimate) {
                bestEstimate = e_0_p3;
                best = d_0_p3;
            }
        }
        if (dx < 0 || dy <= 0) {
            double e_n3_n2 = (initialDist - Math.sqrt(l_n3_n2.distanceSquaredTo(target))) / v_n3_n2;
            if (e_n3_n2 > bestEstimate) {
                bestEstimate = e_n3_n2;
                best = d_n3_n2;
            }
            double e_n3_n1 = (initialDist - Math.sqrt(l_n3_n1.distanceSquaredTo(target))) / v_n3_n1;
            if (e_n3_n1 > bestEstimate) {
                bestEstimate = e_n3_n1;
                best = d_n3_n1;
            }
            double e_n3_0 = (initialDist - Math.sqrt(l_n3_0.distanceSquaredTo(target))) / v_n3_0;
            if (e_n3_0 > bestEstimate) {
                bestEstimate = e_n3_0;
                best = d_n3_0;
            }
            double e_n2_n3 = (initialDist - Math.sqrt(l_n2_n3.distanceSquaredTo(target))) / v_n2_n3;
            if (e_n2_n3 > bestEstimate) {
                bestEstimate = e_n2_n3;
                best = d_n2_n3;
            }
            double e_n2_n2 = (initialDist - Math.sqrt(l_n2_n2.distanceSquaredTo(target))) / v_n2_n2;
            if (e_n2_n2 > bestEstimate) {
                bestEstimate = e_n2_n2;
                best = d_n2_n2;
            }
            double e_n1_n3 = (initialDist - Math.sqrt(l_n1_n3.distanceSquaredTo(target))) / v_n1_n3;
            if (e_n1_n3 > bestEstimate) {
                bestEstimate = e_n1_n3;
                best = d_n1_n3;
            }
        }
        if (dx >= 0 || dy < 0) {
            double e_0_n3 = (initialDist - Math.sqrt(l_0_n3.distanceSquaredTo(target))) / v_0_n3;
            if (e_0_n3 > bestEstimate) {
                bestEstimate = e_0_n3;
                best = d_0_n3;
            }
            double e_p1_n3 = (initialDist - Math.sqrt(l_p1_n3.distanceSquaredTo(target))) / v_p1_n3;
            if (e_p1_n3 > bestEstimate) {
                bestEstimate = e_p1_n3;
                best = d_p1_n3;
            }
            double e_p2_n3 = (initialDist - Math.sqrt(l_p2_n3.distanceSquaredTo(target))) / v_p2_n3;
            if (e_p2_n3 > bestEstimate) {
                bestEstimate = e_p2_n3;
                best = d_p2_n3;
            }
            double e_p2_n2 = (initialDist - Math.sqrt(l_p2_n2.distanceSquaredTo(target))) / v_p2_n2;
            if (e_p2_n2 > bestEstimate) {
                bestEstimate = e_p2_n2;
                best = d_p2_n2;
            }
            double e_p3_n2 = (initialDist - Math.sqrt(l_p3_n2.distanceSquaredTo(target))) / v_p3_n2;
            if (e_p3_n2 > bestEstimate) {
                bestEstimate = e_p3_n2;
                best = d_p3_n2;
            }
            double e_p3_n1 = (initialDist - Math.sqrt(l_p3_n1.distanceSquaredTo(target))) / v_p3_n1;
            if (e_p3_n1 > bestEstimate) {
                bestEstimate = e_p3_n1;
                best = d_p3_n1;
            }
        }
        return best;
    }
}