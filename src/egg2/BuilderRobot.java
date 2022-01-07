package egg2;

import battlecode.common.*;
import java.util.Random;

public strictfp class BuilderRobot extends Robot {
    public enum State {
        SEEDING,
        BUILDING
    }
    private static final SEED_WEIGHT = 0.7;

    private State currentState;
    public BuilderRobot(RobotController rc) {
        super(rc);
        if(Random.random() < SEED_WEIGHT) {
            currentState = SEEDING;
        }
        else {
            currentState = BUILDING;
        }
    }

    public BuilderRobot(RobotController rc, State state) {
        super(rc);
        currentState = state;
    }

    @Override
    public void run() throws GameActionException {
        processNearbyRobots();

        if(currentState = State.SEEDING) {
            seed();
        }
    }

    private String checked;

    public void seed() throws GameActionException {
        MapLocation loc = rc.getLocation();
        if(rc.senseLead(loc) == 0) {
            rc.disintegrate();
        }

        // find nearest empty spot in vision
        // inefficient, but hopefully doesnt matter
        for(int i = 1; i < 7; i++) {
            for(int j = 0; j < i; j++) {
                for(int k = 0; k < (i - j); k++) {
                    MapLocation check = new MapLocation(loc.x + j, loc.y + k);
                    if(check.distanceSquaredTo(loc) <= 20 && rc.senseLead(check == 0)) {
                        if(rc.isMovementReady()) {
                            rc.move(Navigate.navigate(rc, loc, check));
                        }
                        return;
                    }
                }
            }
        }

        // otherwise wander
        if(rc.isMovementReady()) {
            rc.wander();
        }
    }
}
