package parrot3;

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

    private static final int PRODUCE_SOLDIERS_BEFORE_BUILDER = 1;
    private static final int PRODUCE_MINERS_BEFORE_BUILDER = 2;

    private static double incomeAverage = 0;
    private static final double incomeAverageFactor = 0.95;
    private static boolean minerProducedBeforeLab = false;

    int soldiersProduced;
    int minersProduced;

    MapLocation allyLocation;

    boolean portable;

    enum ThreatResponse {
        RUN,
        FIGHT
    }

    static ThreatResponse response = null;

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
        minersProduced = 0;
        portable = false;
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources();
        updateWeights();
        tryActivate();
        switch (rc.getMode()) {
            case TURRET:
                portable = false;
                boolean built = tryBuild();
                boolean repaired = tryRepair();
                
                if (shouldBecomePortable() && rc.canTransform()) {
                    findAllyLocation();
                    if (allyLocation != null) {
                        rc.transform();
                        Communications.zeroArchonPriority(rc);
                        portable = true;
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
        Communications.writeArchonData(rc, portable, response == ThreatResponse.FIGHT);
        //rc.setIndicatorString(Communications.archonNum + ", prty:" + Communications.archonPriority);
        /*rc.setIndicatorString(Communications.readArchonPriority(rc, 0) + " " 
                + Communications.readArchonPriority(rc, 1) + " "
                + Communications.readArchonPriority(rc, 2) + " "
                + Communications.readArchonPriority(rc, 3) + " "
                + Communications.countHigherPriorityArchons(rc) + " "
                + rc.getTeamLeadAmount(rc.getTeam()));*/

        //rc.setIndicatorString("shouldBuild: " + shouldBuild() + "builders:" + Communications.getBuilderCount(rc) + "labs: " + Communications.getLabCount(rc) +  "exploring: " + exploring + "den: " + dangerousEnemiesNearby);
        //rc.setIndicatorString("portable archons: " + Communications.numPortableArchons(rc) + " port: " + portable);
        rc.setIndicatorString("target labs: " + Communications.getTargetLabs(rc) + "is active: " + activeArchon + "income: " + Communications.getIncome(rc) + " threat: " + response + " bilo: " + buildIfLeadOver);
    }

    public void updateIncomeAverage() throws GameActionException {
        incomeAverage = incomeAverageFactor * incomeAverage + (1 - incomeAverageFactor) * Communications.getIncome(rc);
    }
    
    int buildIfLeadOver;
    public static int DEFEND_LEAD = 225;
    public static int MIN_TURN_SAVE_LEAD_FOR_DEFENSE = 10;
    public void calculateShouldBuildLead() throws GameActionException {
        buildIfLeadOver = 0;
        boolean isWaitingForLab = Communications.getBuilderCount(rc) > 0 && Communications.getLabCount(rc) < Communications.getTargetLabs(rc);
        buildIfLeadOver += isWaitingForLab ? 180 : 0;
        boolean isOtherArchonThreatened = Communications.numThreatenedArchons(rc) > 0;
        if (response != ThreatResponse.FIGHT && isOtherArchonThreatened && rc.getRoundNum() > MIN_TURN_SAVE_LEAD_FOR_DEFENSE) {
            buildIfLeadOver += DEFEND_LEAD;
        }
        if (response != ThreatResponse.FIGHT) {
            buildIfLeadOver += Communications.countHigherPriorityArchons(rc) * 75;
        }
    }

    public final int TURNS_IDLE = 20;
    public static final int LOW_LEAD = 25;
    public boolean shouldBecomePortable() throws GameActionException {
        return response == ThreatResponse.RUN || (turnsIdled > TURNS_IDLE && rc.getTeamLeadAmount(rc.getTeam()) < LOW_LEAD * (Communications.countHigherPriorityArchons(rc) + 1) && Communications.numPortableArchons(rc) == 0 && response != ThreatResponse.FIGHT);
    }

    public static final int BECOME_TURRET_LEAD = 150;
    public static final int MAX_PORTABLE_TURNS = 50;
    public static final int HIGH_LEAD = 75;
    int portableTurns;

    public boolean shouldBecomeTurret() throws GameActionException {
        return response == ThreatResponse.FIGHT || isRepairableRobot() || rc.getTeamLeadAmount(rc.getTeam()) >= HIGH_LEAD * (1 + Communications.countHigherPriorityArchons(rc)) || (portableTurns > MAX_PORTABLE_TURNS && !enemiesNearby);
    }

    public static final double TARGET_LABS_INCOME_RATIO = 2;
    public static final double INCOME_SUB = 3;

    public void tryActivate() throws GameActionException {
        if(activeArchon) {
            Communications.updateMinerCount(rc);
            Communications.clearOtherArchonData(rc);
            Communications.updateLabCount(rc);
            Communications.updateBuilderCount(rc);
            Communications.writeArchonActivate(rc);
            updateIncome();
            updateIncomeAverage();
            if (rc.getRoundNum() % 20 == 0) {
                Communications.clearExplore(rc);
            }

            int labs = Communications.getTargetLabs(rc);
            int target = (int) ((incomeAverage - INCOME_SUB) / TARGET_LABS_INCOME_RATIO);
            target = target < 1 ? 1 : target;
            if (labs < target && minerProducedBeforeLab) {
                Communications.incrementTargetLabs(rc);
                minerProducedBeforeLab = false;
            } else if (labs > target) {
                Communications.setTargetLabs(rc, target);
            }
        }
        else {
            if(Communications.shouldActivate(rc)) {
                activeArchon = true;
                tryActivate();
                /*Communications.updateMinerCount(rc);
                Communications.clearOtherArchonData(rc);
                Communications.updateLabCount(rc);
                Communications.updateBuilderCount(rc);
                updateIncome();*/
            }

            updateIncomeAverage();
        }
    }

    public void updateIncome() throws GameActionException {
        /*int leadTotal = rc.getTeamLeadAmount(rc.getTeam());
        int income = leadTotal - previousLead;
        previousLead = leadTotal;*/

        Communications.updateIncome(rc);
    }

    private static final double PANIC_SOLDIER_WEIGHT = 0.5;

    RobotInfo[] nearbyRobots;
    MapLocation nearestEnemy;
    boolean enemiesNearby;
    boolean dangerousEnemiesNearby;
    int danger;
    int allies;
    static final int SOLDIER_DANGER = 3;
    static final int SAGE_DANGER = 6;
    static final int WATCHTOWER_DANGER = 6;
    static final int ARCHON_DANGER = 3;
    static int threatenedTurns = 0;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        enemiesNearby = false;
        dangerousEnemiesNearby = false;
        nearestEnemy = null;
        int enemyDist = Integer.MAX_VALUE;
        int dist;
        allies = 0;
        danger = 0;
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == rc.getTeam()) {
                switch (nearbyRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        allies++;
                }
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
                        danger += SOLDIER_DANGER;
                        dangerousEnemiesNearby = true;
                        break;
                    case SAGE:
                        danger += SAGE_DANGER;
                        dangerousEnemiesNearby = true;
                        break;
                    case WATCHTOWER:
                        danger += WATCHTOWER_DANGER;
                        dangerousEnemiesNearby = true;
                        break;
                    case ARCHON:
                        danger += ARCHON_DANGER;
                        dangerousEnemiesNearby = true;
                        break;
                }
            }
        }
        processAndBroadcastEnemies(nearbyRobots);
        if (enemiesNearby) {
            soldierWeight += PANIC_SOLDIER_WEIGHT;
        }

        if (dangerousEnemiesNearby) {
            if (response == null) {
                if (((incomeAverage * rc.getHealth()) / 75 > danger * danger
                            && Communications.numThreatenedArchons(rc) == 0)
                        || Communications.getArchonCount(rc) < Communications.numPortableArchons(rc) + (portable? 0 : 1)
                        || allies > 0) {
                    response = ThreatResponse.FIGHT;
                } else {
                    response = ThreatResponse.RUN;
                }
            } else {
                switch (response) {
                    case RUN:
                        if (Communications.numPortableArchons(rc) == Communications.getArchonCount(rc)) {
                            response = ThreatResponse.FIGHT;
                        }
                        break;
                    case FIGHT:
                        if (Communications.getArchonCount(rc) < Communications.numPortableArchons(rc) + 1 && danger * danger > (incomeAverage * rc.getHealth()) / 75 && allies == 0) {
                            response = ThreatResponse.RUN;
                        }
                        break;
                }
            }
            threatenedTurns++;
        } else {
            response = null;
            threatenedTurns = 0;
        }
    }

    public void updateWeights() throws GameActionException {
        double totalResources = Communications.readTotalResources(rc);
        double totalEnemies = Communications.readTotalEnemies(rc);
        updateMinerWeight(totalResources);
        updateSoldierWeight(totalEnemies);
        updateBuilderWeight(totalResources);
        if (exploring && Communications.getCurrMinerCount(rc) < explorationMiners) {
            if (!enemiesNearby) {
                Resource r = Communications.readEnemiesData(rc);
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

    private static final double RESOURCE_WEIGHT = 0.0001;
    private static final double BASE_MINER_WEIGHT = 0.4;
    private static final double MAX_RESOURCE_BONUS = 0.8;
    private static final double NOT_ENOUGH_MINERS_BONUS = 1;
    private static final double MAKE_SOLDIERS_THRESHOLD = 2;
    private static final int MINERS_PER_LAB = 2;

    public void updateMinerWeight(double totalResources) throws GameActionException {
        double bonus = RESOURCE_WEIGHT * totalResources;
        if (bonus > MAX_RESOURCE_BONUS) {
            bonus = MAX_RESOURCE_BONUS;
        }
        int numMiners = Communications.getCurrMinerCount(rc);
        if (numMiners < explorationMiners || numMiners < MINERS_PER_LAB * Communications.getLabCount(rc)) {
            bonus += NOT_ENOUGH_MINERS_BONUS;
        }
        minerWeight += BASE_MINER_WEIGHT + bonus;
    }

    private static final double ENEMY_WEIGHT = 0.1;
    private static final double BASE_SOLDIER_WEIGHT = 1;
    private static final double MAX_ENEMIES_BONUS = 0.5;

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
        if (Communications.getBuilderCount(rc) < BUILDERS && Communications.getLabCount(rc) < Communications.getTargetLabs(rc) && minersProduced >= PRODUCE_MINERS_BEFORE_BUILDER) {
            if (!dangerousEnemiesNearby || Communications.getArchonCount(rc) <= Communications.numPortableOrThreatenedArchons(rc))
            builderWeight += BASE_BUILDER_WEIGHT;
        } else {
            builderWeight = 0;
        }
    }
    
    public static final int MOST_EXPENSIVE_UNIT_PRICE_LEAD = 75;
    static final int MAX_HOLD_TURNS = 40;
    public boolean tryBuild() throws GameActionException {
        calculateShouldBuildLead();

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

        if (rc.getTeamLeadAmount(rc.getTeam()) >= 1000 && Communications.getBuilderCount(rc) < 3) {
            type = RobotType.BUILDER;
        }

        if (rc.getTeamLeadAmount(rc.getTeam()) - type.buildCostLead < buildIfLeadOver
                || (type == RobotType.SAGE && response != ThreatResponse.FIGHT && Communications.numThreatenedArchons(rc) > 0)) {
            Communications.incrementArchonPriority(rc);
            return false;
        }

        if (response == ThreatResponse.FIGHT) {
            if (allies == 0 && rc.getTeamLeadAmount(rc.getTeam()) < DEFEND_LEAD && threatenedTurns < MAX_HOLD_TURNS && rc.getHealth() > 90) {
                Communications.incrementArchonPriority(rc);
                return false;
            }
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

        if (tryBuild(type, d)) {
            switch (type) {
                case MINER:
                    minerWeight *= WEIGHT_DECAY;
                    minerProducedBeforeLab = true;
                    minersProduced++;
                    Communications.incrementMinerCount(rc);
                    break;
                case SOLDIER:
                    soldierWeight *= WEIGHT_DECAY;
                    soldiersProduced++;
                    break;
                case BUILDER:
                    builderWeight *= WEIGHT_DECAY;
                    Communications.incrementBuilderCount(rc);
                    break;
                case SAGE:
                    soldierWeight *= WEIGHT_DECAY;
                    break;
            }
            Communications.zeroArchonPriority(rc);
            return true;
        }

        switch (type) {
            case MINER:
                minerWeight /= WEIGHT_DECAY;
                break;
            case SOLDIER:
                soldierWeight /= WEIGHT_DECAY;
                break;
            case BUILDER:
                builderWeight /= WEIGHT_DECAY;
                break;
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
        MapLocation archonCOM = Communications.getArchonCOM(rc);
        if (archonCOM.distanceSquaredTo(rc.getLocation()) > MOVE_DISTANCE_THRESHOLD) {
            allyLocation = archonCOM;
            return;
        }
        allyLocation = null;
    }

    public boolean tryMove() throws GameActionException {
        if (response == ThreatResponse.RUN) {
            MapLocation nearestArchon = Communications.getClosestArchon(rc);
            if (nearestArchon != null) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), allyLocation);
                if (d != null && rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
        }
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
