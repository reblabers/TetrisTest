package pentmino;

import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;

public class RotateStateSequence {
    public static RotateState rotate(RotateState rotateState, RotateDirection rotateDirection) {
        if (rotateDirection == RotateDirection.RIGHT)
            return rotateRight(rotateState);
        else if (rotateDirection == RotateDirection.LEFT)
            return rotateLeft(rotateState);

        assert false;
        throw new IllegalStateException();
    }

    private static RotateState rotateLeft(RotateState rotateState) {
        if (rotateState == RotateState.SPAWN)
            return RotateState.LEFT;
        else if (rotateState == RotateState.RIGHT)
            return RotateState.SPAWN;
        else if (rotateState == RotateState.REVERSE)
            return RotateState.RIGHT;
        else if (rotateState == RotateState.LEFT)
            return RotateState.REVERSE;

        assert false;
        throw new IllegalStateException();
    }

    private static RotateState rotateRight(RotateState rotateState) {
        if (rotateState == RotateState.SPAWN)
            return RotateState.RIGHT;
        else if (rotateState == RotateState.RIGHT)
            return RotateState.REVERSE;
        else if (rotateState == RotateState.REVERSE)
            return RotateState.LEFT;
        else if (rotateState == RotateState.LEFT)
            return RotateState.SPAWN;

        assert false;
        throw new IllegalStateException();
    }
}
