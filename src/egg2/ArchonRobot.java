package egg2;

import battlecode.common.*;

public strictfp class ArchonRobot extends Robot {

    private double minerWeight;
    private double soldierWeight;
    private double builderWeight;

    private static final double WEIGHT_DECAY = 0.8;

    public ArchonRobot(RobotController rc) {
        super(rc);
        minerWeight = 1;
        soldierWeight = 0;
        builderWeight = 0;
    }

    @Override
    public void run() throws GameActionException {
        updateWeights();

        tryBuild();
    }

    private static final double RESOURCE_WEIGHT = 0.001;
    private static final double BASE_MINER_WEIGHT = 0.4;
    private static final double MAX_RESOURCE_BONUS = 0.5;

    public void updateWeights() throws GameActionException {
        updateMinerWeight();
        updateSoldierWeight();
        updateBuilderWeight();
    }

    public void updateMinerWeight() throws GameActionException {
        double totalResources = Communications.readTotalResources(rc);
        double bonus = RESOURCE_WEIGHT * BASE_MINER_WEIGHT;
        if (bonus > MAX_RESOURCE_BONUS) {
            bonus = MAX_RESOURCE_BONUS;
        }
        minerWeight += BASE_MINER_WEIGHT + bonus;
    }

    private static final double ENEMY_WEIGHT = 0.05;
    private static final double BASE_SOLDIER_WEIGHT = 0.6;
    private static final double MAX_ENEMIES_BONUS = 1;

    public void updateSoldierWeight() throws GameActionException {
        double totalEnemies = Communications.readTotalEnemies(rc);
        double bonus = ENEMY_WEIGHT * totalEnemies;
        if (bonus > MAX_ENEMIES_BONUS) {
            bonus = MAX_ENEMIES_BONUS;
        }
        soldierWeight += BASE_SOLDIER_WEIGHT + bonus;
    }

    private static final double BASE_BUILDER_WEIGHT = 0.4;
    
    public void updateBuilderWeight() throws GameActionException {
        builderWeight += BASE_BUILDER_WEIGHT;
    }

    public boolean tryBuild() throws GameActionException {
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

        if (tryBuild(type)) {
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
            return true;
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
}
