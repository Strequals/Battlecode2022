package parrot3_3;

import battlecode.common.*;

public abstract strictfp class BFS {
    public abstract Direction navigateBFS(RobotController rc, MapLocation source, MapLocation target) throws GameActionException;
}
