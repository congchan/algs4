import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
import java.util.List;

/**
 * Corner cases.
 * You may assume that the constructor receives an n-by-n array containing the n2 integers between 0 and n2 − 1,
 * where 0 represents the blank square.
 * Performance requirements.
 * Your implementation should support all Board methods in time proportional to n^2 (or better) in the worst case.
 */

public class Board {
    private final int[][] blocks;
    private final int n;
    private int blankRow;
    private int blankCol;
    private int Manhattan;

    /** construct a board from an n-by-n array of blocks
     * (where blocks[i][j] = block in row i, column j)
     * @param blocks
     */
    public Board(int[][] blocks) {
        if (blocks == null) { throw new NullPointerException(); }
        n = blocks.length;
        this.blocks = new int[n][n];
        Manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
                this.blocks[i][j] = blocks[i][j];

                // calc Manhattan
                int destVal = blocks[i][j];
                if (destVal != 0) {
                    int destRow = (destVal - 1) / n;
                    int destCol = (destVal - 1) % n;
                    Manhattan += Math.abs(destRow - i) + Math.abs(destCol - j);
                }
            }
        }
    }

    /** board dimension n */
    public int dimension() {
        return n;
    }

    /** number of blocks out of place */
    public int hamming() {
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != n * i + j + 1) { cnt++; }
            }
        }
        return cnt;
    }


    /** sum of Manhattan distances between blocks and goal */
    public int manhattan() {
        return Manhattan;
    }

    /** is this board the goal board? */
    public boolean isGoal() {
        return Manhattan == 0;
    }

    /** a board that is obtained by exchanging any pair of blocks */
    public Board twin() {
        int[][] cloned = clone(blocks);
        int row = 0;
        if (blankRow == row) { row++; }
        swap(cloned, row, 0, row, 1);
        return new Board(cloned);
    }

    private void swap(int[][] v, int rowA, int colA, int rowB, int colB) {
        int swap = v[rowA][colA];
        v[rowA][colA] = v[rowB][colB];
        v[rowB][colB] = swap;
    }

    // clone two dimensional array
    private static int[][] clone(int[][] a) {
        int[][] b = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i].clone();
        }
        return b;
    }

    /** does this board equal y? */
    public boolean equals(Object y) {
        if (y == null) return false;
        if (y == this) return true;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;
        if (this.blankCol != that.blankCol) return false;
        if (this.blankRow != that.blankRow) return false;
        if (this.n != that.n) return false;
        for (int row = 0; row < n; row++)
            for (int col = 0; col < n; col++)
                if (this.blocks[row][col] != that.blocks[row][col])
                    return false;
        return true;
    }


    /** all neighboring boards */
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new LinkedList<>();

        if (blankRow > 0) {
            int[][] up = clone(blocks);
            swap(up, blankRow, blankCol, blankRow - 1, blankCol);
            neighbors.add(new Board(up));
        }
        if (blankRow < n - 1) {
            int[][] down = clone(blocks);
            swap(down, blankRow, blankCol, blankRow + 1, blankCol);
            neighbors.add(new Board(down));
        }
        if (blankCol > 0) {
            int[][] left = clone(blocks);
            swap(left, blankRow, blankCol, blankRow, blankCol - 1);
            neighbors.add(new Board(left));
        }
        if (blankCol < n - 1) {
            int[][] right = clone(blocks);
            swap(right, blankRow, blankCol, blankRow, blankCol + 1);
            neighbors.add(new Board(right));
        }
        return neighbors;
    }

    /**  string representation of this board (in the output format specified below) */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(n).append("\n");
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                builder.append(String.format("%2d ", blocks[row][col]));
            }
            builder.append("\n");
        }
        return builder.toString();
    }


    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        StdOut.println(initial + "Manhattan " + initial.Manhattan);

    }
}
