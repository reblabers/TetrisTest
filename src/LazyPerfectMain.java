import core.*;
import pentmino.Pentmino;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;
import pentmino.obj.MotionState;

import java.util.*;

public class LazyPerfectMain {
    public static void main(String[] args) {
        // フィールドの作成
        FieldPentmino field = createField();
        field.show();

        // 4ミノの順番を列挙
        Combinations<PentminoType> combinations = new Combinations<>(PentminoType.values(), 4);
        ArrayList<List<PentminoType>> all = new ArrayList<>();
        for (List<PentminoType> combination : combinations) {
            PentminoType[] types = new PentminoType[combination.size()];
            combination.toArray(types);
            Permutation<PentminoType> permutations = new Permutation<>(types);
            do {
                all.add(permutations.next());
            } while (permutations.hasNext());
        }

        // I + 4ミノの順番から、ホールドも含めた4ミノの順番をマップ
        HashMap<PentminoSet5, List<PentminoSet4>> fourMap = new HashMap<>();
        for (List<PentminoType> types : all) {
            List<PentminoSet4> bags = create4Bags(types.get(0), types.get(1), types.get(2), types.get(3));
            fourMap.put(new PentminoSet5(types.get(0), types.get(1), types.get(2), types.get(3)), bags);
        }
        System.out.println(fourMap.size());

        // 4ミノの順番のみを取得
        HashSet<PentminoSet4> sets = new HashSet<>();
        for (List<PentminoSet4> bag : fourMap.values()) {
            for (PentminoSet4 pentminoSet : bag) {
                sets.add(pentminoSet);
            }
        }
        System.out.println(sets.size());

        // 4ミノ順からパフェをできるパターンを取得
        int count = 0;
        HashMap<PentminoSet4, Result> results = new HashMap<>();
        for (PentminoSet4 set : sets) {
            System.out.print("\r" + count + ": " + set);
            Result result = check(field.copy(), set);
            if (!result.equals(Result.NONE))
                results.put(set, result);
            count++;
        }

        // 4ミノの結果をテト譜で出力
        for (Map.Entry<PentminoSet4, Result> result : results.entrySet()) {
            System.out.print(result.getKey() + ": ");
            showTetfu(result.getValue().getActions());
        }

        System.out.println(results.size());

        // 5ミノの結果を抽出
        HashSet<PentminoSet5> oks = new HashSet<>();
        LOOP:
        for (Map.Entry<PentminoSet5, List<PentminoSet4>> entry : fourMap.entrySet()) {
            List<PentminoSet4> list = entry.getValue();
            for (PentminoSet4 pentminoSet4 : results.keySet()) {
                if (list.contains(pentminoSet4)) {
                    oks.add(entry.getKey());
                    continue LOOP;
                }
            }
        }

//        for (PentminoSet4 ok : oks) {
//            System.out.println(ok);
//        }
        System.out.println(oks.size());

        PentminoType[] types = {
                PentminoType.I,
                PentminoType.L,
                PentminoType.O,
                PentminoType.Z,
                PentminoType.T,
                PentminoType.J,
                PentminoType.S
        };
        for (int i1 = 0; i1 < types.length; i1++) {
            for (int i2 = 0; i2 < types.length; i2++) {
                if (i1 == i2)
                    continue;
                for (int i3 = 0; i3 < types.length; i3++) {
                    if (i1 == i3 || i2 == i3)
                        continue;
                    for (int i4 = 0; i4 < types.length; i4++) {
                        if (i1 == i4 || i2 == i4 || i3 == i4)
                            continue;
                        PentminoSet5 set5 = new PentminoSet5(types[i1], types[i2], types[i3], types[i4]);
                        System.out.printf("I,%s,%s,%s,%s,%s%n", types[i1], types[i2], types[i3], types[i4], oks.contains(set5));
                    }
                }
            }
        }
    }

    private static FieldPentmino createField() {
        Field field = new Field();

        for (int x = 0; x < 4; x++) {
            field.put(x, 0);
            field.put(x, 1);
            field.put(x, 2);
            field.put(x, 3);
        }

        for (int x = 8; x < 10; x++) {
            field.put(x, 0);
            field.put(x, 1);
//            field.put(x, 2);
//            field.put(x, 3);
        }
        field.put(9, 2);

        field.put(7, 0);
        field.put(7, 1);
//        field.put(7, 2);
        field.put(6, 1);

        return new FieldPentmino(field);
    }

    private static List<PentminoSet4> create4Bags(PentminoType a, PentminoType b, PentminoType c, PentminoType d) {
        PentminoType i = PentminoType.I;
        List<PentminoSet4> list = new ArrayList<>();
        list.add(new PentminoSet4(i, a, b, c));
        list.add(new PentminoSet4(i, a, b, d));
        list.add(new PentminoSet4(i, a, c, d));
        list.add(new PentminoSet4(i, a, c, b));
        list.add(new PentminoSet4(i, b, c, d));
        list.add(new PentminoSet4(i, b, c, a));
        list.add(new PentminoSet4(i, b, a, d));
        list.add(new PentminoSet4(i, b, a, c));
        list.add(new PentminoSet4(a, b, c, d));
        list.add(new PentminoSet4(a, b, c, i));
        list.add(new PentminoSet4(a, b, i, d));
        list.add(new PentminoSet4(a, b, i, c));
        list.add(new PentminoSet4(a, i, c, d));
        list.add(new PentminoSet4(a, i, c, b));
        list.add(new PentminoSet4(a, i, b, c));
        list.add(new PentminoSet4(a, i, b, d));
        return list;
    }

