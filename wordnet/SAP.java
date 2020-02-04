/* *****************************************************************************
 *  Name: Shortest ancestral path
 *  Date:
 *  Description: An ancestral path between two vertices v and w in a digraph is
 *      a directed path from v to a common ancestor x, together with a directed
 *      path from w to the same ancestor x. A shortest ancestral path is an
 *      ancestral path of minimum total length. We refer to the common ancestor
 *      in a shortest ancestral path as a shortest common ancestor.
 *      Note also that an ancestral path is a path, but not a directed path.
 *      We generalize the notion of shortest common ancestor to subsets of vertices.
 *      A shortest ancestral path of two subsets of vertices A and B is a shortest
 *      ancestral path over all pairs of vertices v and w, with v in A and w in B.
 * Corner cases.
 *      Throw an IllegalArgumentException in the following situations:
 *      Any argument is null
 *      Any vertex argument is outside its prescribed range
 *      Any iterable argument contains a null item.
 * Performance requirements.
 *      All methods (and the constructor) should take time at most proportional
 *      to E + V in the worst case, where E and V are the number of edges and
 *      vertices in the digraph, respectively. Your data type should use space
 *      proportional to E + V.
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = new Digraph(G); // a deep copy of the input digraph.
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] ancestorAndLength = ancestorAndLength(v, w);
        return ancestorAndLength[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int[] ancestorAndLength = ancestorAndLength(v, w);
        return ancestorAndLength[0];
    }

    // private method length method and ancestor method
    private int[] ancestorAndLength(int v, int w) {
        int[] res = new int[2];
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(digraph, w);
        int minDist = INFINITY;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                int curDist = vBFS.distTo(i) + wBFS.distTo(i);
                if (curDist < minDist) {
                    minDist = curDist;
                    ancestor = i;
                }
            }
        }
        res[0] = ancestor;
        res[1] = (minDist == INFINITY) ? -1 : minDist;
        return res;
    }

    /* length of shortest ancestral path between any
    vertex in v and any vertex in w; -1 if no such path
    */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] ancestorAndLength = ancestorAndLength(v, w);
        return ancestorAndLength[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] ancestorAndLength = ancestorAndLength(v, w);
        return ancestorAndLength[0];
    }

    // private method length method and ancestor method
    private int[] ancestorAndLength(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        for (Integer i : v) if (i == null) throw new IllegalArgumentException();
        for (Integer i : w) if (i == null) throw new IllegalArgumentException();

        int[] res = new int[2];
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(digraph, w);
        int minDist = INFINITY;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i++) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                int curDist = vBFS.distTo(i) + wBFS.distTo(i);
                if (curDist < minDist) {
                    minDist = curDist;
                    ancestor = i;
                }
            }
        }
        res[0] = ancestor;
        res[1] = (minDist == INFINITY) ? -1 : minDist;
        return res;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
