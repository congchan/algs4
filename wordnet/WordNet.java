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

import edu.princeton.cs.algs4.*;

import java.io.File;
import java.util.ArrayList;

public class WordNet {
    private SeparateChainingHashST<String, Bag<Integer>> nouns;
    private Digraph hypernymsDG;
    private int V;
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
        In synsetsReader = new In(new File(synsets));
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
        V = synsetId + 1;
    }

    // read hypernyms
    private void readHypernyms(String hypernyms) {
        hypernymsDG = new Digraph(V);
        In hypernymsReader = new In(new File(hypernyms));
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
    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
    }
}