    private static Result check(FieldPentmino field, PentminoSet4 set) {
        PentminoType first = set.get(0);
        List<MotionState> next1 = getNext(field, first);
        for (MotionState state1 : next1) {
            FieldPentmino copy1 = increaseStep(field, first, state1);
            PentminoType second = set.get(1);
            List<MotionState> next2 = getNext(copy1, second);
            for (MotionState state2 : next2) {
                FieldPentmino copy2 = increaseStep(copy1, second, state2);
                PentminoType third = set.get(2);
                List<MotionState> next3 = getNext(copy2, third);
                for (MotionState state3 : next3) {
                    FieldPentmino copy3 = increaseStep(copy2, third, state3);
                    PentminoType fourth = set.get(3);
                    List<MotionState> next4 = getNext(copy3, fourth);
                    for (MotionState state4 : next4) {
                        FieldPentmino copy4 = increaseStep(copy3, fourth, state4);
                        if (copy4.maxHeight() == 0) {
                            return Result.createOK(field, first, state1, second, state2, third, state3, fourth, state4);
                        }
                    }
                }
            }
        }
        return Result.NONE;
    }

    private static List<MotionState> getNext(FieldPentmino field, PentminoType type) {
        Next next = new Next(field, type);
        next.search();
        List<MotionState> states = next.getStates();

        // 同じ移動先・回転をフィルタ
        HashMap<FilterKey, MotionState> map = new HashMap<>();
        for (MotionState state : states) {
            FilterKey filterKey = new FilterKey(state.next, state.nextRotation);
            if (!map.containsKey(filterKey))
                map.put(filterKey, state);
        }

        int height = field.maxHeight();
        List<MotionState> nextList = new ArrayList<>();
        for (MotionState state : map.values()) {
            FieldPentmino copy = increaseStep(field, type, state);
            if (copy.maxHeight() <= height) {
//                System.out.println(height + ">" + copy.maxHeight() + ": " + state);
                nextList.add(state);
            }
        }

        return nextList;
    }

    private static FieldPentmino increaseStep(FieldPentmino field, PentminoType type, MotionState state) {
        FieldPentmino copy = field.copy();
        copy.put(state.next, new Pentmino(type, state.nextRotation));
        copy.clearLine();
        return copy;
    }

    private static void showTetfu(List<Action> actions) {
        Tetfu tetfu = new Tetfu();
        String code = tetfu.parse(actions);
        System.out.println("http://fumen.zui.jp/?d115@" + code);
    }

    private static void showActions(List<Action> actions) {
        Action lastAction = actions.get(actions.size() - 1);
        lastAction.getLockedField().show();
    }

    private static class PentminoSet4 {
        private final List<PentminoType> types;

        PentminoSet4(PentminoType first, PentminoType second, PentminoType third, PentminoType four) {
            this.types = Arrays.asList(first, second, third, four);
        }

        PentminoType get(int index) {
            return types.get(index);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PentminoSet4 that = (PentminoSet4) o;

            return types != null ? types.equals(that.types) : that.types == null;

        }

        @Override
        public int hashCode() {
            return types != null ? types.hashCode() : 0;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s %s", types.get(0), types.get(1), types.get(2), types.get(3));
        }

        public int size() {
            return types.size();
        }
    }

    private static class PentminoSet5 {
        private final List<PentminoType> types;

        PentminoSet5(PentminoType first, PentminoType second, PentminoType third, PentminoType four) {
            this.types = Arrays.asList(PentminoType.I, first, second, third, four);
        }

        PentminoType get(int index) {
            return types.get(index);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PentminoSet5 that = (PentminoSet5) o;

            return types != null ? types.equals(that.types) : that.types == null;

        }

        @Override
        public int hashCode() {
            return types != null ? types.hashCode() : 0;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s %s %s", types.get(0), types.get(1), types.get(2), types.get(3), types.get(4));
        }

        public int size() {
            return types.size();
        }
    }

    private static class Result {
        static final Result NONE = new Result(null);

        private final List<Action> actions;

        static Result createOK(FieldPentmino field, PentminoType first, MotionState state1, PentminoType second, MotionState state2, PentminoType third, MotionState state3, PentminoType fourth, MotionState state4) {
            Action init = Action.createFirstAction(field);
            Action action1 = new Action(state1.next, first, state1.nextRotation, init);
            Action action2 = new Action(state2.next, second, state2.nextRotation, action1);
            Action action3 = new Action(state3.next, third, state3.nextRotation, action2);
            Action action4 = new Action(state4.next, fourth, state4.nextRotation, action3);
            List<Action> actions = Arrays.asList(action1, action2, action3, action4);
            return new Result(actions);
        }

        Result(List<Action> actions) {
            this.actions = actions;
        }

        public List<Action> getActions() {
            return actions;
        }
    }

    private static class FilterKey {
        private final Coordinate next;
        private final RotateState nextRotation;

        public FilterKey(Coordinate next, RotateState nextRotation) {

            this.next = next;
            this.nextRotation = nextRotation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FilterKey filterKey = (FilterKey) o;

            if (next != null ? !next.equals(filterKey.next) : filterKey.next != null) return false;
            return nextRotation == filterKey.nextRotation;

        }

        @Override
        public int hashCode() {
            int result = next != null ? next.hashCode() : 0;
            result = 31 * result + (nextRotation != null ? nextRotation.hashCode() : 0);
            return result;
        }
    }
}
