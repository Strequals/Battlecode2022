package egg3;

import battlecode.common.*;
import java.util.Random;

public strictfp class BuilderRobot extends Robot {

    MapLocation targetLocation;

    private static final double SEED_WEIGHT = 0.7;
    private static final int MAX_IDLE_SEED = 10;
    private static final int BUILD_THRESHOLD = 500;
    private static final int TOWER_COOLDOWN = 4;  // turns before building tower - 1
    private static final int TOWER_THRESHOLD = 20;

    public BuilderRobot(RobotController rc) {
        super(rc);
    }

    private int towerCooldown = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();
        tryRepair();
        tryBuild();
        trySeed();
        tryMove();
    }
    
    RobotInfo[] nearbyRobots;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        processAndBroadcastEnemies(nearbyRobots);
    }

    public void trySeed() throws GameActionException {
        MapLocation loc = rc.getLocation();
        if(rc.senseLead(loc) == 0) {
            rc.disintegrate();
        }
    }

    public MapLocation findSeedLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation check;
        // find nearest empty spot in vision
        // inefficient, but hopefully doesnt matter
        // these 3 loops check in squares of increasing radius around the current location
        for(int i = 1; i < 7; i++) {
            for(int j = 0; j < i; j++) {
                for(int k = 0; k < (i - j); k++) {
                    check = new MapLocation(current.x + j, current.y + k);
                    if(rc.canSenseLocation(check) && rc.senseLead(check) == 0) {
                        return check;
                    }
                }
            }
        }

        return null;
    }

    /*
    *  returns true on success, false otherwise
    */
    public boolean tryBuild() throws GameActionException {

        if(rc.getTeamLeadAmount(rc.getTeam()) < BUILD_THRESHOLD) {
            return false;
        }

        if(Communications.readTotalEnemies(rc) > TOWER_THRESHOLD && towerCooldown == 0) {
            if(tryBuild(RobotType.WATCHTOWER)) {
                towerCooldown = TOWER_COOLDOWN;
                return true;
            }
        }
        else {
            return tryBuild(RobotType.LABORATORY);
        }

        return false;
    }

    public boolean tryBuild(RobotType type) throws GameActionException {
        if (!rc.isActionReady()) return false;
        MapLocation current = rc.getLocation();
        MapLocation loc;
        for (Direction d : directions) {
            loc = current.add(d);
            if (validBuildLocation(loc) && rc.canBuildRobot(type, d)) {
                rc.buildRobot(type, d);
                return true;
            }
        }

        return false;
    }

    public boolean tryRepair() throws GameActionException {
        if (!rc.isActionReady()) return false;
        Team team = rc.getTeam();
        for (RobotInfo nearbyRobot : nearbyRobots) {
            if (nearbyRobot.team == team && RobotType.BUILDER.canRepair(nearbyRobot.type)
                    && nearbyRobot.health < nearbyRobot.type.getMaxHealth(nearbyRobot.level)) {
                if (rc.canRepair(nearbyRobot.location)) {
                    rc.repair(nearbyRobot.location);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryMove() throws GameActionException {
        if (!rc.isMovementReady()) return false;
        findTarget();
        if(rc.getLocation().distanceSquaredTo(targetLocation) == 0) {
            for(Direction d: directions) {
                if(rc.canMove(d)) {
                    rc.move(d);
                    return true;
                }
            }
        } else {
            Direction d = Navigation.navigate(rc, rc.getLocation(), targetLocation);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
                return true;
            }
        }

        return false;
    }

    public void findTarget() throws GameActionException {
        MapLocation buildLocation = findBuildLocation();
        if (buildLocation != null) {
            targetLocation = buildLocation;
            return;
        }
        
        MapLocation seedLocation = findSeedLocation();
        if (seedLocation != null) {
            targetLocation = seedLocation;
            return;
        }
        
        if (targetLocation == null) {
            targetLocation = getRandomLocationWithinChebyshevDistance(6);
        }
    }

    /*
    *  searches for a spot on odd x and y, not occupied (should also maybe make sure no archons are nearby)
    */
    public MapLocation findBuildLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation check;
        // inefficient, but hopefully doesnt matter
        // these 3 loops check in squares of increasing radius around the current location
        for(int i = 1; i < 7; i++) {
            for(int j = 0; j < i; j++) {
                for(int k = 0; k < (i - j); k++) {
                    check = new MapLocation(current.x + j, current.y + k);
                    if(validBuildLocation(check)) {
                        if(rc.canSenseLocation(check) && rc.senseRobotAtLocation(check) == null) {
                            return check;
                        }
                    }
                }
            }
        }

        return null;
    }
}
