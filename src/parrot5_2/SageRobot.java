package parrot5_2;

import battlecode.common.*;

public strictfp class SageRobot extends Robot {
    private enum AttackType {
        REGULAR,
        ABYSS,  // destroy resources
        CHARGE,  // droids
        FURY // buildings
    }

    private class Attack {
        MapLocation loc;
        AttackType type;
        double score;

        public Attack(MapLocation m, AttackType a, double s) {
            loc = m;
            type = a;
            score = s;
        }

        public void execute() throws GameActionException {
            // rc.setIndicatorString(rc.getLocation().toString() + " " + loc.toString());
            switch(type) {
                case REGULAR: 
                    if(rc.canAttack(loc)) {
                        rc.attack(loc);
                    }
                    break;
                case ABYSS:
                    if(rc.canEnvision(AnomalyType.ABYSS)) {
                        rc.envision(AnomalyType.ABYSS);
                    }
                    break;
                case CHARGE:
                    if(rc.canEnvision(AnomalyType.CHARGE)) {
                        rc.envision(AnomalyType.CHARGE);
                    }
                    break;
                case FURY:
                    if(rc.canEnvision(AnomalyType.FURY)) {
                        rc.envision(AnomalyType.FURY);
                    }
                    break;
            }
        }
    }

    MapLocation targetLocation;
    double locationScore;

    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;
    static final double NON_DAMAGING_MULT = 0.33;  // multiplier to score for damaging a miner
    static final double LEAD_VALUE = 0.5;  // value of lead compared to damage
    static final int GIVE_UP_RADIUS_SQUARED = 2;
    // static final int THREAT_THRESHOLD = 6;
    static final int FRIENDLY_DAMAGE_MULT = 1000;  // multiplier to penalty for damaging friendly buildings
    static final int MIN_SCORE = 10;
    static final boolean DENSITY_PREDICTION = false; 

    static int goArchonSymmetry = 0; // 0 is diagonal, 1 is horizontal, 2 is vertical, 3 go random
    public static MapLocation spawnedArchonLoc;
    boolean firstTurn = true;
    static final int GO_ARCHON_ROUND = 100;

    public SageRobot(RobotController rc) {
        super(rc);
    }

    public static final int ATTACK_DANGEROUS_RUBBLE = 25;
    public static final int HEAL_HEALTH = 46;
    public static final int MINER_BOOST = 15; // 20% of 75 lead of a s

    int turnsNotAttacked = 0;

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        broadcastNearbyResources(isMinerNearby || !areDangerousEnemies ? 0 : MINER_BOOST);

        if(DENSITY_PREDICTION) {
            calculateEnemyDensity();
        }

        /*Direction moved = tryMove();
        boolean attacked = tryAttack();
        if (!areEnemiesNearby && !moved && !attacked && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }*/
        Attack before = rc.isActionReady() ? tryAttack() : null;
        Attack after = null;
        MapLocation myLoc = rc.getLocation();
        int currentRubble = rc.senseRubble(myLoc);
        int nextRubble;
        Direction moveDir = tryMove();
        rc.setIndicatorString("tryMove is " + moveDir);
        if(moveDir != null) {
            /*Direction[] dirs = {moveDir, moveDir.rotateLeft(), moveDir.rotateRight()};
            if(rc.isMovementReady() && rc.isActionReady()) {
                for(Direction di: dirs) {
                    if(rc.canMove(di)) {
                        nextRubble = rc.senseRubble(myLoc.add(di));
                        if (nextRubble - currentRubble <= MAX_RUBBLE_INCREASE) {
                            Attack test = tryAttack(myLoc.add(di));
                            if(after == null || (test != null && test.score > after.score)) {
                                after = test;
                            }
                        }
                    }
                }
                if(after != null) {
                    if (before == null || before.score <= after.score) {
                        moveDir = rc.getLocation().directionTo(after.loc);
                    }
                }
            }*/
            after = tryAttack(myLoc.add(moveDir));
            if(rc.canMove(moveDir)) {
                // Attack after = rc.isActionReady() ? tryAttack(rc.getLocation().add(moveDir)) : null;
                if (before != null) {
                    if (after != null) {
                        if (before.score > after.score) {
                            before.execute();
                            rc.move(moveDir);
                        } else {
                            rc.move(moveDir);
                            after = tryAttack();
                            if (after == null) System.out.println("???");
                            if (after != null) after.execute();
                        }
                    } else {
                        before.execute();
                        rc.move(moveDir);
                    }
                } else {
                    if (after != null) {
                        rc.move(moveDir);
                        after = tryAttack();
                        if (after == null) System.out.println("???");
                        if (after != null) after.execute();
                    } else {
                        rc.move(moveDir);
                    }
                }

            }
        } else {
            if(before != null) {
                before.execute();
            }
        }

        if (rc.isActionReady() && nearestEnemy != null) {
            turnsNotAttacked++;
        } else {
            turnsNotAttacked = 0;
        }

        // update again after attack if movement ready
        /*if(rc.isMovementReady()) {
            moveDir = tryMove();
            if(moveDir != null) {
                if(rc.canMove(moveDir)) {
                    rc.move(moveDir);
                }
            }
        }*/

        if (!areEnemiesNearby && (moveDir == null) && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }

        //rc.setIndicatorString("target: " + targetLocation + ", score: " + locationScore + "tssde: " + turnsSinceSeenDangerousEnemy);
        //rc.setIndicatorLine(rc.getLocation(), targetLocation, 255, 255, 0);
    }

    RobotInfo[] enemies;
    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areDangerousEnemies;
    MapLocation friendlyArchonPos;
    boolean friendlyArchonNearby;
    boolean enemyArchonNearby;
    boolean enemyMinerNearby;
    boolean areAttackableDangerousEnemies;
    MapLocation nearestAlly;
    MapLocation nearestEnemy;
    MapLocation fleeFrom;
    int fleeAttackRadius;
    int maxDamageTaken;
    int allies;
    int turnsSinceSeenDangerousEnemy = 0;
    boolean isMinerNearby;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        enemies = rc.senseNearbyRobots(34, rc.getTeam().opponent());
        fleeFrom = null;
        areEnemiesNearby = false;
        areDangerousEnemies = false;
        friendlyArchonNearby = false;
        friendlyArchonPos = null;
        int fleeDistanceSquared = Integer.MAX_VALUE;
        nearestAlly = null;
        int nearestAllyDistance = Integer.MAX_VALUE;
        nearestEnemy = null;
        int nearestEnemyDistance = Integer.MAX_VALUE;
        int dist;
        Team team = rc.getTeam();
        MapLocation current = rc.getLocation();
        maxDamageTaken = 0;
        int dmg;
        allies = 0;
        isMinerNearby = false;
        areAttackableDangerousEnemies = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == team) {
                switch (otherRobot.type) {
                    case ARCHON:
                        if (current.isWithinDistanceSquared(otherRobot.location, RobotType.ARCHON.actionRadiusSquared)) {
                            friendlyArchonNearby = true;
                        }
                        friendlyArchonPos = otherRobot.location;
                        if (firstTurn) {
                            spawnedArchonLoc = friendlyArchonPos;
                            firstTurn = false;
                        }
                        break;
                    case SOLDIER:
                    case WATCHTOWER:
                    case SAGE:
                        dist = current.distanceSquaredTo(otherRobot.location);
                        if (dist < nearestAllyDistance) {
                            nearestAllyDistance = dist;
                            nearestAlly = otherRobot.location;
                        }
                        allies++;
                        break;
                    case MINER:
                        isMinerNearby = true;
                        break;
                }
            } else {
                areEnemiesNearby = true;
                if (otherRobot.type.canAttack() && otherRobot.mode.canAct) {
                    // condition may need changing if canAttack also involves healing
                    MapLocation attackedFrom;
                    if (otherRobot.mode == RobotMode.TURRET) {
                        attackedFrom = otherRobot.location;
                    } else {
                        attackedFrom = otherRobot.location.add(otherRobot.location.directionTo(current));
                    }
                    int dsq = attackedFrom.distanceSquaredTo(current);
                    if (dsq < fleeDistanceSquared) {
                        fleeDistanceSquared = dsq;
                        fleeFrom = attackedFrom;
                        fleeAttackRadius = otherRobot.type.actionRadiusSquared;
                    }
                    dmg = otherRobot.type.getDamage(otherRobot.level);
                    if (dmg > 0) maxDamageTaken += dmg;
                }
                int otherDist = otherRobot.location.distanceSquaredTo(current);
                if (otherDist < nearestEnemyDistance) {
                    nearestEnemyDistance = otherDist;
                    nearestEnemy = otherRobot.location;
                }
                switch (otherRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        areDangerousEnemies = true;
                        if (otherRobot.location.isWithinDistanceSquared(current, RobotType.SAGE.actionRadiusSquared)) {
                            areAttackableDangerousEnemies = true;
                        }
                        break;
                    case ARCHON:
                        enemyArchonNearby = true;
                        break;
                    case MINER:
                        enemyMinerNearby = true;
                        break;
                }
            }
        }

        if (areDangerousEnemies) {
            turnsSinceSeenDangerousEnemy = 0;
        } else {
            turnsSinceSeenDangerousEnemy++;
        }

        processAndBroadcastEnemies(nearbyRobots);
        if (areDangerousEnemies) {
            broadcastAllies(rc.getLocation(), allies);
        }
    }

    public Attack tryAttack() throws GameActionException {
        return tryAttack(rc.getLocation());  // bit of a waste but im lazy
    }

    private double enemyDensity = 0;
    public Attack tryAttack(MapLocation loc) throws GameActionException {
        if (rc.isActionReady()) {
            Attack result = score(loc);
            return result;
        }
        return null;
    }
    
    // hp density
    public void calculateEnemyDensity() {
        int totalHP = 0;
        int x_min = 1000;
        int x_max = 0;
        int y_min = 1000;
        int y_max = 0;
        for(RobotInfo robot: enemies) {
            switch(robot.type) {
                case SOLDIER:
                case MINER:
                case SAGE:
                case BUILDER:
                    totalHP += robot.type.getMaxHealth(robot.getLevel());
                    MapLocation enemyLoc = robot.getLocation();
                    if(enemyLoc.x < x_min) {
                        x_min = enemyLoc.x;
                    }
                    if(enemyLoc.x > x_max) {
                        x_max = enemyLoc.x;
                    }
                    if(enemyLoc.y < y_min) {
                        y_min = enemyLoc.y;
                    }
                    if(enemyLoc.y > y_max) {
                        y_max = enemyLoc.y;
                    }
            }
        }

        enemyDensity = totalHP == 0 ? 0 : totalHP / ((x_max - x_min + 1) * (y_max - y_min + 1));
    }

    // kills * KILL_SCORE + damage + 22 * enemyDensity * revealedSquares / (rubble / 10)
    // returns best course of action from loc, or null if there is no good options
    RobotInfo[] enemiesAtLoc;
    int rubbleAtLoc;
    int maxDamageTakenAtLoc;
    final int MINER_DESTROY_BONUS = 5;
    final int BUILDER_DESTROY_BONUS = 5;
    final int SOLDIER_DESTROY_BONUS = 10;
    final int SAGE_DESTROY_BONUS = 10;
    final int WATCHTOWER_DESTROY_BONUS = 15;
    final int LABORATORY_DESTROY_BONUS = 20;
    final int ARCHON_DESTROY_BONUS = 100;

    final int ENEMY_RUBBLE_REDUCT = 30;  // strongly recommend at least 10 for both
    final int FRIENDLY_RUBBLE_REDUCT = 20;  // score is divided by 1 + (rubble / this)

    public Attack score(MapLocation loc) throws GameActionException {
        enemiesAtLoc = rc.senseNearbyRobots(loc, RobotType.SAGE.actionRadiusSquared, rc.getTeam().opponent());
        rubbleAtLoc = rc.senseRubble(loc);
        maxDamageTakenAtLoc = 0;

        Attack best = scoreReg(loc);
        //Attack abyss = scoreAbyss(loc);
        Attack charge = scoreCharge(loc);
        Attack fury = scoreFury(loc);

        /*if(abyss.score > best.score) {
            best = abyss;
        }*/
        if(charge.score > best.score) {
            best = charge;
        }
        if(fury.score > best.score) {
            best = fury;
        }
        return best.score * (1 + rubbleAtLoc / 10) < Math.max(MIN_SCORE - turnsNotAttacked, 3) ? null : best;
    }

    public int destroyBonus(RobotType type) {
        switch (type) {
            case MINER:
                return MINER_DESTROY_BONUS;
            case BUILDER:
                return BUILDER_DESTROY_BONUS;
            case SOLDIER:
                return SOLDIER_DESTROY_BONUS;
            case SAGE:
                return SAGE_DESTROY_BONUS;
            case WATCHTOWER:
                return WATCHTOWER_DESTROY_BONUS;
            case LABORATORY:
                return LABORATORY_DESTROY_BONUS;
            case ARCHON:
                return ARCHON_DESTROY_BONUS;
        }
        return 0;
    }


    public Attack scoreReg(MapLocation loc) throws GameActionException {
        double bestScore = -1;
        MapLocation bestLoc = null;
        MapLocation myLoc = rc.getLocation();
        //int bestHealth = 0;
        //int bestDist = Integer.MAX_VALUE;
        int dsq;
        //int bestRubble = 101;
        //int rubble;
        double score;

        // it's ok if enemiesAtLoc is empty because min score is checked, and default score is 0
        for(RobotInfo enemy: enemiesAtLoc) {
            score = (Math.min(enemy.getHealth(), 45) + (enemy.getHealth() <= 45 ? destroyBonus(enemy.type) : 0)) * (enemy.type.canAttack() ? 1 : NON_DAMAGING_MULT) / (1 + rc.senseRubble(enemy.location) / ENEMY_RUBBLE_REDUCT);
            dsq = enemy.location.distanceSquaredTo(myLoc);
            //rubble = rc.senseRubble(enemy.location);
            if(score > bestScore) {
                bestScore = score;
                bestLoc = enemy.getLocation();
                //bestHealth = enemy.health;
                //bestRubble = rubble;
            }

            if(dsq <= enemy.type.actionRadiusSquared) {
                maxDamageTakenAtLoc += enemy.type.getDamage(enemy.getLevel());
            }
        }

        return new Attack(bestLoc, AttackType.REGULAR, bestScore / (1 + rubbleAtLoc / FRIENDLY_RUBBLE_REDUCT));
    }

    public Attack scoreAbyss(MapLocation loc) {
        if(!friendlyArchonNearby && enemyMinerNearby && !isMinerNearby) {
            return new Attack(loc, AttackType.ABYSS, leadWithinAction * 0.99 * LEAD_VALUE / (1 + rubbleAtLoc / FRIENDLY_RUBBLE_REDUCT));
        }
        else {
            return new Attack(null, null, 0);
        }
    }
    
    final double CHARGE_DAMAGE_PERCENT = 0.22;
    public Attack scoreCharge(MapLocation loc) throws GameActionException {
        MapLocation myLoc = rc.getLocation();
        //double score = (Math.abs(loc.x - myLoc.x) + Math.abs(loc.y - myLoc.y)) * enemyDensity * CHARGE_DAMAGE_PERCENT;
        double score = 0;
        double damage;
        for(RobotInfo enemy: enemiesAtLoc) {
            damage = CHARGE_DAMAGE_PERCENT * enemy.type.getMaxHealth(enemy.level);
            if (damage >= enemy.health) {
                switch (enemy.type) {
                    case MINER:
                    case BUILDER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += enemy.health * NON_DAMAGING_MULT / (1 + rc.senseRubble(enemy.location) / ENEMY_RUBBLE_REDUCT);
                        }
                        break;
                    case SAGE:
                    case SOLDIER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += enemy.health / (1 + rc.senseRubble(enemy.location) / ENEMY_RUBBLE_REDUCT);
                        }
                        break;
                }
            } else {
                switch(enemy.type) {
                    case MINER:
                    case BUILDER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += damage * NON_DAMAGING_MULT / (1 + rc.senseRubble(enemy.location) / ENEMY_RUBBLE_REDUCT);
                        }
                        break;
                    case SAGE:
                    case SOLDIER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += damage / (1 + rc.senseRubble(enemy.location) / ENEMY_RUBBLE_REDUCT);
                        }
                        break;
                }
            }
        }
        return new Attack(loc, AttackType.CHARGE, score / (1 + rubbleAtLoc / FRIENDLY_RUBBLE_REDUCT));
    }
    
    final double FURY_DAMAGE_PERCENT = 0.1;
    public Attack scoreFury(MapLocation loc) {
        double score = 0;
        Team team = rc.getTeam();
        double damage;
        double s;
        for(RobotInfo robot: nearbyRobots) {
            if(robot.getMode() == RobotMode.TURRET && robot.getLocation().distanceSquaredTo(loc) <= 25) {
                damage = FURY_DAMAGE_PERCENT * robot.type.getMaxHealth(robot.level);
                if(robot.team == team) {
                    s = -damage * FRIENDLY_DAMAGE_MULT;
                }
                else {
                    if (damage >= robot.health) {
                        s = robot.health + destroyBonus(robot.type);
                    } else {
                        s = damage;
                    }
                    if (!robot.type.canAttack()) {
                        s *= NON_DAMAGING_MULT;
                    }
                }
                score += s;
            }
        }
        return new Attack(loc, AttackType.FURY, score / (1 + rubbleAtLoc / FRIENDLY_RUBBLE_REDUCT));
    }

    public static final double VALUE_THRESHOLD = 0.1;
    public static final int DISTANCE_THRESHOLD = 100;

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
            RobotInfo r = rc.senseRobotAtLocation(t);
            if (r.type.canAttack()) {
                targetLocation = t;
                locationScore = 2;
            } else if (locationScore < 1) {
                targetLocation = t;
                locationScore = 0.5;
            }
        }

        if (targetLocation == null || locationScore < 1) {
            t = findTarget();
            if (t != null) {
                if (areDangerousEnemies) {
                    targetLocation = t;
                    locationScore = 2;
                } else if (locationScore < 1) {
                    targetLocation = t;
                    locationScore = 0.5;
                }
            }
        }

        if (targetLocation == null || locationScore < 1) {
            Resource r = Communications.readEnemiesData(rc);
            if (r != null && r.value > 0 && (targetLocation == null || r.location.distanceSquaredTo(rc.getLocation()) <= DISTANCE_THRESHOLD)) {
                targetLocation = r.location;
                if (r.value < 5) {
                    locationScore = 0.5;
                } else {
                    locationScore = 2;
                }
                return;
            }
        }

        if (targetLocation == null) {
            if (rc.getRoundNum() < GO_ARCHON_ROUND) {
                switch (goArchonSymmetry) {
                    case 0:
                        targetLocation = MapSymmetry.ROTATIONAL.reflect(rc, spawnedArchonLoc);
                        break;
                    case 1:
                        targetLocation = MapSymmetry.HORIZONTAL.reflect(rc, spawnedArchonLoc);
                        break;
                    case 2:
                        targetLocation = MapSymmetry.VERTICAL.reflect(rc, spawnedArchonLoc);
                        break;
                    default:
                        targetLocation = getRandomLocation();
                        break;
                }
                locationScore = 0.5;
                goArchonSymmetry++;
            } else {
                targetLocation = getRandomLocation();
                locationScore = 0.5;
            }
        }
    }


    public double score(int health, int rubble, int cool, int damage, int enemyDamage) {
        int healthAfter = health - damage;
        //return ((healthAfter > 0 ? (double) healthAfter : 0.000001)) * (int) ((1.0 + rubble / 10.0) * cool) / enemyDamage;
        return ((healthAfter > 0 ? (double) healthAfter : 0.000001)) * cool / enemyDamage;
    }

    /**
     * Targets the opposing enemy with the lowest health, prioritizing units that can attack;
     * returns null if no such enemy is found.
     **/
    public MapLocation identifyTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        double health = Double.MAX_VALUE;
        boolean canAttack = false;
        int id = Integer.MAX_VALUE;
        int damage = rc.getType().getDamage(rc.getLevel());
        double s;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.SAGE.actionRadiusSquared)) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct) {
                    s = score(otherRobot.health, rc.senseRubble(otherRobot.location), otherRobot.type.actionCooldown, damage, otherRobot.type.getDamage(otherRobot.level));
                    if (!canAttack || (s < health
                            || (s == health
                                && otherRobot.ID < id))) {
                        health = s;
                        canAttack = true;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                } else if (!canAttack) {
                    s = otherRobot.health;
                    if (s < health
                            || (s == health
                                && otherRobot.ID < id)) {
                        health = s;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Same as identifyTarget(), but is centered around next.
     **/
    public MapLocation identifyTarget(MapLocation next) throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        double health = Double.MAX_VALUE;
        boolean canAttack = false;
        int id = Integer.MAX_VALUE;
        int damage = rc.getType().getDamage(rc.getLevel());
        double s;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && next.isWithinDistanceSquared(otherRobot.location, RobotType.SAGE.actionRadiusSquared)) {

                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct) {
                    s = score(otherRobot.health, rc.senseRubble(otherRobot.location), otherRobot.type.actionCooldown, damage, otherRobot.type.getDamage(otherRobot.level));
                    if (!canAttack || (s < health
                            || (s == health
                                && otherRobot.ID < id))) {
                        health = s;
                        canAttack = true;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                } else if (!canAttack) {
                    s = otherRobot.health;
                    if (s < health
                            || (s == health
                                && otherRobot.ID < id)) {
                        health = s;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Selects an opposing enemy in vision range by the same criteria as identifyTarget.
     */

    /*public MapLocation findTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        int id = Integer.MAX_VALUE;
        boolean canAttack = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct) {
                    if (otherRobot.health < health
                            || (otherRobot.health == health
                                && otherRobot.ID < id)) {
                        health = otherRobot.health;
                        canAttack = true;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                } else if (!canAttack) {
                    if (otherRobot.health < health
                            || (otherRobot.health == health
                                && otherRobot.ID < id)) {
                        health = otherRobot.health;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                }
            }
        }
        return best;
    }*/

    public MapLocation findTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        double health = Double.MAX_VALUE;
        boolean canAttack = false;
        int id = Integer.MAX_VALUE;
        int damage = rc.getType().getDamage(rc.getLevel());
        double s;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct) {
                    s = score(otherRobot.health, rc.senseRubble(otherRobot.location), otherRobot.type.actionCooldown, damage, otherRobot.type.getDamage(otherRobot.level));
                    if (!canAttack || (s < health
                            || (s == health
                                && otherRobot.ID < id))) {
                        health = s;
                        canAttack = true;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                } else if (!canAttack) {
                    s = otherRobot.health;
                    if (s < health
                            || (s == health
                                && otherRobot.ID < id)) {
                        health = s;
                        best = otherRobot.location;
                        id = otherRobot.ID;
                    }
                }
            }
        }
        return best;
    }


    public boolean isAllyInRange(MapLocation l, int dsq) {
        Team team = rc.getTeam();
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team) {
                switch (nearbyRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        if (nearbyRobot.location.isWithinDistanceSquared(l, dsq)) {
                            return true;
                        }
                }
            }
        }
        return false;
    }

    public static final int MAX_RUBBLE_INCREASE = 10;
    public static final int TURNS_AVOID_RUBBLE = 6;

    public static final int FLEE_WHEN_COOLDOWN_ABOVE = 10;
    public static final int HEAL_WHEN_COOLDOWN_ABOVE = 30;

    public Direction tryMove() throws GameActionException {
        findTargets();

        MapLocation current = rc.getLocation();

        if (!rc.isMovementReady()) return null;

        int currentRubble = rc.senseRubble(current);

        if (!areEnemiesNearby && rc.getHealth() < HEAL_HEALTH && !friendlyArchonNearby && rc.getActionCooldownTurns() >= HEAL_WHEN_COOLDOWN_ABOVE) {
            MapLocation nearestArchon = Communications.getClosestArchon(rc);
            if (nearestArchon != null) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), nearestArchon);
                if (d != null) {
                    MapLocation to = current.add(d);
                    if (d != null && rc.canMove(d) &&
                        (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                         || rc.senseRubble(to) - currentRubble <= MAX_RUBBLE_INCREASE)) {
                        return d;
                    }
                }
            }
        }
        boolean healing = friendlyArchonNearby && rc.getHealth() < RobotType.SAGE.getMaxHealth(rc.getLevel());
        /*if (healing) {
            Direction lowest = getDirectionOfLeastRubbleWithinDistanceSquaredOf(friendlyArchonPos, RobotType.ARCHON.actionRadiusSquared);
            if (lowest != null) {
                MapLocation loc = rc.getLocation().add(lowest);
                if (rc.senseRubble(loc) <= rc.senseRubble(current)) {
                    return lowest;
                }
            }
        }*/

        if (fleeFrom != null
                && (!rc.isActionReady() || areAttackableDangerousEnemies)/* && (!isAllyInRange(fleeFrom, fleeAttackRadius) || maxDamageTaken >= rc.getHealth())*/) {

            Direction fleeDir = Navigation.flee(rc, current, fleeFrom);
            if (fleeDir != null) {
                MapLocation fleeLoc = current.add(fleeDir);
                if (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE || rc.senseRubble(fleeLoc) - currentRubble <= MAX_RUBBLE_INCREASE) {
                    if (!healing || fleeLoc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                        return fleeDir;
                    }
                }
            }
            return null;
        }

        /*if(!rc.isActionReady() && areEnemiesNearby) {
            return false;
        }

        if (rc.getLocation()*/

        int targetDistance = rc.getLocation().distanceSquaredTo(targetLocation);

        boolean engage = areEnemiesNearby && targetDistance > RobotType.SAGE.actionRadiusSquared;

        if (nearestEnemy != null) {
            if (rc.isActionReady()) {
                //Move to lower rubble and keep attacking
                
                //Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, d);
                //Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, Direction.NORTH);
                
                Direction lowest;
                if (nearestAlly != null) {
                    //stick to allies
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SAGE.actionRadiusSquared, current.directionTo(nearestAlly));
                } else {
                    //rotate around enemies to find allies
                    Direction d = nearestEnemy.directionTo(current).rotateLeft();
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SAGE.actionRadiusSquared, d);
                }

                /*if (areDangerousEnemies) {
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, nearestEnemy.directionTo(current));
                } else {
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, current.directionTo(targetLocation));
                }*/
                if (lowest != null) {
                    MapLocation loc = rc.getLocation().add(lowest);
                    if (rc.senseRubble(loc) <= currentRubble) {
                        if (!healing || loc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                            return lowest;
                        }
                    }
                }
            } else {
                //Move to lower rubble to prepare for attacking
                Direction lowest = getDirectionOfLeastRubble();
                if (lowest != null) {
                    MapLocation loc = rc.getLocation().add(lowest);
                    if (rc.senseRubble(loc) <= currentRubble) {
                        if (!healing || loc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                            return lowest;
                        }
                    }
                }
            }
        }
        
        //int movementCooldownToTarget = (int) ((StrictMath.sqrt(current.distanceSquaredTo(targetLocation)) - RobotType.SAGE.actionRadiusSquared) * 25);

        if (!areDangerousEnemies || (rc.isActionReady() && current.distanceSquaredTo(targetLocation) > RobotType.SAGE.actionRadiusSquared)) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null) {
                MapLocation to = current.add(d);
                if (d != null && rc.canMove(d) &&
                        (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                         || rc.senseRubble(to) - currentRubble <= MAX_RUBBLE_INCREASE)) {
                    if (!healing || to.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                        return d;
                    }
                }
            }
        }

        if (healing) {
            Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(friendlyArchonPos, RobotType.ARCHON.actionRadiusSquared, current.directionTo(friendlyArchonPos).rotateRight().rotateRight());
            if (lowest != null) {
                MapLocation loc = rc.getLocation().add(lowest);
                if (rc.senseRubble(loc) <= currentRubble) {
                    if (loc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                        return lowest;
                    }
                }
            }
        }
        return null;
    }

    int leadWithinAction = 0;
    @Override
    public Resource senseAllNearbyResources() throws GameActionException {
        int lead;
        int gold;
        int dsq;
        MapLocation best = null;
        int bestDsq = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;
        MapLocation current = rc.getLocation();
        leadWithinAction = 0;
        int totalLead = 0;
        int totalGold = 0;
        for (MapLocation l : rc.senseNearbyLocationsWithGold(rc.getType().visionRadiusSquared)) {
            gold = rc.senseGold(l);
            dsq = current.distanceSquaredTo(l);
            totalGold += gold;
            if (dsq < bestDsq
                    || (dsq == bestDsq && gold > bestScore)) {
                best = l;
                bestDsq = dsq;
                bestScore = gold;
            }
        }

        if (best == null) {
            for (MapLocation l : rc.senseNearbyLocationsWithLead(rc.getType().visionRadiusSquared)) {
                lead = rc.senseLead(l) - 1;
                dsq = current.distanceSquaredTo(l);
                totalLead += lead;
                if(dsq <= 25) {
                    leadWithinAction += lead + 1;
                }
                if (lead > 0 && (dsq < bestDsq
                        || (dsq == bestDsq && lead > bestScore))) {
                    best = l;
                    bestDsq = dsq;
                    bestScore = lead;
                }
            }
        }
        if (best != null) {
            return new Resource(best, totalLead, totalGold);
        }
        return null;
    }
}
