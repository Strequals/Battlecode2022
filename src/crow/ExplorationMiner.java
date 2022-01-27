package crow;

import battlecode.common.*;

public strictfp class ExplorationMiner extends Exploration {
    static boolean[][] explored;

    public ExplorationMiner(RobotController rc) {
        explored = new boolean[rc.getMapWidth() + 8][rc.getMapHeight() + 8];
    }

    public void explore(RobotController rc, MapLocation loc) throws GameActionException {
        int x = loc.x + 4;
        int y = loc.y + 4;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        y--;
        x++;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        y++;
        x--;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        x++;
        explored[x][y] = true;
        y--;
        x++;
        explored[x][y] = true;
        y--;
        x++;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        explored[x][y] = true;
        y--;
        x--;
        explored[x][y] = true;
        y--;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        x--;
        explored[x][y] = true;
        y++;
        x--;
        explored[x][y] = true;
        y++;
        x--;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
        y++;
        explored[x][y] = true;
    }

    public boolean hasExplored(MapLocation loc) {
        return explored[loc.x + 4][loc.y + 4];
    }
    
    public boolean hasExplored(int x, int y) {
        return explored[x + 4][y + 4];
    }
}