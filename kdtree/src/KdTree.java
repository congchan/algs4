
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;
import java.util.List;

/**
 * Data type represent a set of points in the unit square
 * (all points have x- and y-coordinates between 0 and 1)
 * using a 2d-tree to support efficient range search
 * (find all of the points contained in a query rectangle)
 * and nearest-neighbor search (find a closest point to a query point).
 * The idea is to build a BST with points in the nodes,
 * using the x- and y-coordinates of the points as keys in strictly alternating sequence.
 */
public class KdTree {
    private Node root; // root of kd tree
    private int size;
    private Point2D nearestPoint;
    private double nearestDist;

    /** construct an empty set of points */
    public KdTree() {
        root = null;
        size = 0;
    }

    /** To represent a node in a 2d-tree. One approach is to include the point, a link to the left/bottom subtree,
     * a link to the right/top subtree, and an axis-aligned rectangle corresponding to the node.
     * class is static because it does not refer to a generic Key or Value type that depends on the object associated
     * with the outer class. This saves the 8-byte inner class object overhead.
     *
     * Each node corresponds to an axis-aligned rectangle in the unit square, which encloses all of the points in its subtree.
     * The root corresponds to the unit square; the left and right children of the root corresponds to the two rectangles
     * split by the x-coordinate of the point at the root; and so forth.
     */
    private static class Node {
        private Point2D p;      // the point
        private Orientation ori; // The orientation of the node
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        /** Rect is determined by the parent's node split orientation and parent's rect. */
        public Node(Node parent, Point2D p, Orientation ori, Node lb, Node rt) {
            this.p = p;
            this.ori = ori;
            this.lb = lb;
            this.rt = rt;
            if (parent != null) {
                if (parent.ori.orientation == 'x') {
                    if (parent.p.x() > p.x()) { // left
                        this.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());
                    } else {
                        this.rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());
                    }
                } else {
                    if (parent.p.y() > p.y()) { // downside
                        this.rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),  parent.rect.xmax(), parent.p.y());
                    } else {
                        this.rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.rect.ymax());
                    }
                }
            } else {
                this.rect = new RectHV(0, 0, 1, 1);
            }
        }
    }

    /** Orientation class, the x and y fields note whether the node split vertically or horizontally.
     * Line color in red (for vertical splits), and blue (for horizontal splits)*/
    private static class Orientation {
        private char orientation;

        public Orientation() {
            orientation = 'x';
        }

        /** switch the factor */
        private Orientation switchOrientation() {
            if (orientation == 'x') {
                orientation = 'y';
            } else {
                orientation = 'x';
            }
            return this;
        }
    }

    /** Is the set empty?*/
    public boolean isEmpty() {
        return size == 0;
    }

    /**  number of points in the set */
    public int size() {
        return size;
    }

    /** Add the point to the set (if it is not already in the set)
     * At the root we use the x-coordinate
     *      (if the point to be inserted has a smaller x-coordinate than the point at the root, go left;
     *       otherwise go right);
     * then at the next level, we use the y-coordinate
     *      (if the point to be inserted has a smaller y-coordinate than the point in the node, go left;
     *       otherwise go right);
     * then at the next level the x-coordinate, and so forth.
     */
    public void insert(Point2D p) {
        if (p == null) { throw new IllegalArgumentException(); }
        root = insert(null, root, p, new Orientation());
    }

    /** Recursive helper function to perform insertion */
    private Node insert(Node parent, Node node, Point2D p, Orientation ori) {
        if (node == null) {
            size++;
            return new Node(parent, p, ori, null, null);
        }
        if (p.equals(node.p)) return node;
        double cmp = compare(p, node.p, ori);
        if (cmp < 0) node.lb = insert(node, node.lb, p, ori.switchOrientation());
        else node.rt = insert(node, node.rt, p, ori.switchOrientation());
//        size++;
        return node;
    }

    /** customized comparator with given points and and orientation*/
    private double compare(Point2D p, Point2D q, Orientation ori) {
        if (ori.orientation == 'x') {
            return p.x() - q.x();
        } else {
            return p.y() - q.y();
        }
    }

    /** does the set contain point p? */
    public boolean contains(Point2D p) {
        if (p == null) { throw new IllegalArgumentException(); }
        return contains(root, p);
    }

    /** contains helper function
     *  return false if x is null, which means p is not in subtree rooted at x.
     */
    private boolean contains(Node node, Point2D p) {
        if (node == null) return false;
        if (p.equals(node.p)) return true;
        double cmp = compare(p, node.p, node.ori);
        if (cmp < 0) return contains(node.lb, p);
        else return contains(node.rt, p);
    }

    /** Draw all points to standard draw
     * A 2d-tree divides the unit square in a simple way:
     *      all the points to the left of the root go in the left subtree;
     *      all those to the right go in the right subtree; and so forth, recursively.
     * Should draw
     *      all of the points to standard draw in black
     *      and the subdivisions in red (for vertical splits)
     *      and blue (for horizontal splits).
     * No performance requirement.
     * */
    public void draw() {
        draw(root);
    }

    /** recursive helper function for draw */
    private void draw(Node node) {
        if (node != null) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            node.p.draw();
            if (node.ori.orientation == 'x') StdDraw.setPenColor(StdDraw.RED);
            else StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            if (node.ori.orientation == 'x') {
                StdDraw.line(node.p.x(), 0, node.p.x(), 1);
            } else {
                StdDraw.line(0, node.p.y(), 1, node.p.y());
            }
            draw(node.lb);
            draw(node.rt);
        }
    }

    /** Find all points that are inside the rectangle (or on the boundary)
     * To find all points contained in a given query rectangle,
     * start at the root and recursively search for points in both subtrees using the following pruning rule:
     * If the query rectangle does not intersect the rectangle corresponding to a node,
     *      there is no need to explore that node (or its subtrees).
     * A subtree is searched only if it might contain a point contained in the query rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) { throw new IllegalArgumentException(); }
        List<Point2D> pointsInside = new LinkedList<>();
        range(root, pointsInside, rect);
        return pointsInside;
    }

    /** range helper function */
    private void range(Node node, List<Point2D> pointsInside, RectHV rect) {
        if (node != null && rect.intersects(node.rect)) {
            if (rect.contains(node.p)) { pointsInside.add(node.p); }
            range(node.lb, pointsInside, rect);
            range(node.rt, pointsInside, rect);
        }

    }

    /** Find a nearest neighbor in the set to point p; null if the set is empty
     * Start at the root and recursively search in both subtrees using the following pruning rule:
     * if the closest point discovered so far is closer than the distance between the query point and the rectangle
     * corresponding to a node, there is no need to explore that node (or its subtrees).
     * That is, search a node only if it might contain a point that is closer than the best one found so far.
     * The effectiveness of the pruning rule depends on quickly finding a nearby point.
     *      The algorithm moves down the tree recursively, with priority as insert method
     *      (i.e. it chose the order of going left or right depending on whether the point is lesser than the current
     *      node in the split dimension).
     */
    public Point2D nearest(Point2D p) {
        if (p == null) { throw new IllegalArgumentException(); }
        nearestPoint = null;
        nearestDist = Double.POSITIVE_INFINITY;
        if (root != null) { nearest(root, p); }
        return nearestPoint;
    }

    /** recursive helper function for nearest */
    private void nearest(Node node, Point2D p) {
        if (node != null && node.rect.distanceSquaredTo(p) < nearestDist) {
            double dist = node.p.distanceSquaredTo(p);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestPoint = node.p;
            }

            double cmp = compare(p, node.p, node.ori);
            if (cmp < 0) {
                nearest(node.lb, p);
                nearest(node.rt, p);
            } else {
                nearest(node.rt, p);
                nearest(node.lb, p);
            }

        }
    }


    /** unit testing of the methods (optional) */
    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        kdtree.draw();

        double x0 = 0.0, y0 = 0.0;      // initial endpoint of rectangle
        double x1 = 0.0, y1 = 0.0;      // current location of mouse

        // draw the rectangle
        RectHV rect = new RectHV(0, 0, 1, 1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        rect.draw();

        // draw the range search results for kd-tree in blue
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.BLUE);
        for (Point2D p : kdtree.range(rect))
            p.draw();

        StdDraw.show();
        StdDraw.pause(20);

        System.out.println(kdtree.contains(new Point2D(0.5, 0.25)));

    }
}
