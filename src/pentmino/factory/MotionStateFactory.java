package pentmino.factory;

import pentmino.enums.RotateState;
import pentmino.enums.StateType;
import pentmino.obj.Coordinate;
import pentmino.obj.MotionState;

public class MotionStateFactory {
    public static MotionState normalLock(Coordinate start, Coordinate next, RotateState rotateState) {
        return new MotionState(start, next, rotateState, rotateState, StateType.NORMAL_LOCK);
    }

    public static MotionState normalPass(Coordinate start, Coordinate next, RotateState rotateState) {
        return new MotionState(start, next, rotateState, rotateState, StateType.NORMAL_PASS);
    }

    public static MotionState rotateLock(Coordinate start, Coordinate next, RotateState startRotation, RotateState nextRotation) {
        return new MotionState(start, next, startRotation, nextRotation, StateType.ROTATE_LOCK);
    }

    public static MotionState rotatePass(Coordinate start, Coordinate next, RotateState startRotation, RotateState nextRotation) {
        return new MotionState(start, next, startRotation, nextRotation, StateType.ROTATE_PASS);
    }
}
