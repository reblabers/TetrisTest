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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;
        return y == that.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
