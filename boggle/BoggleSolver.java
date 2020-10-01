import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * finds all valid words in a given Boggle board, using a given dictionary.
 * 1. use R-way trie to store dictionary, since we have small alphabets (A-Z) and short keys (words)
 * 2. backtracking optimization: check if the current path is a prefix of any word in the dictionary.
 * 3. a nonrecursive implementation of the prefix query operation.
 * 4. the side of one die is printed with the two-letter sequence Qu instead of Q
 * (and this two-letter sequence must be used together when forming words). When scoring, Qu counts as two letters
 */

public class BoggleSolver {
    private static final int R = 26;        // A-Z, use ASCII
    private Node trie;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    private static class Node {
        private String val;
        private Node[] next = new Node[R];
    }


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trie = new Node();
        loadDict(dictionary);
    }

    private void loadDict(String[] dictionary) {
        for (String s : dictionary) {
            put(trie, s, 0);
        }
    }

    /**
     * Inserts the key as value into the symbol table, overwriting the old value
     * with the new the key is already in the symbol table.
     *
     * @param key the key
     */
    private Node put(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            n++;
            x.val = key;
            return x;
        }
        char c = key.charAt(d);
        x.next[c % R] = put(x.next[c % R], key, d + 1);
        return x;
    }

    private boolean get(Node x, String key, int d) {
        if (x == null) return false;
        if (d == key.length()) {
            return (x.val != null && x.val.equals(key));
        }
        char c = key.charAt(d);
        return get(x.next[c % R], key, d + 1);
    }


    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     * A valid word, a.k.a. a valid path
     * 1. must be composed by following a sequence of adjacent dice—two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.
     * 2. can use each die at most once.
     * 3. must contain at least 3 letters.
     * 4. must be in the dictionary (which typically does not contain proper nouns).
     *
     * @param board
     * @return
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        SET<String> collections = new SET<String>();
        boolean[][] marked = new boolean[board.rows()][board.cols()];
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                dfs(i, j, board, marked, collections, trie);
            }
        }
        return collections;
    }


    private void dfs(int row, int col, BoggleBoard board, boolean[][] marked, SET<String> collections, Node node) {
        marked[row][col] = true;
        char c = board.getLetter(row, col);
        Node nextNode = collect(node, c, collections);
        if (nextNode != null) {
            // iterate adjacent
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < board.rows() && j >= 0 && j < board.cols() && !marked[i][j]) {
                        dfs(i, j, board, marked, collections, nextNode);
                    }
                }
            }
        }
        marked[row][col] = false;
    }

    // collect valid word      * 3. must contain at least 3 letters.
    private Node collect(Node x, char c, SET<String> collections) {
        if (x == null || x.next[c % R] == null) return null;
        Node nextNode = x.next[c % R];
        if (c == 'Q') nextNode = nextNode.next['U' % R];
        if (nextNode != null) {
            String candidate = nextNode.val;
            if (candidate != null && candidate.length() > 2) collections.add(candidate);
        }
        return nextNode;
    }


    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * (You can assume the word contains only the uppercase letters A through Z.)
     * Scoring
     * word length	  	points
     * 3–4		1
     * 5		2
     * 6		3
     * 7		5
     * 8+		11
     * The Qu special case.
     */
    public int scoreOf(String word) {
        if (word == null || word.length() < 3 || !get(trie, word, 0)) return 0;
        if (word.length() <= 4) return 1;
        if (word.length() <= 5) return 2;
        if (word.length() <= 6) return 3;
        if (word.length() <= 7) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        for (String word : new String[]{"SEQ", "CRAW", "PACKET", "null"})
            StdOut.println(word + ' ' + solver.scoreOf(word));
    }
}
