package pentmino.enums;

import pentmino.obj.Range;
import pentmino.blocks.Blocks;
import pentmino.blocks.FixBlocks;
import pentmino.blocks.RotateBlocks;

/**
 * ペントミノの形状を表現
 */
public enum PentminoType {
    T(new RotateBlocks(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {0, 1}})),
    I(new RotateBlocks(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {2, 0}})),
    L(new RotateBlocks(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {1, 1}})),
    J(new RotateBlocks(new int[][]{{0, 0}, {-1, 0}, {1, 0}, {-1, 1}})),
    S(new RotateBlocks(new int[][]{{0, 0}, {-1, 0}, {0, 1}, {1, 1}})),
    Z(new RotateBlocks(new int[][]{{0, 0}, {1, 0}, {0, 1}, {-1, 1}})),
    O(new FixBlocks(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}})),;

    private final Blocks pentmino;

    PentminoType(Blocks pentmino) {
        this.pentmino = pentmino;
    }

    public int[][] getBlocks(int x, int y, RotateState rotateState) {
        int[][] offsets = pentmino.getOffset(rotateState);
        int length = offsets.length;
        int[][] blocks = new int[length][2];
        for (int index = 0; index < length; index++) {
            blocks[index][0] = x + offsets[index][0];
            blocks[index][1] = y + offsets[index][1];
        }
        return blocks;
    }

    public Range getMinMax(RotateState rotateState) {
        int[][] offsets = pentmino.getOffset(rotateState);
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        for (int[] offset : offsets) {
            if (offset[0] < minX)
                minX = offset[0];
            if (maxX < offset[0])
                maxX = offset[0];
            if (offset[1] < minY)
                minY = offset[1];
            if (maxY < offset[1])
                maxY = offset[1];
        }
        return new Range(minX, maxX, minY, maxY);
    }
}
