package parrot4;

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

    public int diff(Direction d1, Direction d2) {
        switch (d1) {
            case NORTH:
                switch (d2) {
                    case NORTH:
                        return 0;
                    case NORTHEAST:
                        return 1;
                    case EAST:
                        return 2;
                    case SOUTHEAST:
                        return 3;
                    case SOUTH:
                        return 4;
                    case SOUTHWEST:
                        return 3;
                    case WEST:
                        return 2;
                    case NORTHWEST:
                        return 1;
                }
                break;
            case NORTHEAST:
                switch (d2) {
                    case NORTH:
                        return 1;
                    case NORTHEAST:
                        return 0;
                    case EAST:
                        return 1;
                    case SOUTHEAST:
                        return 2;
                    case SOUTH:
                        return 3;
                    case SOUTHWEST:
                        return 4;
                    case WEST:
                        return 3;
                    case NORTHWEST:
                        return 2;
                }
                break;
            case EAST:
                switch (d2) {
                    case NORTH:
                        return 2;
                    case NORTHEAST:
                        return 1;
                    case EAST:
                        return 0;
                    case SOUTHEAST:
                        return 1;
                    case SOUTH:
                        return 2;
                    case SOUTHWEST:
                        return 3;
                    case WEST:
                        return 4;
                    case NORTHWEST:
                        return 3;
                }
                break;
            case SOUTHEAST:
                switch (d2) {
                    case NORTH:
                        return 3;
                    case NORTHEAST:
                        return 2;
                    case EAST:
                        return 1;
                    case SOUTHEAST:
                        return 0;
                    case SOUTH:
                        return 1;
                    case SOUTHWEST:
                        return 2;
                    case WEST:
                        return 3;
                    case NORTHWEST:
                        return 4;
                }
                break;
            case SOUTH:
                switch (d2) {
                    case NORTH:
                        return 4;
                    case NORTHEAST:
                        return 3;
                    case EAST:
                        return 2;
                    case SOUTHEAST:
                        return 1;
                    case SOUTH:
                        return 0;
                    case SOUTHWEST:
                        return 1;
                    case WEST:
                        return 2;
                    case NORTHWEST:
                        return 3;
                }
                break;
            case SOUTHWEST:
                switch (d2) {
                    case NORTH:
                        return 3;
                    case NORTHEAST:
                        return 4;
                    case EAST:
                        return 3;
                    case SOUTHEAST:
                        return 2;
                    case SOUTH:
                        return 1;
                    case SOUTHWEST:
                        return 0;
                    case WEST:
                        return 1;
                    case NORTHWEST:
                        return 2;
                }
                break;
            case WEST:
                switch (d2) {
                    case NORTH:
                        return 2;
                    case NORTHEAST:
                        return 3;
                    case EAST:
                        return 4;
                    case SOUTHEAST:
                        return 3;
                    case SOUTH:
                        return 2;
                    case SOUTHWEST:
                        return 1;
                    case WEST:
                        return 0;
                    case NORTHWEST:
                        return 1;
                }
                break;
            case NORTHWEST:
                switch (d2) {
                    case NORTH:
                        return 1;
                    case NORTHEAST:
                        return 2;
                    case EAST:
                        return 3;
                    case SOUTHEAST:
                        return 4;
                    case SOUTH:
                        return 3;
                    case SOUTHWEST:
                        return 2;
                    case WEST:
                        return 1;
                    case NORTHWEST:
                        return 0;
                }
                break;
        }
        return 8;
    }
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

    public Direction getRandomDirection() {
        return directions[rng.nextInt(8)];
    }

    public void updateExploration() throws GameActionException {
        if (exploration == null) {
            exploration = new ExplorationMiner(rc);
        }
        exploration.explore(rc, rc.getLocation());
    }

    static final double EXPLORED_PENALTY = 10000;
    static final double CHECKED_PENALTY = 5000;
    static final int TRIES = 8;
    
    static Exploration exploration;
    public MapLocation getExploreLocation() throws GameActionException {
        if (exploration == null) {
            exploration = new ExplorationMiner(rc);
        }

        MapLocation current = rc.getLocation();
        int currx = current.x;
        int curry = current.y;
        
        MapLocation best = null;
        double bestScore = 1000000;
        double score;
        MapLocation center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);

        int x;
        int y;
        
        MapLocation check;

        double edgeFactor = 0.2 + Math.max(rc.getRoundNum() / 250.0, 0.4);
        double distanceLeeway = StrictMath.max(rc.getMapWidth() / 6.0, rc.getMapHeight() / 6.0);

        double distancePenalty;

        for (int i = TRIES; i--> 0;) {
            x = rng.nextInt(rc.getMapWidth());
            y = rng.nextInt(rc.getMapHeight());
            check = new MapLocation(x, y);
            distancePenalty = StrictMath.sqrt(current.distanceSquaredTo(check)) - distanceLeeway;
            if (distancePenalty < 0) distancePenalty = 0;
            score = distancePenalty
                + (exploration.hasExplored(x, y)? EXPLORED_PENALTY : 0)
                + (Communications.checkExplore(rc, x, y)? CHECKED_PENALTY : 0)
                - edgeFactor * StrictMath.sqrt(center.distanceSquaredTo(check));
            if (score < bestScore) {
                best = check;
                bestScore = score;
            }
        }

        if (best != null) {
            return best;
        } else {
            return getRandomLocation();
        }

        /*if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        x = rng.nextInt(rc.getMapWidth());
        y = rng.nextInt(rc.getMapHeight());
        if (!exploration.hasExplored(x, y)) {
            if (!Communications.checkExplore(rc, x, y)) return new MapLocation(x, y);
            cx = x;
            cx = y;
        }
        return new MapLocation(cx, cy);*/

    }

    public Direction getDirectionOfLeastRubble() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation n = current.add(Direction.NORTH);
        MapLocation ne = current.add(Direction.NORTHEAST);
        MapLocation e = current.add(Direction.EAST);
        MapLocation se = current.add(Direction.SOUTHEAST);
        MapLocation s = current.add(Direction.SOUTH);
        MapLocation sw = current.add(Direction.SOUTHWEST);
        MapLocation w = current.add(Direction.WEST);
        MapLocation nw = current.add(Direction.NORTHWEST);
        int leastRubble = 101;
        Direction best = null;
        int r;
        if (rc.onTheMap(n) && !rc.canSenseRobotAtLocation(n)) {
            r = rc.senseRubble(n);
            if (r < leastRubble) {
                best = Direction.NORTH;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(ne) && !rc.canSenseRobotAtLocation(ne)) {
            r = rc.senseRubble(ne);
            if (r < leastRubble) {
                best = Direction.NORTHEAST;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(e) && !rc.canSenseRobotAtLocation(e)) {
            r = rc.senseRubble(e);
            if (r < leastRubble) {
                best = Direction.EAST;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(se) && !rc.canSenseRobotAtLocation(se)) {
            r = rc.senseRubble(se);
            if (r < leastRubble) {
                best = Direction.SOUTHEAST;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(s) && !rc.canSenseRobotAtLocation(s)) {
            r = rc.senseRubble(s);
            if (r < leastRubble) {
                best = Direction.SOUTH;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(sw) && !rc.canSenseRobotAtLocation(sw)) {
            r = rc.senseRubble(sw);
            if (r < leastRubble) {
                best = Direction.SOUTHWEST;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(w) && !rc.canSenseRobotAtLocation(w)) {
            r = rc.senseRubble(w);
            if (r < leastRubble) {
                best = Direction.WEST;
                leastRubble = r;
            }
        }
        if (rc.onTheMap(nw) && !rc.canSenseRobotAtLocation(nw)) {
            r = rc.senseRubble(nw);
            if (r < leastRubble) {
                best = Direction.NORTHWEST;
                leastRubble = r;
            }
        }
        return best;
    }

    public Direction getDirectionOfLeastRubbleWithinDistanceSquaredOf(MapLocation center, int dsq) throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation n = current.add(Direction.NORTH);
        MapLocation ne = current.add(Direction.NORTHEAST);
        MapLocation e = current.add(Direction.EAST);
        MapLocation se = current.add(Direction.SOUTHEAST);
        MapLocation s = current.add(Direction.SOUTH);
        MapLocation sw = current.add(Direction.SOUTHWEST);
        MapLocation w = current.add(Direction.WEST);
        MapLocation nw = current.add(Direction.NORTHWEST);
        int leastRubble = 101;
        Direction best = null;
        int r;
        if (n.distanceSquaredTo(center) <= dsq && rc.onTheMap(n) && !rc.canSenseRobotAtLocation(n)) {
            r = rc.senseRubble(n);
            if (r < leastRubble) {
                best = Direction.NORTH;
                leastRubble = r;
            }
        }
        if (ne.distanceSquaredTo(center) <= dsq && rc.onTheMap(ne) && !rc.canSenseRobotAtLocation(ne)) {
            r = rc.senseRubble(ne);
            if (r < leastRubble) {
                best = Direction.NORTHEAST;
                leastRubble = r;
            }
        }
        if (e.distanceSquaredTo(center) <= dsq && rc.onTheMap(e) && !rc.canSenseRobotAtLocation(e)) {
            r = rc.senseRubble(e);
            if (r < leastRubble) {
                best = Direction.EAST;
                leastRubble = r;
            }
        }
        if (se.distanceSquaredTo(center) <= dsq && rc.onTheMap(se) && !rc.canSenseRobotAtLocation(se)) {
            r = rc.senseRubble(se);
            if (r < leastRubble) {
                best = Direction.SOUTHEAST;
                leastRubble = r;
            }
        }
        if (s.distanceSquaredTo(center) <= dsq && rc.onTheMap(s) && !rc.canSenseRobotAtLocation(s)) {
            r = rc.senseRubble(s);
            if (r < leastRubble) {
                best = Direction.SOUTH;
                leastRubble = r;
            }
        }
        if (sw.distanceSquaredTo(center) <= dsq && rc.onTheMap(sw) && !rc.canSenseRobotAtLocation(sw)) {
            r = rc.senseRubble(sw);
            if (r < leastRubble) {
                best = Direction.SOUTHWEST;
                leastRubble = r;
            }
        }
        if (w.distanceSquaredTo(center) <= dsq && rc.onTheMap(w) && !rc.canSenseRobotAtLocation(w)) {
            r = rc.senseRubble(w);
            if (r < leastRubble) {
                best = Direction.WEST;
                leastRubble = r;
            }
        }
        if (nw.distanceSquaredTo(center) <= dsq && rc.onTheMap(nw) && !rc.canSenseRobotAtLocation(nw)) {
            r = rc.senseRubble(nw);
            if (r < leastRubble) {
                best = Direction.NORTHWEST;
                leastRubble = r;
            }
        }
        return best;
    }

    public Direction getBiasedDirectionOfLeastRubble(Direction bias) throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation n = current.add(bias);
        
        int leastRubble = 101;
        Direction best = null;
        int r;
        if (rc.onTheMap(n) && !rc.canSenseRobotAtLocation(n)) {
            r = rc.senseRubble(n);
            if (r < leastRubble) {
                best = bias;
                leastRubble = r;
            }
        }

        Direction right = bias.rotateRight();
        MapLocation ne = current.add(right);
        if (rc.onTheMap(ne) && !rc.canSenseRobotAtLocation(ne)) {
            r = rc.senseRubble(ne);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }
        
        Direction left = bias.rotateLeft();
        MapLocation e = current.add(left);
        if (rc.onTheMap(e) && !rc.canSenseRobotAtLocation(e)) {
            r = rc.senseRubble(e);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation se = current.add(right);
        if (rc.onTheMap(se) && !rc.canSenseRobotAtLocation(se)) {
            r = rc.senseRubble(se);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }

        left = left.rotateLeft();
        MapLocation s = current.add(left);
        if (rc.onTheMap(s) && !rc.canSenseRobotAtLocation(s)) {
            r = rc.senseRubble(s);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation sw = current.add(right);
        if (rc.onTheMap(sw) && !rc.canSenseRobotAtLocation(sw)) {
            r = rc.senseRubble(sw);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }

        left = left.rotateLeft();
        MapLocation w = current.add(left);
        if (rc.onTheMap(w) && !rc.canSenseRobotAtLocation(w)) {
            r = rc.senseRubble(w);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation nw = current.add(right);
        if (rc.onTheMap(nw) && !rc.canSenseRobotAtLocation(nw)) {
            r = rc.senseRubble(nw);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }
        return best;
    }

    public Direction getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(MapLocation center, int dsq, Direction bias) throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation n = current.add(bias);
        
        int leastRubble = 101;
        Direction best = null;
        int r;
        if (n.distanceSquaredTo(center) <= dsq && rc.onTheMap(n) && !rc.canSenseRobotAtLocation(n)) {
            r = rc.senseRubble(n);
            if (r < leastRubble) {
                best = bias;
                leastRubble = r;
            }
        }

        Direction right = bias.rotateRight();
        MapLocation ne = current.add(right);
        if (ne.distanceSquaredTo(center) <= dsq && rc.onTheMap(ne) && !rc.canSenseRobotAtLocation(ne)) {
            r = rc.senseRubble(ne);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }
        
        Direction left = bias.rotateLeft();
        MapLocation e = current.add(left);
        if (e.distanceSquaredTo(center) <= dsq && rc.onTheMap(e) && !rc.canSenseRobotAtLocation(e)) {
            r = rc.senseRubble(e);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation se = current.add(right);
        if (se.distanceSquaredTo(center) <= dsq && rc.onTheMap(se) && !rc.canSenseRobotAtLocation(se)) {
            r = rc.senseRubble(se);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }

        left = left.rotateLeft();
        MapLocation s = current.add(left);
        if (s.distanceSquaredTo(center) <= dsq && rc.onTheMap(s) && !rc.canSenseRobotAtLocation(s)) {
            r = rc.senseRubble(s);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation sw = current.add(right);
        if (sw.distanceSquaredTo(center) <= dsq && rc.onTheMap(sw) && !rc.canSenseRobotAtLocation(sw)) {
            r = rc.senseRubble(sw);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }

        left = left.rotateLeft();
        MapLocation w = current.add(left);
        if (w.distanceSquaredTo(center) <= dsq && rc.onTheMap(w) && !rc.canSenseRobotAtLocation(w)) {
            r = rc.senseRubble(w);
            if (r < leastRubble) {
                best = left;
                leastRubble = r;
            }
        }

        right = right.rotateRight();
        MapLocation nw = current.add(right);
        if (nw.distanceSquaredTo(center) <= dsq && rc.onTheMap(nw) && !rc.canSenseRobotAtLocation(nw)) {
            r = rc.senseRubble(nw);
            if (r < leastRubble) {
                best = right;
                leastRubble = r;
            }
        }
        return best;
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

    public boolean tryFlee(MapLocation fleeFrom, MapLocation nearestAlly) throws GameActionException {
        Direction d = Navigation.flee(rc, rc.getLocation(), fleeFrom, nearestAlly);
        if (d != null && rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        return false;
    }

    public static final int ARCHON_SCORE = 2;
    public static final int WATCHTOWER_SCORE = 7;
    public static final int LABORATORY_SCORE = 2;
    public static final int SOLDIER_SCORE = 5;
    public static final int SAGE_SCORE = 6;
    public static final int MINER_SCORE = 1;
    public static final int BUILDER_SCORE = 1;

    public Resource enemyScore(RobotInfo[] nearbyRobots) {
        MapLocation best = null;
        int unitScore = 0;
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
    
    public static final int ALLIES_MULTIPLIER = 20;
    public void broadcastAllies(MapLocation loc, int allies) throws GameActionException {
        Communications.addAlliesData(rc, loc, allies * ALLIES_MULTIPLIER);
    }

    public Resource senseAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = 0;
        MapLocation current = rc.getLocation();
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.senseNearbyLocationsWithGold(rc.getType().visionRadiusSquared)) {
            gold = rc.senseGold(l);
            dsq = current.distanceSquaredTo(l);
            totalGold += gold;
            if (dsq < bestDsq
                    || (dsq == bestDsq && 32 * gold > bestScore)) {
                best = l;
                bestDsq = dsq;
                bestScore = gold * 32;
            }
        }

        //if (best == null) {
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
        //}
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

    public void broadcastNearbyResources(int boost) throws GameActionException {
        Resource r = senseAllNearbyResources();
        if (r != null) {
            Communications.addResourceData(rc, r.location, r.value + boost);
        } else {
            Communications.addResourceData(rc, rc.getLocation(), boost);
        }
    }

    public boolean validBuildLocation(MapLocation l) throws GameActionException {
        return (l.x % 2 == 0) && (l.y % 2 == 0);
    }

    public MapLocation findTargetCorner() {
        int height = rc.getMapHeight();
        int width = rc.getMapWidth();
        MapLocation myLoc = rc.getLocation();

        int x = myLoc.x > (width / 2) ? width - 1 : 0;
        int y = myLoc.y > (height / 2) ? height - 1 : 0;
        return new MapLocation(x, y);
    }

}
