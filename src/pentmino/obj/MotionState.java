package pentmino.obj;

import pentmino.enums.RotateState;
import pentmino.enums.StateType;

/**
 * 移動状態を表現
 */
public class MotionState {
    public final Coordinate start;
    public final Coordinate next;
    public final RotateState startRotation;
    public final RotateState nextRotation;
    public final StateType type;

    public MotionState(Coordinate start, Coordinate next, RotateState startRotation, RotateState nextRotation, StateType type) {
        this.start = start;
        this.next = next;
        this.startRotation = startRotation;
        this.nextRotation = nextRotation;
        this.type = type;
    }

    @Override
    public String toString() {
        return "MotionState{" +
                "start=" + start +
                ", next=" + next +
                ", startRotation=" + startRotation +
                ", nextRotation=" + nextRotation +
                ", type=" + type +
                '}';
    }
}
