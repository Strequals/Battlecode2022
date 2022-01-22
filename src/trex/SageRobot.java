package trex;

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
    static final int KILL_POINTS = 20;  // additional score for killing a unit
    static final int MIN_SCORE = 20;
    static final boolean DENSITY_PREDICTION = false; 

    public SageRobot(RobotController rc) {
        super(rc);
    }

    public static final int ATTACK_DANGEROUS_RUBBLE = 25;
    public static final int HEAL_HEALTH = 20;
    public static final int MINER_BOOST = 15; // 20% of 75 lead of a s

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
        Direction moveDir = tryMove();
        if(moveDir != null) {
            if(rc.canMove(moveDir)) {
                Attack after = rc.isActionReady() ? tryAttack(rc.getLocation().add(moveDir)) : null;
                if (before != null) {
                    if (after != null) {
                        if (before.score >= after.score) {
                            before.execute();
                            rc.move(moveDir);
                        } else {
                            rc.move(moveDir);
                            after.execute();
                        }
                    } else {
                        before.execute();
                        rc.move(moveDir);
                    }
                } else {
                    if (after != null) {
                        rc.move(moveDir);
                        after.execute();
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
    }

    RobotInfo[] enemies;
    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areDangerousEnemies;
    MapLocation friendlyArchonPos;
    boolean friendlyArchonNearby;
    boolean enemyArchonNearby;
    boolean enemyMinerNearby;
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
        maxDamageTaken = 0;
        int dmg;
        allies = 0;
        isMinerNearby = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == team) {
                switch (otherRobot.type) {
                    case ARCHON:
                        if (rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.ARCHON.actionRadiusSquared)) {
                            friendlyArchonNearby = true;
                        }
                        friendlyArchonPos = otherRobot.location;
                        break;
                    case SOLDIER:
                    case WATCHTOWER:
                    case SAGE:
                        dist = rc.getLocation().distanceSquaredTo(otherRobot.location);
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
                        attackedFrom = otherRobot.location.add(otherRobot.location.directionTo(rc.getLocation()));
                    }
                    int dsq = attackedFrom.distanceSquaredTo(rc.getLocation());
                    if (dsq < fleeDistanceSquared) {
                        fleeDistanceSquared = dsq;
                        fleeFrom = attackedFrom;
                        fleeAttackRadius = otherRobot.type.actionRadiusSquared;
                    }
                    dmg = otherRobot.type.getDamage(otherRobot.level);
                    if (dmg > 0) maxDamageTaken += dmg;
                }
                int otherDist = otherRobot.location.distanceSquaredTo(rc.getLocation());
                if (otherDist < nearestEnemyDistance) {
                    nearestEnemyDistance = otherDist;
                    nearestEnemy = otherRobot.location;
                }
                switch (otherRobot.type) {
                    case SOLDIER:
                    case SAGE:
                    case WATCHTOWER:
                        areDangerousEnemies = true;
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
    final int SOLDIER_DESTROY_BONUS = 20;
    final int SAGE_DESTROY_BONUS = 30;
    final int WATCHTOWER_DESTROY_BONUS = 30;
    final int LABORATORY_DESTROY_BONUS = 10;
    final int ARCHON_DESTROY_BONUS = 100;
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
        rc.setIndicatorString("best: " + best.score + "charge: " + charge.score + "fury: " + fury.score);
        return best.score < MIN_SCORE ? null : best;
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
        double bestScore = 0;
        MapLocation bestLoc = null;
        MapLocation myLoc = rc.getLocation();

        // it's ok if enemiesAtLoc is empty because min score is checked, and default score is 0
        for(RobotInfo enemy: enemiesAtLoc) {
            double score = (Math.min(enemy.getHealth(), 45) + (enemy.getHealth() <= 45 ? destroyBonus(enemy.type) : 0)) * (enemy.type.canAttack() ? 1 : NON_DAMAGING_MULT);
            if(score > bestScore) {
                bestScore = score;
                bestLoc = enemy.getLocation();
            }

            if(myLoc.distanceSquaredTo(enemy.getLocation()) <= enemy.type.actionRadiusSquared) {
                maxDamageTakenAtLoc += enemy.type.getDamage(enemy.getLevel());
            }
        }

        return new Attack(bestLoc, AttackType.REGULAR, bestScore);
    }

    public Attack scoreAbyss(MapLocation loc) {
        if(!friendlyArchonNearby && enemyMinerNearby && !isMinerNearby) {
            return new Attack(loc, AttackType.ABYSS, leadWithinAction * 0.99 * LEAD_VALUE);
        }
        else {
            return new Attack(null, null, 0);
        }
    }
    
    final double CHARGE_DAMAGE_PERCENT = 0.22;
    public Attack scoreCharge(MapLocation loc) {
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
                            score += enemy.health * NON_DAMAGING_MULT;
                        }
                        break;
                    case SAGE:
                    case SOLDIER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += enemy.health;
                        }
                        break;
                }
            } else {
                switch(enemy.type) {
                    case MINER:
                    case BUILDER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += damage * NON_DAMAGING_MULT;
                        }
                        break;
                    case SAGE:
                    case SOLDIER:
                        if(myLoc.distanceSquaredTo(enemy.getLocation()) <= 25) {
                            score += damage;
                        }
                        break;
                }
            }
        }
        return new Attack(loc, AttackType.CHARGE, score);
    }
    
    final double FURY_DAMAGE_PERCENT = 0.1;
    public Attack scoreFury(MapLocation loc) {
        double score = 0;
        Team team = rc.getTeam();
        double damage;
        for(RobotInfo robot: nearbyRobots) {
            if(robot.getMode() == RobotMode.TURRET && robot.getLocation().distanceSquaredTo(loc) <= 25) {
                damage = FURY_DAMAGE_PERCENT * robot.type.getMaxHealth(robot.level);
                if(robot.team == team) {
                    score -= damage * FRIENDLY_DAMAGE_MULT;
                }
                else {
                    if (damage >= robot.health) {
                        score += robot.health + destroyBonus(robot.type);
                    } else {
                        score += damage;
                    }
                }
            }
        }
        return new Attack(loc, AttackType.FURY, score);
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
            targetLocation = getRandomLocation();
            locationScore = 0.5;
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

    public static final int FLEE_WHEN_COOLDOWN_ABOVE = 40;

    public Direction tryMove() throws GameActionException {
        findTargets();

        MapLocation current = rc.getLocation();

        if (!rc.isMovementReady()) return null;

        if (!areEnemiesNearby && rc.getHealth() < HEAL_HEALTH && !friendlyArchonNearby) {
            MapLocation nearestArchon = Communications.getClosestArchon(rc);
            if (nearestArchon != null) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), nearestArchon);
                MapLocation to = current.add(d);
                if (d != null && rc.canMove(d) &&
                    (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                     || rc.senseRubble(to) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE)) {
                    return d;
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
                && (rc.getActionCooldownTurns() >= FLEE_WHEN_COOLDOWN_ABOVE || !isAllyInRange(fleeFrom, fleeAttackRadius) || maxDamageTaken >= rc.getHealth())) {

            Direction fleeDir = Navigation.flee(rc, current, fleeFrom);
            MapLocation fleeLoc = current.add(fleeDir);
            if (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE || rc.senseRubble(fleeLoc) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE) {
                if (!healing || fleeLoc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                    return fleeDir;
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
                    if (rc.senseRubble(loc) <= rc.senseRubble(current)) {
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
                    if (rc.senseRubble(loc) <= rc.senseRubble(current)) {
                        if (!healing || loc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                            return lowest;
                        }
                    }
                }
            }
        }
        
        if (!areDangerousEnemies || current.distanceSquaredTo(targetLocation) > RobotType.SAGE.actionRadiusSquared) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            MapLocation to = current.add(d);
            if (d != null && rc.canMove(d) &&
                    (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                     || rc.senseRubble(to) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE)) {
                if (!healing || to.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                    return d;
                }
            }
        }

        if (healing) {
            Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(friendlyArchonPos, RobotType.ARCHON.actionRadiusSquared, current.directionTo(friendlyArchonPos).rotateRight().rotateRight());
            if (lowest != null) {
                MapLocation loc = rc.getLocation().add(lowest);
                if (rc.senseRubble(loc) <= rc.senseRubble(current)) {
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
