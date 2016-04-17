package core;

class FieldUtil {
    public static final int FIELD_WIDTH = 10;
    public static final int FIELD_HEIGHT = 25;
    private static final int LINES_IN_ONE_LONG = 5;

    public static int getBitPosition(int y) {
        return y % LINES_IN_ONE_LONG;
    }

    public static int getBlockSlide(int x, int y) {
        int bitPosition = getBitPosition(y);
        return bitPosition * FIELD_WIDTH + (FIELD_WIDTH - x - 1);
    }

    public static long getBlockMask(int x, int y) {
        int slide = getBlockSlide(x, y);
        return 1L << slide;
    }

    public static long getMultiLineMask(int line) {
        return (1L << (line * FIELD_WIDTH)) - 1;
    }

    public static int getLineSlide(int y) {
        int bitPosition = FieldUtil.getBitPosition(y);
        return bitPosition * FIELD_WIDTH;
    }

    public static int getBoardNumber(int y) {
        return (int) Math.floor(y / LINES_IN_ONE_LONG);
    }

    public static boolean isInField(int x, int y) {
        return 0 <= x && x < FIELD_WIDTH && 0 <= y && y < FIELD_HEIGHT;
    }

    public static int getDigit(int slide) {
        return LINES_IN_ONE_LONG * FIELD_WIDTH - slide;
    }
}
