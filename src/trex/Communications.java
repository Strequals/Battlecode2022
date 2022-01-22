package trex;
import battlecode.common.*;
public strictfp class Communications {
    private static final int RESOURCE_START = 0;
    private static final int RESOURCE_NUM = 8;
    private static final int ENEMIES_START = 8;
    private static final int ENEMIES_NUM = 8;
    private static final int ALLIES_START = 16;
    private static final int ALLIES_NUM = 8;
    private static final int BUILDER_COUNT_INDEX = 53;
    private static final int LAB_COUNT_INDEX = 54;
    private static final int ARCHON_DATA_START = 55;
    private static final int PREV_INCOME_INDEX = 59;
    private static final int INCOME_INDEX = 60;
    private static final int MINER_COUNT_PREV = 61;
    private static final int MINER_COUNT_INDEX = 62;
    private static final int ARCHON_PRIORITY_INDEX = 63;
    private static final double RESOURCE_DECAY_FACTOR = 0.9;
    private static final double ENEMIES_DECAY_FACTOR = 0.5;
    private static final double ALLIES_DECAY_FACTOR = 0.1;
    private static final int UPDATE_RADIUS_SQUARED = 5;
    private static int[] array = new int[64];
    private static int lastUpdated = -1;
    public static int archonNum = -1;
    public static int archonPriority = 1;
    private static void updateArray(RobotController rc) throws GameActionException {
        if (lastUpdated < rc.getRoundNum()) {
            lastUpdated = rc.getRoundNum();
            array[0] = rc.readSharedArray(0);
            array[1] = rc.readSharedArray(1);
            array[2] = rc.readSharedArray(2);
            array[3] = rc.readSharedArray(3);
            array[4] = rc.readSharedArray(4);
            array[5] = rc.readSharedArray(5);
            array[6] = rc.readSharedArray(6);
            array[7] = rc.readSharedArray(7);
            array[8] = rc.readSharedArray(8);
            array[9] = rc.readSharedArray(9);
            array[10] = rc.readSharedArray(10);
            array[11] = rc.readSharedArray(11);
            array[12] = rc.readSharedArray(12);
            array[13] = rc.readSharedArray(13);
            array[14] = rc.readSharedArray(14);
            array[15] = rc.readSharedArray(15);
            array[16] = rc.readSharedArray(16);
            array[17] = rc.readSharedArray(17);
            array[18] = rc.readSharedArray(18);
            array[19] = rc.readSharedArray(19);
            array[20] = rc.readSharedArray(20);
            array[21] = rc.readSharedArray(21);
            array[22] = rc.readSharedArray(22);
            array[23] = rc.readSharedArray(23);
            array[24] = rc.readSharedArray(24);
            array[25] = rc.readSharedArray(25);
            array[26] = rc.readSharedArray(26);
            array[27] = rc.readSharedArray(27);
            array[28] = rc.readSharedArray(28);
            array[29] = rc.readSharedArray(29);
            array[30] = rc.readSharedArray(30);
            array[31] = rc.readSharedArray(31);
            array[32] = rc.readSharedArray(32);
            array[33] = rc.readSharedArray(33);
            array[34] = rc.readSharedArray(34);
            array[35] = rc.readSharedArray(35);
            array[36] = rc.readSharedArray(36);
            array[37] = rc.readSharedArray(37);
            array[38] = rc.readSharedArray(38);
            array[39] = rc.readSharedArray(39);
            array[40] = rc.readSharedArray(40);
            array[41] = rc.readSharedArray(41);
            array[42] = rc.readSharedArray(42);
            array[43] = rc.readSharedArray(43);
            array[44] = rc.readSharedArray(44);
            array[45] = rc.readSharedArray(45);
            array[46] = rc.readSharedArray(46);
            array[47] = rc.readSharedArray(47);
            array[48] = rc.readSharedArray(48);
            array[49] = rc.readSharedArray(49);
            array[50] = rc.readSharedArray(50);
            array[51] = rc.readSharedArray(51);
            array[52] = rc.readSharedArray(52);
            array[53] = rc.readSharedArray(53);
            array[54] = rc.readSharedArray(54);
            array[55] = rc.readSharedArray(55);
            array[56] = rc.readSharedArray(56);
            array[57] = rc.readSharedArray(57);
            array[58] = rc.readSharedArray(58);
            array[59] = rc.readSharedArray(59);
            array[60] = rc.readSharedArray(60);
            array[61] = rc.readSharedArray(61);
            array[62] = rc.readSharedArray(62);
            array[63] = rc.readSharedArray(63);
        }
    }
    private static MapLocation location(int index) {
        return new MapLocation(((array[2 * index] % 64)), (((array[2 * index + 1] % 128) / 2)));
    }
    private static void writeData(RobotController rc, int index, int x, int y, int v, int t) throws GameActionException {
        if (v >= 512) {
            v = 511;
        }
        array[2 * index] = (t / 2) * 64 + x;
        rc.writeSharedArray(2 * index, array[2 * index]);
        array[2 * index + 1] = v * 128 + y * 2 + (t % 2);
        rc.writeSharedArray(2 * index + 1, array[2 * index + 1]);
    }
    private static void writeData(RobotController rc, int index, MapLocation location, int value, int roundNum) throws GameActionException {
        writeData(rc, index, location.x, location.y, value, roundNum);
    }
    private static void addData(RobotController rc, MapLocation location, int value, int start, int num, double decay) throws GameActionException {
        updateArray(rc);
        if (value > 0) {
            int lowestIndex = 0;
            double lowestValue = Double.MAX_VALUE;
            double v;
            int closestIndex = 0;
            int closestDistance = Integer.MAX_VALUE;
            int dx;
            int dy;
            int d;
            int roundNum = rc.getRoundNum();
            for (int i = start + num - 1; i-- > start;) {
                v = ((((double) ((array[2 * i + 1] / 128))) * StrictMath.pow(decay, roundNum - ((2 * (array[2 * i] / 64) + (array[2 * i + 1] % 2))))));
                if (v < lowestValue) {
                    lowestValue = v;
                    lowestIndex = i;
                }
                dx = location.x - ((array[2 * i] % 64));
                dy = location.y - (((array[2 * i + 1] % 128) / 2));
                d = dx * dx + dy * dy;
                if (d < closestDistance) {
                    closestDistance = d;
                    closestIndex = i;
                }
            }
            if (closestDistance <= UPDATE_RADIUS_SQUARED) {
                if (((2 * (array[2 * closestIndex] / 64) + (array[2 * closestIndex + 1] % 2))) < roundNum) {
                    writeData(rc, closestIndex, location.x, location.y, value, roundNum);
                }
            } else if (lowestValue < value) {
                writeData(rc, lowestIndex, location.x, location.y, value, roundNum);
            }
        } else {
            int closestIndex = 0;
            int closestDistance = Integer.MAX_VALUE;
            int dx;
            int dy;
            int d;
            int roundNum = rc.getRoundNum();
            for (int i = start + num - 1; i-- > start;) {
                dx = location.x - ((array[2 * i] % 64));
                dy = location.y - (((array[2 * i + 1] % 128) / 2));
                d = dx * dx + dy * dy;
                if (d < closestDistance) {
                    closestDistance = d;
                    closestIndex = i;
                }
            }
            if (closestDistance <= UPDATE_RADIUS_SQUARED) {
                if (((2 * (array[2 * closestIndex] / 64) + (array[2 * closestIndex + 1] % 2))) < roundNum) {
                    writeData(rc, closestIndex, location.x, location.y, value, roundNum);
                }
            }
        }
    }
    public static void addResourceData(RobotController rc, MapLocation location, int value) throws GameActionException {
        addData(rc, location, value, RESOURCE_START, RESOURCE_NUM, RESOURCE_DECAY_FACTOR);
    }
    public static void addEnemyData(RobotController rc, MapLocation location, int value) throws GameActionException {
        addData(rc, location, value, ENEMIES_START, ENEMIES_NUM, ENEMIES_DECAY_FACTOR);
    }
    public static void addAlliesData(RobotController rc, MapLocation location, int value) throws GameActionException {
        addData(rc, location, value, ALLIES_START, ALLIES_NUM, ALLIES_DECAY_FACTOR);
    }
    private static double resourceHeuristic(double v_adjusted, int distanceSquared, double decay) {
        return v_adjusted * Math.pow(decay, Math.sqrt(distanceSquared));
    }
    private static Resource readData(RobotController rc, int start, int num, double decay) throws GameActionException {
        updateArray(rc);
        MapLocation current = rc.getLocation();
        int highestIndex = 0;
        double highestValue = 0;
        double v;
        int roundNum = rc.getRoundNum();
        for (int i = start + num - 1; i-- > start;) {
            v = resourceHeuristic(((((double) ((array[2 * i + 1] / 128))) * StrictMath.pow(decay, roundNum - ((2 * (array[2 * i] / 64) + (array[2 * i + 1] % 2)))))),
                    (int) (Math.pow(current.x-((array[2 * i] % 64)), 2) + Math.pow(current.y-(((array[2 * i + 1] % 128) / 2)), 2)), decay);
            if (v > highestValue) {
                highestValue = v;
                highestIndex = i;
            }
        }
        return new Resource(location(highestIndex), (int) ((((double) ((array[2 * highestIndex + 1] / 128))) * StrictMath.pow(decay, roundNum - ((2 * (array[2 * highestIndex] / 64) + (array[2 * highestIndex + 1] % 2)))))));
    }
    public static Resource readResourceData(RobotController rc) throws GameActionException {
        return readData(rc, RESOURCE_START, RESOURCE_NUM, RESOURCE_DECAY_FACTOR);
    }
    public static Resource readEnemiesData(RobotController rc) throws GameActionException {
        return readData(rc, ENEMIES_START, ENEMIES_NUM, ENEMIES_DECAY_FACTOR);
    }
    public static Resource readAlliesData(RobotController rc) throws GameActionException {
        return readData(rc, ALLIES_START, ALLIES_NUM, ALLIES_DECAY_FACTOR);
    }
    private static void markEnemies(RobotController rc, double decay) throws GameActionException {
        for (int i = ENEMIES_START + ENEMIES_NUM; i-- > ENEMIES_START;) {
            rc.setIndicatorDot(location(i), 0, 0, 10 * (int) ((((double) ((array[2 * i + 1] / 128))) * StrictMath.pow(decay, rc.getRoundNum() - ((2 * (array[2 * i] / 64) + (array[2 * i + 1] % 2)))))));
        }
    }
    private static double readTotal(RobotController rc, int start, int num, double decay) throws GameActionException {
        updateArray(rc);
        double total = 0;
        int i;
        double v;
        int roundNum = rc.getRoundNum();
        for (int j = num; j-- > 0;) {
            i = j + start;
            v = ((((double) ((array[2 * i + 1] / 128))) * StrictMath.pow(decay, roundNum - ((2 * (array[2 * i] / 64) + (array[2 * i + 1] % 2))))));
            total += v;
        }
        return total;
    }
    public static double readTotalResources(RobotController rc) throws GameActionException {
        return readTotal(rc, RESOURCE_START, RESOURCE_NUM, RESOURCE_DECAY_FACTOR);
    }
    public static double readTotalEnemies(RobotController rc) throws GameActionException {
        return readTotal(rc, ENEMIES_START, ENEMIES_NUM, ENEMIES_DECAY_FACTOR);
    }
    public static int getIncome(RobotController rc) {
        return array[PREV_INCOME_INDEX];
    }
    public static void correctIncome(RobotController rc, int mag) throws GameActionException {
        rc.writeSharedArray(INCOME_INDEX, array[INCOME_INDEX] + mag);
    }
    public static void updateIncome(RobotController rc) throws GameActionException {
        rc.writeSharedArray(PREV_INCOME_INDEX, array[INCOME_INDEX]);
        rc.writeSharedArray(INCOME_INDEX, 0);
    }
    public static int getPrevMinerCount(RobotController rc) {
        return array[MINER_COUNT_PREV];
    }
    public static int getCurrMinerCount(RobotController rc) {
        return array[MINER_COUNT_INDEX];
    }
    public static void incrementMinerCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(MINER_COUNT_INDEX, array[MINER_COUNT_INDEX] + 1);
    }
    public static void updateMinerCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(MINER_COUNT_PREV, array[MINER_COUNT_INDEX]);
        rc.writeSharedArray(MINER_COUNT_INDEX, 0);
    }
    public static int getLabCount(RobotController rc) {
        return array[LAB_COUNT_INDEX] >> 6;
    }
    public static void updateLabCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(LAB_COUNT_INDEX, array[LAB_COUNT_INDEX] % 64 * 64);
    }
    public static void incrementLabCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(LAB_COUNT_INDEX, array[LAB_COUNT_INDEX] + 1);
    }
    public static int getBuilderCount(RobotController rc) {
        return array[BUILDER_COUNT_INDEX] >> 7;
    }
    public static void updateBuilderCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(BUILDER_COUNT_INDEX, array[BUILDER_COUNT_INDEX] % 128 * 128);
    }
    public static void incrementBuilderCount(RobotController rc) throws GameActionException {
        rc.writeSharedArray(BUILDER_COUNT_INDEX, array[BUILDER_COUNT_INDEX] + 1);
    }
    public static void writeArchonPriority(RobotController rc) throws GameActionException {
        updateArray(rc);
        int v = array[ARCHON_PRIORITY_INDEX];
        switch (archonNum) {
            case 0:
                v = 16 * (v / 16) + archonPriority;
                break;
            case 1:
                v = 256 * (v / 256) + 16 * archonPriority + (v % 16);
                break;
            case 2:
                v = 4096 * (v / 4096) + 256 * archonPriority + (v % 256);
                break;
            case 3:
                v = 4096 * archonPriority + (v % 4096);
                break;
        }
        array[ARCHON_PRIORITY_INDEX] = v;
        rc.writeSharedArray(ARCHON_PRIORITY_INDEX, v);
    }
    public static int readArchonPriority(RobotController rc, int archon) throws GameActionException {
        updateArray(rc);
        int v = array[ARCHON_PRIORITY_INDEX];
        switch (archon) {
            case 0:
                return v % 16;
            case 1:
                return (v / 16) % 16;
            case 2:
                return (v / 256) % 16;
            case 3:
                return (v / 4096);
        }
        return 0;
    }
    public static int countHigherPriorityArchons(RobotController rc) throws GameActionException {
        int priority = readArchonPriority(rc, archonNum);
        int higherPriorityArchons = 0;
        for (int archon = 0; archon < 4; archon++) {
            if (readArchonPriority(rc, archon) > priority) {
                higherPriorityArchons++;
            }
        }
        return higherPriorityArchons;
    }
    public static void calculateArchonNumber(RobotController rc) throws GameActionException {
        updateArray(rc);
        archonNum = 0;
        int v = array[ARCHON_PRIORITY_INDEX];
        while (v > 0) {
            v /= 16;
            archonNum++;
        }
    }
    public static void incrementArchonPriority(RobotController rc) throws GameActionException {
        archonPriority++;
        if (archonPriority > 15) archonPriority = 15;
        writeArchonPriority(rc);
    }
    public static void zeroArchonPriority(RobotController rc) throws GameActionException {
        archonPriority = 1;
        writeArchonPriority(rc);
    }
    public static void maxArchonPriority(RobotController rc) throws GameActionException {
        archonPriority = 15;
        writeArchonPriority(rc);
    }
    public static void writeArchonData(RobotController rc, boolean isPortable, boolean isThreatened) throws GameActionException {
        updateArray(rc);
        int val = (((isPortable? 8192 : 0) + (isThreatened? 16384 : 0) + 4096 + rc.getLocation().x * 64 + rc.getLocation().y));
        System.out.println(val);
        int index = ARCHON_DATA_START + archonNum;
        array[index] = val;
        rc.writeSharedArray(index, val);
    }
    public static void clearOtherArchonData(RobotController rc) throws GameActionException {
        updateArray(rc);
        switch (archonNum) {
            case 0:
                array[ARCHON_DATA_START + 1] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 1, 0);
                array[ARCHON_DATA_START + 2] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 2, 0);
                array[ARCHON_DATA_START + 3] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 3, 0);
                break;
            case 1:
                array[ARCHON_DATA_START] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 1, 0);
                array[ARCHON_DATA_START + 2] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 2, 0);
                array[ARCHON_DATA_START + 3] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 3, 0);
                break;
            case 2:
                array[ARCHON_DATA_START] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 1, 0);
                array[ARCHON_DATA_START + 1] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 2, 0);
                array[ARCHON_DATA_START + 3] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 3, 0);
                break;
            case 3:
                array[ARCHON_DATA_START] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 1, 0);
                array[ARCHON_DATA_START + 1] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 2, 0);
                array[ARCHON_DATA_START + 2] = 0;
                rc.writeSharedArray(ARCHON_DATA_START + 3, 0);
                break;
        }
    }
    public static MapLocation getClosestArchon(RobotController rc) throws GameActionException {
        updateArray(rc);
        MapLocation current = rc.getLocation();
        MapLocation closest = null;
        int dist = Integer.MAX_VALUE;
        MapLocation archonLoc;
        int d;
        int val = array[ARCHON_DATA_START];
        if (((val > 4096))) {
            archonLoc = new MapLocation((((val / 64) % 64)), ((val % 64)));
            d = current.distanceSquaredTo(archonLoc);
            if (d < dist) {
                closest = archonLoc;
                dist = d;
            }
        }
        val = array[ARCHON_DATA_START + 1];
        if (((val > 4096))) {
            archonLoc = new MapLocation((((val / 64) % 64)), ((val % 64)));
            d = current.distanceSquaredTo(archonLoc);
            if (d < dist) {
                closest = archonLoc;
                dist = d;
            }
        }
        val = array[ARCHON_DATA_START + 2];
        if (((val > 4096))) {
            archonLoc = new MapLocation((((val / 64) % 64)), ((val % 64)));
            d = current.distanceSquaredTo(archonLoc);
            if (d < dist) {
                closest = archonLoc;
                dist = d;
            }
        }
        val = array[ARCHON_DATA_START + 3];
        if (((val > 4096))) {
            archonLoc = new MapLocation((((val / 64) % 64)), ((val % 64)));
            d = current.distanceSquaredTo(archonLoc);
            if (d < dist) {
                closest = archonLoc;
                dist = d;
            }
        }
        return closest;
    }
    public static int getArchonCount(RobotController rc) {
        int numArchons = 0;
        if (((array[ARCHON_DATA_START] > 4096))) numArchons++;
        if (((array[ARCHON_DATA_START+1] > 4096))) numArchons++;
        if (((array[ARCHON_DATA_START+2] > 4096))) numArchons++;
        if (((array[ARCHON_DATA_START+3] > 4096))) numArchons++;
        return numArchons;
    }
    public static MapLocation getArchonCOM(RobotController rc) throws GameActionException {
        updateArray(rc);
        double sx = 0;
        double sy = 0;
        int val = array[ARCHON_DATA_START];
        int numArchons = 0;
        if (((val > 4096))) {
            sx += (((val / 64) % 64));
            sy += ((val % 64));
            numArchons++;
        }
        val = array[ARCHON_DATA_START + 1];
        if (((val > 4096))) {
            sx += (((val / 64) % 64));
            sy += ((val % 64));
            numArchons++;
        }
        val = array[ARCHON_DATA_START + 2];
        if (((val > 4096))) {
            sx += (((val / 64) % 64));
            sy += ((val % 64));
            numArchons++;
        }
        val = array[ARCHON_DATA_START + 3];
        if (((val > 4096))) {
            sx += (((val / 64) % 64));
            sy += ((val % 64));
            numArchons++;
        }
        return new MapLocation((int) StrictMath.round(sx / numArchons), (int) StrictMath.round(sy / numArchons));
    }
}
