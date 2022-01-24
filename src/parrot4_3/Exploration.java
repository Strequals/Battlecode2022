package parrot4_3;

import battlecode.common.*;

public abstract strictfp class Exploration {
    
    public abstract void explore(RobotController rc, MapLocation loc) throws GameActionException;

    public abstract boolean hasExplored(MapLocation l);

    public abstract boolean hasExplored(int x, int y);


}
