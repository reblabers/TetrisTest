package pentmino;

import pentmino.enums.PentminoType;
import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;
import pentmino.obj.Range;

/**
 * 1つのペントミノを表現
 */
public class Pentmino {
    private final PentminoType type;
    private final RotateState rotateState;

    public Pentmino(PentminoType type, RotateState rotateState) {
        this.type = type;
        this.rotateState = rotateState;
    }

    public Pentmino rotate(RotateDirection rotateDirection) {
        RotateState rotated = RotateStateSequence.rotate(rotateState, rotateDirection);
        return new Pentmino(type, rotated);
    }

    public int[][] getBlocks(int x, int y) {
        return type.getBlocks(x, y, rotateState);
    }

    public Range getFieldRange(int fieldWidth, int fieldHeight) {
        Range range = type.getMinMax(rotateState);
        return new Range(-range.minX, fieldWidth - range.maxX - 1, -range.minY, fieldHeight - range.maxY - 1);
    }

    public PentminoType getType() {
        return type;
    }

    public RotateState getRotateState() {
        return rotateState;
    }

    public int getHeight() {
        Range range = type.getMinMax(rotateState);
        return range.maxY - range.minY + 1;
    }
}