package four;

import battlecode.common.*;

public strictfp class Resource {
    MapLocation location;
    int value;
    int lead;
    int gold;

    public Resource(MapLocation l, int v) {
        location = l;
        value = v;
    }

    public Resource(MapLocation l, int pb, int au) {
        location = l;
        lead = pb;
        gold = au;
        value = heuristic(pb, au);
    }

    public static final int GOLD_WEIGHT = 32;
    public static int heuristic(int pb, int au) {
        return GOLD_WEIGHT * au + pb;
    }
}
