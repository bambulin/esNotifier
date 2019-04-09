package cz.bambula.esnotifier.trigonometry;

/**
 * Created by tkozel on 10/20/16.
 */
public class LineSegment extends Line {
    // starting point
    private Point startPoint;
    // ending point
    private Point endPoint;

    public LineSegment(Point startPoint, Point endPoint) {
        super(startPoint, endPoint);
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public boolean hasIntersectionWith(LineSegment lineSegment) {
        if (startPoint.equals(lineSegment.startPoint) || endPoint.equals(lineSegment.endPoint) ||
                startPoint.equals(lineSegment.endPoint) || endPoint.equals(lineSegment.startPoint)) {
            return true;
        }
        Point intersection = findIntersection(lineSegment);
        if (intersection == null) {
            return false;
        } else if (intersection.isInfinite()) {
            return containsPoint(lineSegment.startPoint) || containsPoint(lineSegment.endPoint) ||
                    lineSegment.containsPoint(startPoint) || lineSegment.containsPoint(endPoint);
        } else {
            return containsPoint(intersection) && lineSegment.containsPoint(intersection);
        }
    }

    public boolean containsPoint(Point p) {
        double highXLimit;
        double lowXLimit;
        double highYLimit;
        double lowYLimit;
        if (DoubleComparator.compare(startPoint.getX(), endPoint.getX()) > 0) {
            highXLimit = startPoint.getX();
            lowXLimit = endPoint.getX();
        } else {
            highXLimit = endPoint.getX();
            lowXLimit = startPoint.getX();
        }
        if (DoubleComparator.compare(startPoint.getY(), endPoint.getY()) > 0) {
            highYLimit = startPoint.getY();
            lowYLimit = endPoint.getY();
        } else {
            highYLimit = endPoint.getY();
            lowYLimit = startPoint.getY();
        }
        return DoubleComparator.compare(p.getX(), lowXLimit) >= 0 && DoubleComparator.compare(p.getX(), highXLimit) <= 0 &&
               DoubleComparator.compare(p.getY(), lowYLimit) >= 0 && DoubleComparator.compare(p.getY(), highYLimit) <= 0;
    }

}
