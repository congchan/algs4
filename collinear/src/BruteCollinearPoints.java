import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Examines 4 points at a time and checks whether they all lie on the same line segment, return all such line segments.
 * To check whether the 4 points p, q, r, and s are collinear,
 * check whether the three slopes between p and q, between p and r, and between p and s are all equal.
 * Performance requirement.
 *      The order of growth of the running time of your program should be n^4 in the worst case
 *      and it should use space proportional to n plus the number of line segments returned.
 */

public class BruteCollinearPoints {
    private static final int N = 4;
    private final ArrayList<LineSegment> ls;
    private Point[] buffer;

    /**
     * finds all line segments containing 4 points
     * Throw a java.lang.IllegalArgumentException if input is invalid
     * There are two easy opportunities.
     *      First, you can iterate through all combinations of 4 points (N choose 4) instead of all 4 tuples (N^4),
     *              saving a factor of 4! = 24.
     *      Second, you don't need to consider whether 4 points are collinear if you already know that
     *              the first 3 are not collinear; this can save you a factor of N on typical inputs.
     * @param points
     */
    public BruteCollinearPoints(Point[] points) {
        if (isInvalid(points)) { throw new java.lang.IllegalArgumentException(); }
        ls = new ArrayList<>();
        double pq;
        double pr;
        double ps;

        for (int p = 0; p <= buffer.length - N; p++) {
            for (int q = buffer.length - N + 1; q > p; q--) {
                for (int r = buffer.length - 2; r > q; r--) {
                    for (int s = buffer.length - 1; s > r; s--) {
                        pq = buffer[p].slopeTo(buffer[q]);
                        pr = buffer[p].slopeTo(buffer[r]);
                        if (Double.compare(pq, pr) == 0) {
                            ps = buffer[p].slopeTo(buffer[s]);
                            if (Double.compare(pq, ps) == 0) {
                                ls.add(new LineSegment(buffer[p], buffer[s]));
                            }
                        }
                    }
                }
            }
        }

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
        buffer = new Point[points.length];
        System.arraycopy(points, 0, buffer, 0, points.length);
        Arrays.sort(buffer); // Sorts the specified array of objects into ascending order
        for (int i = 0; i < points.length - 1; i++) {
            if (buffer[i].compareTo(buffer[i + 1]) == 0) { return true; }
        }
        return false;
    }


    // the number of line segments
    public int numberOfSegments() {
        return ls.size();
    }

    /**
     * the line segments, include each line segment containing 4 points exactly once.
     * If 4 points appear on a line segment in the order p→q→r→s,
     * then you should include either the line segment p→s or s→p (but not both)
     * and you should not include subsegments such as p→r or q→r.
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

    }
}
