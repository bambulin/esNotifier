package cz.bambula.esnotifier.trigonometry;

/**
 * Created by tkozel on 10/20/16.
 */

public class Line {
    // line parameters for equasion y = ax + b
    private double a;
    private double b;

    private boolean vertical;
    private double xForVertical;

    public Line(Point p1, Point p2) {
        if (DoubleComparator.compare(p1.getX(), p2.getX()) == 0) {
            vertical = true;
            xForVertical = p1.getX();
        } else {
            a = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
            b = p1.getY() - a * p1.getX();
            vertical = false;
        }
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public boolean isVertical() {
        return vertical;
    }

    public double getXForVertical() {
        return xForVertical;
    }

    public Point findIntersection(Line line) {
        if (vertical || line.isVertical()) {
            if (vertical && line.isVertical()) {
                if (DoubleComparator.compare(xForVertical, line.xForVertical) != 0) {
                    // parallel verticals
                    return null;
                } else {
                    // identical verticals
                    return Point.INFINITE;
                }
            } else {
                Line verticalLine;
                Line nonVertical;
                if (vertical) {
                    verticalLine = this;
                    nonVertical = line;
                } else {
                    verticalLine = line;
                    nonVertical = this;
                }
                double intersecY = nonVertical.a * verticalLine.xForVertical + nonVertical.b;
                return new Point(verticalLine.xForVertical, intersecY);
            }
        } else if (DoubleComparator.compare(a, line.a) == 0) {
            double c = Math.ulp(b);
            if (DoubleComparator.compare(b, line.getB()) == 0) {
                // lines are identical
                return Point.INFINITE;
            } else {
                // lines are parallel
                return null;
            }
        }
        double intersecX = (b - line.b) / (line.a - a);
        return new Point(intersecX, a * intersecX + b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Line line = (Line) o;

        if (vertical != line.vertical) {
            return false;
        }
        if (vertical) {
            return DoubleComparator.compare(line.xForVertical, xForVertical) == 0;
        }
        return DoubleComparator.compare(line.a, a) == 0 && DoubleComparator.compare(line.b, b) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(a);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (vertical ? 1 : 0);
        temp = Double.doubleToLongBits(xForVertical);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
