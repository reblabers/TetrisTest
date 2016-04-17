package pentmino;

import pentmino.enums.PentminoType;
import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;

public class TestPattern {
    public static int[][] get(Pentmino start, RotateDirection rtype) {
        PentminoType type = start.getType();
        RotateState rotateState = start.getRotateState();
        return get(rtype, type, rotateState);
    }

    private static int[][] get(RotateDirection rtype, PentminoType type, RotateState rotateState) {
        if (type == PentminoType.I)
            return getI(rtype, rotateState);
        else if (type == PentminoType.O)
            return new int[][]{};
        return getOther(rtype, rotateState);
    }

    private static int[][] getI(RotateDirection rtype, RotateState rotateState) {
        if (rotateState == RotateState.SPAWN) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2},
                };
            } else {
                return new int[][]{
                        {0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1},
                };
            }
        } else if (rotateState == RotateState.RIGHT) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1},
                };
            } else {
                return new int[][]{
                        {0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2},
                };
            }
        } else if (rotateState == RotateState.REVERSE) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2},
                };
            } else {
                return new int[][]{
                        {0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1},
                };
            }
        } else if (rotateState == RotateState.LEFT) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1},
                };
            } else {
                return new int[][]{
                        {0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2},
                };
            }
        }

        assert false;
        throw new IllegalStateException();
    }

    private static int[][] getOther(RotateDirection rtype, RotateState rotateState) {
        if (rotateState == RotateState.SPAWN) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2},
                };
            } else {
                return new int[][]{
                        {0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2},
                };
            }
        } else if (rotateState == RotateState.RIGHT) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2},
                };
            } else {
                return new int[][]{
                        {0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2},
                };
            }
        } else if (rotateState == RotateState.REVERSE) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2},
                };
            } else {
                return new int[][]{
                        {0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2},
                };
            }
        } else if (rotateState == RotateState.LEFT) {
            if (rtype == RotateDirection.RIGHT) {
                return new int[][]{
                        {0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2},
                };
            } else {
                return new int[][]{
                        {0, 0},  {-1, 0}, {-1, -1}, {0, 2}, {-1, 2},
                };
            }
        }

        assert false;
        throw new IllegalStateException();
    }
}
