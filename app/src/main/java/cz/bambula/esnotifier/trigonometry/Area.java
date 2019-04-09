package cz.bambula.esnotifier.trigonometry;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tkozel on 10/20/16.
 */

public class Area {
    private List<Point> corners;
    private List<LineSegment> edges;

    public Area(List<Point> corners) {
        this.corners = corners;
    }

    public List<LineSegment> getEdges() {
        if (edges == null) {
            edges = new ArrayList<>(corners.size());
            Point edgeStart;
            Point edgeEnd;
            for (int i = 0; i < corners.size(); i++) {
                edgeStart = corners.get(i);
                if (i < corners.size() - 1) {
                    edgeEnd = corners.get(i + 1);
                } else {
                    edgeEnd = corners.get(0);
                }
                edges.add(new LineSegment(edgeStart, edgeEnd));
            }
        }
        return edges;
    }

    public boolean hasIntersectionWith(@NonNull Area area) {
        // areas have crossing edges
        for (LineSegment edge: area.getEdges()) {
            if (hasIntersectionWithEdge(edge)) {
                return true;
            }
        }
        // one area lies inside another
        return (area.containsPoint(corners.get(0)) || containsPoint(area.corners.get(0)));
    }

    private boolean hasIntersectionWithEdge(@NonNull LineSegment edge) {
        for (LineSegment ownEdge: getEdges()) {
            if (ownEdge.hasIntersectionWith(edge)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsPoint(Point p) {
        EscapeHalfline escapeHalfline = new EscapeHalfline(p);
        int crossingCount = 0;
        for (LineSegment edge : edges) {
            if (escapeHalfline.hasIntersectionWith(edge)) {
                crossingCount++;
            }
        }
        return crossingCount % 2 != 0;
    }
}
