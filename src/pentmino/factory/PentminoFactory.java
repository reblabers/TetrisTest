package pentmino.factory;

import pentmino.Pentmino;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;

public class PentminoFactory {
    public static Pentmino spawn(PentminoType type) {
        return new Pentmino(type, RotateState.SPAWN);
    }

    public static Pentmino left(PentminoType type) {
        return new Pentmino(type, RotateState.LEFT);
    }

    public static Pentmino right(PentminoType type) {
        return new Pentmino(type, RotateState.RIGHT);
    }

    public static Pentmino reverse(PentminoType type) {
        return new Pentmino(type, RotateState.REVERSE);
    }

    public static Pentmino create(PentminoType type, RotateState rotateState) {
        return new Pentmino(type, rotateState);
    }
}
