import core.Field;
import core.FieldPentmino;
import core.Next;
import pentmino.Pentmino;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;
import pentmino.obj.MotionState;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Search {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Field field = new Field();
        FieldPentmino fieldPentmino = new FieldPentmino(field);
        LinkedList<PentminoType> bags = createBags();
        Search search = new Search();
        search.run(fieldPentmino, bags);
    }

    private static LinkedList<PentminoType> createBags() {
        LinkedList<PentminoType> bags = new LinkedList<>();
        bags.addAll(Arrays.asList(PentminoType.I, PentminoType.J, PentminoType.L, PentminoType.O, PentminoType.S, PentminoType.Z, PentminoType.T));
        return bags;
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(6);
    private final Set<String> searched = Collections.synchronizedSet(new HashSet<>());
    private final List<Action> results = Collections.synchronizedList(new ArrayList<>());
    private LinkedList<PentminoType> bags;
    private final AtomicInteger counter = new AtomicInteger(0);

    public void run(FieldPentmino fieldPentmino, LinkedList<PentminoType> bags) throws ExecutionException, InterruptedException {
        this.bags = bags;

        PentminoType hold = bags.get(0);
        int bagIndex = 1;
        Action action = new Action(fieldPentmino);
        run(action, hold, bagIndex);

        int current;
        do {
            Thread.sleep(TimeUnit.SECONDS.toMillis(2L));
            System.out.println("---");
            current = counter.get();
            System.out.println("tasks = " + current);
            System.out.println("searched = " + searched.size());
            System.out.println("results = " + results.size());
        } while (current != 0);
    }

    private void run(Action parent, PentminoType hold, int bagIndex) {
        if (bagIndex < bags.size())
            normal(parent, hold, bagIndex);
        hold(parent, hold, bagIndex);
    }

    private void normal(Action parent, PentminoType hold, int bagIndex) {
        PentminoType type = bags.get(bagIndex);
        int leftPentmino = bags.size() - bagIndex + 1;
        List<Action> actions = getActions(parent, type, leftPentmino);
        registerTasks(actions, hold, bagIndex + 1);
    }

    private void hold(Action parent, PentminoType hold, int bagIndex) {
        int leftPentmino = bags.size() - bagIndex + 1;
        List<Action> actions = getActions(parent, hold, leftPentmino);

        if (bagIndex < bags.size()) {
            PentminoType newHold = bags.get(bagIndex);
            registerTasks(actions, newHold, bagIndex + 1);
        } else {
            for (Action action : actions) {
                if (action != null) {
                    int lowDigCount = action.fieldPentmino.lowDigCount();
                    if (lowDigCount <= 0) {
//                        System.out.println();
//                        System.out.println("@@@");
//                        action.fieldPentmino.show();
//                        action.show();
                        results.add(action);
                    }
                }
            }
        }
    }

    private List<Action> getActions(Action parent, PentminoType type, int leftPentmino) {
        FieldPentmino fieldPentmino = parent.getFieldPentmino();
        Next next = new Next(fieldPentmino, type);
        next.search();
        List<MotionState> states = next.getStates();

        List<Action> actions = new LinkedList<>();
        for (MotionState state : states) {
            Action action = createNextFieldPentmino(parent, type, fieldPentmino, state, leftPentmino);
            if (action != null)
                actions.add(action);
        }
        return actions;
    }

    private void registerTasks(List<Action> actions, PentminoType nextHold, int nextBagIndex) {
        for (Action action : actions) {
            String key = action.getFieldPentmino().toBase64() + nextHold.name() + nextBagIndex;
            if (searched.contains(key))
                continue;
            searched.add(key);
            startTask(() -> run(action, nextHold, nextBagIndex));
        }
    }

    private void startTask(Runnable runnable) {
        counter.incrementAndGet();
        executorService.submit(() -> {
            runnable.run();
            counter.decrementAndGet();
        });
    }

    private Action createNextFieldPentmino(Action parent, PentminoType type, FieldPentmino fieldPentmino, MotionState state, int leftPentmino) {
        Coordinate coordinate = state.next;
        RotateState rotation = state.nextRotation;
        Pentmino pentmino = new Pentmino(type, rotation);
        FieldPentmino newFieldPentmino = fieldPentmino.copy();
        newFieldPentmino.put(coordinate, pentmino);

        int sumDeleteLine = newFieldPentmino.clearLine() + parent.sumDeleteLine;

        int lowDigCount = newFieldPentmino.lowDigCount();

        if (4 < sumDeleteLine + newFieldPentmino.maxHeight() || Math.min(2.0, leftPentmino) < lowDigCount)
            return null;
        return new Action(newFieldPentmino, type, coordinate, rotation, parent, sumDeleteLine);
    }

    private class Action {
        private final FieldPentmino fieldPentmino;
        private final PentminoType type;
        private final Coordinate coordinate;
        private final RotateState rotate;
        private final Action parent;
        private final int sumDeleteLine;

        public Action(FieldPentmino fieldPentmino) {
            this(fieldPentmino, null, null, null, null, 0);
        }

        public Action(FieldPentmino fieldPentmino, PentminoType type, Coordinate coordinate, RotateState rotate, Action parent, int sumDeleteLine) {
            this.fieldPentmino = fieldPentmino;
            this.type = type;
            this.coordinate = coordinate;
            this.rotate = rotate;
            this.parent = parent;
            this.sumDeleteLine = sumDeleteLine;
        }

        public FieldPentmino getFieldPentmino() {
            return fieldPentmino.copy();
        }

        public void show() {
            if (parent == null)
                return;
            parent.show();
            System.out.print(type.name() + " ");
        }
    }
}
