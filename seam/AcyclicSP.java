/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: Computes shortest paths in an pixel-weighted acyclic digraph.
 *  Compute preorder and postorder for weighted pixel.
 *  The weighted pixel is represented as Double[][]
 *  Runs in O(E + V) time.
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

public class AcyclicSP {
    // private Canvas canvas;
    private double[][] distTo; // distTo[row][col] = distance of shortest s->p path
    // private Pixel[][] pixelTo; // pixelTo[row][col] = last pixel on shortest s->p path
    private int[][] colTo; // colTo[row][col] = column in the row above olong the best path

    /**
     * Determines a depth-first order for the weighted pixel {@code canvas}.
     * from index(col, row)
     *
     * @param canvas the weighted pixel canvas
     */
    public AcyclicSP(Canvas canvas, int col, int row) {
        // this.canvas = canvas;
        distTo = new double[canvas.height()][canvas.width()];
        // pixelTo = new Pixel[canvas.height()][canvas.width()];
        colTo = new int[canvas.height()][canvas.width()];
        for (int i = 0; i < canvas.height(); i++) {
            for (int j = 0; j < canvas.width(); j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        distTo[row][col] = canvas.getEnergy(col, row);

        // get topologicalOrder
        Iterable<Pixel> topological = canvas.getTopological();
        // TopologicalOrder tp = new TopologicalOrder(canvas, col, row);
        // Iterable<Pixel> topological = tp.reversePost();
        // relax vertices in topological order
        for (Pixel p : topological) {
            relax(canvas, p);
        }
    }

    // relax pixel p
    private void relax(Canvas canvas, Pixel p) {
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

    public static void main(String[] args) {

    }
}
