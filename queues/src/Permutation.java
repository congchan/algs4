/**
 * Takes an integer k as a command-line argument; 
 * reads in a sequence of strings from standard input using StdIn.readString(); 
 * and prints exactly k of them, uniformly at random. 
 * Print each item from the sequence at most once.
 * Assume that 0 < k < n, where n is the number of string on standard input.
 * Using Reservoir Sampling, 
 * only one RandomizedQueue object of maximum size at most k is used.
 * @author  Cong Chen
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        int k;
        int n = 0;
        try {
            // Parse the string argument into an integer value.
            k = Integer.parseInt(args[0]);
            RandomizedQueue<String> rq = new RandomizedQueue<String>();

            while (!StdIn.isEmpty()) {
                n++;
                String in = StdIn.readString();

                /** Reservoir Sampling
                * 1. Keep the first k items in memory.
    			  2. When the i-th item arrives (for $i>k$):
    			     * with probability $k/i$, keep the new item 
    			     and discard an old one, 
    			     selecting which to replace at random, 
    			     each with chance $1/k$.
        			 * with probability $1-k/i$, ignore the new one
                */
                if (n > k && StdRandom.uniform(n) < k)  {
                    rq.dequeue();
                } else if (n > k) { continue; }

                rq.enqueue(in);
            }

            for (int i = 0; i < k; i++) {
                System.out.println(rq.dequeue());
            }


        } catch (NumberFormatException nfe) {
            System.out.println("The argument must be an integer.");
        }
    }
}
