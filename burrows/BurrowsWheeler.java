/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: The goal of the Burrows–Wheeler transform is not to compress a
 *  message, but rather to transform it into a form that is more amenable for
 *  compression. The Burrows–Wheeler transform rearranges the characters in the
 *  input so that there are lots of clusters with repeated characters, but in
 *  such a way that it is still possible to recover the original input.
 *
 *  Performance requirements.
 *  The running time of your Burrows–Wheeler transform must be proportional to
 *  n + R (or better) in the worst case, excluding the time to construct the
 *  circular suffix array.
 *  The running time of your Burrows–Wheeler inverse transform must be proportional
 *  to n + R (or better) in the worst case.
 *  The amount of memory used by both the Burrows–Wheeler transform and inverse
 *  transform must be proportional to n + R (or better) in the worst case.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    /**
     * apply Burrows-Wheeler transform,
     * reading from standard input and writing to standard output
     * Consider the result of sorting the n circular suffixes of s.
     * The Burrows–Wheeler transform is the last column in the sorted suffixes
     * array t[], preceded by the row number first in which the original string
     * ends up.
     * Example: "ABRACADABRA!"
     * i     Original Suffixes          Sorted Suffixes       t    index[i]
     * --    -----------------------     -----------------------    --------
     * 0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
     * 1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
     * 2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
     * *3    A C A D A B R A ! A B R     A B R A C A D A B R A !   *0
     * 4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
     * 5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
     * 6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
     * 7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
     * 8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
     * 9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
     * 10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
     * 11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
     * <p>
     * Transform to:
     * 3
     * ARD!RCAAAABB
     * <p>
     * output as:
     * 00 00 00 03 41 52 44 21 52 43 41 41 41 41 42 42
     * 128 bits
     * the integer 3 is represented using 4 bytes (00 00 00 03).
     * The character 'A' is represented by hex 41, and so forth.
     */
    public static void transform() {
        String input = BinaryStdIn.readString();
        int n = input.length();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        int first = 0;
        char[] tail = new char[n];
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) first = i;
            int col = (n + csa.index(i) - 1) % n;
            tail[i] = input.charAt(col);
        }
        BinaryStdOut.write(first);
        for (char c : tail) {
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    /**
     * apply Burrows-Wheeler inverse transform
     * reading from standard input and writing to standard output
     * The input to the Burrows–Wheeler decoder is an integer as first,
     * followed by a sequence of extended ASCII characters (0x00 to 0xFF) as the
     * last column t[] of the sorted suffixes.
     * <p>
     * 1. From t[], deduce the first column of the sorted suffixes because
     * it consists of precisely the same characters, but in sorted order.
     * 2. Constructing the next[] array from t[].
     * define next[i]:
     * If the jth original suffix (original string, shifted j characters to the
     * left) is the ith row in the sorted order, we define next[i] to be the row
     * in the sorted order where the (j + 1)st original suffix appears.
     * For example, if first is the row in which the original input string appears,
     * then next[first] is the row in the sorted order where the 1st original suffix
     * (the original string left-shifted by 1) appears; next[next[first]] is the
     * row in the sorted order where the 2nd original suffix appears; and so forth.
     * i      Sorted Suffixes     t      next[i]
     * --    -----------------------      -------
     * 0    ! ? ? ? ? ? ? ? ? ? ? A        3
     * 1    A ? ? ? ? ? ? ? ? ? ? R        0
     * 2    A ? ? ? ? ? ? ? ? ? ? D        6
     * *3    A ? ? ? ? ? ? ? ? ? ? !        7
     * 4    A ? ? ? ? ? ? ? ? ? ? R        8
     * 5    A ? ? ? ? ? ? ? ? ? ? C        9
     * 6    B ? ? ? ? ? ? ? ? ? ? A       10
     * 7    B ? ? ? ? ? ? ? ? ? ? A       11
     * 8    C ? ? ? ? ? ? ? ? ? ? A        5
     * 9    D ? ? ? ? ? ? ? ? ? ? A        2
     * 10    R ? ? ? ? ? ? ? ? ? ? B        1
     * 11    R ? ? ? ? ? ? ? ? ? ? B        4
     * 3. given the next[] array and first, we can reconstruct the original input
     * string because the first character of the ith original suffix is the ith
     * character in the input string.
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();
        int n = input.length();
        char[] tail = input.toCharArray();
        char[] sortedHead = new char[n];
        int[] next = new int[n];
        // Compute frequency counts
        int[] count = new int[R + 1];
        for (char c : tail) count[c + 1]++;
        // Transform counts to indices
        for (int r = 0; r < R; r++) count[r + 1] += count[r];
        // get sorted head col and compute next
        for (int i = 0; i < n; i++) {
            char c = tail[i];
            sortedHead[count[c]] = c;
            next[count[c]++] = i;
        }

        // decoding from next and first
        char[] origin = new char[n];
        // int i = 0, j = first;
        for (int i = 0; i < n; i++) {
            origin[i] = sortedHead[first];
            BinaryStdOut.write(origin[i]);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    /**
     * Sample client that calls Burrows-Wheeler {@code transform()} if the command-line
     * argument is "-" an Burrows-Wheeler {@code inverseTransform()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
