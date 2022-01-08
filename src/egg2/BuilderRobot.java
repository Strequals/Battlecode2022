package egg2;

import battlecode.common.*;
import java.util.Random;

public strictfp class BuilderRobot extends Robot {

    MapLocation seedLocation;

    public enum State {
        SEEDING,
        BUILDING
    }
    private static final double SEED_WEIGHT = 0.7;

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

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();

        if(currentState == State.SEEDING) {
            seed();
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

        tryMove();
    }

    public boolean tryMove() throws GameActionException {
        seedLocation = findSeedLocation();
        Direction d = Navigation.navigate(rc, rc.getLocation(), seedLocation);
        if (d != null && rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        return false;
    }

    public MapLocation findSeedLocation() throws GameActionException {
        MapLocation current = rc.getLocation();
        MapLocation check;
        // find nearest empty spot in vision
        // inefficient, but hopefully doesnt matter
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

        return getRandomLocationWithinChebyshevDistance(6);


    }
}
