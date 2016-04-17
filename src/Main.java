import core.*;
import pentmino.Pentmino;
import pentmino.RotateStateSequence;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateDirection;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;
import pentmino.obj.MotionState;
import pentmino.obj.Range;

import java.util.*;

public class Main {
    public static void main(String[] args) {
//        example1();
//        example2();

        // 次の移動先を列挙
//        exampleNext();

        // 7種ペントミノをランダムにおいてテト譜を作成
        exampleActions();
    }

    private static void example1() {
        System.out.println("# Init");
        FieldPentmino field =  createTetrisField();
        field.show();

        // 5列にTをハードドロップ
        System.out.println("# Drop T-Reverse on 5");
        Pentmino pentmino = new Pentmino(PentminoType.T, RotateState.REVERSE);
        int nextX = 5;
        int nextY = field.harddrop(nextX, pentmino);
        field.put(nextX, nextY, pentmino);
        field.show();

        // 0列にIをハードドロップ
        System.out.println("# Drop I-Left on 0");
        Pentmino pentmino2 = new Pentmino(PentminoType.I, RotateState.LEFT);
        int nextX2 = 0;
        int nextY2 = field.harddrop(nextX2, pentmino2);
        field.put(nextX2, nextY2, pentmino2);
        field.clearLine();
        field.show();
    }

    private static void example2() {
        System.out.println("# Init");
        FieldPentmino field =  createTSFinField();
        field.show();

        // (4, 3)で右回転
        System.out.println("# T turn right on (4, 3)");
        PentminoType type = PentminoType.T;
        RotateState state = RotateState.REVERSE;
        RotateDirection rotate = RotateDirection.RIGHT;
        Pentmino pentmino = new Pentmino(type, state);

        // 移動先の計算
        RotateState nextRotate = RotateStateSequence.rotate(state, rotate);
        Coordinate nextCoordinate = field.rotate(4, 3, pentmino, rotate);

        // 確定
        field.put(nextCoordinate.x, nextCoordinate.y, new Pentmino(type, nextRotate));
        field.show();
    }

    private static void exampleNext() {
        FieldPentmino fieldPentmino = createTetrisField();

        // 移動先候補リスト
        PentminoType type = PentminoType.I;
        Next next = new Next(fieldPentmino, type);
        next.search();
        List<MotionState> states = next.getStates();

        // 移動先のX座標順にソート表示
        Collections.sort(states, (a, b) -> Integer.compare(a.next.x, b.next.x));
        for (MotionState state : states) {
            System.out.println(state);
        }
    }

    private static void exampleActions() {
        List<Action> actions = createRandomActions();
        Tetfu tetfu = new Tetfu();
        String code = tetfu.parse(actions);
        System.out.println("http://fumen.zui.jp/?d115@" + code);

        // 最後のフィールドを表示
        Action lastAction = actions.get(actions.size() - 1);
        lastAction.getLockedField().show();
    }

    private static FieldPentmino createTetrisField() {
        Field field = new Field();

        for (int x = 1; x < 10; x++) {
            field.put(x, 0);
            field.put(x, 1);
            field.put(x, 2);
            field.put(x, 3);
        }

        return new FieldPentmino(field);
    }

    private static FieldPentmino createTSFinField() {
        Field field = new Field();

        for (int x = 0; x < 4; x++) {
            field.put(x, 0);
            field.put(x, 1);
        }

        for (int x = 6; x < 10; x++) {
            field.put(x, 0);
            field.put(x, 1);
            field.put(x, 2);
            field.put(x, 3);
            field.put(x, 4);
        }

        field.put(4, 0);
        field.put(5, 4);
        field.put(4, 4);

        return new FieldPentmino(field);
    }

    private static List<Action> createRandomActions() {
        // Create Filed
        Field field = new Field();
        FieldPentmino fieldPentmino = new FieldPentmino(field);

        // ミノと回転の作成
        List<PentminoType> types = Arrays.asList(PentminoType.values());
        Collections.shuffle(types);
        List<RotateState> rotateStates = Arrays.asList(RotateState.values());

        // 動作の記録
        List<Action> actions = new ArrayList<>();
        Random random = new Random();

        // 最初の動作
        Action prev = Action.createFirstAction(fieldPentmino);
        actions.add(prev);

        for (PentminoType type : types) {
            // 回転方向を決める
            Collections.shuffle(rotateStates);
            RotateState rotateState = rotateStates.get(0);

            // ペントミノの作成
            Pentmino pentmino = new Pentmino(type, rotateState);

            // 次の移動先
            Range range = pentmino.getFieldRange(10, 20);
            double value = random.nextDouble();
            int nextX = (int) ((range.maxX - range.minX + 1) * value + range.minX);
            int nextY = fieldPentmino.harddrop(nextX, pentmino);

            // ペントミノを置いてライン消去
            fieldPentmino.put(nextX, nextY, pentmino);
            fieldPentmino.clearLine();

            // 動作の記録
            Action newAction = new Action(nextX, nextY, type, rotateState, fieldPentmino, prev);
            actions.add(newAction);
            prev = newAction;
        }

        return actions;
    }
}