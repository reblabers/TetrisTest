package pentmino.blocks;

import pentmino.enums.RotateState;

/**
 * ブロックの構成を保持
 */
public interface Blocks {
    /**
     * 回転軸から相対的なブロックの座標を返却
     *
     * @param rotateState 回転状態
     * @return 回転軸からの相対座標
     */
    int[][] getOffset(RotateState rotateState);
}
