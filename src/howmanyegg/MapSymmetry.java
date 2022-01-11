package howmanyegg;

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

    public static MapSymmetry detectSymmetry(RobotController rc) throws GameActionException {
        boolean possibleHorizontal = true;
        boolean possibleVertical = true;
        boolean possibleRotational = true;
        
        MapLocation r;
        int rubble;
        for (MapLocation l : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), rc.getType().visionRadiusSquared)) {
            rubble = rc.senseRubble(l);
            r = HORIZONTAL.reflect(rc, l);
            if (rubble != rc.senseRubble(r)) {
                possibleHorizontal = false;
            }
        }

        return HORIZONTAL;
    }
}
