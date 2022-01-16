package chicken8;

import battlecode.common.*;

public strictfp class SoldierRobot extends Robot {

    MapLocation targetLocation;
    double locationScore;
    static final double SCORE_DECAY = 0.8;
    static final double CHANGE_TARGET_THRESHOLD = 0.1;
    static final int GIVE_UP_RADIUS_SQUARED = 2;
    static final boolean MOVE_IF_ATTACK_CD = false;

    public SoldierRobot(RobotController rc) {
        super(rc);
    }

    @Override
    public void run() throws GameActionException {
        
        processNearbyRobots();
        broadcastNearbyResources();
        
        /*Direction moved = tryMove();
        boolean attacked = tryAttack();
        if (!areEnemiesNearby && !moved && !attacked && rc.isMovementReady() && rc.isActionReady()) {
            locationScore *= SCORE_DECAY;
        }*/

        MapLocation attackBefore = tryAttack();
        Direction moveDir = tryMove();
        boolean attacked = false;
        if (moveDir != null) {
            MapLocation finalLoc = rc.getLocation().add(moveDir);
            MapLocation attackAfter = tryAttack(finalLoc);
            if (attackAfter != null) {
                if (attackBefore == null || rc.senseRubble(finalLoc) <= rc.senseRubble(rc.getLocation())) {
                    rc.move(moveDir);
                    rc.attack(attackAfter);
                    attacked = true;
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

        rc.setIndicatorString("target: " + targetLocation + ", score: " + locationScore);
    }

    RobotInfo[] nearbyRobots;
    boolean areEnemiesNearby;
    boolean areDangerousEnemies;
    boolean friendlyArchonNearby;
    MapLocation nearestAlly;
    MapLocation nearestEnemy;
    MapLocation fleeFrom;
    int fleeAttackRadius;
    int maxDamageTaken;
    int allies;
    int turnsSinceSeenDangerousEnemy = 0;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        fleeFrom = null;
        areEnemiesNearby = false;
        areDangerousEnemies = false;
        friendlyArchonNearby = false;
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
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team == team) {
                switch (otherRobot.type) {
                    case ARCHON:
                        friendlyArchonNearby = true;
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
            targetLocation = t;
            if (areDangerousEnemies) {
                locationScore = 2;
            } else {
                locationScore = 0.5;
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


    /**
     * Targets the opposing enemy with the lowest health, prioritizing units that can attack;
     * returns null if no such enemy is found.
     **/
    public MapLocation identifyTarget() throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        int id = Integer.MAX_VALUE;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && rc.getLocation().isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {

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
    }

    /**
     * Same as identifyTarget(), but is centered around next.
     **/
    public MapLocation identifyTarget(MapLocation next) throws GameActionException {
        if (!areEnemiesNearby) return null;
        MapLocation best = null;
        int health = Integer.MAX_VALUE;
        boolean canAttack = false;
        int id = Integer.MAX_VALUE;
        for (RobotInfo otherRobot : nearbyRobots) {
            if (otherRobot.team != rc.getTeam()
                    && next.isWithinDistanceSquared(otherRobot.location, RobotType.SOLDIER.actionRadiusSquared)) {

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
    }

    /**
     * Selects an opposing enemy in vision range by the same criteria as identifyTarget.
     */

    public MapLocation findTarget() throws GameActionException {
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

    public Direction tryMove() throws GameActionException {
        findTargets();

        MapLocation current = rc.getLocation();

        if (!rc.isMovementReady()) return null;

        if (fleeFrom != null
                && (!isAllyInRange(fleeFrom, fleeAttackRadius) || maxDamageTaken >= rc.getHealth())
                && !friendlyArchonNearby) {

            Direction fleeDir = Navigation.flee(rc, current, fleeFrom);
            MapLocation fleeLoc = current.add(fleeDir);
            if (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE || rc.senseRubble(fleeLoc) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE) {
                return fleeDir;
            }
            return null;
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
                Direction lowest = getDirectionOfLeastRubbleWithinDistanceSquaredOf(nearestEnemy, RobotType.SOLDIER.actionRadiusSquared);
                if (lowest != null) {
                    MapLocation loc = rc.getLocation().add(lowest);
                    if (rc.senseRubble(loc) > rc.senseRubble(rc.getLocation())) {
                        return null;
                    }
                    return lowest;
                }
            } else {
                //Move to lower rubble to prepare for attacking
                Direction lowest = getDirectionOfLeastRubble();
                if (lowest != null) {
                    MapLocation loc = rc.getLocation().add(lowest);
                    if (rc.senseRubble(loc) > rc.senseRubble(rc.getLocation())) {
                        return null;
                    }
                    return lowest;
                }
            }
        }

        if (!areEnemiesNearby) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            MapLocation to = current.add(d);
            if (d != null && rc.canMove(d) &&
                    (turnsSinceSeenDangerousEnemy >= TURNS_AVOID_RUBBLE
                     || rc.senseRubble(to) - rc.senseRubble(current) <= MAX_RUBBLE_INCREASE)) {
                return d;
            }
        }
        return null;
    }
}
