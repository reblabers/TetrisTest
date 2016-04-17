package pentmino.obj;

public class Range {
    public final int minX;
    public final int maxX; // include
    public final int minY;
    public final int maxY; // include

    public Range(int minX, int maxX, int minY, int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public String toString() {
        return "Range{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                '}';
    }
}
