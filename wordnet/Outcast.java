/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDist = 0;
        String outcast = null;
        for (String n : nouns) {
            int curDist = sumDistances(n, nouns);
            if (maxDist < curDist) {
                maxDist = curDist;
                outcast = n;
            }
        }
        return outcast;
    }

    // compute sum of distances
    private int sumDistances(String thisNoun, String[] nouns) {
        int sum = 0;
        for (String n : nouns) {
            sum += wordNet.distance(thisNoun, n);
        }
        return sum;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
