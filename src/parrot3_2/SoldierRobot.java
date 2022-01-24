package parrot3_2;

import battlecode.common.*;

public strictfp class SoldierRobot extends Robot {

    MapLocation targetLocation;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;
    static final int GIVE_UP_RADIUS_SQUARED = 2;
    static final int GO_ARCHON_ROUND = 100;

    public SoldierRobot(RobotController rc) {
        super(rc);
    }
    
    public static final int ATTACK_DANGEROUS_RUBBLE = 25;
    public static final int HEAL_HEALTH = 10;
    public static final int MINER_BOOST = 10;

    public static MapLocation spawnedArchonLoc;
    public static boolean firstTurn = true;

    static int goArchonSymmetry = 0; // 0 is diagonal, 1 is horizontal, 2 is vertical, 3 go random

    @Override
    public void run() throws GameActionException {
        
        processNearbyRobots();
        broadcastNearbyResources(isMinerNearby || !areEnemiesNearby ? 0 : MINER_BOOST);
        
        /*Direction moved = tryMove();
        boolean attacked = tryAttack();
        if (!areEnemiesNearby && !moved && !attacked && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }*/

        MapLocation attackBefore = tryAttack();
        boolean isBeforeDangerous = false;
        if (attackBefore != null) {
            isBeforeDangerous = rc.senseRobotAtLocation(attackBefore).type.canAttack();
        }
        Direction moveDir = tryMove();
        boolean attacked = false;
        if (moveDir != null) {
            MapLocation finalLoc = rc.getLocation().add(moveDir);
            MapLocation attackAfter = tryAttack(finalLoc);
            boolean isAfterDangerous = false;
            if (attackAfter != null) {
                isAfterDangerous = rc.senseRobotAtLocation(attackAfter).type.canAttack();
            }
            if (attackAfter != null) {
                int finalRubble = rc.senseRubble(finalLoc);
                int currentRubble = rc.senseRubble(rc.getLocation());
                if (attackBefore == null
                        || (finalRubble <= currentRubble && (!isBeforeDangerous || isAfterDangerous))
                        || (finalRubble - currentRubble <= ATTACK_DANGEROUS_RUBBLE && isAfterDangerous && !isBeforeDangerous)) {
                    rc.move(moveDir);
                    nearbyRobots = rc.senseNearbyRobots();
                    if ((attackAfter = tryAttack()) != null) {
                        rc.attack(attackAfter);
                        attacked = true;
                    }
                } else {
                    rc.attack(attackBefore);
                    rc.move(moveDir);
                    attacked = true;
                }
            } else if (attackBefore != null) {
                rc.attack(attackBefore);
                rc.move(moveDir);
                attacked = true;
            } else {
                rc.move(moveDir);
            }
        } else if (attackBefore != null) {
            rc.attack(attackBefore);
            attacked = true;
        }

        if (!areEnemiesNearby && (moveDir == null) && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }

        rc.setIndicatorString("target: " + targetLocation + ", score: " + locationScore + "tssde: " + turnsSinceSeenDangerousEnemy);
    }

    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areDangerousEnemies;
    MapLocation friendlyArchonPos;
    boolean friendlyArchonNearby;
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
                        friendlyArchonNearby = true;
                        friendlyArchonPos = otherRobot.location;
                        if (firstTurn) {
                            spawnedArchonLoc = friendlyArchonPos;
                            firstTurn = false;
                        }
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

    public MapLocation tryAttack() throws GameActionException {
        if (rc.isActionReady()) {
            MapLocation target = identifyTarget();
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    public MapLocation tryAttack(MapLocation next) throws GameActionException {
        if (rc.isActionReady()) {
            MapLocation target = identifyTarget(next);
            if (target != null) {
                return target;
            }
        }
        return null;
    }
    
    /**
     * Targets the opposing enemy with the lowest health, prioritizing units that can attack;
     * returns null if no such enemy is found.
     **/
    /*public MapLocation identifyTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {
                if (otherRobot.type.canAttack()
                        && otherRobot.mode.canAct
                        && rc.getLocation().isWithinDistanceSquared(otherRobot.location, otherRobot.type.actionRadiusSquared)) {
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
    }*/

    /**
     * Selects an opposing enemy in vision range by the same criteria as identifyTarget.
     */

    /*public MapLocation findTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()) {
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
    }*/

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
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {
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
                    && next.isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {

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
                    s = score(otherRobot.health, rc.senseRubble(otherRobot.location), otherRobot.type.actionCooldown, damage, otherRobot.type.getDamage(otherRobot.level));
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
    public static final int HEAL_DISTANCE_SUB = 4;
    public static final int HEAL_DISTANCE_FACTOR = 2;

    public Direction tryMove() throws GameActionException {
        findTargets();

        MapLocation current = rc.getLocation();

        if (!rc.isMovementReady()) return null;


        MapLocation nearestArchon = Communications.getClosestArchon(rc);
        /*int healHealth = HEAL_HEALTH;
        int nearestArchonDistance = 1000000;
        if (nearestArchon != null) {
            nearestArchonDistance = (int) StrictMath.sqrt(nearestArchon.distanceSquaredTo(current));
            healHealth = RobotType.SOLDIER.getMaxHealth(rc.getLevel()) - HEAL_DISTANCE_FACTOR * (nearestArchonDistance - HEAL_DISTANCE_SUB);
            if (healHealth < HEAL_HEALTH) healHealth = HEAL_HEALTH;
        }*/

        if ((!areEnemiesNearby || nearestAlly == null) && rc.getHealth() < HEAL_HEALTH && !friendlyArchonNearby) {
            if (nearestArchon != null) {
                Direction d = Navigation.navigate(rc, rc.getLocation(), nearestArchon);
                if (d != null) {
                    MapLocation to = current.add(d);
                    if (d != null && rc.canMove(d) &&
                        (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                         || rc.senseRubble(to) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE)) {
                        return d;
                    }
                }
            }
        }
        boolean healing = friendlyArchonNearby && rc.getHealth() < RobotType.SOLDIER.getMaxHealth(rc.getLevel());
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
                && (!isAllyInRange(fleeFrom, fleeAttackRadius) || maxDamageTaken >= rc.getHealth())
                && (!friendlyArchonNearby || rc.getHealth() < HEAL_HEALTH)) {

            Direction fleeDir = Navigation.flee(rc, current, fleeFrom);
            if (fleeDir != null) {
                MapLocation fleeLoc = current.add(fleeDir);
                if (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE || rc.senseRubble(fleeLoc) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE) {
                    if (!healing || fleeLoc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                        return fleeDir;
                    }
                }
                return null;
            }
        }

        /*if(!rc.isActionReady() && areEnemiesNearby) {
            return false;
        }

        if (rc.getLocation()*/

        int targetDistance = rc.getLocation().distanceSquaredTo(targetLocation);

        boolean engage = areEnemiesNearby && targetDistance > RobotType.SOLDIER.actionRadiusSquared;

        

        if (nearestEnemy != null) {
            if (rc.isActionReady()) {
                //Move to lower rubble and keep attacking
                
                //Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, d);
                //Direction lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, Direction.NORTH);
                
                Direction lowest;
                boolean takeOverEqual = false;
                if (nearestAlly != null) {
                    //stick to allies
                    Direction bias = current.directionTo(nearestAlly);
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, bias);
                    if (lowest != null && diff(lowest, bias) <= 1) {
                        takeOverEqual = true;
                    }
                } else {
                    //rotate around enemies to find allies
                    Direction d = nearestEnemy.directionTo(current).rotateLeft();
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, d);
                    if (lowest != null && diff(lowest, d) <= 1) {
                        takeOverEqual = true;
                    }
                }

                /*if (areDangerousEnemies) {
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, nearestEnemy.directionTo(current));
                } else {
                    lowest = getBiasedDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared, current.directionTo(targetLocation));
                }*/
                if (lowest != null) {
                    MapLocation loc = rc.getLocation().add(lowest);
                    int locRubble = rc.senseRubble(loc);
                    int currentRubble = rc.senseRubble(current);
                    if (locRubble < currentRubble
                            || (takeOverEqual && locRubble == currentRubble)) {
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
        
        if (!areDangerousEnemies || current.distanceSquaredTo(targetLocation) > RobotType.SOLDIER.actionRadiusSquared) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null) {
                MapLocation to = current.add(d);
                if (d != null && rc.canMove(d) &&
                        (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                         || rc.senseRubble(to) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE)) {
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
                if (rc.senseRubble(loc) <= rc.senseRubble(current)) {
                    if (loc.distanceSquaredTo(friendlyArchonPos) <= RobotType.ARCHON.actionRadiusSquared) {
                        return lowest;
                    }
                }
            }
        }
        return null;
    }
}
