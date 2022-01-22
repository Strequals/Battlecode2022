package parrot_2;

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
        Communications.incrementBuilderCount(rc);
        calculateShouldMutateLevel();
        boolean repaired = tryRepair();
        boolean mutated = tryMutate();
        boolean built = tryBuild();
        if (tryMove()) {
            repaired = repaired || tryRepair();
            mutated = mutated || tryMutate();
            built = built || tryBuild();
            if (!repaired && !mutated && !built && rc.getTeamLeadAmount(rc.getTeam()) > WATCHTOWER_THRESHOLD) {
                locationScore *= SCORE_DECAY;
            }
        }
    }
    
    RobotInfo[] nearbyRobots;
    boolean labNearby;
    boolean spottedByEnemy;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        MapLocation fleeFrom = null;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        processAndBroadcastEnemies(nearbyRobots);
        labNearby = false;
        MapLocation current = rc.getLocation();
        spottedByEnemy = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == rc.getTeam()) {
                if (otherRobot.type == RobotType.LABORATORY) {
                    labNearby = true;
                }
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
                if (otherRobot.location.isWithinDistanceSquared(current, otherRobot.type.visionRadiusSquared)) {
                    spottedByEnemy = true;
                }
            }
        }

        if (fleeFrom != null) {
            tryFlee(fleeFrom);
            targetLocation = null;
            //locationScore *= SCORE_DECAY;
        }
    }

    /*public void trySeed() throws GameActionException {
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
    }*/
    
    public static final int WATCHTOWER_THRESHOLD = 1000;
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
        if (Communications.getLabCount(rc) < ArchonRobot.LABS) {
            return tryBuild(RobotType.LABORATORY);
        } else if (rc.getTeamLeadAmount(rc.getTeam()) > WATCHTOWER_THRESHOLD) {
            return tryBuild(RobotType.WATCHTOWER);
        }

        return false;
    }

    public boolean shouldBuild() {
        //TODO: do not build if an archon is under attack
        return true;
    }

    public boolean tryBuild(RobotType type) throws GameActionException {
        if (!rc.isActionReady()) return false;

        if (type == RobotType.LABORATORY) {
            if (spottedByEnemy) {
                return false;
            }
        }
        
        MapLocation current = rc.getLocation();
        MapLocation loc;
        Direction best = null;
        int rubble = 101;
        int r;
        for (Direction d : directions) {
            if (rc.canBuildRobot(type, d)) {
                loc = current.add(d);
                r = rc.senseRubble(loc);
                if (r < rubble) {
                    best = d;
                    rubble = r;
                    
                }
            }
        }

        if (best != null) {
            rc.buildRobot(type, best);
            if (type == RobotType.LABORATORY) {
                Communications.correctIncome(rc, 180);
            }
            else {
                Communications.correctIncome(rc, 150);
            }
            return true;
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

    public MapLocation buildFrom() throws GameActionException {
        if (!rc.canSenseLocation(targetLocation)) {
            return targetLocation;
        }

        MapLocation best = null;
        int bestRubble = 101;
        MapLocation current = rc.getLocation();
        if (!rc.canSenseRobotAtLocation(targetLocation) || targetLocation.equals(current)) {
            best = targetLocation;
            bestRubble = rc.senseRubble(targetLocation);
        }
        MapLocation loc;
        int rubble;
        for (Direction d : directions) {
            loc = targetLocation.add(d);
            if (loc.equals(current) || (rc.canSenseLocation(loc) && !rc.canSenseRobotAtLocation(loc))) {
                rubble = rc.senseRubble(loc);
                if (rubble < bestRubble) {
                    bestRubble = rubble;
                    best = loc;
                }
            }
        }
        return best;
    }

    public boolean tryMove() throws GameActionException {
        if (!rc.isMovementReady()) return false;
        findTarget();

        MapLocation buildFrom = buildFrom();

        if(buildFrom != null) {
            Direction d = null;
            if (rc.getLocation().distanceSquaredTo(buildFrom) > 2) {
                d = Navigation.navigate(rc, rc.getLocation(), buildFrom);
            } else if (!rc.getLocation().equals(buildFrom)) {
                d = rc.getLocation().directionTo(buildFrom);
            }
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
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



        /*MapLocation seedLocation = findSeedLocation();
        if (seedLocation != null) {
            targetLocation = seedLocation;
            locationScore = 1;
            return;
        }*/
        
        if (targetLocation == null) {
            if (Communications.getLabCount(rc) < ArchonRobot.LABS) {
                targetLocation = findTargetCorner();
            } else {
                targetLocation = getRandomLocation();
            }
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
        boolean isPrototype = false;
        int bestDist = Integer.MAX_VALUE;
        int dist;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team) {
                if (RobotType.BUILDER.canRepair(nearbyRobot.type)
                    && nearbyRobot.health < nearbyRobot.type.getMaxHealth(nearbyRobot.level)) {
                    dist = current.distanceSquaredTo(nearbyRobot.location);
                    if (nearbyRobot.mode == RobotMode.PROTOTYPE) {
                        if (!isPrototype || dist < bestDist) {
                            best = nearbyRobot.location;
                            isPrototype = true;
                            bestDist = dist;
                        }
                    } else {
                        if (!isPrototype && dist < bestDist) {
                            best = nearbyRobot.location;
                            bestDist = dist;
                        }
                    }
                } else if (RobotType.BUILDER.canMutate(nearbyRobot.type)
                        && nearbyRobot.level < shouldMutateLevel) {
                    dist = current.distanceSquaredTo(nearbyRobot.location);
                    if (!isPrototype && dist < bestDist) {
                        best = nearbyRobot.location;
                        bestDist = dist;
                    }
                }
            }
        }

        if (best == null) {
            MapLocation nearestArchon = Communications.getClosestArchon(rc);
            if (current.distanceSquaredTo(nearestArchon) > RobotType.BUILDER.visionRadiusSquared) {
                best = nearestArchon;
            }
        }
            
        return best;
    }

    /*
    *  searches for a not occupied (should also maybe make sure no archons are nearby)
    */
    public MapLocation findBuildLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation best = null;
        int bestRubble = Integer.MAX_VALUE;
        int rubble;
        for (MapLocation check : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 8)) {
            rubble = rc.senseRubble(check);
            if (rubble < bestRubble) {
                if (!rc.canSenseRobotAtLocation(check)) {
                    best = check;
                    bestRubble = rubble;
                }
            }
        }

        return best;
    }

}
