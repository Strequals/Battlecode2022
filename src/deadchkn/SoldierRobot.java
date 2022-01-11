package deadchkn;

import battlecode.common.*;
import trex.Communications;
import trex.MapSymmetry;
import trex.Navigation;
import trex.Resource;
import trex.Robot;

public strictfp class SoldierRobot extends Robot {

    MapLocation targetLocation;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;
    static final int GIVE_UP_RADIUS_SQUARED = 2;
    static final boolean MOVE_IF_ATTACK_CD = true;
    MapLocation createdLocation;
    boolean checkHorizontal = true;
    boolean checkVertical = true;
    boolean checkRotational = true;
    MapLocation horizontal;
    MapLocation vertical;
    MapLocation rotational;

    public SoldierRobot(RobotController rc) throws GameActionException {
        super(rc);
        createdLocation = rc.getLocation();
        horizontal = MapSymmetry.HORIZONTAL.reflect(rc, createdLocation);
        vertical = MapSymmetry.VERTICAL.reflect(rc, createdLocation);
        rotational = MapSymmetry.ROTATIONAL.reflect(rc, createdLocation);
    }

    static final int CHECK_DISTANCE = 9;

    public void updateCheckedLocations() throws GameActionException {
        MapLocation current = rc.getLocation();
        if (current.distanceSquaredTo(horizontal) < CHECK_DISTANCE) {
            checkHorizontal = false;
        }
        if (current.distanceSquaredTo(vertical) < CHECK_DISTANCE) {
            checkVertical = false;
        }
        if (current.distanceSquaredTo(rotational) < CHECK_DISTANCE) {
            checkRotational = false;
        }
    }

    public MapLocation targetArchonLocation() {
        if (checkHorizontal) {
            return horizontal;
        } else if (checkRotational) {
            return rotational;
        } else if (checkVertical) {
            return vertical;
        }
        return null;
    }

    @Override
    public void run() throws GameActionException {
        
        processNearbyRobots();
        broadcastNearbyResources();
        updateCheckedLocations();
        
        boolean moved = tryMove();
        boolean attacked = tryAttack();
        if (!areEnemiesNearby && !moved && !attacked && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }
        rc.setIndicatorLine(rc.getLocation(), targetLocation, 255, 0, 0);
    }

    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean friendlyArchonNearby;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        MapLocation fleeFrom = null;
        areEnemiesNearby = false;
        friendlyArchonNearby = false;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {
                if (otherRobot.type == RobotType.ARCHON) {
                    friendlyArchonNearby = true;
                }
            } else {
                areEnemiesNearby = true;
                if (otherRobot.type.canAttack() && otherRobot.mode.canAct) {
                    // condition may need changing if canAttack also involves healing
                    MapLocation attackedFrom;
                    if (otherRobot.mode == RobotMode.TURRET) {
                        attackedFrom = otherRobot.location;
                    } else {
                        attackedFrom = otherRobot.location.add(otherRobot.location.directionTo(rc.getLocation()));
                    }
                    int dsq = attackedFrom.distanceSquaredTo(rc.getLocation());
                    if (dsq < fleeDistanceSquared) {
                        fleeDistanceSquared = dsq;
                        fleeFrom = attackedFrom;
                    }
                }
            }
        }

        if (fleeFrom != null && !friendlyArchonNearby) {
            tryAttack();
            tryFlee(fleeFrom);
            targetLocation = null;
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

    /**
     * Selects an opposing enemy in vision range by the same criteria as identifyTarget.
     */

    public MapLocation findTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()) {
                if (otherRobot.type.canAttack()
                        /*&& otherRobot.mode.canAct
                        && rc.getLocation().isWithinDistanceSquared(otherRobot.location, otherRobot.type.actionRadiusSquared)*/) {
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

    public static final double VALUE_THRESHOLD = 0.01;

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
        if (targetLocation == null || locationScore < 1) {
            Resource r = Communications.readEnemiesData(rc);
            if (r != null && locationScore > VALUE_THRESHOLD) {
                targetLocation = r.location;
                locationScore = 2;
                return;
            }
        }

        if (targetLocation == null) {
            MapLocation targetArchon = targetArchonLocation();
            if (targetArchon != null) {
                targetLocation = targetArchon;
                locationScore = 0.5;
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

        if(rc.getActionCooldownTurns() > 10 && !MOVE_IF_ATTACK_CD) {
            return false;
        }

        if (!rc.getLocation().isWithinDistanceSquared(targetLocation, GIVE_UP_RADIUS_SQUARED)) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }
        return false;
    }
}
