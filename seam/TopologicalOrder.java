/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class TopologicalOrder {
    private boolean[][] marked;       // marked[v] = has v been marked in dfs?
    // private int[][] pre;                 // pre[v]    = preorder  number of v
    private int[][] post;                // post[v]   = postorder number of v
    // private Queue<Pixel> preorder;   // vertices in preorder
    private Queue<Pixel> postorder;  // vertices in postorder
    // private int preCounter;            // counter for preorder numbering
    private int postCounter;           // counter for postorder numbering

    public TopologicalOrder(Canvas canvas) {
        if (canvas == null) throw new IllegalArgumentException();
        int height = canvas.height();
        int width = canvas.width();
        // pre = new int[height][width];
        post = new int[height][width];
        postorder = new Queue<Pixel>();
        // preorder = new Queue<Pixel>();
        marked = new boolean[height][width];
        // run dfs from top row
        int row = 0;
        for (int col = 0; col < canvas.width(); col++)
            if (!marked[row][col]) dfs(canvas, col, row);
    }


    /**
     * run DFS in pixel-weighted canvas from pixel at column x and row y
     * and compute preorder/postorder
     *
     * @param canvas canvas
     * @param x      column x
     * @param y      row y
     */
    private void dfs(Canvas canvas, int x, int y) {
        marked[y][x] = true;
        // pre[y][x] = preCounter++;
        Pixel pixel = canvas.getPixel(x, y);
        // preorder.enqueue(pixel);
        for (Pixel p : canvas.adj(x, y)) {
            if (!marked[p.getRow()][p.getCol()]) {
                dfs(canvas, p.getCol(), p.getRow());
            }
        }
        postorder.enqueue(pixel);
        post[y][x] = postCounter++;
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

    // /**
    //  * Returns the preorder number of vertex {@code v}.
    //  *
    //  * @return the preorder number of vertex {@code v}
    //  * @throws IllegalArgumentException unless {@code 0 <= v < V}
    //  */
    // public int pre(int i, int j) {
    //     return pre[i][j];
    // }

    /**
     * Returns the postorder number of vertex {@code v}.
     *
     * @return the postorder number of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int post(int i, int j) {
        return post[i][j];
    }


    public static void main(String[] args) {

    }
}
