package parrot3_2;

import battlecode.common.*;
import java.util.Random;

public strictfp class Navigation {

    private static MinHeap<MapLocation> heap = null;
    private static final int MAX_RUBBLE = 100;
    private static double rubbleEstimate = -1;
    private static final double RUBBLE_MEMORY = 0.9;
    private static MapLocation previous = null;
    private static int turnsWaited = 0;
    private static final int WAIT_TURNS = 8;
    private static BFS bfs = null;

    public static Direction navigate(RobotController rc, MapLocation from, MapLocation to) throws GameActionException {
        if (bfs == null) {
            switch (rc.getType()) {
                case BUILDER:
                    bfs = new BFSMiner();
                    break;
                case MINER:
                    //bfs = new BFSMiner();
                    //break;
                case SOLDIER:
                    bfs = new BFSSoldier();
                    break;
                case ARCHON:
                case LABORATORY:
                case WATCHTOWER:
                    bfs = new BFSWatchtower();
                    break;
            }
        }
        if (bfs != null && Clock.getBytecodesLeft() >= 4000) {
            int before = Clock.getBytecodeNum();
            Direction d = bfs.navigateBFS(rc, from, to);
            rc.setIndicatorString("bfs bytecodes: " + (Clock.getBytecodeNum() - before));
            if (d != null) {
                return d;
            }
        }
        return navigateFuzzy(rc, from, to);
    }

    private static double fuzzyHeuristic(int dist, int rubble, double estimatedRubble) {
        return (1 + rubble / 10) + dist * (1 + estimatedRubble / 10);
    }

    private static double rubbleEstimate(RobotController rc, MapLocation current) throws GameActionException {
        if (rubbleEstimate < 0) {
            MapLocation l;
            int totalRubble = 0;
            int locations = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    l = new MapLocation(current.x + dx, current.y + dy);
                    if (rc.canSenseLocation(l)) {
                        totalRubble += rc.senseRubble(l);
                        locations++;
                    }
                }
            }
            rubbleEstimate = ((double) totalRubble) / locations;
        } else {
            if (rc.canSenseLocation(current)) {
                rubbleEstimate = RUBBLE_MEMORY * rubbleEstimate
                    + (1 - RUBBLE_MEMORY) * rc.senseRubble(current);
            }
        }
        return rubbleEstimate;
    }

    public static Direction navigateFuzzy(RobotController rc, MapLocation from, MapLocation to) throws GameActionException {
        boolean forceMove = false;
        if (turnsWaited >= WAIT_TURNS) {
            turnsWaited = 0;
            forceMove = true;
        }
        int dist = Robot.getChebyshevDistance(from, to);
        MapLocation next;
        int nextDist;
        int rubble;
        double score;
        double bestScore = Double.MAX_VALUE;
        Direction best = null;
        double estimatedRubble = rubbleEstimate(rc, from);
        for (Direction d : Robot.directions) {
            if (rc.canMove(d)) {
                next = from.add(d);
                nextDist = Robot.getChebyshevDistance(next, to);
                if (forceMove || (nextDist <= dist && !next.equals(previous))) {
                    if (rc.canSenseLocation(next)) {
                        rubble = rc.senseRubble(next);
                    } else {
                        rubble = 100;
                    }
                    score = fuzzyHeuristic(nextDist, rubble, estimatedRubble);

                    if (score < bestScore) {
                        bestScore = score;
                        best = d;
                    }
                }
            }
        }
        if (best != null) {
            previous = from;
            turnsWaited = 0;
        } else {
            turnsWaited++;
        }
        return best;
    }
    
    public static Direction navigateDjistra(RobotController rc, MapLocation from, MapLocation to) throws GameActionException {
        if (heap == null) {
            heap = new MinHeap<MapLocation>(16);
        }

        heap.push(0, from);

        while (!heap.isEmpty()) {
            
        }

        heap.clear();
        return null;
    }

    public static double fleeHeuristic(int dsq, int rubble, double estimatedRubble) {
        return Math.sqrt(dsq) * (1 + estimatedRubble / 10) - (1 + rubble / 10);
    }

    public static Direction flee(RobotController rc, MapLocation current, MapLocation fleeFrom) throws GameActionException {
        int dist = current.distanceSquaredTo(fleeFrom);
        MapLocation next;
        int nextDist;
        int rubble;
        double score;
        double bestScore = Double.MIN_VALUE;
        Direction best = null;
        double estimatedRubble = rubbleEstimate(rc, current);
        for (Direction d : Robot.directions) {
            if (rc.canMove(d)) {
                next = current.add(d);
                nextDist = next.distanceSquaredTo(fleeFrom);
                if (nextDist >= dist) {
                    if (rc.canSenseLocation(next)) {
                        rubble = rc.senseRubble(next);
                    } else {
                        rubble = 100;
                    }
                    score = fleeHeuristic(nextDist, rubble, estimatedRubble);

                    if (score > bestScore) {
                        bestScore = score;
                        best = d;
                    }
                }
            }
        }
        return best;
    }

    public static double fleeHeuristic(int dsq, int allyDsq, int rubble, double estimatedRubble) {
        return (Math.sqrt(dsq) - Math.sqrt(allyDsq)) * (1 + estimatedRubble / 10) - (1 + rubble / 10);
    }

    public static Direction flee(RobotController rc, MapLocation current, MapLocation fleeFrom, MapLocation nearestAlly) throws GameActionException {
        int dist = current.distanceSquaredTo(fleeFrom);
        MapLocation next;
        int nextDist;
        int rubble;
        double score;
        double bestScore = Double.MIN_VALUE;
        Direction best = null;
        double estimatedRubble = rubbleEstimate(rc, current);
        int allyDist;
        for (Direction d : Robot.directions) {
            if (rc.canMove(d)) {
                next = current.add(d);
                nextDist = next.distanceSquaredTo(fleeFrom);
                allyDist = next.distanceSquaredTo(nearestAlly);
                if (nextDist >= dist) {
                    if (rc.canSenseLocation(next)) {
                        rubble = rc.senseRubble(next);
                    } else {
                        rubble = 100;
                    }
                    score = fleeHeuristic(nextDist, allyDist, rubble, estimatedRubble);

                    if (score > bestScore) {
                        bestScore = score;
                        best = d;
                    }
                }
            }
        }
        return best;
    }

}
