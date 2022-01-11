package deadchkn;

import battlecode.common.*;

public strictfp class ArchonRobot extends Robot {

    private double minerWeight;
    private double soldierWeight;
    private double builderWeight;
    private boolean dormant;
    
    public static final int ACTIVATE_THRESHOLD = 75;

    private static final double WEIGHT_DECAY = 0.5;

    public ArchonRobot(RobotController rc) throws GameActionException {
        super(rc);
        minerWeight = 256;
        soldierWeight = -16;
        builderWeight = -64;
        dormant = true;
        Communications.calculateArchonNumber(rc);
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        //addPotentialArchonTargets();
        updateDormantState();
        updateWeights();
        tryBuild();
        tryRepair();
        Communications.writeArchonPriority(rc);
        //rc.setIndicatorString(Communications.archonNum + ", prty:" + Communications.archonPriority);
        /*rc.setIndicatorString(Communications.readArchonPriority(rc, 0) + " " 
                + Communications.readArchonPriority(rc, 1) + " "
                + Communications.readArchonPriority(rc, 2) + " "
                + Communications.readArchonPriority(rc, 3) + " "
                + Communications.countHigherPriorityArchons(rc) + " "
                + rc.getTeamLeadAmount(rc.getTeam()));*/
        rc.setIndicatorString("dormant: " + dormant + ", priority: " + Communications.archonPriority);
    }

    private static final int ARCHON_TARGET_WEIGHT = 511;
    private boolean enemiesSeenYet = false;
    private static final double VALUE_THRESHOLD = 10; // should be the same as in soldier;

    public void addPotentialArchonTargets() throws GameActionException {

        if (Communications.archonNum == 0) {
            Resource res = Communications.readEnemiesData(rc);
            rc.setIndicatorString("res.value=" + res.value);
            if (!enemiesSeenYet && res.value < VALUE_THRESHOLD) {
                MapLocation v = MapSymmetry.VERTICAL.reflect(rc, rc.getLocation());
                MapLocation h = MapSymmetry.HORIZONTAL.reflect(rc, rc.getLocation());
                MapLocation r = MapSymmetry.ROTATIONAL.reflect(rc, rc.getLocation());
                Communications.addEnemyData(rc, v, ARCHON_TARGET_WEIGHT);
                Communications.addEnemyData(rc, h, ARCHON_TARGET_WEIGHT);
                Communications.addEnemyData(rc, r, ARCHON_TARGET_WEIGHT);
                rc.setIndicatorLine(rc.getLocation(), v, 0, 255, 0);
                rc.setIndicatorLine(rc.getLocation(), h, 0, 255, 0);
                rc.setIndicatorLine(rc.getLocation(), r, 0, 255, 0);
            }
        }
    }

    private static final double PANIC_SOLDIER_WEIGHT = 0.5;

    RobotInfo[] nearbyRobots;
    MapLocation nearestEnemy;
    boolean enemiesNearby = false;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        enemiesNearby = false;
        nearestEnemy = null;
        int enemyDist = Integer.MAX_VALUE;
        int dist;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == rc.getTeam()) {

            } else {
                enemiesNearby = true;
                if (nearbyRobot.type.canAttack()) {
                    dist = rc.getLocation().distanceSquaredTo(nearbyRobot.location);
                    if (dist < enemyDist) {
                        nearestEnemy = nearbyRobot.location;
                        enemyDist = dist;
                    }
                }
            }
        }
        processAndBroadcastEnemies(nearbyRobots);
        if (enemiesNearby) {
            Communications.maxArchonPriority(rc);
            soldierWeight += PANIC_SOLDIER_WEIGHT;
        }
    }

    private static final double RESOURCE_WEIGHT = 0.00005;
    private static final double BASE_MINER_WEIGHT = 0.15;
    private static final double MAX_RESOURCE_BONUS = 0.1;

    public void updateWeights() throws GameActionException {
        double totalResources = Communications.readTotalResources(rc);
        double totalEnemies = Communications.readTotalEnemies(rc);
        updateMinerWeight(totalResources);
        if (!dormant) updateSoldierWeight(totalEnemies);
        if (!dormant) updateBuilderWeight(totalResources);
    }

    public void updateMinerWeight(double totalResources) throws GameActionException {
        double bonus = RESOURCE_WEIGHT * totalResources;
        if (bonus > MAX_RESOURCE_BONUS) {
            bonus = MAX_RESOURCE_BONUS;
        }
        minerWeight += BASE_MINER_WEIGHT + bonus;
    }

    private static final double ENEMY_WEIGHT = 0.1;
    private static final double BASE_SOLDIER_WEIGHT = 1;
    private static final double MAX_ENEMIES_BONUS = 2;

    public void updateSoldierWeight(double totalEnemies) throws GameActionException {
        double bonus = ENEMY_WEIGHT * totalEnemies;
        if (bonus > MAX_ENEMIES_BONUS) {
            bonus = MAX_ENEMIES_BONUS;
        }
        soldierWeight += BASE_SOLDIER_WEIGHT + bonus;
    }
    
    public void updateDormantState() throws GameActionException {
        if (dormant) {
            if (Communications.archonNum == 0
                    || rc.getTeamLeadAmount(rc.getTeam()) > (1 + Communications.countHigherPriorityArchons(rc)) * ACTIVATE_THRESHOLD
                    || enemiesNearby) {
                dormant = false;
            }
        } else {
            if (rc.getTeamLeadAmount(rc.getTeam()) < (1 + Communications.countHigherPriorityArchons(rc)) * ACTIVATE_THRESHOLD
                    && !enemiesNearby
                    && Communications.archonNum != 0) {
                dormant = true;
            }
        }
    }
    
    private static final double RESOURCE_PENALTY = 0.0002;
    private static final double BASE_BUILDER_WEIGHT = 2.5;
    private static final double MAX_RESOURCE_PENALTY = 2;
    
    public void updateBuilderWeight(double totalResources) throws GameActionException {
        double penalty = RESOURCE_PENALTY * totalResources;
        if (penalty > MAX_RESOURCE_PENALTY) {
            penalty = MAX_RESOURCE_PENALTY;
        }
        builderWeight += BASE_BUILDER_WEIGHT - penalty;
    }
    
    public static final int MOST_EXPENSIVE_UNIT_PRICE_LEAD = 75;
    public boolean tryBuild() throws GameActionException {
        if ((1 + Communications.countHigherPriorityArchons(rc)) * MOST_EXPENSIVE_UNIT_PRICE_LEAD
                > rc.getTeamLeadAmount(rc.getTeam())) {
            Communications.incrementArchonPriority(rc);
            return false;
        }

        RobotType type = RobotType.MINER;
        double weight = minerWeight;

        if (soldierWeight > weight) {
            weight = soldierWeight;
            type = RobotType.SOLDIER;
        }

        if (builderWeight > weight) {
            weight = builderWeight;
            type = RobotType.BUILDER;
        }

        Direction d = null;
        boolean cancelBuild = false;
        switch (type) {
            case MINER:
                Resource r = Communications.readResourceData(rc);
                if (r != null) {
                    d = rc.getLocation().directionTo(r.location);
                } else {
                    d = getRandomDirection();
                }
                break;
            case SOLDIER:
                if (dormant) {
                    cancelBuild = true;
                    break;
                }
                if (nearestEnemy != null) {
                    d = rc.getLocation().directionTo(nearestEnemy);
                } else {
                    Resource s = Communications.readEnemiesData(rc);
                    if (s != null) {
                        d = rc.getLocation().directionTo(s.location);
                    } else {
                        d = getRandomDirection();
                    }
                }
                break;
            case BUILDER:
                if (dormant) {
                    cancelBuild = true;
                    break;
                }
                if (nearestEnemy != null) {
                    d = nearestEnemy.directionTo(rc.getLocation());
                } else {
                    d = getRandomDirection();
                }
        }

        if (cancelBuild || tryBuild(type, d)) {
            switch (type) {
                case MINER:
                    minerWeight *= WEIGHT_DECAY;
                    break;
                case SOLDIER:
                    soldierWeight *= WEIGHT_DECAY;
                    break;
                case BUILDER:
                    builderWeight *= WEIGHT_DECAY;
                    break;
            }
            if (type == RobotType.MINER) {
                Communications.zeroArchonPriority(rc);
            } else {
                Communications.incrementArchonPriority(rc);
            }
            return true;
        }

        Communications.incrementArchonPriority(rc);
        return false;
    }

    public boolean tryBuild(RobotType type) throws GameActionException {
        for (Direction d : directions) {
            if (rc.canBuildRobot(type, d)) {
                rc.buildRobot(type, d);
                return true;
            }
        }
        return false;
    }

    public boolean tryBuild(RobotType type, Direction d) throws GameActionException {
        if (d == Direction.CENTER) {
            d = getRandomDirection();
        }

        if (rc.canBuildRobot(type, d)) {
            rc.buildRobot(type, d);
            return true;
        }
        Direction left = d;
        Direction right = d;
        for (int i = 3; i-- > 0;) {
            left = left.rotateLeft();
            right = right.rotateRight();
            if (rc.canBuildRobot(type, left)) {
                rc.buildRobot(type, left);
                return true;
            }
            if (rc.canBuildRobot(type, right)) {
                rc.buildRobot(type, right);
                return true;
            }
        }
        left = left.rotateLeft();
        if (rc.canBuildRobot(type, left)) {
            rc.buildRobot(type, left);
            return true;
        }
        return false;
    }

    public boolean tryRepair() throws GameActionException {
        if (!rc.isActionReady()) return false;
        MapLocation repairable = identifyRepairableRobot();
        if (repairable != null && rc.canRepair(repairable)) {
            rc.repair(repairable);
            return true;
        }

        return false;
        
    }

public MapLocation identifyRepairableRobot() throws GameActionException {
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        Team team = rc.getTeam();
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == team
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.ARCHON.actionRadiusSquared)
                    && otherRobot.health < otherRobot.type.getMaxHealth(otherRobot.level)) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct) {
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
