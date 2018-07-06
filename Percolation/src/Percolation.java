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
    private static final byte BLOCKED = 0;
    private static final byte OPEN = 1;
    private static final byte CONNECT_TO_TOP = 4;
    private static final byte CONNECT_TO_BOTTOM = 2;
    private int n;
    private int openSitesNum;
    private byte[] siteStates;
    private WeightedQuickUnionUF wqu;
    private boolean isPercolates;

    /**
     * create n-by-n grid, with all sites blocked
     * @param n
     */
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        isPercolates = false;
        this.n = n;
        openSitesNum = 0;
        siteStates = new byte[n * n];
        wqu = new WeightedQuickUnionUF(n * n);

        for (int i = 0; i < n * n; i++) {
            siteStates[i] = BLOCKED;
        }
    }

    /** open site (row, col) if it is not open already. */
    public void open(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        int p = translate(row, col);
        if (siteStates[p] == BLOCKED) {
            siteStates[p] = OPEN;
            openSitesNum += 1;

            if (n == 1) {
                siteStates[p] = (CONNECT_TO_BOTTOM | CONNECT_TO_TOP);
                isPercolates = true;
                return;
            }

            int newParent = unionNeighbours(row, col);
            if (siteStates[newParent] >= (CONNECT_TO_TOP | CONNECT_TO_BOTTOM)) {
                isPercolates = true;
            }
        }
    }

    /** Union any open neighbour. */
    private int unionNeighbours(int row, int col) {
        int cur = translate(row, col);
        int parent = wqu.find(cur);
        int up = translate(Math.max(row - 1, 1), col); // up
        int down = translate(Math.min(row + 1, n), col); // down
        int left = translate(row, Math.max(col - 1, 1)); // left
        int right = translate(row, Math.min(col + 1, n)); // right

        if (row == 1) { // top row
            siteStates[parent] = (byte) (siteStates[parent] | CONNECT_TO_TOP);
        } else if (row == n) { // bottom row
            siteStates[parent] = (byte) (siteStates[parent] | CONNECT_TO_BOTTOM);
        }


        byte upState = union(up, cur, parent);
        byte downState = union(down, cur, parent);
        byte leftState = union(left, cur, parent);
        byte rightState = union(right, cur, parent);
        int newParent = wqu.find(cur);
        siteStates[newParent] = (byte) (siteStates[parent] | siteStates[newParent] | upState | downState | leftState | rightState);
        return newParent;
    }

    /** if site[row][col] is open, update its parent */
    private byte union(int neighbour, int cur, int parent) {
        if (neighbour != cur && siteStates[neighbour] != BLOCKED) {
            int neighbourParent = wqu.find(neighbour);
            wqu.union(neighbour, cur);
            return siteStates[neighbourParent];
        }
        return siteStates[parent];
    }


    /** convert (row, col) to array index. */
    private int translate(int row, int col) {
        return (row - 1) * n + (col - 1);
    }

    /** is site (row, col) open? */
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        return siteStates[translate(row, col)] != BLOCKED;
    }

    /** is site (row, col) full? Determined by its root's state*/
    public boolean isFull(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException();
        }
        int cur = translate(row, col);
        if (siteStates[cur] != BLOCKED) {
            return siteStates[wqu.find(cur)] >= CONNECT_TO_TOP;
        }
        return false;
    }

    public int numberOfOpenSites() {
        // number of open sites
        return openSitesNum;
    }

    /** does the system percolate?. */
    public boolean percolates() {
        return isPercolates;
    }

    public static void main(String[] args) {   // test client (optional)

    }

}