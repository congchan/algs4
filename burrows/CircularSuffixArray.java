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

import edu.princeton.cs.algs4.StdRandom;

public class CircularSuffixArray {
    private int n;
    private String s;
    private int[] index;
    private CircularSuffix[] suffixArray;
    private int first;
    private static final int CUTOFF = 15;   // cutoff to insertion sort

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
        sort(suffixArray);
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
     * Rearranges the array of strings in ascending order.
     *
     * @param a the array to be sorted
     */
    private static void sort(CircularSuffix[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length - 1, 0);
        assert isSorted(a);
    }

    // return the dth character of s, -1 if d = length of s
    private static int charAt(CircularSuffix s, int d) {
        assert d >= 0 && d <= s.length();
        if (d == s.length()) return -1;
        return s.charAt(d);
    }


    // 3-way string quicksort a[lo..hi] starting at dth character
    private static void sort(CircularSuffix[] a, int lo, int hi, int d) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(a[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(a[i], d);
            if (t < v) exch(a, lt++, i++);
            else if (t > v) exch(a, i, gt--);
            else i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(a, lo, lt - 1, d);
        // NOTE: charAt() will return -1 if it hits the end of the string,
        // therefore this check avoids continually sorting on a unary string
        if (v >= 0) sort(a, lt, gt, d + 1);
        sort(a, gt + 1, hi, d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void insertion(CircularSuffix[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j - 1], d); j--)
                exch(a, j, j - 1);
    }

    // exchange a[i] and a[j]
    private static void exch(CircularSuffix[] a, int i, int j) {
        CircularSuffix temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private static boolean less(CircularSuffix v, CircularSuffix w, int d) {
        // assert v.substring(0, d).equals(w.substring(0, d));
        for (int i = d; i < Math.min(v.length(), w.length()); i++) {
            if (v.charAt(i) < w.charAt(i)) return true;
            if (v.charAt(i) > w.charAt(i)) return false;
        }
        return v.length() < w.length();
    }

    // is the array sorted
    private static boolean isSorted(CircularSuffix[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        return true;
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
