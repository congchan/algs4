/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: to get the topological order,
 *  run dfs from a specific start point(col, row)
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class TopologicalOrder {
    private Canvas canvas;
    private boolean[][] marked;       // marked[v] = has v been marked in dfs?
    private double[][] distTo; // distTo[row][col] = distance of shortest s->p path
    private int[][] colTo; // colTo[row][col] = column in the row above olong the best path
    // private int[][] pre;                 // pre[v]    = preorder  number of v
    // private int[][] post;                // post[v]   = postorder number of v
    // private Queue<Pixel> preorder;   // vertices in preorder
    private Queue<Pixel> postorder;  // vertices in postorder
    // private Stack<Pixel> reversePostOrder; // vertices in reverse postorder
    // private int preCounter;            // counter for preorder numbering
    // private int postCounter;           // counter for postorder numbering

    public TopologicalOrder(Canvas canvas) {
        if (canvas == null) throw new IllegalArgumentException();
        int height = canvas.height();
        int width = canvas.width();
        if (height == 0 || width == 0) throw new IllegalArgumentException();
        this.canvas = canvas;
        // pre = new int[height][width];
        // post = new int[height][width];
        postorder = new Queue<Pixel>();
        // reversePostOrder = new Stack<Pixel>();
        // preorder = new Queue<Pixel>();
        marked = new boolean[height][width];
        // run dfs from top row (which are considered as one pixel)
        dfs(0, 0);
        // int row = 0;
        // for (int col = 0; col < canvas.width(); col++)
        //     if (!marked[row][col]) dfs(col, row);

    }


    /**
     * run DFS in pixel-weighted canvas from pixel at column x and row y
     * and compute preorder/postorder
     *
     * @param x column x
     * @param y row y
     */
    private void dfs(int x, int y) {
        marked[y][x] = true;
        // pre[y][x] = preCounter++;
        Pixel pixel = new Pixel(x, y);
        // preorder.enqueue(pixel);
        for (Pixel p : canvas.adj(x, y)) {
            if (!marked[p.getRow()][p.getCol()]) {
                dfs(p.getCol(), p.getRow());
            }
        }
        postorder.enqueue(pixel);
        // reversePostOrder.push(pixel);
        // post[y][x] = postCounter++;
    }

    /**
     * Returns the vertices in reverse postorder.
     *
     * @return the vertices in reverse postorder, as an iterable of vertices
     */
    public Iterable<Pixel> reversePost() {
        Stack<Pixel> reverse = new Stack<Pixel>();
        for (Pixel p : postorder)
            reverse.push(p);
        return reverse;
    }

    /**
     * Computes shortest paths in an pixel-weighted acyclic digraph.
     * Compute preorder and postorder for weighted pixel.
     * The weighted pixel is represented as Double[][]
     * Runs in O(E + V) time.
     */
    public void buildSingleSourceSP(int col, int row) {
        distTo = new double[canvas.height()][canvas.width()];
        colTo = new int[canvas.height()][canvas.width()];
        for (int i = 0; i < canvas.height(); i++) {
            for (int j = 0; j < canvas.width(); j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        distTo[row][col] = canvas.getEnergy(col, row);
        // relax vertices in topological order
        Iterable<Pixel> reversePostOrderP = reversePost();
        for (Pixel p : reversePostOrderP) {
            relax(p);
        }
    }

    // relax pixel p
    private void relax(Pixel p) {
        for (Pixel nextP : canvas.adj(p.getCol(), p.getRow())) {
            if (distTo[p.getRow()][p.getCol()] < Double.POSITIVE_INFINITY) {
                double candidateDist = distTo[p.getRow()][p.getCol()] +
                        canvas.getEnergy(nextP.getCol(), nextP.getRow());
                if (distTo[nextP.getRow()][nextP.getCol()] > candidateDist) {
                    distTo[nextP.getRow()][nextP.getCol()] = candidateDist;
                    // pixelTo[nextP.getRow()][nextP.getCol()] = p;
                    colTo[nextP.getRow()][nextP.getCol()] = p.getCol();
                }
            }
        }
    }


    /**
     * Returns the length of a shortest path from the source pixel {@code s} to pixel {@code p}.
     *
     * @return the length of a shortest path from the source pixel {@code s} to pixel {@code p};
     * {@code Double.POSITIVE_INFINITY} if no such path
     */
    public double distTo(int col, int row) {
        return distTo[row][col];
    }


    /**
     * Is there a path from the source pixel {@code s} to pixel {@code p}?
     *
     * @return {@code true} if there is a path from the source vertex
     * {@code s} to pixel {@code p}, and {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public boolean hasPathTo(int col, int row) {
        return distTo[row][col] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a shortest path from the source pixel {@code s} to pixel {@code p}.
     *
     * @return a shortest path from the source vertex {@code s} to vertex {@code p}
     * as an iterable of edges, and {@code null} if no such path
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> pathTo(int col, int row) {
        if (!hasPathTo(col, row)) return null;
        Stack<Integer> path = new Stack<Integer>();
        // path.push(canvas.getPixel(col, row));
        path.push(col);
        for (int i = row; i > 0; i--) {
            col = colTo[i][col];
            path.push(col);
        }
        // for (int lastP = pixelTo[row][col];
        //      lastP != null;
        //      lastP = pixelTo[lastP.getRow()][lastP.getCol()]) {
        //     path.push(lastP.getCol());
        // }
        return path;
    }

    // /**
    //  * Returns the preorder number of vertex {@code v}.
    //  *
    //  * @return the preorder number of vertex {@code v}
    //  * @throws IllegalArgumentException unless {@code 0 <= v < V}
    //  */
    // public int pre(int i, int j) {
    //     return pre[i][j];
    // }

    // /**
    //  * Returns the postorder number of vertex {@code v}.
    //  *
    //  * @return the postorder number of vertex {@code v}
    //  * @throws IllegalArgumentException unless {@code 0 <= v < V}
    //  */
    // public int post(int i, int j) {
    //     return post[i][j];
    // }


    public static void main(String[] args) {

    }
}
