package parrot5_4;

import battlecode.common.*;

public strictfp class LaboratoryRobot extends Robot {
    private static final double MIN_LEAD = 0;
    private static int TARGET_RATE = 4;  // will only transmute if rate is this or better
    static final int MAX_IDLE_TURNS = 5;
    static final int MAX_MOVING_TURNS = 20;
    static final int MAX_LABS_NEARBY = 5;

    private MapLocation targetCorner;

    public LaboratoryRobot(RobotController rc) {
        super(rc);
        targetCorner = findTargetCorner();
        idleTurns = MAX_IDLE_TURNS;
    }


    private int idleTurns = 0;
    private int movingTurns = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        Communications.incrementLabCount(rc);

        switch (rc.getMode()) {
            case TURRET:
                if (shouldBecomePortable() && rc.canTransform()) {
                    rc.transform();
                } else {
                    tryTransmute();
                }
                break;
            case PORTABLE:
                if (shouldBecomeTurret() && rc.canTransform()) {
                    rc.transform();
                    movingTurns = 0;
                } else {
                    tryMove();
                    movingTurns++;
                }
        }

    }

    public boolean nearCorner() {
        return rc.getLocation().distanceSquaredTo(targetCorner) < 20;
    }

    public boolean nearCorner(MapLocation loc) {
        return loc.distanceSquaredTo(targetCorner) < 3;
    }

    public boolean shouldBecomePortable() throws GameActionException {
        return ((rc.getTransmutationRate() > TARGET_RATE || nearbyLabs > MAX_LABS_NEARBY) && idleTurns > MAX_IDLE_TURNS) || !bestRubbleInArea() || fleeFrom != null;
    }

    public boolean shouldBecomeTurret() throws GameActionException {
        return (rc.getTransmutationRate() <= TARGET_RATE || movingTurns > MAX_MOVING_TURNS) && bestRubbleInArea() && fleeFrom == null;
    }

    MapLocation bestRubbleLoc;
    public boolean bestRubbleInArea() throws GameActionException {
        MapLocation best = null;
        int bestRubble = 11;
        int rubble = 0;
        int bestdsq = Integer.MAX_VALUE;
        int dsq = 0;
        MapLocation current = rc.getLocation();
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 5)) {
            rubble = rc.senseRubble(check) / 10;
            dsq = check.distanceSquaredTo(current);
            if (rubble < bestRubble || (rubble == bestRubble && dsq < bestdsq)) {
                if (!rc.canSenseRobotAtLocation(check) || current.equals(check)) {
                    best = check;
                    bestRubble = rubble;
                    bestdsq = dsq;
                }
            }
        }
        bestRubbleLoc = best;
        return rc.senseRubble(rc.getLocation()) / 10 <= bestRubble;
    }


    RobotInfo[] nearbyRobots;
    int friendlies = 0;
    boolean areDangerousEnemies;
    boolean spottedByEnemy = false;
    MapLocation fleeFrom;
    MapLocation nearestAlly;
    int nearbyLabs; // labs that are in turret mode only
    int labs;
    public void processNearbyRobots() throws GameActionException {
        Team team = rc.getTeam();
        nearbyRobots = rc.senseNearbyRobots();
        int friendlyCount = 0;
        int minEnemyDistance = 999;
        MapLocation current = rc.getLocation();
        nearestAlly = null;
        fleeFrom = null;
        int nearestAllyDist = Integer.MAX_VALUE;
        int allyDist;
        nearbyLabs = 0;
        labs = 0;

        processAndBroadcastEnemies(nearbyRobots);

        for (RobotInfo otherRobot : nearbyRobots) {
            if(otherRobot.team == team) {
                friendlyCount++;
                allyDist = otherRobot.location.distanceSquaredTo(current);
                if (allyDist < nearestAllyDist) {
                    nearestAlly = otherRobot.location;
                    nearestAllyDist = allyDist;
                }
                if (otherRobot.type == RobotType.LABORATORY) {
                    if (otherRobot.mode == RobotMode.TURRET) {
                        nearbyLabs++;
                    }
                    labs++;
                }
            }
            else {
                switch (otherRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        areDangerousEnemies = true;
                        int distance = current.distanceSquaredTo(otherRobot.location);
                        if(distance < minEnemyDistance) {
                            minEnemyDistance = distance;
                            fleeFrom = otherRobot.location;
                        }
                }
                if (current.isWithinDistanceSquared(otherRobot.location, otherRobot.type.visionRadiusSquared)) {
                    spottedByEnemy = true;
                }
            }
        }
        friendlies = friendlyCount;
    }

    public static final int WAIT_MINERS = 2;
    public static final int INCREASE_RATE_TURNS = 20;

    public void tryTransmute() throws GameActionException {
        int lead = rc.getTeamLeadAmount(rc.getTeam());
        int miners = Communications.getPrevMinerCount(rc);
        if ((miners < WAIT_MINERS || miners < Communications.getLabCount(rc) * ArchonRobot.MINERS_PER_LAB) && Communications.numPortableOrThreatenedArchons(rc) < Communications.getArchonCount(rc)) {
            //let archon produce some miners
            // idleTurns++;
            return;
        }
        if(rc.canTransmute() && lead >= MIN_LEAD && rc.getTransmutationRate() <= TARGET_RATE) {
            rc.transmute();
            idleTurns = 0;
        }
        else if (rc.getTransmutationRate() > TARGET_RATE){
            idleTurns++;
            if (idleTurns >= INCREASE_RATE_TURNS) {
                TARGET_RATE++;
                idleTurns = 0;
            }
        }
    }

    public MapLocation findGoodLocation() throws GameActionException {
        MapLocation best = null;
        int bestRubble = 101;
        int rubble = 0;
        int bestDistance = 100;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 8)) {
            rubble = rc.senseRubble(check);
            if (rubble < bestRubble) {
                if (!rc.canSenseRobotAtLocation(check) || rc.getLocation().equals(check)) {
                    best = check;
                    bestRubble = rubble;
                }
            }
            else if(rubble == bestRubble) {
                if(check.distanceSquaredTo(targetCorner) < bestDistance) {
                    bestDistance = check.distanceSquaredTo(targetCorner);
                    best = check;
                }
            }
        }
        return best;
    }

    public boolean tryMove() throws GameActionException {
        boolean switchCorners = false;
        if (nearCorner() && rc.getTransmutationRate() > TARGET_RATE) {
            switchCorners = true;
        }


        if(switchCorners) {
            if(rc.getRoundNum() % 2 == 0) {
                if(targetCorner.x == 0) {
                    targetCorner = new MapLocation(rc.getMapWidth() - 1, targetCorner.y);
                }
                else {
                    targetCorner = new MapLocation(0, targetCorner.y);
                }
            }
            else {
                if(targetCorner.y == 0) {
                    targetCorner = new MapLocation(targetCorner.x, rc.getMapHeight() - 1);
                }
                else {
                    targetCorner = new MapLocation(targetCorner.x, 0);
                }
            }
            idleTurns = 0;
            movingTurns = 0;
        }

        if(rc.isMovementReady()) {
            if(fleeFrom != null) {
                Direction d = Navigation.flee(rc, rc.getLocation(), fleeFrom);
                if(d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            if (!bestRubbleInArea()) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), bestRubbleLoc);
                if (d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            if (nearestAlly != null) {
                if (!nearCorner() && labs < MAX_LABS_NEARBY) {
                    Direction d = Navigation.navigate(rc, rc.getLocation(), targetCorner);
                    if(d != null && rc.canMove(d)) {
                        rc.move(d);
                        return true;
                    }
                }
                Direction d = Navigation.flee(rc, rc.getLocation(), nearestAlly);
                if (d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
            

            MapLocation goodLocation = findGoodLocation();
            Direction d = Navigation.navigate(rc, rc.getLocation(), goodLocation);
            if(d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }
}
