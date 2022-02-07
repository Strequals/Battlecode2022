package crow_3;

import battlecode.common.*;

public strictfp class StringSet {
    String string;

    public StringSet() {
        string = new String();
    }

    public void add(MapLocation l) {
        string = string + " " + rep(l);
    }

    public boolean contains(MapLocation l) {
        return string.contains(rep(l));
    }

    public String rep(MapLocation l) {
        return l.x + "," + l.y;
    }

    public void clear() {
        string = "";
    }
}
