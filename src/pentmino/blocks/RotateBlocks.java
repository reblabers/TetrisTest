package pentmino.blocks;

import pentmino.enums.RotateState;

public class RotateBlocks implements Blocks {
    private final int[][] spawn;

    public RotateBlocks(int[][] spawn) {
        this.spawn = spawn;
    }

    public int[][] getOffset(RotateState rotateState) {
        return rotateState.transform(spawn);
    }
}
