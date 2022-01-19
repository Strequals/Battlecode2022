package velociraptor;

import battlecode.common.*;

public strictfp class ArchonRobot extends Robot {

    private double minerWeight;
    private double soldierWeight;
    private double builderWeight;
    private boolean hasMovedAfterBecomingPortable;
    private int previousLead = 200;
    private boolean activeArchon = false;

    private static final double WEIGHT_DECAY = 0.5;

    public ArchonRobot(RobotController rc) throws GameActionException {
        super(rc);
        minerWeight = 512;
        soldierWeight = -8;
        builderWeight = -64;
        Communications.calculateArchonNumber(rc);
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        updateWeights();
        tryActivate();
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
    }

    public void tryActivate() throws GameActionException {
        if(activeArchon) {
            Communications.updateMinerCount(rc);
            updateIncome();
        }
        else {
            int difference = Communications.getCurrMinerCount(rc) - Communications.getPrevMinerCount(rc);
            int archonCount = rc.getArchonCount();
            if(difference > archonCount || Communications.getCurrMinerCount(rc) > rc.getRobotCount() - archonCount) {
                activeArchon = true;
                Communications.updateMinerCount(rc);
                updateIncome();
            }
        }
    }

    public void updateIncome() throws GameActionException {
        int leadTotal = rc.getTeamLeadAmount(rc.getTeam());
        int income = leadTotal - previousLead;
        previousLead = leadTotal;


    }

    private static final double PANIC_SOLDIER_WEIGHT = 1;

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
    private static final double BASE_MINER_WEIGHT = 0.4;
    private static final double MAX_RESOURCE_BONUS = 0.4;

    public void updateWeights() throws GameActionException {
        double totalResources = Communications.readTotalResources(rc);
        double totalEnemies = Communications.readTotalEnemies(rc);
        updateMinerWeight(totalResources);
        updateSoldierWeight(totalEnemies);
        updateBuilderWeight(totalResources);
        rc.setIndicatorString("resources: " + totalResources + ", enemies: " + totalEnemies + ", miner: " + minerWeight + ", soldier: " + soldierWeight + ", builder: " + builderWeight);
    }

    public void updateMinerWeight(double totalResources) throws GameActionException {
        double bonus = RESOURCE_WEIGHT * totalResources;
        if (bonus > MAX_RESOURCE_BONUS) {
            bonus = MAX_RESOURCE_BONUS;
        }
        minerWeight += BASE_MINER_WEIGHT + bonus;
    }

    private static final double ENEMY_WEIGHT = 0.1;
    private static final double BASE_SOLDIER_WEIGHT = 2;
    private static final double MAX_ENEMIES_BONUS = 1;

    public void updateSoldierWeight(double totalEnemies) throws GameActionException {
        double bonus = ENEMY_WEIGHT * totalEnemies;
        if (bonus > MAX_ENEMIES_BONUS) {
            bonus = MAX_ENEMIES_BONUS;
        }
        soldierWeight += BASE_SOLDIER_WEIGHT + bonus;
    }
    
    private static final double RESOURCE_PENALTY = 0.0002;
    private static final double BASE_BUILDER_WEIGHT = 1.5;
    private static final double MAX_RESOURCE_PENALTY = 1;
    
    public void updateBuilderWeight(double totalResources) throws GameActionException {
        double penalty = RESOURCE_PENALTY * totalResources;
        if (penalty > MAX_RESOURCE_PENALTY) {
            penalty = MAX_RESOURCE_PENALTY;
        }
        builderWeight += BASE_BUILDER_WEIGHT - penalty;
    }
    
    public static final int MOST_EXPENSIVE_UNIT_PRICE_LEAD = 75;
    private int minercd = 0;
    public boolean tryBuild() throws GameActionException {
        if ((1 + Communications.countHigherPriorityArchons(rc)) * MOST_EXPENSIVE_UNIT_PRICE_LEAD
                > rc.getTeamLeadAmount(rc.getTeam())) {
            Communications.incrementArchonPriority(rc);
            return false;
        }
        RobotType type = RobotType.MINER;
        double weight = minerWeight;
        int income = Communications.getIncome(rc);

        if (income < 75 && Communications.getPrevMinerCount(rc) > 5 * rc.getArchonCount() + 10) {
            type = RobotType.BUILDER;
        }

        if (income + (rc.getTeamLeadAmount(rc.getTeam()) / 5) > 75 || Communications.getPrevMinerCount(rc) > 20) {
            weight = soldierWeight;
            type = RobotType.SOLDIER;
            
        }

        if (income + (rc.getTeamLeadAmount(rc.getTeam()) / 5) > rc.getArchonCount() * 100) {
            weight = builderWeight;
            type = RobotType.BUILDER;
        }

        Direction d = null;
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
                if (nearestEnemy != null) {
                    d = nearestEnemy.directionTo(rc.getLocation());
                } else {
                    d = getRandomDirection();
                }
        }

        if (tryBuild(type, d)) {
            switch (type) {
                case MINER:
                    minerWeight *= WEIGHT_DECAY;
                    Communications.correctIncome(rc, 50);
                    break;
                case SOLDIER:
                    soldierWeight *= WEIGHT_DECAY;
                    Communications.correctIncome(rc, 75);
                    break;
                case BUILDER:
                    builderWeight *= WEIGHT_DECAY;
                    Communications.correctIncome(rc, 40);
                    break;
            }
            Communications.zeroArchonPriority(rc);
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
