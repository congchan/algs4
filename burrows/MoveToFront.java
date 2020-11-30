/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: Move-to-front encoding and decoding
 *      maintain an ordered sequence of the characters in the alphabet by
 *      repeatedly reading a character from the input message; printing the
 *      position in the sequence in which that character appears; and moving
 *      that character to the front of the sequence.
 *
 *  As a simple example,
 *  if the initial ordering over a 6-character alphabet is A B C D E F, and
 *  we want to encode the input CAAABCCCACCF, then we would update the
 *  move-to-front sequence as follows:
 *  move-to-front    in   out
    -------------    ---  ---
     A B C D E F      C    2
     C A B D E F      A    1
     A C B D E F      A    0
     A C B D E F      A    0
     A C B D E F      B    2
     B A C D E F      C    2
     C B A D E F      C    0
     C B A D E F      C    0
     C B A D E F      A    2
     A C B D E F      C    1
     C A B D E F      C    0
     C A B D E F      F    5
     F C A B D E
 *
 *  Performance requirements.
 *  The running time of both move-to-front encoding and decoding must be
 *  proportional to n R (or better) in the worst case and proportional to
 *  n + R (or better) on inputs that arise when compressing typical English
 *  text, where n is the number of characters in the input and R is the
 *  alphabet size. The amount of memory used by both move-to-front encoding
 *  and decoding must be proportional to n + R (or better) in the worst case.
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;
    private static final int LG_R = 8;

    /**
     * apply move-to-front encoding, reading from standard input and writing to
     * standard output
     * 1. maintain an ordered sequence of the 256 extended ASCII characters.
     * (0x00 to 0xFF)
     * 2. Initialize the sequence by making the ith character in the sequence
     * equal to the ith extended ASCII character.
     * 3. read each 8-bit character c from standard input, one at a time;
     * 4. output the 8-bit index in the sequence where c appears;
     * and move c to the front.
     */
    public static void encode() {
        char[] sequence = new char[R];
        for (int i = 0; i < R; i++)
            sequence[i] = (char) i; // Initialize sequence for chars.
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int idx = getIdx(sequence, c);
            BinaryStdOut.write(idx, LG_R);
            System.arraycopy(sequence, 0, sequence, 1, idx);
            sequence[0] = c;
        }

        BinaryStdOut.close();
    }

    private static int getIdx(char[] sequence, char c) {
        int i = 0;
        while (i < sequence.length) {
            if (sequence[i] == c)
                return i;
            i++;
        }
        return -1;
    }

    /**
     * apply move-to-front decoding, reading from standard input and writing to
     * standard output
     * 1. Initialize an ordered sequence of 256 characters, where extended ASCII
     * character i appears ith in the sequence.
     * 2. Now, read each 8-bit character i (but treat it as an integer between 0
     * and 255) from standard input one at a time;
     * 3. write the ith character in the sequence;
     * 4. and move that character to the front.
     */
    public static void decode() {
        char[] sequence = new char[R];
        for (int i = 0; i < R; i++)
            sequence[i] = (char) i; // Initialize seqence for chars.
        while (!BinaryStdIn.isEmpty()) {
            int idx = BinaryStdIn.readInt(LG_R);
            char c = sequence[idx];
            BinaryStdOut.write(c);
            System.arraycopy(sequence, 0, sequence, 1, idx);
            sequence[0] = c;
        }

        BinaryStdOut.close();

    }

    /**
     * Sample client that calls move-to-front {@code encode()} if the command-line
     * argument is "-" an move-to-front {@code decode()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
