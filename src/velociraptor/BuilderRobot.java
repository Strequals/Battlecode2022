package velociraptor;

import battlecode.common.*;
import java.util.Random;

public strictfp class BuilderRobot extends Robot {

    MapLocation targetLocation;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;

    private static final double SEED_WEIGHT = 0.7;
    private static final int MAX_IDLE_SEED = 10;
    private static final int BUILD_THRESHOLD = 500;
    private static final int TOWER_COOLDOWN = 4;  // turns before building tower - 1
    private static final int TOWER_THRESHOLD = 20;

    public BuilderRobot(RobotController rc) {
        super(rc);
    }

    private int towerCooldown = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        calculateShouldMutateLevel();
        boolean repaired = tryRepair();
        boolean mutated = tryMutate();
        boolean built = tryBuild();
        if (!repaired && !mutated && !built) trySeed();
        if (tryMove()) {
            repaired = tryRepair();
            mutated = tryMutate();
            built = tryBuild();
            if (!repaired && !mutated && !built) {
                trySeed();
                locationScore *= SCORE_DECAY;
            }
        }
    }
    
    RobotInfo[] nearbyRobots;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        MapLocation fleeFrom = null;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        processAndBroadcastEnemies(nearbyRobots);
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {

            } else {
                if (otherRobot.type.canAttack() && otherRobot.mode.canAct) {
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

        if (fleeFrom != null) {
            tryFlee(fleeFrom);
            targetLocation = null;
            //locationScore *= SCORE_DECAY;
        }
    }

    public void trySeed() throws GameActionException {
        MapLocation loc = rc.getLocation();
        if(rc.senseLead(loc) == 0) {
            rc.disintegrate();
        }
    }

    public MapLocation findSeedLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation best = null;
        int bestDist = Integer.MAX_VALUE;
        int dist;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), RobotType.BUILDER.visionRadiusSquared)) {
            dist = check.distanceSquaredTo(current);
            if (dist < bestDist) {
                if (rc.canSenseLocation(check) && rc.senseLead(check) == 0 && !rc.canSenseRobotAtLocation(check)) {
                    best = check;
                    bestDist = dist;
                }
            }
        }

        return best;
    }

    /*
    *  returns true on success, false otherwise
    */
    public boolean tryBuild() throws GameActionException {

        if(!shouldBuild()) {
            return false;
        }

        /*if(Communications.readTotalEnemies(rc) > TOWER_THRESHOLD && towerCooldown == 0) {
            if(tryBuild(RobotType.WATCHTOWER)) {
                towerCooldown = TOWER_COOLDOWN;
                return true;
            }
        }
        else {
            return tryBuild(RobotType.LABORATORY);
        }*/

        return tryBuild(RobotType.WATCHTOWER);
    }

    public boolean shouldBuild() {
        return rc.getTeamLeadAmount(rc.getTeam()) >= BUILD_THRESHOLD;
    }

    public boolean tryBuild(RobotType type) throws GameActionException {
        if (!rc.isActionReady()) return false;
        MapLocation current = rc.getLocation();
        MapLocation loc;
        for (Direction d : directions) {
            loc = current.add(d);
            if (validBuildLocation(loc) && rc.canBuildRobot(type, d)) {
                if (rc.canSenseLocation(loc) && rc.senseLead(loc) > 0) {
                    rc.buildRobot(type, d);
                    if(type == RobotType.LABORATORY) {
                        Communications.correctIncome(rc, 800);
                    }
                    else {
                        Communications.correctIncome(rc, 180);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public boolean tryRepair() throws GameActionException {
        if (!rc.isActionReady()) return false;
        Team team = rc.getTeam();
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team && RobotType.BUILDER.canRepair(nearbyRobot.type)
                    && nearbyRobot.health < nearbyRobot.type.getMaxHealth(nearbyRobot.level)) {
                if (rc.canRepair(nearbyRobot.location)) {
                    rc.repair(nearbyRobot.location);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryMutate() throws GameActionException {
        if (!rc.isActionReady()) return false;
        Team team = rc.getTeam();
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team && RobotType.BUILDER.canMutate(nearbyRobot.type)
                && nearbyRobot.level < shouldMutateLevel) {
                if (rc.canMutate(nearbyRobot.location)) {
                    rc.mutate(nearbyRobot.location);
                    if(shouldMutateLevel == 2) {
                        Communications.correctIncome(rc, 600);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryMove() throws GameActionException {
        if (!rc.isMovementReady()) return false;
        findTarget();
        if(rc.getLocation().distanceSquaredTo(targetLocation) == 0) {
            for(Direction d: directions) {
                if(rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
        } else {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }

        return false;
    }

    public void findTarget() throws GameActionException {
        if (targetLocation != null) {
            if (rc.getLocation().isWithinDistanceSquared(targetLocation, 2)) {
                targetLocation = null;
            } else if (locationScore < CHANGE_TARGET_THRESHOLD) {
                targetLocation = null;
            }
        }

        MapLocation repairMutateLocation = findRepairOrMutateLocation();
        if (repairMutateLocation != null) {
            targetLocation = repairMutateLocation;
            locationScore = 2;
            return;
        }

        if (shouldBuild()) {
            MapLocation buildLocation = findBuildLocation();
            if (buildLocation != null) {
                targetLocation = buildLocation;
                locationScore = 1;
                return;
            }
        }

        MapLocation seedLocation = findSeedLocation();
        if (seedLocation != null) {
            targetLocation = seedLocation;
            locationScore = 1;
            return;
        }
        
        if (targetLocation == null) {
            targetLocation = getRandomLocation();
        }
    }

    public MapLocation findRepairLocation() throws GameActionException {
        Team team = rc.getTeam();
        MapLocation current = rc.getLocation();
        MapLocation best = null;
        int bestDist = Integer.MAX_VALUE;
        int dist;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team && RobotType.BUILDER.canRepair(nearbyRobot.type)
                    && nearbyRobot.health < nearbyRobot.type.getMaxHealth(nearbyRobot.level)) {
                dist = current.distanceSquaredTo(nearbyRobot.location);
                if (dist < bestDist) {
                    best = nearbyRobot.location;
                    bestDist = dist;
                }
            }
        }
        return best;
    }
    public static final int MUTATE_1 = 1000;
    public static final int MUTATE_2 = 1500;
    public int shouldMutateLevel = 1;
    public void calculateShouldMutateLevel() throws GameActionException {
        int leadAmount = rc.getTeamLeadAmount(rc.getTeam());
        if (leadAmount > MUTATE_1) {
            shouldMutateLevel = 2;
        } else {
            shouldMutateLevel = 1;
        }
    }

    public MapLocation findRepairOrMutateLocation() throws GameActionException {
        Team team = rc.getTeam();
        MapLocation current = rc.getLocation();
        MapLocation best = null;
        int bestDist = Integer.MAX_VALUE;
        int dist;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team) {
                if (RobotType.BUILDER.canRepair(nearbyRobot.type)
                    && nearbyRobot.health < nearbyRobot.type.getMaxHealth(nearbyRobot.level)) {
                    dist = current.distanceSquaredTo(nearbyRobot.location);
                    if (dist < bestDist) {
                        best = nearbyRobot.location;
                        bestDist = dist;
                    }
                } else if (RobotType.BUILDER.canMutate(nearbyRobot.type)
                        && nearbyRobot.level < shouldMutateLevel) {
                    dist = current.distanceSquaredTo(nearbyRobot.location);
                    if (dist < bestDist) {
                        best = nearbyRobot.location;
                        bestDist = dist;
                    }
                }
            }
        }
        return best;
    }

    /*
    *  searches for a spot on odd x and y, not occupied (should also maybe make sure no archons are nearby)
    */
    public MapLocation findBuildLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation best = null;
        int bestDist = Integer.MAX_VALUE;
        int dist;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), RobotType.BUILDER.visionRadiusSquared)) {
            dist = check.distanceSquaredTo(current);
            if (dist < bestDist) {
                if (validBuildLocation(check) && !rc.canSenseRobotAtLocation(check)) {
                    best = check;
                    bestDist = dist;
                }
            }
        }

        return best;
    }

}
