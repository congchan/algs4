import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayDeque;

/**
 * implement the A* algorithm and priority queue(s) to find the solution of 8 puzzel problem,
 * To solve the puzzle from a given search node on the priority queue,
 *      the total number of moves we need to make (including those already made) is at least its priority,
 *      Consequently, when the goal board is dequeued,
 *          we have discovered not only a sequence of moves from the initial board to the goal board,
 *          but one that makes the fewest number of moves.
 * Corner cases.
 * The constructor should throw a java.lang.IllegalArgumentException if passed a null argument.
 */
public class Solver {
    private int moves = -1;
    private Searchnode init;
    private Searchnode goal;

    /** find a solution to the initial board (using the A* algorithm)
     * Detecting unsolvable puzzles.
     *      run the A* algorithm on two puzzle instances
     *      one with the initial board and one with the initial board modified by swapping a pair of blocks
     *      - in lockstep (alternating back and forth between exploring search nodes in each of the two game trees).
     *      Exactly one of the two will lead to the goal board.
     */
    public Solver(Board initial) {
        if (initial == null) { throw new IllegalArgumentException(); }
        MinPQ<Searchnode> ques = new MinPQ<>();
        MinPQ<Searchnode> quesModified = new MinPQ<>();
        init = new Searchnode(initial, 0, null);
        ques.insert(init);
        Searchnode initTwin = new Searchnode(initial.twin(), 0, null);
        quesModified.insert(initTwin);

        while (!ques.isEmpty()) {
            // if anyone find the goal, break the loop
            if (find(ques)) {
                moves = goal.moves;
                break;
            }
            if (find(quesModified)) {
                break;
            }
        }

    }

    /** return true if find the goal board, else continue explore */
    private boolean find(MinPQ<Searchnode> q) {
        Searchnode tobeProcessed = q.delMin();
        if (tobeProcessed.board.isGoal()) {
            goal = tobeProcessed;
            return true;
        }
        exploreNeighbor(tobeProcessed, q);
        return false;
    }

    /** is the initial board solvable? */
    public boolean isSolvable() {
        return moves > -1;
    }

    /** min number of moves to solve initial board; -1 if unsolvable */
    public int moves() {
        return moves;
    }

    /**  sequence of boards in a shortest solution; null if unsolvable */
    public Iterable<Board> solution() {
        ArrayDeque<Board> path = null;
        if (isSolvable()) {
            path = new ArrayDeque<>();
            Searchnode cur = goal;
            while (cur.predecessor != null) {
                path.addFirst(cur.board);
                cur = cur.predecessor;
            }
            path.addFirst(init.board);
        }
        return path;
    }

    /** To reduce unnecessary exploration of useless search nodes,
     * when considering the neighbors of a search node,
     * don't enqueue a neighbor if its board is the same as the board of the predecessor search node.
     * @param node
     */
    private void exploreNeighbor(Searchnode node, MinPQ<Searchnode> q) {
        Iterable<Board> candidates = node.board.neighbors();
        for (Board candidate : candidates) {
            if (node.predecessor != null && candidate.equals(node.predecessor.board)) {
                continue;
            }
            Searchnode newNode = new Searchnode(candidate, node.moves + 1, node);
            q.insert(newNode);
        }
    }

    /** A search node of the game contains:
     *      a board,
     *      the number of moves made to reach the board, (which is the cost from init to cur)
     *      and the predecessor search node.
     * Optimization.
     *      To avoid recomputing the Manhattan priority of a search node from scratch
     *      each time during various priority queue operations, pre-compute its value
     *      when you construct the search node; save it in an instance variable;
     * The manhattanPriority is used for A* cost
     */
    private class Searchnode implements Comparable<Searchnode> {
        private Board board;
        private int moves;
        private Searchnode predecessor;
        private int manhattanPriority;

        public Searchnode(Board cur, int moves, Searchnode p) {
            board = cur;
            this.moves = moves;
            predecessor = p;
            manhattanPriority = cur.manhattan() + moves;
        }

        public int compareTo(Searchnode that) {
            return this.manhattanPriority - that.manhattanPriority;
        }
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

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
