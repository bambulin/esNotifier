package cz.bambula.esnotifier.trigonometry;

/**
 * Created by tom on 5/1/15.
 */
public class Point {
    public static final Point INFINITE = new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    private double x;
    private double y;


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isInfinite() {
        return Double.isInfinite(x) && Double.isInfinite(y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        return DoubleComparator.compare(point.y, y) == 0 && DoubleComparator.compare(point.x, x) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
