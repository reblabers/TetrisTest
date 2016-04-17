package pentmino.obj;

/**
 * 座標を表現
 */
public class Coordinate {
    public static final Coordinate NULL = new Coordinate(-1, -1);

    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate down() {
        return new Coordinate(x, y - 1);
    }

    public Coordinate right() {
        return new Coordinate(x + 1, y);
    }

    public Coordinate left() {
        return new Coordinate(x - 1, y);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
