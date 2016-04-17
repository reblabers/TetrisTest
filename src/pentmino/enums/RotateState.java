package pentmino.enums;

public enum RotateState {
    SPAWN(new int[][]{{1, 0}, {0, 1}}),
    LEFT(new int[][]{{0, -1}, {1, 0}}),
    RIGHT(new int[][]{{0, 1}, {-1, 0}}),
    REVERSE(new int[][]{{-1, 0}, {0, -1}}),;

    private final int[][] rotate;

    RotateState(int[][] rotate) {
        this.rotate = rotate;
    }

    public int[][] transform(int[][] offsets) {
        int[][] newOffsets = new int[offsets.length][2];
        for (int index = 0; index < offsets.length; index++)
            newOffsets[index] = transform(offsets[index]);
        return newOffsets;
    }

    private int[] transform(int[] offset) {
        return new int[]{dot2(offset, rotate[0]), dot2(offset, rotate[1])};
    }

    private int dot2(int[] a, int[] b) {
        return a[0] * b[0] + a[1] * b[1];
    }
}