package parrot3_3;

import battlecode.common.*;

public strictfp enum MapSymmetry {
    HORIZONTAL,
    VERTICAL,
    ROTATIONAL;
    
    public MapLocation reflect(RobotController rc, MapLocation loc) throws GameActionException {
        switch (this) {
            case HORIZONTAL:
                return new MapLocation(rc.getMapWidth() - loc.x - 1, loc.y);
            case VERTICAL:
                return new MapLocation(loc.x, rc.getMapHeight() - loc.y - 1);
            case ROTATIONAL:
                return new MapLocation(rc.getMapWidth() - loc.x - 1, rc.getMapHeight() - loc.y - 1);
        }
        return null;
    }
}
