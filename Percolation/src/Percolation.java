import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/** Percolation Model:
 *  1. A percolation system is represented as an n-by-n grid of sites.
 *  2. The row and column indices are integers between 1 and n
 *  3. Each site is either open (TRUE) or blocked (FALSE).
 *  4. A full site is an open site that can be connected to an open site
 *   in the top row via a chain of neighboring (left, right, up, down) open sites.
 *  5. The system percolates if there is a full site in the bottom row.
 *   In other words, a system percolates if we fill all open sites connected to
 *   the top row and that process fills some open site on the bottom row.
 **/
public class Percolation {
    private int n;
    private int num;
    private boolean[][] grid;
    private WeightedQuickUnionUF wqu;
    private int top;
    private int bottom;

    /**
     * create n-by-n grid, with all sites blocked
     * since the specification requires index from 1 - n,
     * we have to create a [n+1]x[n+1] grid
     * @param n
     */
    public Percolation(int n) {
        if(n < 1) {
            throw new IllegalArgumentException();
        }

        this.n = n;
        num = 0;
        grid = new boolean[n + 1][n + 1];

        // The last two elements stand for the virtual top and down site
        wqu = new WeightedQuickUnionUF(n * n + 2);
        top = n * n;
        bottom = n * n + 1;

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                grid[i][j] = false;
            }
        }

    }

    /** open site (row, col) if it is not open already. */
    public void open(int row, int col) {
        if(row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        if (!grid[row][col]) {
            grid[row][col] = true;
            num += 1;
            unionNeighbours(row, col);
        }
    }

    private void unionNeighbours(int row, int col) {
        // union any open neighbour
        int p = translate(row, col);

        if (row == 1) {
            wqu.union(p, top);
            union(Math.min(row + 1, n), col, p); // down
        } else if (row == n) {
            wqu.union(p, bottom);
            union(Math.max(row - 1, 1), col, p); // up
        } else {
            union(row - 1, col, p); // up
            union(row + 1, col, p); // down
        }

        union(row, Math.max(col - 1, 1), p); // left
        union(row, Math.min(col + 1, n), p); // right
    }

    private void union(int row, int col, int p) {
        // if site[row][col] is open, union with p
        if (isOpen(row, col)) {
            wqu.union(translate(row, col), p);
        }
    }

    private int translate(int row, int col) {
        // return the corresponds unionFind index
        return (row - 1) * n + (col - 1);
    }

    public boolean isOpen(int row, int col) {
        if(row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        // is site (row, col) open?
        return grid[row][col];
    }

    public boolean isFull(int row, int col) {
        // is site (row, col) full?
        if(row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        return wqu.connected(translate(row, col), top);
    }

    public int numberOfOpenSites() {
        // number of open sites
        return num;
    }

    public boolean percolates() {
        // does the system percolate?
        return wqu.connected(top, bottom);
    }

    public static void main(String[] args) {   // test client (optional)

    }

}