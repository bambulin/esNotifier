package cz.bambula.esnotifier.trigonometry;

/**
 * Created by tkozel on 10/20/16.
 */

public class EscapeHalfline extends Line {
    private Point startPoint;

    public EscapeHalfline(Point startPoint) {
        super(startPoint, new Point(startPoint.getX() + 1, startPoint.getY()));
        this.startPoint = startPoint;
    }

    public boolean hasIntersectionWith(LineSegment lineSegment) {
        if (containsPoint(lineSegment.getStartPoint()) || containsPoint(lineSegment.getEndPoint())) {
            return true;
        }
        // both line segment points are above ore below the escape halfline
        if (DoubleComparator.compare(startPoint.getY(), lineSegment.getStartPoint().getY()) < 0 &&
            DoubleComparator.compare(startPoint.getY(), lineSegment.getEndPoint().getY()) < 0 ||
            DoubleComparator.compare(startPoint.getY(), lineSegment.getStartPoint().getY()) > 0 &&
            DoubleComparator.compare(startPoint.getY(), lineSegment.getEndPoint().getY()) > 0) {
            return false;
        }

        Point intersection = findIntersection(lineSegment);
        if (intersection == null) {
            return false;
        }
        return containsPoint(intersection) && lineSegment.containsPoint(intersection);
    }

    private boolean containsPoint(Point point) {
        return DoubleComparator.compare(startPoint.getX(), point.getX()) <= 0 &&
                DoubleComparator.compare(startPoint.getY(), point.getY()) == 0;
    }
}
