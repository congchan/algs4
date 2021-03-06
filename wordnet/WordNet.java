/* *****************************************************************************
 *  Name: WordNet
 *  Date:
 *  Description:
 *  List of synsets.
 *      The file synsets.txt contains all noun synsets in WordNet, one per line.
 *      Line i of the file (counting from 0) contains the information for synset i.
 *      The first field is the synset id, which is always the integer i;
 *      the second field is the synonym set (or synset);
 *      and the third field is its dictionary definition (or gloss),
 *          which is not relevant to this assignment.
 *  List of hypernyms.
 *      The file hypernyms.txt contains the hypernym relationships.
 *      Line i of the file (counting from 0) contains the hypernyms of synset i.
 *      The first field is the synset id, which is always the integer i;
 *      subsequent fields are the id numbers of the synset’s hypernyms.
 *  Corner cases.
 *      Throw an IllegalArgumentException in the following situations:
 *      Any argument to the constructor or an instance method is null
 *      The input to the constructor does not correspond to a rooted DAG.
 *      Any of the noun arguments in distance() or sap() is not a WordNet noun.
 *  Performance requirements.
 *      Your data type should use space linear in the input size (size of
 *      synsets and hypernyms files).
 *      The constructor should take time linearithmic (or better) in the input size.
 *      The method isNoun() should run in time logarithmic (or better) in the number of nouns.
 *      The methods distance() and sap() should run in time linear in the size of the WordNet digraph.
 *      For the analysis, assume that the number of nouns per synset is bounded by a constant.
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class WordNet {
    private SeparateChainingHashST<String, Bag<Integer>> nouns;
    private Digraph hypernymsDG;
    private int numV;
    private SAP sap;
    private ArrayList<String> synsetsList;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        nouns = new SeparateChainingHashST<String, Bag<Integer>>();
        synsetsList = new ArrayList<>();
        readSynsets(synsets);
        readHypernyms(hypernyms);
        sap = new SAP(hypernymsDG);
    }

    // read synsets nouns and ids
    private void readSynsets(String synsets) {
        In synsetsReader = new In(synsets);
        String line;
        int synsetId = 0;
        while ((line = synsetsReader.readLine()) != null) {
            String[] parsedLine = line.split("\\,");
            synsetId = Integer.parseInt(parsedLine[0]);
            String synset = parsedLine[1];
            synsetsList.add(synset);
            String[] parsedSynset = synset.split(" ");
            for (String n : parsedSynset) {
                if (nouns.contains(n)) {
                    nouns.get(n).add(synsetId);
                } else {
                    Bag<Integer> ids = new Bag<Integer>();
                    ids.add(synsetId);
                    nouns.put(n, ids);
                }
            }
        }
        numV = synsetId + 1;
    }

    // read hypernyms
    private void readHypernyms(String hypernyms) {
        hypernymsDG = new Digraph(numV);
        In hypernymsReader = new In(hypernyms);
        String line;
        while ((line = hypernymsReader.readLine()) != null) {
            String[] parsedLine = line.split("\\,");
            int synsetId = 0;
            for (int i = 0; i < parsedLine.length; i++) {
                if (i == 0) {
                    synsetId = Integer.parseInt(parsedLine[i]);
                } else {
                    hypernymsDG.addEdge(synsetId, Integer.parseInt(parsedLine[i]));
                }
            }
        }
        if (!isRootedDAG(hypernymsDG)) {
            throw new IllegalArgumentException();
        }
    }

    // check if a graph is rooted DAG: it is acyclic
    // and has one vertex—the root—that is an ancestor of every other vertex.
    private boolean isRootedDAG(Digraph G) {
        DirectedCycle isDag = new DirectedCycle(hypernymsDG);
        if (isDag.hasCycle()) return false;
        int nRoot = 0;
        for (int i = 0; i < G.V(); i++) {
            if (G.outdegree(i) == 0) nRoot++;
        }
        return nRoot == 1;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Iterable<String> allNouns = nouns.keys(); // O(N)
        return allNouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return nouns.contains(word); // O(1)
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        Iterable<Integer> nounAIDs = nouns.get(nounA);
        Iterable<Integer> nounBIDs = nouns.get(nounB);
        return sap.length(nounAIDs, nounBIDs);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        Iterable<Integer> nounAIDs = nouns.get(nounA);
        Iterable<Integer> nounBIDs = nouns.get(nounB);
        int ancestorID = sap.ancestor(nounAIDs, nounBIDs);
        return synsetsList.get(ancestorID);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        for (String s : wordnet.nouns()) {
            StdOut.printf("%s\n", s);
        }
        int distance = wordnet.distance("a", "c");
        String ancestor = wordnet.sap("a", "c");
        StdOut.printf("length = %d, ancestor = %s\n", distance, ancestor);
    }
}
