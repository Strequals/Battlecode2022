package chicken;

import battlecode.common.*;

public strictfp class WatchtowerRobot extends Robot {

    MapLocation targetLocation;

    public WatchtowerRobot(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        switch (rc.getMode()) {
            case TURRET:
                tryAttack();
            case PORTABLE:
                tryMove();
        }
    }

    public void runTurret() throws GameActionException {
        tryAttack();
    }

    public void runPortable() throws GameActionException {
        tryMove();
    }

    public boolean shouldBecomeTurret() {
        return false;
    }

    public boolean shouldBecomePortable() {
        return false;
    }

    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areAttackableEnemies;
    int nearbyWatchtowers;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        areEnemiesNearby = false;
        areAttackableEnemies = false;
        nearbyWatchtowers = 0;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {
                if (otherRobot.type == RobotType.WATCHTOWER) {
                    nearbyWatchtowers++;
                }
            } else {
                areEnemiesNearby = true;
                if (otherRobot.location.isWithinDistanceSquared(rc.getLocation(), RobotType.WATCHTOWER.actionRadiusSquared)) {
                    areAttackableEnemies = true;
                }
                break;
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

    public boolean tryMove() throws GameActionException {
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
