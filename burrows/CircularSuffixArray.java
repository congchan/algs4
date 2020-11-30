/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: circular suffix array,
 *  which describes the abstraction of a sorted array of the n circular suffixes
 *  of a string of length n.
 *
 *  As an example, consider the string "ABRACADABRA!" of length 12.
 *  The table below shows its 12 circular suffixes and the result of sorting them.
 *  i       Original Suffixes           Sorted Suffixes         index[i]
    --    -----------------------     -----------------------    --------
     0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
     1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
     2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
     3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
     4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
     5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
     6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
     7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
     8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
     9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
    10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
    11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
*
*   We define index[i] to be the index of the original suffix that appears ith
*   in the sorted array.
*   For example, index[11] = 2 means that the 2nd original suffix appears 11th
*   in the sorted order (i.e., last alphabetically).
*
*   Performance requirements.
*       On typical English text, your data type must use space proportional to
*       n + R (or better) and the constructor must take time proportional to
*       n log n (or better). The methods length() and index() must take constant
*       time in the worst case.
 **************************************************************************** */

import edu.princeton.cs.algs4.Quick3way;

public class CircularSuffixArray {
    private int n;
    private String s;
    private int[] index;
    private CircularSuffix[] suffixArray;
    private int first;

    /**
     * circuBurrowsWheelerlar suffix array of s
     * Throw an IllegalArgumentException in the constructor if the argument is null.
     *
     * @param s
     */
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        this.s = s;
        n = s.length();
        char[] chars = s.toCharArray();
        suffixArray = new CircularSuffix[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = new CircularSuffix(i);
        }
        Quick3way.sort(suffixArray);
        index = new int[n];
        for (int i = 0; i < n; i++) {
            index[i] = suffixArray[i].offset;
            if (suffixArray[i].offset == 0) first = i;
        }

    }

    /**
     * The constructor should take constant time and use constant space.
     */
    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final int offset;

        CircularSuffix(int offset) {
            this.offset = offset;
        }


        int length() {
            return n;
        }

        char charAt(int i) {
            return s.charAt((offset + i) % n);
        }

        public int compareTo(CircularSuffix that) {
            if (this == that) return 0;  // optimization
            for (int i = 0; i < n; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return +1;
            }
            return 0;
        }

        public char getLastColumn() {
            int col = (n + offset - 1) % n;
            return s.charAt(col);
        }

        public String toString() {
            return s.substring(offset) + s.substring(0, offset);
        }

    }

    // length of s
    public int length() {
        return n;
    }

    /**
     * returns index of ith sorted suffix
     * Throw an IllegalArgumentException in the method index() if i is outside
     * its prescribed range (between 0 and n − 1).
     *
     * @param i
     * @return
     */
    public int index(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException();
        return index[i];
    }

    private int first() {
        return first;
    }

    private char getLastColumn(int i) {
        int col = (n + suffixArray[i].offset - 1) % n;
        return s.charAt(col);
    }

    private char getFirstColumn(int i) {
        int col = suffixArray[i].offset;
        return s.charAt(col);
    }


    /**
     * unit testing (required)
     * call each public method directly and help verify that they work as prescribed
     * (e.g., by printing results to standard output).
     *
     * @param args
     */
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(csa.first());
        for (int i = 0; i < csa.length(); i++) {
            CircularSuffix c = csa.suffixArray[i];
            System.out.print(i + " " + csa.index[i] + " " + csa.length() + " ");
            System.out
                    .print(csa.getFirstColumn(i) + " " + c.toString() + " " + csa.getLastColumn(i));
            System.out.println();
        }

    }
}
