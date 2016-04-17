package core;

import pentmino.Pentmino;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;

public class Action {
    public static final Action DUMMY = new Action(null, null, null, null, null);

    public static Action createFirstAction(FieldPentmino fieldPentmino) {
        return new Action(null, null, null, fieldPentmino.copy(), DUMMY);
    }

    private final Coordinate coordinate;
    private final PentminoType type;
    private final RotateState rotate;
    private final FieldPentmino lockedField;
    private final Action previous;

    public Action(int x, int y, PentminoType type, RotateState rotate, FieldPentmino lockedField, Action previous) {
        this(new Coordinate(x, y), type, rotate, lockedField, previous);
    }

    public Action(Coordinate coordinate, PentminoType type, RotateState rotate, Action previous) {
        FieldPentmino fieldPentmino = previous.getLockedField().copy();
        Pentmino pentmino = new Pentmino(type, rotate);
        fieldPentmino.put(coordinate, pentmino);
        fieldPentmino.clearLine();

        this.coordinate = coordinate;
        this.type = type;
        this.rotate = rotate;
        this.lockedField = fieldPentmino;
        this.previous = previous;
    }

    public Action(Coordinate coordinate, PentminoType type, RotateState rotate, FieldPentmino lockedField, Action previous) {
        this.coordinate = coordinate;
        this.type = type;
        this.rotate = rotate;
        this.lockedField = lockedField;
        this.previous = previous;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public RotateState getRotate() {
        return rotate;
    }

    public PentminoType getType() {
        return type;
    }

    public FieldPentmino getLockedField() {
        return lockedField;
    }

    public Action getPrevious() {
        return previous == null ? DUMMY : previous;
    }
}
