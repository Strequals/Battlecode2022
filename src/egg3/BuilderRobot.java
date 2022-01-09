package egg3;

import battlecode.common.*;
import java.util.Random;

public strictfp class BuilderRobot extends Robot {

    MapLocation seedLocation;

    public enum State {
        SEEDING,
        BUILDING
    }
    private static final double SEED_WEIGHT = 0.7;
    private static final int MAX_IDLE_SEED = 10;
    private static final int BUILD_THRESHOLD = 500;
    private static final int TOWER_COOLDOWN = 4;  // turns before building tower - 1
    private static final int TOWER_THRESHOLD = 20;

    private State currentState;
    public BuilderRobot(RobotController rc) {
        super(rc);
        /*if(Random.random() < SEED_WEIGHT) {
            currentState = SEEDING;
        }
        else {
            currentState = BUILDING;
        }*/
        currentState = State.SEEDING;
    }

    public BuilderRobot(RobotController rc, State state) {
        super(rc);
        currentState = state;
    }


    private int towerCooldown = 0;
    @Override
    public void run() throws GameActionException {
        processNearbyRobots();

        if(currentState == State.SEEDING) {
            seed();
        }
        else if(currentState == State.SEEDING) {
            tryBuild();
        }
    }
    
    RobotInfo[] nearbyRobots;
    public void processNearbyRobots() throws GameActionException {
        nearbyRobots = rc.senseNearbyRobots();
        processAndBroadcastEnemies(nearbyRobots);
    }

    public void seed() throws GameActionException {
        MapLocation loc = rc.getLocation();
        if(rc.senseLead(loc) == 0) {
            rc.disintegrate();
        }

        trySeedMove();
    }

    public boolean trySeedMove() throws GameActionException {
        seedLocation = findSeedLocation();
        Direction d = Navigation.navigate(rc, rc.getLocation(), seedLocation);
        if (d != null && rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        return false;
    }

    private int idleTurns = 0;
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
        idleTurns++;
        if(idleTurns > MAX_IDLE_SEED) {
            currentState = State.BUILDING;
        }
        return getRandomLocationWithinChebyshevDistance(6);
    }

    /*
    *  returns true on success, false otherwise
    */
    public boolean tryBuild() throws GameActionException {
        if(towerCooldown > 0) {
            towerCooldown--;
        }

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
        MapLocation target = findBuildSpot();
        if(rc.getLocation().distanceSquaredTo(target) == 0) {
            for(Direction d: Direction.allDirections()) {
                if(rc.canMove(d)) {
                    rc.move(d);
                    break;
                }
            }
        }

        if(rc.getLocation().distanceSquaredTo(target) > 2) {
            Direction d = Navigation.navigate(rc, rc.getLocation(), target);
            if (d != null && rc.canMove(d)) {
                rc.move(d);
            }
        }
        
        if(rc.getLocation().distanceSquaredTo(target) <= 2) {
            if(rc.canBuildRobot(type, rc.getLocation().directionTo(target))) {
                rc.buildRobot(type, rc.getLocation().directionTo(target));
                return true;
            }
        }

        return false;
    }

    /*
    *  searches for a spot on odd x and y, not occupied (should also maybe make sure no archons are nearby)
    */
    public MapLocation findBuildSpot() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation check;
        // inefficient, but hopefully doesnt matter
        // these 3 loops check in squares of increasing radius around the current location
        for(int i = 1; i < 7; i++) {
            for(int j = 0; j < i; j++) {
                for(int k = 0; k < (i - j); k++) {
                    check = new MapLocation(current.x + j, current.y + k);
                    if((current.x + j) % 2 == 1 && (current.y + k) % 2 == 1) {
                        if(rc.canSenseLocation(check) && rc.senseRobotAtLocation(check) == null) {
                            return check;
                        }
                    }
                }
            }
        }

        return getRandomLocationWithinChebyshevDistance(6);
    }
}
