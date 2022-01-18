package birb_2;

import battlecode.common.*;

public strictfp class ArchonRobot extends Robot {

    private double minerWeight;
    private double soldierWeight;
    private double builderWeight;
    private boolean hasMovedAfterBecomingPortable;
    private int previousLead = 200;
    private boolean activeArchon = false;
    private boolean exploring;
    
    private int explorationMiners;
    private static final int BASE_MIN_MINER = 2;
    private static final double MINER_RATIO = 0.004;

    private static final double WEIGHT_DECAY = 0.5;

    public ArchonRobot(RobotController rc) throws GameActionException {
        super(rc);
        minerWeight = 16;
        soldierWeight = -8;
        builderWeight = -64;
        Communications.calculateArchonNumber(rc);
        explorationMiners = (int) (MINER_RATIO * rc.getMapWidth() * rc.getMapHeight()) + BASE_MIN_MINER;
        exploring = true;
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
        Communications.writeArchonData(rc);
        //rc.setIndicatorString(Communications.archonNum + ", prty:" + Communications.archonPriority);
        /*rc.setIndicatorString(Communications.readArchonPriority(rc, 0) + " " 
                + Communications.readArchonPriority(rc, 1) + " "
                + Communications.readArchonPriority(rc, 2) + " "
                + Communications.readArchonPriority(rc, 3) + " "
                + Communications.countHigherPriorityArchons(rc) + " "
                + rc.getTeamLeadAmount(rc.getTeam()));*/

        rc.setIndicatorString("miners:" + Communications.getPrevMinerCount(rc));
    }

    public void tryActivate() throws GameActionException {
        if(activeArchon) {
            Communications.updateMinerCount(rc);
            Communications.clearOtherArchonData(rc);
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
            soldierWeight += PANIC_SOLDIER_WEIGHT;
        }
    }

    private static final double RESOURCE_WEIGHT = 0.0001;
    private static final double BASE_MINER_WEIGHT = 0.4;
    private static final double MAX_RESOURCE_BONUS = 0.8;
    private static final double NOT_ENOUGH_MINERS_BONUS = 0.2;
    private static final double MAKE_SOLDIERS_THRESHOLD = 2;

    public void updateWeights() throws GameActionException {
        double totalResources = Communications.readTotalResources(rc);
        double totalEnemies = Communications.readTotalEnemies(rc);
        updateMinerWeight(totalResources);
        updateSoldierWeight(totalEnemies);
        updateBuilderWeight(totalResources);
        if (exploring && Communications.getCurrMinerCount(rc) < explorationMiners) {
            if (!enemiesNearby) {
                Resource r = Communications.readEnemiesData(rc);
                rc.setIndicatorString("" + r.value);
                if (r.value < MAKE_SOLDIERS_THRESHOLD) {
                    if (soldierWeight > minerWeight) {
                        soldierWeight = minerWeight - 0.000001;
                    }
                    /*if (builderWeight > minerWeight) {
                        builderWeight = minerWeight - 0.000001;
                    }*/
                } else {
                    exploring = false;
                }
                //if (builderWeight > minerWeight) builderWeight = minerWeight - 0.000001;
            } else {
                exploring = false;
            }
        }
        //rc.setIndicatorString("m: " + minerWeight + ", s: " + soldierWeight + ", b: " + builderWeight);
    }

    public void updateMinerWeight(double totalResources) throws GameActionException {
        double bonus = RESOURCE_WEIGHT * totalResources;
        if (bonus > MAX_RESOURCE_BONUS) {
            bonus = MAX_RESOURCE_BONUS;
        }
        if (Communications.getCurrMinerCount(rc) < explorationMiners) {
            bonus += NOT_ENOUGH_MINERS_BONUS;
        }
        minerWeight += BASE_MINER_WEIGHT + bonus;
    }

    private static final double ENEMY_WEIGHT = 0.1;
    private static final double BASE_SOLDIER_WEIGHT = 3;
    private static final double MAX_ENEMIES_BONUS = 2;

    public void updateSoldierWeight(double totalEnemies) throws GameActionException {
        double bonus = ENEMY_WEIGHT * totalEnemies;
        if (bonus > MAX_ENEMIES_BONUS) {
            bonus = MAX_ENEMIES_BONUS;
        }
        soldierWeight += BASE_SOLDIER_WEIGHT + bonus;
    }
    
    private static final double RESOURCE_PENALTY = 0.0002;
    private static final double BASE_BUILDER_WEIGHT = 0;
    private static final double MAX_RESOURCE_PENALTY = 1.5;

    public double builderWeightMultiplier() throws GameActionException {
        int roundNum = rc.getRoundNum();
        if (roundNum <= 50) {
            return roundNum / 50.0;
        } else {
            return 1;
        }
    }
    
    public void updateBuilderWeight(double totalResources) throws GameActionException {
        double penalty = RESOURCE_PENALTY * totalResources;
        if (penalty > MAX_RESOURCE_PENALTY) {
            penalty = MAX_RESOURCE_PENALTY;
        }
        builderWeight += builderWeightMultiplier() * (BASE_BUILDER_WEIGHT - penalty);
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
        switch (type) {
            case MINER:
                Resource r = Communications.readResourceData(rc);
                if (r != null) {
                    d = Navigation.navigate(rc, rc.getLocation(), r.location);
                    if (d == null) {
                        d = rc.getLocation().directionTo(r.location);
                    }
                } else {
                    d = getDirectionOfLeastRubble();
                }
                break;
            case SOLDIER:
                if (nearestEnemy != null) {
                    d = Navigation.navigate(rc, rc.getLocation(), nearestEnemy);
                    if (d == null) {
                        d = rc.getLocation().directionTo(nearestEnemy);
                    }
                } else {
                    Resource s = Communications.readEnemiesData(rc);
                    if (s != null) {
                        d = Navigation.navigate(rc, rc.getLocation(), s.location);
                        if (d == null) {
                            d = rc.getLocation().directionTo(s.location);
                        }
                    } else {
                        d = getDirectionOfLeastRubble();
                    }
                }
                break;
            case BUILDER:
                if (nearestEnemy != null) {
                    d = nearestEnemy.directionTo(rc.getLocation());
                } else {
                    d = getDirectionOfLeastRubble();
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
        if (enemiesNearby && type == RobotType.SOLDIER) {
            Communications.maxArchonPriority(rc);
        } else {
            Communications.incrementArchonPriority(rc);
        }
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
