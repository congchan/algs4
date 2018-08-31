import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Data type represent a set of points in the unit square
 * (all points have x- and y-coordinates between 0 and 1)
 * Use mutable data type PointSET.java that represents a set of points in the unit square.
 * Using a red–black BST
 * Corner cases.
 *      Throw a java.lang.IllegalArgumentException if any argument is null.
 * Performance requirements.
 *      Your implementation should support insert() and contains() in time proportional
 *      to the logarithm of the number of points in the set in the worst case;
 *      it should support nearest() and range() in time proportional to
 *      the number of points in the set.
 */

public class PointSET {
    private TreeSet<Point2D> points;

    /** construct an empty set of points */
    public PointSET() {
        points = new TreeSet<>();

    }

    /**  is the set empty?*/
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**  number of points in the set */
    public int size() {
        return points.size();
    }

    /** add the point to the set (if it is not already in the set) */
    public void insert(Point2D p) {
        if (p == null) { throw new NullPointerException(); }
        if (!points.contains(p)) {
            points.add(p);
        }
    }

    /** does the set contain point p? */
    public boolean contains(Point2D p) {
        if (p == null) { throw new NullPointerException(); }
        return points.contains(p);
    }

    /** draw all points to standard draw */
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    /** all points that are inside the rectangle (or on the boundary) */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) { throw new NullPointerException(); }
        List<Point2D> pointsInside = new LinkedList<>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                pointsInside.add(p);
            }
        }
        return pointsInside;
    }

    /** a nearest neighbor in the set to point p; null if the set is empty */
    public Point2D nearest(Point2D p) {
        if (p == null) { throw new NullPointerException(); }
        Point2D nearestPoint = null;
        for (Point2D q : points) {
            if (nearestPoint != null) {
                if (p.distanceSquaredTo(q) < p.distanceSquaredTo(nearestPoint)) {
                    nearestPoint = q;
                }
            } else {
                nearestPoint = q;
            }
        }
        return nearestPoint;
    }

    /** unit testing of the methods (optional) */
    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        PointSET PointSET = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            PointSET.insert(p);
            brute.insert(p);
        }

        double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
        double x1 = 0.0, y1 = 0.0;      // current location of mouse
        boolean isDragging = false;     // is the user dragging a rectangle

        // draw the points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        brute.draw();
        StdDraw.show();

        // process range search queries
        StdDraw.enableDoubleBuffering();
        while (true) {

            // user starts to drag a rectangle
            if (StdDraw.isMousePressed() && !isDragging) {
                x0 = x1 = StdDraw.mouseX();
                y0 = y1 = StdDraw.mouseY();
                isDragging = true;
            }

            // user is dragging a rectangle
            else if (StdDraw.isMousePressed() && isDragging) {
                x1 = StdDraw.mouseX();
                y1 = StdDraw.mouseY();
            }

            // user stops dragging rectangle
            else if (!StdDraw.isMousePressed() && isDragging) {
                isDragging = false;
            }

            // draw the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw the rectangle
            RectHV rect = new RectHV(Math.min(x0, x1), Math.min(y0, y1),
                    Math.max(x0, x1), Math.max(y0, y1));
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius();
            rect.draw();

            // draw the range search results for brute-force data structure in red
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            for (Point2D p : brute.range(rect))
                p.draw();

            // draw the range search results for kd-tree in blue
            StdDraw.setPenRadius(0.02);
            StdDraw.setPenColor(StdDraw.BLUE);
            for (Point2D p : PointSET.range(rect))
                p.draw();

            StdDraw.show();
            StdDraw.pause(20);
        }

    }
}
