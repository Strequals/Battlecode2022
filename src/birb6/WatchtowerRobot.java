package birb6;

import battlecode.common.*;

public strictfp class WatchtowerRobot extends Robot {
    private static final int MIN_IDLE_TURNS = 1;
    private static final int MAX_IDLE_TURNS = 10;
    private static final int CRITICAL_MASS = 16;
    private static final int INITIAL_IDLE_TURNS = 50;

    private boolean hasMovedAfterBecomingPortable;
    private int idleTurns = 0;

    MapLocation targetLocation;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;
    static final int GIVE_UP_RADIUS_SQUARED = 2;

    public WatchtowerRobot(RobotController rc) {
        super(rc);
        idleTurns = -INITIAL_IDLE_TURNS;
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        switch (rc.getMode()) {
            case TURRET:
                tryAttack();
                if(shouldBecomePortable()) {
                    if(rc.canTransform()) {
                        rc.transform();
                        hasMovedAfterBecomingPortable = false;
                    }
                }
                if(!areAttackableEnemies) {
                    idleTurns++;
                }
                break;
            case PORTABLE:
                if (shouldBecomeTurret() && hasMovedAfterBecomingPortable) {
                    if(rc.canTransform()) {
                        rc.transform();
                    }
                } else {
                    if (tryMove()) {
                        hasMovedAfterBecomingPortable = true;
                    }
                }
                break;
        }
    }

    public void runTurret() throws GameActionException {
        tryAttack();
    }

    public void runPortable() throws GameActionException {
        tryMove();
    }

    public boolean shouldBecomeTurret() {
        return areAttackableDangerousEnemies;
    }

    public boolean shouldBecomePortable() {
        return ((!areAttackableEnemies || (!areAttackableDangerousEnemies && idleTurns > MAX_IDLE_TURNS)) && (idleTurns > MAX_IDLE_TURNS || (idleTurns > MIN_IDLE_TURNS && nearbyWatchtowers > CRITICAL_MASS)));
    }

    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areAttackableEnemies;
    boolean areAttackableDangerousEnemies;
    int nearbyWatchtowers;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        areEnemiesNearby = false;
        areAttackableEnemies = false;
        areAttackableDangerousEnemies = false;
        nearbyWatchtowers = 0;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {
                /*if (otherRobot.type == RobotType.WATCHTOWER && otherRobot.mode == RobotMode.TURRET) {
                    nearbyWatchtowers++;
                }*/
                nearbyWatchtowers++;
            } else {
                areEnemiesNearby = true;
                if (otherRobot.location.isWithinDistanceSquared(rc.getLocation(), RobotType.WATCHTOWER.actionRadiusSquared)) {
                    areAttackableEnemies = true;
                    if (otherRobot.type.canAttack() && otherRobot.mode.canAct) {
                        areAttackableDangerousEnemies = true;
                    }
                }
            }
        }
        processAndBroadcastEnemies(nearbyRobots);
    }

    public boolean tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            MapLocation target = identifyTarget();
            if (target != null && rc.canAttack(target)) {
                rc.attack(target);
                return true;
            }
        }
        return false;
    }
    
    public void findTargets() throws GameActionException {
        if (targetLocation != null) {
            if (rc.getLocation().isWithinDistanceSquared(targetLocation, GIVE_UP_RADIUS_SQUARED)) {
                targetLocation = null;
            } else if (locationScore < CHANGE_TARGET_THRESHOLD) {
                targetLocation = null;
            }
        }
        
        MapLocation t = identifyTarget();
        if (t != null) {
            targetLocation = t;
            locationScore = 1;
            return;
        }
        if (targetLocation == null) {
            Resource r = Communications.readEnemiesData(rc);
            if (r != null && locationScore > SoldierRobot.VALUE_THRESHOLD) {
                targetLocation = r.location;
                locationScore = 2;
                return;
            }
        }

        if (targetLocation == null) {
            targetLocation = getRandomLocation();
            locationScore = 0.5;
        }
    }

    public boolean tryMove() throws GameActionException {
        findTargets();
        if (!rc.getLocation().isWithinDistanceSquared(targetLocation, GIVE_UP_RADIUS_SQUARED)) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }

    /**
     * Targets the opposing enemy with the lowest health, prioritizing units that can attack;
     * returns null if no such enemy is found.
     **/
    public MapLocation identifyTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct
                        && rc.getLocation().isWithinDistanceSquared(otherRobot.location, otherRobot.type.actionRadiusSquared)) {
                    if (otherRobot.health < health) {
                        health = otherRobot.health;
                        canAttack = true;
                        best = otherRobot.location;
                    }
                } else if (!canAttack) {
                    if (otherRobot.health < health) {
                        health = otherRobot.health;
                        best = otherRobot.location;
                    }
                }
            }
        }
        return best;
    }
}
