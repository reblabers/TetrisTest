package core;

import pentmino.Pentmino;
import pentmino.RotateStateSequence;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;
import pentmino.enums.StateType;
import pentmino.factory.MotionStateFactory;
import pentmino.obj.Coordinate;
import pentmino.obj.MotionState;
import pentmino.obj.Range;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class Next {
    public static final int FIELD_WIDTH = 10;
    public static final int FIELD_HEIGHT = 20;

    private final LinkedList<MotionState> candidates = new LinkedList<>();
    private final List<MotionState> states = new ArrayList<>();

    private final RotateStateManager visited = new RotateStateManager(FIELD_WIDTH, FIELD_HEIGHT);

    private final Field field;
    private final FieldPentmino fieldPentmino;
    private final PentminoType type;
    private final Cache cache;

    private boolean isSearch = false;

    public Next(FieldPentmino fieldPentmino, PentminoType type) {
        this.fieldPentmino = fieldPentmino;
        this.field = fieldPentmino.getField();
        this.type = type;
        this.cache = new Cache(field, type);
        nextStep(MotionStateFactory.normalPass(new Coordinate(5, 20), new Coordinate(5, 19), RotateState.SPAWN));
    }

    public void search() {
        if (isSearch)
            return;

        this.isSearch = true;
        while (!candidates.isEmpty()) {
            MotionState state = candidates.removeFirst();
            checks(state);
        }
    }

    private void checks(MotionState state) {
        Coordinate current = state.next;

        // 通常操作のチェック
        Coordinate down = current.down();
        if (isInField(down))
            checkNormal(current, down);

        Coordinate left = current.left();
        if (isInField(left))
            checkNormal(current, left);

        Coordinate right = current.right();
        if (isInField(right))
            checkNormal(current, right);

        // 回転操作のチェック
        checkRotate(state);
    }

    private boolean isInField(Coordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;
        return 0 <= x && x < FIELD_WIDTH && 0 <= y && y < FIELD_HEIGHT;
    }

    private void checkNormal(Coordinate current, Coordinate next) {
        if (cache.spawn(current) && cache.spawn(next))
            foundNormal(current, next, RotateState.SPAWN);

        if (cache.left(current) && cache.left(next))
            foundNormal(current, next, RotateState.LEFT);

        if (cache.right(current) && cache.right(next))
            foundNormal(current, next, RotateState.RIGHT);

        if (cache.reverse(current) && cache.reverse(next))
            foundNormal(current, next, RotateState.REVERSE);
    }

    private void foundNormal(Coordinate current, Coordinate next, RotateState rotateState) {
        Pentmino pentmino = new Pentmino(type, rotateState);
        if (fieldPentmino.isOnGround(next, pentmino)) {
            MotionState state = MotionStateFactory.normalLock(current, next, rotateState);
            saveState(state);
            nextStep(state);
        } else {
            MotionState state = MotionStateFactory.normalPass(current, next, rotateState);
            saveState(state);
            nextStep(state);
        }
    }

    private void checkRotate(MotionState state) {
        Coordinate current = state.next;
        RotateState currentRotation = state.nextRotation;
        checkRotate(current, currentRotation, RotateDirection.RIGHT);
        checkRotate(current, currentRotation, RotateDirection.LEFT);
    }

    private void checkRotate(Coordinate current, RotateState start, RotateDirection direction) {
        Pentmino pentmino = new Pentmino(type, start);
        Coordinate next = fieldPentmino.rotate(current, pentmino, direction);
        RotateState end = RotateStateSequence.rotate(start, direction);
        if (isRotateable(next))
            foundRotate(current, next, start, end);
    }

    private boolean isRotateable(Coordinate next) {
        return next != Coordinate.NULL;
    }

    private void foundRotate(Coordinate current, Coordinate next, RotateState start, RotateState end) {
        Pentmino pentmino = new Pentmino(type, end);
        if (fieldPentmino.isOnGround(next, pentmino)) {
            MotionState state = MotionStateFactory.rotateLock(current, next, start, end);
            saveState(state);
            nextStep(state);
        } else {
            MotionState state = MotionStateFactory.rotatePass(current, next, start, end);
            saveState(state);
            nextStep(state);
        }
    }

    private void saveState(MotionState state) {
        Coordinate current = state.next;

        if (!isInField(current))
            return;

//        // 保存済みか確認
//        RotateState nextRotation = state.nextRotation;
//        boolean succeed = saved.add(current, nextRotation);
//        if (!succeed)
////            return;

        if (state.type == StateType.NORMAL_LOCK || state.type == StateType.ROTATE_LOCK)
            states.add(state);
    }

    private void nextStep(MotionState state) {
        Coordinate current = state.next;

        if (!isInField(current))
            return;

        // 保存済みか確認
        RotateState nextRotation = state.nextRotation;
        boolean succeed = visited.add(current, nextRotation);
        if (!succeed)
            return;

        candidates.add(state);
    }

    public List<MotionState> getStates() {
        return states;
    }

//    public List<Coordinate> getPositions() {
//        List<Coordinate> next = new ArrayList<>();
//        for (int row = 0; row < FIELD_HEIGHT; row++) {
//            for (int col = 0; col < FIELD_WIDTH; col++) {
//                if (saved.contains(row, col))
//                    next.add(new Coordinate(col, row));
//            }
//        }
//        return next;
//    }

    public boolean isSearched() {
        return this.isSearch;
    }

    private class Cache {
        public static final int FIELD_WIDTH = 10;
        public static final int FIELD_HEIGHT = 20;

        private boolean[][] spawn;
        private boolean[][] left;
        private boolean[][] right;
        private boolean[][] reverse;

        public Cache(Field field, PentminoType type) {
            this.spawn = fix(field, type, RotateState.SPAWN);
            this.left = fix(field, type, RotateState.LEFT);
            this.right = fix(field, type, RotateState.RIGHT);
            this.reverse = fix(field, type, RotateState.REVERSE);
        }

        private boolean[][] fix(Field field, PentminoType type, RotateState rotateState) {
            Pentmino spawn = new Pentmino(type, rotateState);
            Range range = spawn.getFieldRange(FIELD_WIDTH, FIELD_HEIGHT);
            boolean[][] pass = new boolean[FIELD_HEIGHT][FIELD_WIDTH];
            for (int x = range.minX; x <= range.maxX; x++)
                for (int y = range.minY; y < FIELD_HEIGHT; y++)
                    pass[y][x] = fieldPentmino.existsSpace(x, y, spawn);
            return pass;
        }

        public boolean spawn(Coordinate coordinate) {
            return spawn(coordinate.x, coordinate.y);
        }

        private boolean spawn(int x, int y) {
            return spawn[y][x];
        }

        public boolean left(Coordinate coordinate) {
            return left(coordinate.x, coordinate.y);
        }

        private boolean left(int x, int y) {
            return left[y][x];
        }

        public boolean right(Coordinate coordinate) {
            return right(coordinate.x, coordinate.y);
        }

        private boolean right(int x, int y) {
            return right[y][x];
        }

        public boolean reverse(Coordinate coordinate) {
            return reverse(coordinate.x, coordinate.y);
        }

        private boolean reverse(int x, int y) {
            return reverse[y][x];
        }

        public void print() {
            boolean[][] flag = spawn;
            for (int y = flag.length - 1; 0 <= y; y--) {
                for (int x = 0; x < flag[y].length; x++)
                    System.out.print(flag[y][x] ? "o" : ".");
                System.out.println();
            }
        }
    }

    private class RotateStateManager {
        private final EnumSet[][] sets;

        public RotateStateManager(int width, int height) {
            this.sets = new EnumSet[height][width];
        }

        public boolean contains(Coordinate coordinate, RotateState state) {
            return contains(coordinate.x, coordinate.y, state);
        }

        public boolean contains(int x, int y, RotateState state) {
            return sets[y][x] != null && sets[y][x].contains(state);
        }

        public boolean add(Coordinate coordinate, RotateState state) {
            int x = coordinate.x;
            int y = coordinate.y;

            if (sets[y][x] == null) {
                sets[y][x] = EnumSet.of(state);
            } else {
                EnumSet flag = sets[y][x];
                if (contains(coordinate, state))
                    return false;
                // noinspection unchecked
                flag.add(state);
            }

            return true;
        }

        public void print() {
            for (int y = sets.length - 1; 0 <= y; y--) {
                for (int x = 0; x < sets[y].length; x++)
                    System.out.print(sets[y][x] != null ? "o" : ".");
                System.out.println();
            }
        }
    }
}