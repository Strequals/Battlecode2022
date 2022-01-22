package trex;

import battlecode.common.*;

public strictfp class ArchonRobot extends Robot {

    private double minerWeight;
    private double soldierWeight;
    private double builderWeight;
    private boolean hasMovedAfterBecomingPortable;
    private int previousLead = 200;
    private boolean activeArchon = false;
    private boolean exploring;
    private int turnsIdled;
    
    private int explorationMiners;
    private static final int BASE_MIN_MINER = 2;
    private static final double MINER_RATIO = 0.002;

    private static final double WEIGHT_DECAY = 0.5;

    private static final int PRODUCE_SOLDIERS_BEFORE_BUILDER = 2;

    int soldiersProduced;

    MapLocation allyLocation;

    public ArchonRobot(RobotController rc) throws GameActionException {
        super(rc);
        minerWeight = 16;
        soldierWeight = -8;
        builderWeight = 0;
        Communications.calculateArchonNumber(rc);
        explorationMiners = (int) (MINER_RATIO * rc.getMapWidth() * rc.getMapHeight()) + BASE_MIN_MINER;
        exploring = true;
        portableTurns = 0;
        turnsIdled = 0;
        soldiersProduced = 0;
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        updateWeights();
        tryActivate();
        switch (rc.getMode()) {
            case TURRET:
                boolean built = shouldBuild() && tryBuild();
                rc.setIndicatorString("shouldbuild?" + shouldBuild() + "built: " + built + "hpa: " + Communications.countHigherPriorityArchons(rc));
                boolean repaired = tryRepair();
                
                if (shouldBecomePortable() && rc.canTransform()) {
                    findAllyLocation();
                    if (allyLocation != null) {
                        rc.transform();
                        Communications.zeroArchonPriority(rc);
                        portableTurns = 0;
                    }
                }

                if (!built && !repaired) {
                    turnsIdled++;
                }
                break;
            case PORTABLE:
                findAllyLocation();
                if ((allyLocation == null || shouldBecomeTurret()) && !tryMoveToLowerRubble() && rc.canTransform()) {
                    rc.transform();
                    Communications.zeroArchonPriority(rc);
                } else {
                    tryMove();
                    if (allyLocation == null) {
                        if (rc.canTransform()) {
                            rc.transform();
                            turnsIdled = 0;
                        }
                    }
                    portableTurns++;
                }
                break;
        }
        Communications.writeArchonPriority(rc);
        Communications.writeArchonData(rc);
        //rc.setIndicatorString(Communications.archonNum + ", prty:" + Communications.archonPriority);
        /*rc.setIndicatorString(Communications.readArchonPriority(rc, 0) + " " 
                + Communications.readArchonPriority(rc, 1) + " "
                + Communications.readArchonPriority(rc, 2) + " "
                + Communications.readArchonPriority(rc, 3) + " "
                + Communications.countHigherPriorityArchons(rc) + " "
                + rc.getTeamLeadAmount(rc.getTeam()));*/

        //rc.setIndicatorString("shouldBuild: " + shouldBuild() + "builders:" + Communications.getBuilderCount(rc) + "labs: " + Communications.getLabCount(rc) +  "exploring: " + exploring + "den: " + dangerousEnemiesNearby);
    }

    public boolean shouldBuild() {
        boolean isWaitingForLab = Communications.getBuilderCount(rc) > 0 && Communications.getLabCount(rc) < LABS;
        return (!isWaitingForLab || rc.getTeamGoldAmount(rc.getTeam()) >= 20 || rc.getTeamLeadAmount(rc.getTeam()) >= 225);
    }

    public final int TURNS_IDLE = 20;
    public static final int LOW_LEAD = 25;
    public boolean shouldBecomePortable() throws GameActionException {
        return turnsIdled > TURNS_IDLE && rc.getTeamLeadAmount(rc.getTeam()) < LOW_LEAD * (Communications.countHigherPriorityArchons(rc) + 1);
    }

    public static final int BECOME_TURRET_LEAD = 150;
    public static final int MAX_PORTABLE_TURNS = 50;
    public static final int HIGH_LEAD = 75;
    int portableTurns;

    public boolean shouldBecomeTurret() throws GameActionException {
        return isRepairableRobot() || rc.getTeamLeadAmount(rc.getTeam()) >= HIGH_LEAD * (1 + Communications.countHigherPriorityArchons(rc)) || (portableTurns > MAX_PORTABLE_TURNS && !enemiesNearby);
    }

    public void tryActivate() throws GameActionException {
        if(activeArchon) {
            Communications.updateMinerCount(rc);
            Communications.clearOtherArchonData(rc);
            Communications.updateLabCount(rc);
            Communications.updateBuilderCount(rc);
            // updateIncome();
        }
        else {
            int difference = Communications.getCurrMinerCount(rc) - Communications.getPrevMinerCount(rc);
            int archonCount = rc.getArchonCount();
            if(difference > archonCount || Communications.getCurrMinerCount(rc) > rc.getRobotCount() - archonCount) {
                activeArchon = true;
                Communications.updateMinerCount(rc);
                Communications.clearOtherArchonData(rc);
                Communications.updateLabCount(rc);
                Communications.updateBuilderCount(rc);
                // updateIncome();
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
    boolean enemiesNearby;
    boolean dangerousEnemiesNearby;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        enemiesNearby = false;
        dangerousEnemiesNearby = false;
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
                switch (nearbyRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                    case ARCHON:
                        dangerousEnemiesNearby = true;
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
    private static final double NOT_ENOUGH_MINERS_BONUS = 0.6;
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
    
    private static final double BASE_BUILDER_WEIGHT = 10;

    public static final int BUILDERS = 1;
    public static final int LABS = 1;
    public void updateBuilderWeight(double totalResources) throws GameActionException {
        if (Communications.getBuilderCount(rc) < BUILDERS && Communications.getLabCount(rc) < LABS && !exploring && soldiersProduced >= PRODUCE_SOLDIERS_BEFORE_BUILDER) {
            builderWeight += BASE_BUILDER_WEIGHT;
        } else {
            builderWeight = 0;
        }
    }
    
    public static final int MOST_EXPENSIVE_UNIT_PRICE_LEAD = 75;
    public boolean tryBuild() throws GameActionException {
        int hpa = Communications.countHigherPriorityArchons(rc);
        if (hpa > 0 && (1 + Communications.countHigherPriorityArchons(rc)) * MOST_EXPENSIVE_UNIT_PRICE_LEAD
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

        if (rc.getTeamGoldAmount(rc.getTeam()) >= 20) {
            type = RobotType.SAGE;
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
            case SAGE:
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
                break;
        }

        rc.setIndicatorString("tried building " + type + " in direction " + d);

        if (tryBuild(type, d)) {
            switch (type) {
                case MINER:
                    minerWeight *= WEIGHT_DECAY;
                    Communications.correctIncome(rc, 50);
                    break;
                case SOLDIER:
                    soldierWeight *= WEIGHT_DECAY;
                    Communications.correctIncome(rc, 75);
                    soldiersProduced++;
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
        MapLocation repairable = null;
        if (dangerousEnemiesNearby) {
            repairable = identifyRepairableRobotWhileUnderAttack();
        } else {
            repairable = identifyRepairableRobot();
        }
        if (repairable != null && rc.canRepair(repairable)) {
            rc.repair(repairable);
            return true;
        }

        return false;
        
    }

    public boolean isRepairableRobot() throws GameActionException {
        MapLocation current = rc.getLocation();
        Team team = rc.getTeam();
        for (RobotInfo otherRobot : nearbyRobots) {
            switch (otherRobot.type) {
                case SOLDIER:
                case SAGE:
                    if (otherRobot.team == team
                        && current.isWithinDistanceSquared(otherRobot.location, RobotType.ARCHON.actionRadiusSquared)
                        && otherRobot.health < otherRobot.type.getMaxHealth(otherRobot.level)) {
                        return true;
                    }
            }
        }
        return false;
    }

    public static final int CRITICAL_HEALTH = 10;

    public MapLocation identifyRepairableRobot() throws GameActionException {
        MapLocation best = null;
        int health = Integer.MIN_VALUE;
        boolean canAttack = false;
        Team team = rc.getTeam();
        boolean isCritical = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.mode == RobotMode.DROID) {
                if (otherRobot.team == team
                        && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.ARCHON.actionRadiusSquared)
                        && otherRobot.health < otherRobot.type.getMaxHealth(otherRobot.level)) {
                    if (otherRobot.type.canAttack()
                            && otherRobot.mode.canAct) {
                        if (otherRobot.health > health && !isCritical) {
                            health = otherRobot.health;
                            canAttack = true;
                            best = otherRobot.location;
                        } else if (otherRobot.health < CRITICAL_HEALTH && (!isCritical || otherRobot.health < health)) {
                            isCritical = true;
                            health = otherRobot.health;
                            canAttack = true;
                            best = otherRobot.location;
                        }
                    } else if (!canAttack) {
                        if (otherRobot.health > health) {
                            health = otherRobot.health;
                            best = otherRobot.location;
                        }
                    }
                }
            }
        }
        return best;
    }

    public MapLocation identifyRepairableRobotWhileUnderAttack() throws GameActionException {
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        Team team = rc.getTeam();
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.mode == RobotMode.DROID) {
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
        }
        return best;
    }
    
    public static final int ALLY_THRESHOLD = 1;
    public static final int MOVE_DISTANCE_THRESHOLD = 20;
    public void findAllyLocation() throws GameActionException {
        Resource allies = Communications.readAlliesData(rc);
        if (allies.value >= ALLY_THRESHOLD) {
            if (allies.location.distanceSquaredTo(rc.getLocation()) > MOVE_DISTANCE_THRESHOLD) {
                allyLocation = allies.location;
                return;
            }
        }
        allyLocation = null;
    }

    public boolean tryMove() throws GameActionException {
        if (allyLocation != null) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), allyLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }

        return tryMoveToLowerRubble();
    }

    public boolean tryMoveToLowerRubble() throws GameActionException {
        Direction d;
        if (allyLocation != null) {
            d = getBiasedDirectionOfLeastRubble(rc.getLocation().directionTo(allyLocation));
        } else {
            d = getDirectionOfLeastRubble();
        }
        if (d != null) {
            MapLocation to = rc.getLocation().add(d);
            if (rc.senseRubble(to) < rc.senseRubble(rc.getLocation())) {
                if (rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
        }
        return false;
    }


}
