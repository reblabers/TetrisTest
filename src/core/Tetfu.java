package core;

import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tetfu {
    public static final String ENCODE_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public static final int TETFU_FIELD_TOP = 22;
    public static final int TETFU_FIELD_WIDTH = 10;
    public static final int TETFU_FIELD_BLOCKS = 24 * TETFU_FIELD_WIDTH;

    private final List<Integer> values = new ArrayList<>();

    public String parse(Action lastAction) {
        LinkedList<Action> actions = new LinkedList<>();
        Action action = lastAction;
        while (!action.equals(Action.DUMMY)) {
            actions.addFirst(action);
            action = action.getPrevious();
        }
        return parse(actions.subList(1, actions.size()));
    }

    public String parse(List<Action> actions) {
        values.clear();

        invoke(actions);

        StringBuilder builder = new StringBuilder();
        for (Integer value : values)
            builder.append(encode(value));
        return builder.toString();
    }

    private char encode(Integer value) {
        return ENCODE_TABLE.charAt(value);
    }

    private void invoke(List<Action> actions) {
        int repeatCount = 0;
        Field prevField = new Field();
        for (int index = 0; index < actions.size(); index++) {
            Action action = actions.get(index);
            Field currentField = action.getPrevious().getLockedField().getField();

            // field settings
            if (repeatCount == 0) {
                int counter = 0;
                boolean lastBlock = diff(prevField, currentField, 0, TETFU_FIELD_TOP);
                for (int y = TETFU_FIELD_TOP; 0 <= y; y--) {
                    for (int x = 0; x < TETFU_FIELD_WIDTH; x++) {
                        boolean currentBlock = diff(prevField, currentField, x, y);
                        if (lastBlock == currentBlock) {
                            counter++;
                        } else {
                            fixField(lastBlock, counter);
                            lastBlock = currentBlock;
                            counter = 1;
                        }
                    }
                }

                if (lastBlock) {
                    fixField(true, counter + TETFU_FIELD_WIDTH);
                    if (counter + TETFU_FIELD_WIDTH == TETFU_FIELD_BLOCKS) {
                        // field repeat
                        repeatCount = actions.size() - index - 1;
                        values.add(repeatCount);
                    }
                } else {
                    fixField(false, counter);
                    fixField(true, TETFU_FIELD_WIDTH);
                }
            } else {
                repeatCount--;
            }

            // action settings
            parseAction(action, true, false, true, false, false);

            prevField = action.getLockedField().getField();
        }
    }

    private boolean diff(Field prev, Field current, int x, int y) {
        return prev.isEmpty(x, y) == current.isEmpty(x, y);
    }

    private void fixField(boolean isEmpty, int num) {
        int a1 = isEmpty ? 8 : 16;
        int a2 = num - 1;
        int temp = a1 * TETFU_FIELD_BLOCKS + a2;
        values.add(temp % 64);
        values.add((int) Math.floor(temp / 64));
    }

    private void parseAction(Action action, boolean locked, boolean commented, boolean color, boolean mirror, boolean blockUp) {
        Coordinate coordinate = action.getCoordinate();
        RotateState rotate = action.getRotate();
        PentminoType type = action.getType();

        int value = 0;
        value += boolToInt(!locked);
        value *= 2;
        value += boolToInt(commented);
        value *= 2;
        value += boolToInt(color);
        value *= 2;
        value += boolToInt(mirror);
        value *= 2;
        value += boolToInt(blockUp);
        value *= TETFU_FIELD_BLOCKS;
        value += coordinateToInt(coordinate, type, rotate);
        value *= 4;
        value += rotateToInt(rotate);
        value *= 8;
        value += pentminoToInt(type);

        int third = (int) Math.floor(value / 4096);
        int tmp = value % 4096;
        int second = (int) Math.floor(tmp / 64);
        int first = tmp % 64;

        values.add(first);
        values.add(second);
        values.add(third);
    }

    private int boolToInt(boolean v) {
        return v ? 1 : 0;
    }

    private int coordinateToInt(Coordinate coordinate, PentminoType type, RotateState rotate) {
        int x = coordinate.x;
        int y = coordinate.y;

        if (type == PentminoType.O)
            y += 1;
        else if (type == PentminoType.I && rotate == RotateState.REVERSE)
            x -= 1;
        else if (type == PentminoType.I && rotate == RotateState.LEFT)
            y += 1;
        else if (type == PentminoType.S && rotate == RotateState.SPAWN)
            y += 1;
        else if (type == PentminoType.S && rotate == RotateState.RIGHT)
            x += 1;
        else if (type == PentminoType.Z && rotate == RotateState.SPAWN)
            y += 1;
        else if (type == PentminoType.Z && rotate == RotateState.LEFT)
            x -= 1;

        return (TETFU_FIELD_TOP - y) * TETFU_FIELD_WIDTH + x;
    }

    private int rotateToInt(RotateState state) {
        if (state == RotateState.REVERSE)
            return 0;
        else if (state == RotateState.RIGHT)
            return 1;
        else if (state == RotateState.SPAWN)
            return 2;
        else if (state == RotateState.LEFT)
            return 3;
        assert false;
        throw new UnsupportedOperationException();
    }

    private int pentminoToInt(PentminoType type) {
        if (type == null)
            return 0;
        if (type == PentminoType.I)
            return 1;
        else if (type == PentminoType.L)
            return 2;
        else if (type == PentminoType.O)
            return 3;
        else if (type == PentminoType.Z)
            return 4;
        else if (type == PentminoType.T)
            return 5;
        else if (type == PentminoType.J)
            return 6;
        else if (type == PentminoType.S)
            return 7;
        assert false;
        throw new UnsupportedOperationException();
    }
}
