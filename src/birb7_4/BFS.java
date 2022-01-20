package birb7_4;

import battlecode.common.*;

public abstract strictfp class BFS {
    public abstract Direction navigateBFS(RobotController rc, MapLocation source, MapLocation target) throws GameActionException;
}
