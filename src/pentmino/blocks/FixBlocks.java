package pentmino.blocks;

import pentmino.enums.RotateState;

public class FixBlocks implements Blocks {
    private final int[][] spawn;

    public FixBlocks(int[][] spawn) {
        this.spawn = spawn;
    }

    public int[][] getOffset(RotateState rotateState) {
        return spawn;
    }
}
