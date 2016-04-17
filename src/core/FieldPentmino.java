package core;

import pentmino.Pentmino;
import pentmino.TestPattern;
import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;
import pentmino.enums.TSpin;
import pentmino.obj.Coordinate;

public class FieldPentmino {
    private final Field field;

    public FieldPentmino() {
        this(new Field());
    }

    public FieldPentmino(Field field) {
        this.field = new Field(field);
    }

    public FieldPentmino(FieldPentmino fieldPentmino) {
        this(fieldPentmino.field);
    }

    /**
     * 回転軸の座標を中心にペントミノをおいたときブロックの上である確認
     *
     * @param coordinate 回転軸の座標
     * @param pentmino   ペントミノ
     * @return ブロックの上なら{@code true}
     */
    public boolean isOnGround(Coordinate coordinate, Pentmino pentmino) {
        return isOnGround(coordinate.x, coordinate.y, pentmino);
    }

    public boolean isOnGround(int x, int y, Pentmino pentmino) {
        int[][] blocks = pentmino.getBlocks(x, y);
        for (int[] block : blocks) {
            int downY = block[1] - 1;
            if (!field.isEmpty(block[0], downY))
                return true;
        }
        return false;
    }

    /**
     * 回転軸の座標を中心にペントミノが入るスペースがあるか確認
     *
     * @param coordinate 回転軸の座標
     * @param pentmino   ペントミノ
     * @return スペースが存在するなら{@code true}
     */
    public boolean existsSpace(Coordinate coordinate, Pentmino pentmino) {
        return existsSpace(coordinate.x, coordinate.y, pentmino);
    }

    public boolean existsSpace(int x, int y, Pentmino pentmino) {
        int[][] blocks = pentmino.getBlocks(x, y);

        // 最も低いブロックのY座標を探索
        // 探索中にもしフィールド外のブロックがある場合は処理を終了
        int minY = Integer.MAX_VALUE;
        for (int[] block : blocks) {
            if (!field.isInField(block[0], block[1]))
                return false;

            if (block[1] < minY)
                minY = block[1];
        }

        // マスクの合成
        long mask = 0L;
        for (int[] block : blocks) {
            int newY = block[1] - minY;
            mask |= FieldUtil.getBlockMask(block[0], newY);
        }

        int height = pentmino.getHeight();
        return (field.subField(minY, height) & mask) == 0L;
    }

    /**
     * 指定した回転軸のペントミノに従って、フィールドにブロックを配置
     *
     * @param coordinate 回転軸の座標
     * @param pentmino   ペントミノ
     */
    public void put(Coordinate coordinate, Pentmino pentmino) {
        put(coordinate.x, coordinate.y, pentmino);
    }

    public void put(int x, int y, Pentmino pentmino) {
        int[][] blocks = pentmino.getBlocks(x, y);
        for (int[] block : blocks)
            field.put(block[0], block[1]);
    }

    /**
     * 指定した回転軸のX座標でペントミノをハードドロップさせたとき移動するY座標を計算
     *
     * @param x        回転軸のX座標
     * @param pentmino ペントミノ
     * @return ハードドロップ後の回転軸のY座標
     */
    public int harddrop(int x, Pentmino pentmino) {
        int row = Field.FIELD_TOP;
        for (; 0 <= row; row--)
            if (!existsSpace(x, row, pentmino))
                break;
        return row + 1;
    }

    /**
     * 指定した回転軸の座標でペントミノを回転させたとき次に移動する座標を計算
     *
     * @param coordinate 回転軸の座標
     * @param pentmino   ペントミノ
     * @param rotate     回転方向
     * @return 回転後の回転軸の座標
     */
    public Coordinate rotate(Coordinate coordinate, Pentmino pentmino, RotateDirection rotate) {
        return rotate(coordinate.x, coordinate.y, pentmino, rotate);
    }

    public Coordinate rotate(int x, int y, Pentmino pentmino, RotateDirection rotate) {
        Pentmino newPentmino = pentmino.rotate(rotate);

        int[][] offsets = TestPattern.get(pentmino, rotate);
        for (int[] offset : offsets) {
            int newX = x + offset[0];
            int newY = y + offset[1];
            if (existsSpace(newX, newY, newPentmino))
                return new Coordinate(newX, newY);
        }

        return Coordinate.NULL;
    }

    /**
     * T-Spinの判定
     *
     * @param coordinate 回転軸の座標
     * @param state      回転後のペントミノの状態
     * @return スピンの判定結果
     */
    public TSpin checkTSpin(Coordinate coordinate, RotateState state) {
        long subField = field.subField(coordinate.y - 1, 3);

        long mask1 = FieldUtil.getBlockMask(coordinate.x - 1, 2);
        boolean exist1 = (subField & mask1) != 0L;

        long mask2 = FieldUtil.getBlockMask(coordinate.x + 1, 2);
        boolean exist2 = (subField & mask2) != 0L;

        long mask3 = FieldUtil.getBlockMask(coordinate.x + 1, 0);
        boolean exist3 = (subField & mask3) != 0L;

        long mask4 = FieldUtil.getBlockMask(coordinate.x - 1, 0);
        boolean exist4 = (subField & mask4) != 0L;

        if (state == RotateState.SPAWN) {
            if ((exist1 & exist2 & exist3) | (exist1 & exist2 & exist4))
                return TSpin.NORMAL;
            else if ((exist1 & exist3 & exist4) | (exist2 & exist3 & exist4))
                return TSpin.MINI;
        } else if (state == RotateState.RIGHT) {
            if ((exist1 & exist2 & exist3) | (exist2 & exist3 & exist4))
                return TSpin.NORMAL;
            else if ((exist1 & exist3 & exist4) | (exist1 & exist2 & exist4))
                return TSpin.MINI;
        } else if (state == RotateState.REVERSE) {
            if ((exist1 & exist3 & exist4) | (exist2 & exist3 & exist4))
                return TSpin.NORMAL;
            else if ((exist1 & exist2 & exist3) | (exist1 & exist2 & exist4))
                return TSpin.MINI;
        } else if (state == RotateState.LEFT) {
            if ((exist1 & exist3 & exist4) | (exist1 & exist2 & exist4))
                return TSpin.NORMAL;
            else if ((exist1 & exist2 & exist3) | (exist2 & exist3 & exist4))
                return TSpin.MINI;
        }
        return TSpin.NONE;
    }

    public int clearLine() {
        return field.clearLine();
    }

    public void show() {
        field.show();
    }

    public Field getField() {
        return field;
    }

    public FieldPentmino copy() {
        return new FieldPentmino(this);
    }

    public String toBase64() {
        return field.toHashString();
    }

    public int maxHeight() {
        return field.maxHeight();
    }

    public int lowDigCount() {
        return field.lowDigCount();
    }
}