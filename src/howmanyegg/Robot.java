package howmanyegg;

import battlecode.common.*;
import java.util.Random;

public strictfp abstract class Robot {
    
    RobotController rc;

    static final Random rng = new Random();

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    public Robot(RobotController rc) {
        this.rc = rc;
    }

    public abstract void run() throws GameActionException;

    public MapLocation getRandomLocation() {
        return new MapLocation(
                rng.nextInt(rc.getMapWidth()),
                rng.nextInt(rc.getMapHeight()));
    }

    public MapLocation getRandomLocationWithinChebyshevDistance(int dist) {
        MapLocation current = rc.getLocation();
        int x = rng.nextInt(2 * dist + 1) - dist + current.x;
        int y = rng.nextInt(2 * dist + 1) - dist + current.y;
        if (x < 0) x = 0;
        if (x >= rc.getMapWidth()) x = rc.getMapWidth() - 1;
        if (y < 0) y = 0;
        if (y >= rc.getMapHeight()) y = rc.getMapHeight() - 1;
        return new MapLocation(x, y);
    }

    public static int getChebyshevDistance(MapLocation l1, MapLocation l2) {
        int dx = l1.x - l2.x;
        int dy = l1.y - l2.y;
        return (dx > 0 ? dx : -dx) + (dy > 0 ? dy : -dy);
    }
    
    public boolean tryFlee(MapLocation fleeFrom) throws GameActionException {
        Direction d = Navigation.flee(rc, rc.getLocation(), fleeFrom);
        if (d != null && rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        return false;
    }

    public static final int ARCHON_SCORE = 20;
    public static final int WATCHTOWER_SCORE = 3;
    public static final int LABORATORY_SCORE = 5;
    public static final int SOLDIER_SCORE = 2;
    public static final int SAGE_SCORE = 3;
    public static final int MINER_SCORE = 1;
    public static final int BUILDER_SCORE = 1;

    public Resource enemyScore(RobotInfo[] nearbyRobots) {
        MapLocation best = null;
        int unitScore = Integer.MIN_VALUE;
        Team team = rc.getTeam();
        int score = 0;
        int s = 0;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != team) {
                switch (otherRobot.type) {
                    case ARCHON:
                        s = ARCHON_SCORE;
                        break;
                    case WATCHTOWER:
                        s = WATCHTOWER_SCORE;
                        break;
                    case LABORATORY:
                        s = LABORATORY_SCORE;
                        break;
                    case SOLDIER:
                        s = SOLDIER_SCORE;
                        break;
                    case SAGE:
                        s = SAGE_SCORE;
                        break;
                    case MINER:
                        s = MINER_SCORE;
                        break;
                    case BUILDER:
                        s = BUILDER_SCORE;
                        break;
                }
                score += s;
                if (s > unitScore) {
                    best = otherRobot.location;
                    unitScore = s;
                }
            }
        }
        if (best != null) {
            return new Resource(best, score);
        } else {
            return null;
        }
    }

    private static final int ARCHON_ENEMY_THREAT_INCREASE = 10;

    public void processAndBroadcastEnemies(RobotInfo[] nearbyRobots) throws GameActionException {
        Resource r = enemyScore(nearbyRobots);
        if (r != null) {
            if (rc.getType() == RobotType.ARCHON) {
                r.value = r.value * ARCHON_ENEMY_THREAT_INCREASE;
            }
            Communications.addEnemyData(rc, r.location, r.value);
        } else {
            Communications.addEnemyData(rc, rc.getLocation(), 0);
        }
    }

    public Resource senseAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;
        MapLocation current = rc.getLocation();
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.senseNearbyLocationsWithGold(rc.getType().visionRadiusSquared)) {
            gold = rc.senseGold(l);
            dsq = current.distanceSquaredTo(l);
            totalGold += gold;
            if (dsq < bestDsq
                    || (dsq == bestDsq && gold > bestScore)) {
                best = l;
                bestDsq = dsq;
                bestScore = gold;
            }
        }

        if (best == null) {
            for (MapLocation l : rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared)) {
                lead = rc.senseLead(l) - 1;
                dsq = current.distanceSquaredTo(l);
                totalLead += lead;
                if (lead > 0 && (dsq < bestDsq
                        || (dsq == bestDsq && lead > bestScore))) {
                    best = l;
                    bestDsq = dsq;
                    bestScore = lead;
                }
            }
        }
        if (best != null) {
            return new Resource(best, totalLead, totalGold);
        }
        return null;
    }

    public void broadcastNearbyResources() throws GameActionException {
        Resource r = senseAllNearbyResources();
        if (r != null) {
            Communications.addResourceData(rc, r.location, r.value);
        } else {
            Communications.addResourceData(rc, rc.getLocation(), 0);
        }
    }

    public boolean validBuildLocation(MapLocation l) throws GameActionException {
        return (l.x % 2 == 0) && (l.y % 2 == 0);
    }
}
