import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Given a point p, determines whether p participates in a set of 4 or more collinear points.
 * 1. Think of p as the origin.
 * 2. For each other point q, determine the slope it makes with p.
 * 3. Sort the points according to the slopes they makes with p.
 * 4. Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p.
 *      If so, these points, together with p, are collinear.
 * Applying this method for each of the n points in turn.
 * The algorithm solves the problem because points that have equal slopes with respect to p are collinear,
 *      and sorting brings such points together.
 * The algorithm is fast because the bottleneck operation is sorting.
 *
 * Corner cases.
 *      Throw a java.lang.IllegalArgumentException if the argument to the constructor is null,
 *      if any point in the array is null,
 *      or if the argument to the constructor contains a repeated point.
 * Performance requirement.
 *      The order of growth of the running time of your program should be n^2 log n in the worst case
 *      and it should use space proportional to n plus the number of line segments returned.
 *      Should work properly even if the input has 5 or more collinear points.
 *
 */


public class FastCollinearPoints {
    private static final int N = 4;
    private final ArrayList<LineSegment> ls = new ArrayList<>();
    private Point[] sortedBySlopePoints;
    private Point[] sortedPoints;

    /**
     * finds all line segments containing 4 or more points
     * As Arrays.sort() is guaranteed to be stable:
     *      equal elements will not be reordered as a result of the sort.
     *      So the input points array is already sorted by points natural order
     * @param points
     */
    public FastCollinearPoints(Point[] points) {

        if (isInvalid(points)) { throw new java.lang.IllegalArgumentException(); }
        if (points.length < 4) { return; }

        for (Point p : sortedPoints) {
            // copy it for each p, to keep the original natural order
            sortedBySlopePoints = sortedPoints.clone();
            Arrays.sort(sortedBySlopePoints, p.slopeOrder()); // the first one is p itself
            int k = 2; // any two point could form a line
            int begin = 1;
            int end = 1;
            double slopeA;
            double slopeB = p.slopeTo(sortedBySlopePoints[1]);
            for (int i = 1; i < sortedBySlopePoints.length - 1; i++) {
                slopeA = slopeB;
                slopeB = p.slopeTo(sortedBySlopePoints[i + 1]);

                if (Double.compare(slopeA, slopeB)  == 0) {
                    k++;
                    end = i + 1;
                    if (i + 1 < sortedBySlopePoints.length - 1) { continue; }
                }

                if (k >= N) { // a valid collinear
                    addSegments(p, begin, end);
                }
                k = 2;
                begin = i + 1;

            }
        }

    }

    /**
     * check if new found collinear points already exist in the LineSegment
     *      1. Since the every possible segment is created by points it contains,
     *      2. and we iterate through the sortedPoints to find segment
     *      3. so every non-duplicate new segment is created from its smallest point member
     *      4. any duplicate segment is created later by its other member other than the smallest
     *      if not, add in
     * @param start, end
     */
    private void addSegments(Point cur, int start, int end) {
        if (cur.compareTo(sortedBySlopePoints[start]) > 0) { return; }
        ls.add(new LineSegment(cur, sortedBySlopePoints[end]));
    }


    /**
     * return true
     * if the argument to the constructor is null,
     * if any point in the array is null,
     * or if the argument to the constructor contains a repeated point.
     * @param points
     * @return boolean
     */
    private boolean isInvalid(Point[] points) {
        if (points == null) { return true; }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) { return true; }
        }
        sortedPoints = new Point[points.length];
        sortedPoints = points.clone();
        Arrays.sort(sortedPoints); // Sorts the specified array of objects into ascending order
        for (int i = 0; i < sortedPoints.length - 1; i++) {
            if (sortedPoints[i].compareTo(sortedPoints[i + 1]) == 0) { return true; }
        }
        sortedBySlopePoints = new Point[points.length];
        return false;
    }

    /** the number of line segments
     *
     * @return
     */
    public int numberOfSegments() {
        return ls.size();
    }


    /** the line segments
     *  include each maximal line segment containing 4 (or more) points exactly once.
     *  For example, if 5 points appear on a line segment in the order p→q→r→s→t,
     *  then do not include the subsegments p→s or q→t.
     *
     *
     * @return
     */
    public LineSegment[] segments() {
        return ls.toArray(new LineSegment[ls.size()]);
    }

    /** takes the name of an input file as a command-line argument;
     * read the input file; prints to standard output the line segments that your program discovers, one per line;
     * and draws to standard draw the line segments.
     * @param args
     */
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

    }

}
