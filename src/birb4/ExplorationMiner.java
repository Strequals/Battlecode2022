package birb4;

import battlecode.common.*;

public strictfp class ExplorationMiner extends Exploration {
    static boolean[][] explored;

    public ExplorationMiner(RobotController rc) {
        explored = new boolean[rc.getMapWidth()][rc.getMapHeight()];
    }

    public void explore(RobotController rc, MapLocation loc) throws GameActionException {
        explored[loc.x][loc.y] = true;
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHEAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTHEAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.EAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHEAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHEAST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.SOUTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.WEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTHWEST);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
        loc = loc.add(Direction.NORTH);
        if (rc.onTheMap(loc)) {
            explored[loc.x][loc.y] = true;
        }
    }

    public boolean hasExplored(MapLocation loc) {
        return explored[loc.x][loc.y];
    }
    
    public boolean hasExplored(int x, int y) {
        return explored[x][y];
    }
}