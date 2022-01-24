package parrot3;
import battlecode.common.*;
public strictfp class BFSWatchtower extends BFS {
    static MapLocation l_n5_n3;
    static double v_n5_n3;
    static Direction d_n5_n3;
    static MapLocation l_n5_n2;
    static double v_n5_n2;
    static Direction d_n5_n2;
    static MapLocation l_n5_n1;
    static double v_n5_n1;
    static Direction d_n5_n1;
    static MapLocation l_n5_0;
    static double v_n5_0;
    static Direction d_n5_0;
    static MapLocation l_n5_p1;
    static double v_n5_p1;
    static Direction d_n5_p1;
    static MapLocation l_n5_p2;
    static double v_n5_p2;
    static Direction d_n5_p2;
    static MapLocation l_n5_p3;
    static double v_n5_p3;
    static Direction d_n5_p3;
    static MapLocation l_n4_n4;
    static double v_n4_n4;
    static Direction d_n4_n4;
    static MapLocation l_n4_n3;
    static double v_n4_n3;
    static Direction d_n4_n3;
    static MapLocation l_n4_n2;
    static double v_n4_n2;
    static Direction d_n4_n2;
    static MapLocation l_n4_n1;
    static double v_n4_n1;
    static Direction d_n4_n1;
    static MapLocation l_n4_0;
    static double v_n4_0;
    static Direction d_n4_0;
    static MapLocation l_n4_p1;
    static double v_n4_p1;
    static Direction d_n4_p1;
    static MapLocation l_n4_p2;
    static double v_n4_p2;
    static Direction d_n4_p2;
    static MapLocation l_n4_p3;
    static double v_n4_p3;
    static Direction d_n4_p3;
    static MapLocation l_n4_p4;
    static double v_n4_p4;
    static Direction d_n4_p4;
    static MapLocation l_n3_n5;
    static double v_n3_n5;
    static Direction d_n3_n5;
    static MapLocation l_n3_n4;
    static double v_n3_n4;
    static Direction d_n3_n4;
    static MapLocation l_n3_n3;
    static double v_n3_n3;
    static Direction d_n3_n3;
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
    static MapLocation l_n3_p3;
    static double v_n3_p3;
    static Direction d_n3_p3;
    static MapLocation l_n3_p4;
    static double v_n3_p4;
    static Direction d_n3_p4;
    static MapLocation l_n3_p5;
    static double v_n3_p5;
    static Direction d_n3_p5;
    static MapLocation l_n2_n5;
    static double v_n2_n5;
    static Direction d_n2_n5;
    static MapLocation l_n2_n4;
    static double v_n2_n4;
    static Direction d_n2_n4;
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
    static MapLocation l_n2_p4;
    static double v_n2_p4;
    static Direction d_n2_p4;
    static MapLocation l_n2_p5;
    static double v_n2_p5;
    static Direction d_n2_p5;
    static MapLocation l_n1_n5;
    static double v_n1_n5;
    static Direction d_n1_n5;
    static MapLocation l_n1_n4;
    static double v_n1_n4;
    static Direction d_n1_n4;
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
    static MapLocation l_n1_p4;
    static double v_n1_p4;
    static Direction d_n1_p4;
    static MapLocation l_n1_p5;
    static double v_n1_p5;
    static Direction d_n1_p5;
    static MapLocation l_0_n5;
    static double v_0_n5;
    static Direction d_0_n5;
    static MapLocation l_0_n4;
    static double v_0_n4;
    static Direction d_0_n4;
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
    static MapLocation l_0_p4;
    static double v_0_p4;
    static Direction d_0_p4;
    static MapLocation l_0_p5;
    static double v_0_p5;
    static Direction d_0_p5;
    static MapLocation l_p1_n5;
    static double v_p1_n5;
    static Direction d_p1_n5;
    static MapLocation l_p1_n4;
    static double v_p1_n4;
    static Direction d_p1_n4;
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
    static MapLocation l_p1_p4;
    static double v_p1_p4;
    static Direction d_p1_p4;
    static MapLocation l_p1_p5;
    static double v_p1_p5;
    static Direction d_p1_p5;
    static MapLocation l_p2_n5;
    static double v_p2_n5;
    static Direction d_p2_n5;
    static MapLocation l_p2_n4;
    static double v_p2_n4;
    static Direction d_p2_n4;
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
    static MapLocation l_p2_p4;
    static double v_p2_p4;
    static Direction d_p2_p4;
    static MapLocation l_p2_p5;
    static double v_p2_p5;
    static Direction d_p2_p5;
    static MapLocation l_p3_n5;
    static double v_p3_n5;
    static Direction d_p3_n5;
    static MapLocation l_p3_n4;
    static double v_p3_n4;
    static Direction d_p3_n4;
    static MapLocation l_p3_n3;
    static double v_p3_n3;
    static Direction d_p3_n3;
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
    static MapLocation l_p3_p3;
    static double v_p3_p3;
    static Direction d_p3_p3;
    static MapLocation l_p3_p4;
    static double v_p3_p4;
    static Direction d_p3_p4;
    static MapLocation l_p3_p5;
    static double v_p3_p5;
    static Direction d_p3_p5;
    static MapLocation l_p4_n4;
    static double v_p4_n4;
    static Direction d_p4_n4;
    static MapLocation l_p4_n3;
    static double v_p4_n3;
    static Direction d_p4_n3;
    static MapLocation l_p4_n2;
    static double v_p4_n2;
    static Direction d_p4_n2;
    static MapLocation l_p4_n1;
    static double v_p4_n1;
    static Direction d_p4_n1;
    static MapLocation l_p4_0;
    static double v_p4_0;
    static Direction d_p4_0;
    static MapLocation l_p4_p1;
    static double v_p4_p1;
    static Direction d_p4_p1;
    static MapLocation l_p4_p2;
    static double v_p4_p2;
    static Direction d_p4_p2;
    static MapLocation l_p4_p3;
    static double v_p4_p3;
    static Direction d_p4_p3;
    static MapLocation l_p4_p4;
    static double v_p4_p4;
    static Direction d_p4_p4;
    static MapLocation l_p5_n3;
    static double v_p5_n3;
    static Direction d_p5_n3;
    static MapLocation l_p5_n2;
    static double v_p5_n2;
    static Direction d_p5_n2;
    static MapLocation l_p5_n1;
    static double v_p5_n1;
    static Direction d_p5_n1;
    static MapLocation l_p5_0;
    static double v_p5_0;
    static Direction d_p5_0;
    static MapLocation l_p5_p1;
    static double v_p5_p1;
    static Direction d_p5_p1;
    static MapLocation l_p5_p2;
    static double v_p5_p2;
    static Direction d_p5_p2;
    static MapLocation l_p5_p3;
    static double v_p5_p3;
    static Direction d_p5_p3;
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
        l_n3_n3 = l_n2_n2.add(Direction.SOUTHWEST);
        v_n3_n3 = 1000000000;
        l_n3_p3 = l_n2_p2.add(Direction.NORTHWEST);
        v_n3_p3 = 1000000000;
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
        l_p3_n3 = l_p2_n2.add(Direction.SOUTHEAST);
        v_p3_n3 = 1000000000;
        l_p3_p3 = l_p2_p2.add(Direction.NORTHEAST);
        v_p3_p3 = 1000000000;
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
        l_n4_n4 = l_n3_n3.add(Direction.SOUTHWEST);
        v_n4_n4 = 1000000000;
        l_n4_p4 = l_n3_p3.add(Direction.NORTHWEST);
        v_n4_p4 = 1000000000;
        l_n3_n4 = l_n3_n3.add(Direction.SOUTH);
        v_n3_n4 = 1000000000;
        l_n3_p4 = l_n3_p3.add(Direction.NORTH);
        v_n3_p4 = 1000000000;
        l_n2_n4 = l_n2_n3.add(Direction.SOUTH);
        v_n2_n4 = 1000000000;
        l_n2_p4 = l_n2_p3.add(Direction.NORTH);
        v_n2_p4 = 1000000000;
        l_n1_n4 = l_n1_n3.add(Direction.SOUTH);
        v_n1_n4 = 1000000000;
        l_n1_p4 = l_n1_p3.add(Direction.NORTH);
        v_n1_p4 = 1000000000;
        l_0_n4 = l_0_n3.add(Direction.SOUTH);
        v_0_n4 = 1000000000;
        l_0_p4 = l_0_p3.add(Direction.NORTH);
        v_0_p4 = 1000000000;
        l_p1_n4 = l_p1_n3.add(Direction.SOUTH);
        v_p1_n4 = 1000000000;
        l_p1_p4 = l_p1_p3.add(Direction.NORTH);
        v_p1_p4 = 1000000000;
        l_p2_n4 = l_p2_n3.add(Direction.SOUTH);
        v_p2_n4 = 1000000000;
        l_p2_p4 = l_p2_p3.add(Direction.NORTH);
        v_p2_p4 = 1000000000;
        l_p3_n4 = l_p3_n3.add(Direction.SOUTH);
        v_p3_n4 = 1000000000;
        l_p3_p4 = l_p3_p3.add(Direction.NORTH);
        v_p3_p4 = 1000000000;
        l_p4_n4 = l_p3_n3.add(Direction.SOUTHEAST);
        v_p4_n4 = 1000000000;
        l_p4_p4 = l_p3_p3.add(Direction.NORTHEAST);
        v_p4_p4 = 1000000000;
        l_n4_n3 = l_n3_n3.add(Direction.WEST);
        v_n4_n3 = 1000000000;
        l_p4_n3 = l_p3_n3.add(Direction.EAST);
        v_p4_n3 = 1000000000;
        l_n4_n2 = l_n3_n2.add(Direction.WEST);
        v_n4_n2 = 1000000000;
        l_p4_n2 = l_p3_n2.add(Direction.EAST);
        v_p4_n2 = 1000000000;
        l_n4_n1 = l_n3_n1.add(Direction.WEST);
        v_n4_n1 = 1000000000;
        l_p4_n1 = l_p3_n1.add(Direction.EAST);
        v_p4_n1 = 1000000000;
        l_n4_0 = l_n3_0.add(Direction.WEST);
        v_n4_0 = 1000000000;
        l_p4_0 = l_p3_0.add(Direction.EAST);
        v_p4_0 = 1000000000;
        l_n4_p1 = l_n3_p1.add(Direction.WEST);
        v_n4_p1 = 1000000000;
        l_p4_p1 = l_p3_p1.add(Direction.EAST);
        v_p4_p1 = 1000000000;
        l_n4_p2 = l_n3_p2.add(Direction.WEST);
        v_n4_p2 = 1000000000;
        l_p4_p2 = l_p3_p2.add(Direction.EAST);
        v_p4_p2 = 1000000000;
        l_n4_p3 = l_n3_p3.add(Direction.WEST);
        v_n4_p3 = 1000000000;
        l_p4_p3 = l_p3_p3.add(Direction.EAST);
        v_p4_p3 = 1000000000;
        l_n3_n5 = l_n3_n4.add(Direction.SOUTH);
        v_n3_n5 = 1000000000;
        l_n3_p5 = l_n3_p4.add(Direction.NORTH);
        v_n3_p5 = 1000000000;
        l_n2_n5 = l_n2_n4.add(Direction.SOUTH);
        v_n2_n5 = 1000000000;
        l_n2_p5 = l_n2_p4.add(Direction.NORTH);
        v_n2_p5 = 1000000000;
        l_n1_n5 = l_n1_n4.add(Direction.SOUTH);
        v_n1_n5 = 1000000000;
        l_n1_p5 = l_n1_p4.add(Direction.NORTH);
        v_n1_p5 = 1000000000;
        l_0_n5 = l_0_n4.add(Direction.SOUTH);
        v_0_n5 = 1000000000;
        l_0_p5 = l_0_p4.add(Direction.NORTH);
        v_0_p5 = 1000000000;
        l_p1_n5 = l_p1_n4.add(Direction.SOUTH);
        v_p1_n5 = 1000000000;
        l_p1_p5 = l_p1_p4.add(Direction.NORTH);
        v_p1_p5 = 1000000000;
        l_p2_n5 = l_p2_n4.add(Direction.SOUTH);
        v_p2_n5 = 1000000000;
        l_p2_p5 = l_p2_p4.add(Direction.NORTH);
        v_p2_p5 = 1000000000;
        l_p3_n5 = l_p3_n4.add(Direction.SOUTH);
        v_p3_n5 = 1000000000;
        l_p3_p5 = l_p3_p4.add(Direction.NORTH);
        v_p3_p5 = 1000000000;
        l_n5_n3 = l_n4_n3.add(Direction.WEST);
        v_n5_n3 = 1000000000;
        l_p5_n3 = l_p4_n3.add(Direction.EAST);
        v_p5_n3 = 1000000000;
        l_n5_n2 = l_n4_n2.add(Direction.WEST);
        v_n5_n2 = 1000000000;
        l_p5_n2 = l_p4_n2.add(Direction.EAST);
        v_p5_n2 = 1000000000;
        l_n5_n1 = l_n4_n1.add(Direction.WEST);
        v_n5_n1 = 1000000000;
        l_p5_n1 = l_p4_n1.add(Direction.EAST);
        v_p5_n1 = 1000000000;
        l_n5_0 = l_n4_0.add(Direction.WEST);
        v_n5_0 = 1000000000;
        l_p5_0 = l_p4_0.add(Direction.EAST);
        v_p5_0 = 1000000000;
        l_n5_p1 = l_n4_p1.add(Direction.WEST);
        v_n5_p1 = 1000000000;
        l_p5_p1 = l_p4_p1.add(Direction.EAST);
        v_p5_p1 = 1000000000;
        l_n5_p2 = l_n4_p2.add(Direction.WEST);
        v_n5_p2 = 1000000000;
        l_p5_p2 = l_p4_p2.add(Direction.EAST);
        v_p5_p2 = 1000000000;
        l_n5_p3 = l_n4_p3.add(Direction.WEST);
        v_n5_p3 = 1000000000;
        l_p5_p3 = l_p4_p3.add(Direction.EAST);
        v_p5_p3 = 1000000000;
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
        if (rc.onTheMap(l_n3_p3) && !rc.canSenseRobotAtLocation(l_n3_p3)) {
            v_n3_p3 = v_n2_p2 + 1.0 + rc.senseRubble(l_n3_p3) / 10.0;
            d_n3_p3 = d_n2_p2;
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
        if (rc.onTheMap(l_p3_p3) && !rc.canSenseRobotAtLocation(l_p3_p3)) {
            v_p3_p3 = v_p2_p2 + 1.0 + rc.senseRubble(l_p3_p3) / 10.0;
            d_p3_p3 = d_p2_p2;
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
        if (rc.onTheMap(l_p3_n3) && !rc.canSenseRobotAtLocation(l_p3_n3)) {
            v_p3_n3 = v_p2_n2 + 1.0 + rc.senseRubble(l_p3_n3) / 10.0;
            d_p3_n3 = d_p2_n2;
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
        if (rc.onTheMap(l_n3_n3) && !rc.canSenseRobotAtLocation(l_n3_n3)) {
            v_n3_n3 = v_n2_n2 + 1.0 + rc.senseRubble(l_n3_n3) / 10.0;
            d_n3_n3 = d_n2_n2;
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
        if (rc.onTheMap(l_n4_p4) && !rc.canSenseRobotAtLocation(l_n4_p4)) {
            v_n4_p4 = v_n3_p3 + 1.0 + rc.senseRubble(l_n4_p4) / 10.0;
            d_n4_p4 = d_n3_p3;
        }
        if (rc.onTheMap(l_n3_p4) && !rc.canSenseRobotAtLocation(l_n3_p4)) {
            v_n3_p4 = v_n3_p3;
            d_n3_p4 = d_n3_p3;
            if (v_n3_p4 > v_n2_p3) {
                v_n3_p4 = v_n2_p3;
                d_n3_p4 = d_n2_p3;
            }
            v_n3_p4 += 1.0 + rc.senseRubble(l_n3_p4) / 10.0;
        }
        if (rc.onTheMap(l_n2_p4) && !rc.canSenseRobotAtLocation(l_n2_p4)) {
            v_n2_p4 = v_n3_p3;
            d_n2_p4 = d_n3_p3;
            if (v_n2_p4 > v_n2_p3) {
                v_n2_p4 = v_n2_p3;
                d_n2_p4 = d_n2_p3;
            }
            if (v_n2_p4 > v_n1_p3) {
                v_n2_p4 = v_n1_p3;
                d_n2_p4 = d_n1_p3;
            }
            v_n2_p4 += 1.0 + rc.senseRubble(l_n2_p4) / 10.0;
        }
        if (rc.onTheMap(l_n1_p4) && !rc.canSenseRobotAtLocation(l_n1_p4)) {
            v_n1_p4 = v_n2_p3;
            d_n1_p4 = d_n2_p3;
            if (v_n1_p4 > v_n1_p3) {
                v_n1_p4 = v_n1_p3;
                d_n1_p4 = d_n1_p3;
            }
            if (v_n1_p4 > v_0_p3) {
                v_n1_p4 = v_0_p3;
                d_n1_p4 = d_0_p3;
            }
            v_n1_p4 += 1.0 + rc.senseRubble(l_n1_p4) / 10.0;
        }
        if (rc.onTheMap(l_0_p4) && !rc.canSenseRobotAtLocation(l_0_p4)) {
            v_0_p4 = v_n1_p3;
            d_0_p4 = d_n1_p3;
            if (v_0_p4 > v_0_p3) {
                v_0_p4 = v_0_p3;
                d_0_p4 = d_0_p3;
            }
            if (v_0_p4 > v_p1_p3) {
                v_0_p4 = v_p1_p3;
                d_0_p4 = d_p1_p3;
            }
            v_0_p4 += 1.0 + rc.senseRubble(l_0_p4) / 10.0;
        }
        if (rc.onTheMap(l_p1_p4) && !rc.canSenseRobotAtLocation(l_p1_p4)) {
            v_p1_p4 = v_0_p3;
            d_p1_p4 = d_0_p3;
            if (v_p1_p4 > v_p1_p3) {
                v_p1_p4 = v_p1_p3;
                d_p1_p4 = d_p1_p3;
            }
            if (v_p1_p4 > v_p2_p3) {
                v_p1_p4 = v_p2_p3;
                d_p1_p4 = d_p2_p3;
            }
            v_p1_p4 += 1.0 + rc.senseRubble(l_p1_p4) / 10.0;
        }
        if (rc.onTheMap(l_p2_p4) && !rc.canSenseRobotAtLocation(l_p2_p4)) {
            v_p2_p4 = v_p1_p3;
            d_p2_p4 = d_p1_p3;
            if (v_p2_p4 > v_p2_p3) {
                v_p2_p4 = v_p2_p3;
                d_p2_p4 = d_p2_p3;
            }
            if (v_p2_p4 > v_p3_p3) {
                v_p2_p4 = v_p3_p3;
                d_p2_p4 = d_p3_p3;
            }
            v_p2_p4 += 1.0 + rc.senseRubble(l_p2_p4) / 10.0;
        }
        if (rc.onTheMap(l_p3_p4) && !rc.canSenseRobotAtLocation(l_p3_p4)) {
            v_p3_p4 = v_p2_p3;
            d_p3_p4 = d_p2_p3;
            if (v_p3_p4 > v_p3_p3) {
                v_p3_p4 = v_p3_p3;
                d_p3_p4 = d_p3_p3;
            }
            v_p3_p4 += 1.0 + rc.senseRubble(l_p3_p4) / 10.0;
        }
        if (rc.onTheMap(l_p4_p4) && !rc.canSenseRobotAtLocation(l_p4_p4)) {
            v_p4_p4 = v_p3_p3 + 1.0 + rc.senseRubble(l_p4_p4) / 10.0;
            d_p4_p4 = d_p3_p3;
        }
        if (rc.onTheMap(l_p4_p3) && !rc.canSenseRobotAtLocation(l_p4_p3)) {
            v_p4_p3 = v_p3_p2;
            d_p4_p3 = d_p3_p2;
            if (v_p4_p3 > v_p3_p3) {
                v_p4_p3 = v_p3_p3;
                d_p4_p3 = d_p3_p3;
            }
            v_p4_p3 += 1.0 + rc.senseRubble(l_p4_p3) / 10.0;
        }
        if (rc.onTheMap(l_p4_p2) && !rc.canSenseRobotAtLocation(l_p4_p2)) {
            v_p4_p2 = v_p3_p1;
            d_p4_p2 = d_p3_p1;
            if (v_p4_p2 > v_p3_p2) {
                v_p4_p2 = v_p3_p2;
                d_p4_p2 = d_p3_p2;
            }
            if (v_p4_p2 > v_p3_p3) {
                v_p4_p2 = v_p3_p3;
                d_p4_p2 = d_p3_p3;
            }
            v_p4_p2 += 1.0 + rc.senseRubble(l_p4_p2) / 10.0;
        }
        if (rc.onTheMap(l_p4_p1) && !rc.canSenseRobotAtLocation(l_p4_p1)) {
            v_p4_p1 = v_p3_0;
            d_p4_p1 = d_p3_0;
            if (v_p4_p1 > v_p3_p1) {
                v_p4_p1 = v_p3_p1;
                d_p4_p1 = d_p3_p1;
            }
            if (v_p4_p1 > v_p3_p2) {
                v_p4_p1 = v_p3_p2;
                d_p4_p1 = d_p3_p2;
            }
            v_p4_p1 += 1.0 + rc.senseRubble(l_p4_p1) / 10.0;
        }
        if (rc.onTheMap(l_p4_0) && !rc.canSenseRobotAtLocation(l_p4_0)) {
            v_p4_0 = v_p3_n1;
            d_p4_0 = d_p3_n1;
            if (v_p4_0 > v_p3_0) {
                v_p4_0 = v_p3_0;
                d_p4_0 = d_p3_0;
            }
            if (v_p4_0 > v_p3_p1) {
                v_p4_0 = v_p3_p1;
                d_p4_0 = d_p3_p1;
            }
            v_p4_0 += 1.0 + rc.senseRubble(l_p4_0) / 10.0;
        }
        if (rc.onTheMap(l_p4_n1) && !rc.canSenseRobotAtLocation(l_p4_n1)) {
            v_p4_n1 = v_p3_n2;
            d_p4_n1 = d_p3_n2;
            if (v_p4_n1 > v_p3_n1) {
                v_p4_n1 = v_p3_n1;
                d_p4_n1 = d_p3_n1;
            }
            if (v_p4_n1 > v_p3_0) {
                v_p4_n1 = v_p3_0;
                d_p4_n1 = d_p3_0;
            }
            v_p4_n1 += 1.0 + rc.senseRubble(l_p4_n1) / 10.0;
        }
        if (rc.onTheMap(l_p4_n2) && !rc.canSenseRobotAtLocation(l_p4_n2)) {
            v_p4_n2 = v_p3_n3;
            d_p4_n2 = d_p3_n3;
            if (v_p4_n2 > v_p3_n2) {
                v_p4_n2 = v_p3_n2;
                d_p4_n2 = d_p3_n2;
            }
            if (v_p4_n2 > v_p3_n1) {
                v_p4_n2 = v_p3_n1;
                d_p4_n2 = d_p3_n1;
            }
            v_p4_n2 += 1.0 + rc.senseRubble(l_p4_n2) / 10.0;
        }
        if (rc.onTheMap(l_p4_n3) && !rc.canSenseRobotAtLocation(l_p4_n3)) {
            v_p4_n3 = v_p3_n3;
            d_p4_n3 = d_p3_n3;
            if (v_p4_n3 > v_p3_n2) {
                v_p4_n3 = v_p3_n2;
                d_p4_n3 = d_p3_n2;
            }
            v_p4_n3 += 1.0 + rc.senseRubble(l_p4_n3) / 10.0;
        }
        if (rc.onTheMap(l_p4_n4) && !rc.canSenseRobotAtLocation(l_p4_n4)) {
            v_p4_n4 = v_p3_n3 + 1.0 + rc.senseRubble(l_p4_n4) / 10.0;
            d_p4_n4 = d_p3_n3;
        }
        if (rc.onTheMap(l_p3_n4) && !rc.canSenseRobotAtLocation(l_p3_n4)) {
            v_p3_n4 = v_p2_n3;
            d_p3_n4 = d_p2_n3;
            if (v_p3_n4 > v_p3_n3) {
                v_p3_n4 = v_p3_n3;
                d_p3_n4 = d_p3_n3;
            }
            v_p3_n4 += 1.0 + rc.senseRubble(l_p3_n4) / 10.0;
        }
        if (rc.onTheMap(l_p2_n4) && !rc.canSenseRobotAtLocation(l_p2_n4)) {
            v_p2_n4 = v_p1_n3;
            d_p2_n4 = d_p1_n3;
            if (v_p2_n4 > v_p2_n3) {
                v_p2_n4 = v_p2_n3;
                d_p2_n4 = d_p2_n3;
            }
            if (v_p2_n4 > v_p3_n3) {
                v_p2_n4 = v_p3_n3;
                d_p2_n4 = d_p3_n3;
            }
            v_p2_n4 += 1.0 + rc.senseRubble(l_p2_n4) / 10.0;
        }
        if (rc.onTheMap(l_p1_n4) && !rc.canSenseRobotAtLocation(l_p1_n4)) {
            v_p1_n4 = v_0_n3;
            d_p1_n4 = d_0_n3;
            if (v_p1_n4 > v_p1_n3) {
                v_p1_n4 = v_p1_n3;
                d_p1_n4 = d_p1_n3;
            }
            if (v_p1_n4 > v_p2_n3) {
                v_p1_n4 = v_p2_n3;
                d_p1_n4 = d_p2_n3;
            }
            v_p1_n4 += 1.0 + rc.senseRubble(l_p1_n4) / 10.0;
        }
        if (rc.onTheMap(l_0_n4) && !rc.canSenseRobotAtLocation(l_0_n4)) {
            v_0_n4 = v_n1_n3;
            d_0_n4 = d_n1_n3;
            if (v_0_n4 > v_0_n3) {
                v_0_n4 = v_0_n3;
                d_0_n4 = d_0_n3;
            }
            if (v_0_n4 > v_p1_n3) {
                v_0_n4 = v_p1_n3;
                d_0_n4 = d_p1_n3;
            }
            v_0_n4 += 1.0 + rc.senseRubble(l_0_n4) / 10.0;
        }
        if (rc.onTheMap(l_n1_n4) && !rc.canSenseRobotAtLocation(l_n1_n4)) {
            v_n1_n4 = v_n2_n3;
            d_n1_n4 = d_n2_n3;
            if (v_n1_n4 > v_n1_n3) {
                v_n1_n4 = v_n1_n3;
                d_n1_n4 = d_n1_n3;
            }
            if (v_n1_n4 > v_0_n3) {
                v_n1_n4 = v_0_n3;
                d_n1_n4 = d_0_n3;
            }
            v_n1_n4 += 1.0 + rc.senseRubble(l_n1_n4) / 10.0;
        }
        if (rc.onTheMap(l_n2_n4) && !rc.canSenseRobotAtLocation(l_n2_n4)) {
            v_n2_n4 = v_n3_n3;
            d_n2_n4 = d_n3_n3;
            if (v_n2_n4 > v_n2_n3) {
                v_n2_n4 = v_n2_n3;
                d_n2_n4 = d_n2_n3;
            }
            if (v_n2_n4 > v_n1_n3) {
                v_n2_n4 = v_n1_n3;
                d_n2_n4 = d_n1_n3;
            }
            v_n2_n4 += 1.0 + rc.senseRubble(l_n2_n4) / 10.0;
        }
        if (rc.onTheMap(l_n3_n4) && !rc.canSenseRobotAtLocation(l_n3_n4)) {
            v_n3_n4 = v_n3_n3;
            d_n3_n4 = d_n3_n3;
            if (v_n3_n4 > v_n2_n3) {
                v_n3_n4 = v_n2_n3;
                d_n3_n4 = d_n2_n3;
            }
            v_n3_n4 += 1.0 + rc.senseRubble(l_n3_n4) / 10.0;
        }
        if (rc.onTheMap(l_n4_n4) && !rc.canSenseRobotAtLocation(l_n4_n4)) {
            v_n4_n4 = v_n3_n3 + 1.0 + rc.senseRubble(l_n4_n4) / 10.0;
            d_n4_n4 = d_n3_n3;
        }
        if (rc.onTheMap(l_n4_n3) && !rc.canSenseRobotAtLocation(l_n4_n3)) {
            v_n4_n3 = v_n3_n3;
            d_n4_n3 = d_n3_n3;
            if (v_n4_n3 > v_n3_n2) {
                v_n4_n3 = v_n3_n2;
                d_n4_n3 = d_n3_n2;
            }
            v_n4_n3 += 1.0 + rc.senseRubble(l_n4_n3) / 10.0;
        }
        if (rc.onTheMap(l_n4_n2) && !rc.canSenseRobotAtLocation(l_n4_n2)) {
            v_n4_n2 = v_n3_n3;
            d_n4_n2 = d_n3_n3;
            if (v_n4_n2 > v_n3_n2) {
                v_n4_n2 = v_n3_n2;
                d_n4_n2 = d_n3_n2;
            }
            if (v_n4_n2 > v_n3_n1) {
                v_n4_n2 = v_n3_n1;
                d_n4_n2 = d_n3_n1;
            }
            v_n4_n2 += 1.0 + rc.senseRubble(l_n4_n2) / 10.0;
        }
        if (rc.onTheMap(l_n4_n1) && !rc.canSenseRobotAtLocation(l_n4_n1)) {
            v_n4_n1 = v_n3_n2;
            d_n4_n1 = d_n3_n2;
            if (v_n4_n1 > v_n3_n1) {
                v_n4_n1 = v_n3_n1;
                d_n4_n1 = d_n3_n1;
            }
            if (v_n4_n1 > v_n3_0) {
                v_n4_n1 = v_n3_0;
                d_n4_n1 = d_n3_0;
            }
            v_n4_n1 += 1.0 + rc.senseRubble(l_n4_n1) / 10.0;
        }
        if (rc.onTheMap(l_n4_0) && !rc.canSenseRobotAtLocation(l_n4_0)) {
            v_n4_0 = v_n3_n1;
            d_n4_0 = d_n3_n1;
            if (v_n4_0 > v_n3_0) {
                v_n4_0 = v_n3_0;
                d_n4_0 = d_n3_0;
            }
            if (v_n4_0 > v_n3_p1) {
                v_n4_0 = v_n3_p1;
                d_n4_0 = d_n3_p1;
            }
            v_n4_0 += 1.0 + rc.senseRubble(l_n4_0) / 10.0;
        }
        if (rc.onTheMap(l_n4_p1) && !rc.canSenseRobotAtLocation(l_n4_p1)) {
            v_n4_p1 = v_n3_0;
            d_n4_p1 = d_n3_0;
            if (v_n4_p1 > v_n3_p1) {
                v_n4_p1 = v_n3_p1;
                d_n4_p1 = d_n3_p1;
            }
            if (v_n4_p1 > v_n3_p2) {
                v_n4_p1 = v_n3_p2;
                d_n4_p1 = d_n3_p2;
            }
            v_n4_p1 += 1.0 + rc.senseRubble(l_n4_p1) / 10.0;
        }
        if (rc.onTheMap(l_n4_p2) && !rc.canSenseRobotAtLocation(l_n4_p2)) {
            v_n4_p2 = v_n3_p1;
            d_n4_p2 = d_n3_p1;
            if (v_n4_p2 > v_n3_p2) {
                v_n4_p2 = v_n3_p2;
                d_n4_p2 = d_n3_p2;
            }
            if (v_n4_p2 > v_n3_p3) {
                v_n4_p2 = v_n3_p3;
                d_n4_p2 = d_n3_p3;
            }
            v_n4_p2 += 1.0 + rc.senseRubble(l_n4_p2) / 10.0;
        }
        if (rc.onTheMap(l_n4_p3) && !rc.canSenseRobotAtLocation(l_n4_p3)) {
            v_n4_p3 = v_n3_p2;
            d_n4_p3 = d_n3_p2;
            if (v_n4_p3 > v_n3_p3) {
                v_n4_p3 = v_n3_p3;
                d_n4_p3 = d_n3_p3;
            }
            v_n4_p3 += 1.0 + rc.senseRubble(l_n4_p3) / 10.0;
        }
        if (rc.onTheMap(l_n3_p5) && !rc.canSenseRobotAtLocation(l_n3_p5)) {
            v_n3_p5 = v_n4_p4;
            d_n3_p5 = d_n4_p4;
            if (v_n3_p5 > v_n3_p4) {
                v_n3_p5 = v_n3_p4;
                d_n3_p5 = d_n3_p4;
            }
            if (v_n3_p5 > v_n2_p4) {
                v_n3_p5 = v_n2_p4;
                d_n3_p5 = d_n2_p4;
            }
            v_n3_p5 += 1.0 + rc.senseRubble(l_n3_p5) / 10.0;
        }
        if (rc.onTheMap(l_n2_p5) && !rc.canSenseRobotAtLocation(l_n2_p5)) {
            v_n2_p5 = v_n3_p4;
            d_n2_p5 = d_n3_p4;
            if (v_n2_p5 > v_n2_p4) {
                v_n2_p5 = v_n2_p4;
                d_n2_p5 = d_n2_p4;
            }
            if (v_n2_p5 > v_n1_p4) {
                v_n2_p5 = v_n1_p4;
                d_n2_p5 = d_n1_p4;
            }
            v_n2_p5 += 1.0 + rc.senseRubble(l_n2_p5) / 10.0;
        }
        if (rc.onTheMap(l_n1_p5) && !rc.canSenseRobotAtLocation(l_n1_p5)) {
            v_n1_p5 = v_n2_p4;
            d_n1_p5 = d_n2_p4;
            if (v_n1_p5 > v_n1_p4) {
                v_n1_p5 = v_n1_p4;
                d_n1_p5 = d_n1_p4;
            }
            if (v_n1_p5 > v_0_p4) {
                v_n1_p5 = v_0_p4;
                d_n1_p5 = d_0_p4;
            }
            v_n1_p5 += 1.0 + rc.senseRubble(l_n1_p5) / 10.0;
        }
        if (rc.onTheMap(l_0_p5) && !rc.canSenseRobotAtLocation(l_0_p5)) {
            v_0_p5 = v_n1_p4;
            d_0_p5 = d_n1_p4;
            if (v_0_p5 > v_0_p4) {
                v_0_p5 = v_0_p4;
                d_0_p5 = d_0_p4;
            }
            if (v_0_p5 > v_p1_p4) {
                v_0_p5 = v_p1_p4;
                d_0_p5 = d_p1_p4;
            }
            v_0_p5 += 1.0 + rc.senseRubble(l_0_p5) / 10.0;
        }
        if (rc.onTheMap(l_p1_p5) && !rc.canSenseRobotAtLocation(l_p1_p5)) {
            v_p1_p5 = v_0_p4;
            d_p1_p5 = d_0_p4;
            if (v_p1_p5 > v_p1_p4) {
                v_p1_p5 = v_p1_p4;
                d_p1_p5 = d_p1_p4;
            }
            if (v_p1_p5 > v_p2_p4) {
                v_p1_p5 = v_p2_p4;
                d_p1_p5 = d_p2_p4;
            }
            v_p1_p5 += 1.0 + rc.senseRubble(l_p1_p5) / 10.0;
        }
        if (rc.onTheMap(l_p2_p5) && !rc.canSenseRobotAtLocation(l_p2_p5)) {
            v_p2_p5 = v_p1_p4;
            d_p2_p5 = d_p1_p4;
            if (v_p2_p5 > v_p2_p4) {
                v_p2_p5 = v_p2_p4;
                d_p2_p5 = d_p2_p4;
            }
            if (v_p2_p5 > v_p3_p4) {
                v_p2_p5 = v_p3_p4;
                d_p2_p5 = d_p3_p4;
            }
            v_p2_p5 += 1.0 + rc.senseRubble(l_p2_p5) / 10.0;
        }
        if (rc.onTheMap(l_p3_p5) && !rc.canSenseRobotAtLocation(l_p3_p5)) {
            v_p3_p5 = v_p2_p4;
            d_p3_p5 = d_p2_p4;
            if (v_p3_p5 > v_p3_p4) {
                v_p3_p5 = v_p3_p4;
                d_p3_p5 = d_p3_p4;
            }
            if (v_p3_p5 > v_p4_p4) {
                v_p3_p5 = v_p4_p4;
                d_p3_p5 = d_p4_p4;
            }
            v_p3_p5 += 1.0 + rc.senseRubble(l_p3_p5) / 10.0;
        }
        if (rc.onTheMap(l_p5_p3) && !rc.canSenseRobotAtLocation(l_p5_p3)) {
            v_p5_p3 = v_p4_p2;
            d_p5_p3 = d_p4_p2;
            if (v_p5_p3 > v_p4_p3) {
                v_p5_p3 = v_p4_p3;
                d_p5_p3 = d_p4_p3;
            }
            if (v_p5_p3 > v_p4_p4) {
                v_p5_p3 = v_p4_p4;
                d_p5_p3 = d_p4_p4;
            }
            v_p5_p3 += 1.0 + rc.senseRubble(l_p5_p3) / 10.0;
        }
        if (rc.onTheMap(l_p5_p2) && !rc.canSenseRobotAtLocation(l_p5_p2)) {
            v_p5_p2 = v_p4_p1;
            d_p5_p2 = d_p4_p1;
            if (v_p5_p2 > v_p4_p2) {
                v_p5_p2 = v_p4_p2;
                d_p5_p2 = d_p4_p2;
            }
            if (v_p5_p2 > v_p4_p3) {
                v_p5_p2 = v_p4_p3;
                d_p5_p2 = d_p4_p3;
            }
            v_p5_p2 += 1.0 + rc.senseRubble(l_p5_p2) / 10.0;
        }
        if (rc.onTheMap(l_p5_p1) && !rc.canSenseRobotAtLocation(l_p5_p1)) {
            v_p5_p1 = v_p4_0;
            d_p5_p1 = d_p4_0;
            if (v_p5_p1 > v_p4_p1) {
                v_p5_p1 = v_p4_p1;
                d_p5_p1 = d_p4_p1;
            }
            if (v_p5_p1 > v_p4_p2) {
                v_p5_p1 = v_p4_p2;
                d_p5_p1 = d_p4_p2;
            }
            v_p5_p1 += 1.0 + rc.senseRubble(l_p5_p1) / 10.0;
        }
        if (rc.onTheMap(l_p5_0) && !rc.canSenseRobotAtLocation(l_p5_0)) {
            v_p5_0 = v_p4_n1;
            d_p5_0 = d_p4_n1;
            if (v_p5_0 > v_p4_0) {
                v_p5_0 = v_p4_0;
                d_p5_0 = d_p4_0;
            }
            if (v_p5_0 > v_p4_p1) {
                v_p5_0 = v_p4_p1;
                d_p5_0 = d_p4_p1;
            }
            v_p5_0 += 1.0 + rc.senseRubble(l_p5_0) / 10.0;
        }
        if (rc.onTheMap(l_p5_n1) && !rc.canSenseRobotAtLocation(l_p5_n1)) {
            v_p5_n1 = v_p4_n2;
            d_p5_n1 = d_p4_n2;
            if (v_p5_n1 > v_p4_n1) {
                v_p5_n1 = v_p4_n1;
                d_p5_n1 = d_p4_n1;
            }
            if (v_p5_n1 > v_p4_0) {
                v_p5_n1 = v_p4_0;
                d_p5_n1 = d_p4_0;
            }
            v_p5_n1 += 1.0 + rc.senseRubble(l_p5_n1) / 10.0;
        }
        if (rc.onTheMap(l_p5_n2) && !rc.canSenseRobotAtLocation(l_p5_n2)) {
            v_p5_n2 = v_p4_n3;
            d_p5_n2 = d_p4_n3;
            if (v_p5_n2 > v_p4_n2) {
                v_p5_n2 = v_p4_n2;
                d_p5_n2 = d_p4_n2;
            }
            if (v_p5_n2 > v_p4_n1) {
                v_p5_n2 = v_p4_n1;
                d_p5_n2 = d_p4_n1;
            }
            v_p5_n2 += 1.0 + rc.senseRubble(l_p5_n2) / 10.0;
        }
        if (rc.onTheMap(l_p5_n3) && !rc.canSenseRobotAtLocation(l_p5_n3)) {
            v_p5_n3 = v_p4_n4;
            d_p5_n3 = d_p4_n4;
            if (v_p5_n3 > v_p4_n3) {
                v_p5_n3 = v_p4_n3;
                d_p5_n3 = d_p4_n3;
            }
            if (v_p5_n3 > v_p4_n2) {
                v_p5_n3 = v_p4_n2;
                d_p5_n3 = d_p4_n2;
            }
            v_p5_n3 += 1.0 + rc.senseRubble(l_p5_n3) / 10.0;
        }
        if (rc.onTheMap(l_p3_n5) && !rc.canSenseRobotAtLocation(l_p3_n5)) {
            v_p3_n5 = v_p2_n4;
            d_p3_n5 = d_p2_n4;
            if (v_p3_n5 > v_p3_n4) {
                v_p3_n5 = v_p3_n4;
                d_p3_n5 = d_p3_n4;
            }
            if (v_p3_n5 > v_p4_n4) {
                v_p3_n5 = v_p4_n4;
                d_p3_n5 = d_p4_n4;
            }
            v_p3_n5 += 1.0 + rc.senseRubble(l_p3_n5) / 10.0;
        }
        if (rc.onTheMap(l_p2_n5) && !rc.canSenseRobotAtLocation(l_p2_n5)) {
            v_p2_n5 = v_p1_n4;
            d_p2_n5 = d_p1_n4;
            if (v_p2_n5 > v_p2_n4) {
                v_p2_n5 = v_p2_n4;
                d_p2_n5 = d_p2_n4;
            }
            if (v_p2_n5 > v_p3_n4) {
                v_p2_n5 = v_p3_n4;
                d_p2_n5 = d_p3_n4;
            }
            v_p2_n5 += 1.0 + rc.senseRubble(l_p2_n5) / 10.0;
        }
        if (rc.onTheMap(l_p1_n5) && !rc.canSenseRobotAtLocation(l_p1_n5)) {
            v_p1_n5 = v_0_n4;
            d_p1_n5 = d_0_n4;
            if (v_p1_n5 > v_p1_n4) {
                v_p1_n5 = v_p1_n4;
                d_p1_n5 = d_p1_n4;
            }
            if (v_p1_n5 > v_p2_n4) {
                v_p1_n5 = v_p2_n4;
                d_p1_n5 = d_p2_n4;
            }
            v_p1_n5 += 1.0 + rc.senseRubble(l_p1_n5) / 10.0;
        }
        if (rc.onTheMap(l_0_n5) && !rc.canSenseRobotAtLocation(l_0_n5)) {
            v_0_n5 = v_n1_n4;
            d_0_n5 = d_n1_n4;
            if (v_0_n5 > v_0_n4) {
                v_0_n5 = v_0_n4;
                d_0_n5 = d_0_n4;
            }
            if (v_0_n5 > v_p1_n4) {
                v_0_n5 = v_p1_n4;
                d_0_n5 = d_p1_n4;
            }
            v_0_n5 += 1.0 + rc.senseRubble(l_0_n5) / 10.0;
        }
        if (rc.onTheMap(l_n1_n5) && !rc.canSenseRobotAtLocation(l_n1_n5)) {
            v_n1_n5 = v_n2_n4;
            d_n1_n5 = d_n2_n4;
            if (v_n1_n5 > v_n1_n4) {
                v_n1_n5 = v_n1_n4;
                d_n1_n5 = d_n1_n4;
            }
            if (v_n1_n5 > v_0_n4) {
                v_n1_n5 = v_0_n4;
                d_n1_n5 = d_0_n4;
            }
            v_n1_n5 += 1.0 + rc.senseRubble(l_n1_n5) / 10.0;
        }
        if (rc.onTheMap(l_n2_n5) && !rc.canSenseRobotAtLocation(l_n2_n5)) {
            v_n2_n5 = v_n3_n4;
            d_n2_n5 = d_n3_n4;
            if (v_n2_n5 > v_n2_n4) {
                v_n2_n5 = v_n2_n4;
                d_n2_n5 = d_n2_n4;
            }
            if (v_n2_n5 > v_n1_n4) {
                v_n2_n5 = v_n1_n4;
                d_n2_n5 = d_n1_n4;
            }
            v_n2_n5 += 1.0 + rc.senseRubble(l_n2_n5) / 10.0;
        }
        if (rc.onTheMap(l_n3_n5) && !rc.canSenseRobotAtLocation(l_n3_n5)) {
            v_n3_n5 = v_n4_n4;
            d_n3_n5 = d_n4_n4;
            if (v_n3_n5 > v_n3_n4) {
                v_n3_n5 = v_n3_n4;
                d_n3_n5 = d_n3_n4;
            }
            if (v_n3_n5 > v_n2_n4) {
                v_n3_n5 = v_n2_n4;
                d_n3_n5 = d_n2_n4;
            }
            v_n3_n5 += 1.0 + rc.senseRubble(l_n3_n5) / 10.0;
        }
        if (rc.onTheMap(l_n5_n3) && !rc.canSenseRobotAtLocation(l_n5_n3)) {
            v_n5_n3 = v_n4_n4;
            d_n5_n3 = d_n4_n4;
            if (v_n5_n3 > v_n4_n3) {
                v_n5_n3 = v_n4_n3;
                d_n5_n3 = d_n4_n3;
            }
            if (v_n5_n3 > v_n4_n2) {
                v_n5_n3 = v_n4_n2;
                d_n5_n3 = d_n4_n2;
            }
            v_n5_n3 += 1.0 + rc.senseRubble(l_n5_n3) / 10.0;
        }
        if (rc.onTheMap(l_n5_n2) && !rc.canSenseRobotAtLocation(l_n5_n2)) {
            v_n5_n2 = v_n4_n3;
            d_n5_n2 = d_n4_n3;
            if (v_n5_n2 > v_n4_n2) {
                v_n5_n2 = v_n4_n2;
                d_n5_n2 = d_n4_n2;
            }
            if (v_n5_n2 > v_n4_n1) {
                v_n5_n2 = v_n4_n1;
                d_n5_n2 = d_n4_n1;
            }
            v_n5_n2 += 1.0 + rc.senseRubble(l_n5_n2) / 10.0;
        }
        if (rc.onTheMap(l_n5_n1) && !rc.canSenseRobotAtLocation(l_n5_n1)) {
            v_n5_n1 = v_n4_n2;
            d_n5_n1 = d_n4_n2;
            if (v_n5_n1 > v_n4_n1) {
                v_n5_n1 = v_n4_n1;
                d_n5_n1 = d_n4_n1;
            }
            if (v_n5_n1 > v_n4_0) {
                v_n5_n1 = v_n4_0;
                d_n5_n1 = d_n4_0;
            }
            v_n5_n1 += 1.0 + rc.senseRubble(l_n5_n1) / 10.0;
        }
        if (rc.onTheMap(l_n5_0) && !rc.canSenseRobotAtLocation(l_n5_0)) {
            v_n5_0 = v_n4_n1;
            d_n5_0 = d_n4_n1;
            if (v_n5_0 > v_n4_0) {
                v_n5_0 = v_n4_0;
                d_n5_0 = d_n4_0;
            }
            if (v_n5_0 > v_n4_p1) {
                v_n5_0 = v_n4_p1;
                d_n5_0 = d_n4_p1;
            }
            v_n5_0 += 1.0 + rc.senseRubble(l_n5_0) / 10.0;
        }
        if (rc.onTheMap(l_n5_p1) && !rc.canSenseRobotAtLocation(l_n5_p1)) {
            v_n5_p1 = v_n4_0;
            d_n5_p1 = d_n4_0;
            if (v_n5_p1 > v_n4_p1) {
                v_n5_p1 = v_n4_p1;
                d_n5_p1 = d_n4_p1;
            }
            if (v_n5_p1 > v_n4_p2) {
                v_n5_p1 = v_n4_p2;
                d_n5_p1 = d_n4_p2;
            }
            v_n5_p1 += 1.0 + rc.senseRubble(l_n5_p1) / 10.0;
        }
        if (rc.onTheMap(l_n5_p2) && !rc.canSenseRobotAtLocation(l_n5_p2)) {
            v_n5_p2 = v_n4_p1;
            d_n5_p2 = d_n4_p1;
            if (v_n5_p2 > v_n4_p2) {
                v_n5_p2 = v_n4_p2;
                d_n5_p2 = d_n4_p2;
            }
            if (v_n5_p2 > v_n4_p3) {
                v_n5_p2 = v_n4_p3;
                d_n5_p2 = d_n4_p3;
            }
            v_n5_p2 += 1.0 + rc.senseRubble(l_n5_p2) / 10.0;
        }
        if (rc.onTheMap(l_n5_p3) && !rc.canSenseRobotAtLocation(l_n5_p3)) {
            v_n5_p3 = v_n4_p2;
            d_n5_p3 = d_n4_p2;
            if (v_n5_p3 > v_n4_p3) {
                v_n5_p3 = v_n4_p3;
                d_n5_p3 = d_n4_p3;
            }
            if (v_n5_p3 > v_n4_p4) {
                v_n5_p3 = v_n4_p4;
                d_n5_p3 = d_n4_p4;
            }
            v_n5_p3 += 1.0 + rc.senseRubble(l_n5_p3) / 10.0;
        }
        int dx = target.x - source.x;
        int dy = target.y - source.y;
        switch (dx) {
            case -5:
                switch (dy) {
                    case -3:
                        return d_n5_n3;
                    case -2:
                        return d_n5_n2;
                    case -1:
                        return d_n5_n1;
                    case 0:
                        return d_n5_0;
                    case 1:
                        return d_n5_p1;
                    case 2:
                        return d_n5_p2;
                    case 3:
                        return d_n5_p3;
                }
                break;
            case -4:
                switch (dy) {
                    case -4:
                        return d_n4_n4;
                    case -3:
                        return d_n4_n3;
                    case -2:
                        return d_n4_n2;
                    case -1:
                        return d_n4_n1;
                    case 0:
                        return d_n4_0;
                    case 1:
                        return d_n4_p1;
                    case 2:
                        return d_n4_p2;
                    case 3:
                        return d_n4_p3;
                    case 4:
                        return d_n4_p4;
                }
                break;
            case -3:
                switch (dy) {
                    case -5:
                        return d_n3_n5;
                    case -4:
                        return d_n3_n4;
                    case -3:
                        return d_n3_n3;
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
                    case 3:
                        return d_n3_p3;
                    case 4:
                        return d_n3_p4;
                    case 5:
                        return d_n3_p5;
                }
                break;
            case -2:
                switch (dy) {
                    case -5:
                        return d_n2_n5;
                    case -4:
                        return d_n2_n4;
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
                    case 4:
                        return d_n2_p4;
                    case 5:
                        return d_n2_p5;
                }
                break;
            case -1:
                switch (dy) {
                    case -5:
                        return d_n1_n5;
                    case -4:
                        return d_n1_n4;
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
                    case 4:
                        return d_n1_p4;
                    case 5:
                        return d_n1_p5;
                }
                break;
            case 0:
                switch (dy) {
                    case -5:
                        return d_0_n5;
                    case -4:
                        return d_0_n4;
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
                    case 4:
                        return d_0_p4;
                    case 5:
                        return d_0_p5;
                }
                break;
            case 1:
                switch (dy) {
                    case -5:
                        return d_p1_n5;
                    case -4:
                        return d_p1_n4;
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
                    case 4:
                        return d_p1_p4;
                    case 5:
                        return d_p1_p5;
                }
                break;
            case 2:
                switch (dy) {
                    case -5:
                        return d_p2_n5;
                    case -4:
                        return d_p2_n4;
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
                    case 4:
                        return d_p2_p4;
                    case 5:
                        return d_p2_p5;
                }
                break;
            case 3:
                switch (dy) {
                    case -5:
                        return d_p3_n5;
                    case -4:
                        return d_p3_n4;
                    case -3:
                        return d_p3_n3;
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
                    case 3:
                        return d_p3_p3;
                    case 4:
                        return d_p3_p4;
                    case 5:
                        return d_p3_p5;
                }
                break;
            case 4:
                switch (dy) {
                    case -4:
                        return d_p4_n4;
                    case -3:
                        return d_p4_n3;
                    case -2:
                        return d_p4_n2;
                    case -1:
                        return d_p4_n1;
                    case 0:
                        return d_p4_0;
                    case 1:
                        return d_p4_p1;
                    case 2:
                        return d_p4_p2;
                    case 3:
                        return d_p4_p3;
                    case 4:
                        return d_p4_p4;
                }
                break;
            case 5:
                switch (dy) {
                    case -3:
                        return d_p5_n3;
                    case -2:
                        return d_p5_n2;
                    case -1:
                        return d_p5_n1;
                    case 0:
                        return d_p5_0;
                    case 1:
                        return d_p5_p1;
                    case 2:
                        return d_p5_p2;
                    case 3:
                        return d_p5_p3;
                }
                break;
        }
        Direction best = null;
        double bestEstimate = 0;
        double initialDist = Math.sqrt(source.distanceSquaredTo(target));
        if (dx > 0 || dy >= 0) {
            double e_p1_p5 = (initialDist - Math.sqrt(l_p1_p5.distanceSquaredTo(target))) / v_p1_p5;
            if (e_p1_p5 > bestEstimate) {
                bestEstimate = e_p1_p5;
                best = d_p1_p5;
            }
            double e_p2_p5 = (initialDist - Math.sqrt(l_p2_p5.distanceSquaredTo(target))) / v_p2_p5;
            if (e_p2_p5 > bestEstimate) {
                bestEstimate = e_p2_p5;
                best = d_p2_p5;
            }
            double e_p3_p4 = (initialDist - Math.sqrt(l_p3_p4.distanceSquaredTo(target))) / v_p3_p4;
            if (e_p3_p4 > bestEstimate) {
                bestEstimate = e_p3_p4;
                best = d_p3_p4;
            }
            double e_p3_p5 = (initialDist - Math.sqrt(l_p3_p5.distanceSquaredTo(target))) / v_p3_p5;
            if (e_p3_p5 > bestEstimate) {
                bestEstimate = e_p3_p5;
                best = d_p3_p5;
            }
            double e_p4_p3 = (initialDist - Math.sqrt(l_p4_p3.distanceSquaredTo(target))) / v_p4_p3;
            if (e_p4_p3 > bestEstimate) {
                bestEstimate = e_p4_p3;
                best = d_p4_p3;
            }
            double e_p4_p4 = (initialDist - Math.sqrt(l_p4_p4.distanceSquaredTo(target))) / v_p4_p4;
            if (e_p4_p4 > bestEstimate) {
                bestEstimate = e_p4_p4;
                best = d_p4_p4;
            }
            double e_p5_0 = (initialDist - Math.sqrt(l_p5_0.distanceSquaredTo(target))) / v_p5_0;
            if (e_p5_0 > bestEstimate) {
                bestEstimate = e_p5_0;
                best = d_p5_0;
            }
            double e_p5_p1 = (initialDist - Math.sqrt(l_p5_p1.distanceSquaredTo(target))) / v_p5_p1;
            if (e_p5_p1 > bestEstimate) {
                bestEstimate = e_p5_p1;
                best = d_p5_p1;
            }
            double e_p5_p2 = (initialDist - Math.sqrt(l_p5_p2.distanceSquaredTo(target))) / v_p5_p2;
            if (e_p5_p2 > bestEstimate) {
                bestEstimate = e_p5_p2;
                best = d_p5_p2;
            }
            double e_p5_p3 = (initialDist - Math.sqrt(l_p5_p3.distanceSquaredTo(target))) / v_p5_p3;
            if (e_p5_p3 > bestEstimate) {
                bestEstimate = e_p5_p3;
                best = d_p5_p3;
            }
        }
        if (dx <= 0 || dy > 0) {
            double e_n5_p1 = (initialDist - Math.sqrt(l_n5_p1.distanceSquaredTo(target))) / v_n5_p1;
            if (e_n5_p1 > bestEstimate) {
                bestEstimate = e_n5_p1;
                best = d_n5_p1;
            }
            double e_n5_p2 = (initialDist - Math.sqrt(l_n5_p2.distanceSquaredTo(target))) / v_n5_p2;
            if (e_n5_p2 > bestEstimate) {
                bestEstimate = e_n5_p2;
                best = d_n5_p2;
            }
            double e_n5_p3 = (initialDist - Math.sqrt(l_n5_p3.distanceSquaredTo(target))) / v_n5_p3;
            if (e_n5_p3 > bestEstimate) {
                bestEstimate = e_n5_p3;
                best = d_n5_p3;
            }
            double e_n4_p3 = (initialDist - Math.sqrt(l_n4_p3.distanceSquaredTo(target))) / v_n4_p3;
            if (e_n4_p3 > bestEstimate) {
                bestEstimate = e_n4_p3;
                best = d_n4_p3;
            }
            double e_n4_p4 = (initialDist - Math.sqrt(l_n4_p4.distanceSquaredTo(target))) / v_n4_p4;
            if (e_n4_p4 > bestEstimate) {
                bestEstimate = e_n4_p4;
                best = d_n4_p4;
            }
            double e_n3_p4 = (initialDist - Math.sqrt(l_n3_p4.distanceSquaredTo(target))) / v_n3_p4;
            if (e_n3_p4 > bestEstimate) {
                bestEstimate = e_n3_p4;
                best = d_n3_p4;
            }
            double e_n3_p5 = (initialDist - Math.sqrt(l_n3_p5.distanceSquaredTo(target))) / v_n3_p5;
            if (e_n3_p5 > bestEstimate) {
                bestEstimate = e_n3_p5;
                best = d_n3_p5;
            }
            double e_n2_p5 = (initialDist - Math.sqrt(l_n2_p5.distanceSquaredTo(target))) / v_n2_p5;
            if (e_n2_p5 > bestEstimate) {
                bestEstimate = e_n2_p5;
                best = d_n2_p5;
            }
            double e_n1_p5 = (initialDist - Math.sqrt(l_n1_p5.distanceSquaredTo(target))) / v_n1_p5;
            if (e_n1_p5 > bestEstimate) {
                bestEstimate = e_n1_p5;
                best = d_n1_p5;
            }
            double e_0_p5 = (initialDist - Math.sqrt(l_0_p5.distanceSquaredTo(target))) / v_0_p5;
            if (e_0_p5 > bestEstimate) {
                bestEstimate = e_0_p5;
                best = d_0_p5;
            }
        }
        if (dx < 0 || dy <= 0) {
            double e_n5_n3 = (initialDist - Math.sqrt(l_n5_n3.distanceSquaredTo(target))) / v_n5_n3;
            if (e_n5_n3 > bestEstimate) {
                bestEstimate = e_n5_n3;
                best = d_n5_n3;
            }
            double e_n5_n2 = (initialDist - Math.sqrt(l_n5_n2.distanceSquaredTo(target))) / v_n5_n2;
            if (e_n5_n2 > bestEstimate) {
                bestEstimate = e_n5_n2;
                best = d_n5_n2;
            }
            double e_n5_n1 = (initialDist - Math.sqrt(l_n5_n1.distanceSquaredTo(target))) / v_n5_n1;
            if (e_n5_n1 > bestEstimate) {
                bestEstimate = e_n5_n1;
                best = d_n5_n1;
            }
            double e_n5_0 = (initialDist - Math.sqrt(l_n5_0.distanceSquaredTo(target))) / v_n5_0;
            if (e_n5_0 > bestEstimate) {
                bestEstimate = e_n5_0;
                best = d_n5_0;
            }
            double e_n4_n4 = (initialDist - Math.sqrt(l_n4_n4.distanceSquaredTo(target))) / v_n4_n4;
            if (e_n4_n4 > bestEstimate) {
                bestEstimate = e_n4_n4;
                best = d_n4_n4;
            }
            double e_n4_n3 = (initialDist - Math.sqrt(l_n4_n3.distanceSquaredTo(target))) / v_n4_n3;
            if (e_n4_n3 > bestEstimate) {
                bestEstimate = e_n4_n3;
                best = d_n4_n3;
            }
            double e_n3_n5 = (initialDist - Math.sqrt(l_n3_n5.distanceSquaredTo(target))) / v_n3_n5;
            if (e_n3_n5 > bestEstimate) {
                bestEstimate = e_n3_n5;
                best = d_n3_n5;
            }
            double e_n3_n4 = (initialDist - Math.sqrt(l_n3_n4.distanceSquaredTo(target))) / v_n3_n4;
            if (e_n3_n4 > bestEstimate) {
                bestEstimate = e_n3_n4;
                best = d_n3_n4;
            }
            double e_n2_n5 = (initialDist - Math.sqrt(l_n2_n5.distanceSquaredTo(target))) / v_n2_n5;
            if (e_n2_n5 > bestEstimate) {
                bestEstimate = e_n2_n5;
                best = d_n2_n5;
            }
            double e_n1_n5 = (initialDist - Math.sqrt(l_n1_n5.distanceSquaredTo(target))) / v_n1_n5;
            if (e_n1_n5 > bestEstimate) {
                bestEstimate = e_n1_n5;
                best = d_n1_n5;
            }
        }
        if (dx >= 0 || dy < 0) {
            double e_0_n5 = (initialDist - Math.sqrt(l_0_n5.distanceSquaredTo(target))) / v_0_n5;
            if (e_0_n5 > bestEstimate) {
                bestEstimate = e_0_n5;
                best = d_0_n5;
            }
            double e_p1_n5 = (initialDist - Math.sqrt(l_p1_n5.distanceSquaredTo(target))) / v_p1_n5;
            if (e_p1_n5 > bestEstimate) {
                bestEstimate = e_p1_n5;
                best = d_p1_n5;
            }
            double e_p2_n5 = (initialDist - Math.sqrt(l_p2_n5.distanceSquaredTo(target))) / v_p2_n5;
            if (e_p2_n5 > bestEstimate) {
                bestEstimate = e_p2_n5;
                best = d_p2_n5;
            }
            double e_p3_n5 = (initialDist - Math.sqrt(l_p3_n5.distanceSquaredTo(target))) / v_p3_n5;
            if (e_p3_n5 > bestEstimate) {
                bestEstimate = e_p3_n5;
                best = d_p3_n5;
            }
            double e_p3_n4 = (initialDist - Math.sqrt(l_p3_n4.distanceSquaredTo(target))) / v_p3_n4;
            if (e_p3_n4 > bestEstimate) {
                bestEstimate = e_p3_n4;
                best = d_p3_n4;
            }
            double e_p4_n4 = (initialDist - Math.sqrt(l_p4_n4.distanceSquaredTo(target))) / v_p4_n4;
            if (e_p4_n4 > bestEstimate) {
                bestEstimate = e_p4_n4;
                best = d_p4_n4;
            }
            double e_p4_n3 = (initialDist - Math.sqrt(l_p4_n3.distanceSquaredTo(target))) / v_p4_n3;
            if (e_p4_n3 > bestEstimate) {
                bestEstimate = e_p4_n3;
                best = d_p4_n3;
            }
            double e_p5_n3 = (initialDist - Math.sqrt(l_p5_n3.distanceSquaredTo(target))) / v_p5_n3;
            if (e_p5_n3 > bestEstimate) {
                bestEstimate = e_p5_n3;
                best = d_p5_n3;
            }
            double e_p5_n2 = (initialDist - Math.sqrt(l_p5_n2.distanceSquaredTo(target))) / v_p5_n2;
            if (e_p5_n2 > bestEstimate) {
                bestEstimate = e_p5_n2;
                best = d_p5_n2;
            }
            double e_p5_n1 = (initialDist - Math.sqrt(l_p5_n1.distanceSquaredTo(target))) / v_p5_n1;
            if (e_p5_n1 > bestEstimate) {
                bestEstimate = e_p5_n1;
                best = d_p5_n1;
            }
        }
        return best;
    }
}