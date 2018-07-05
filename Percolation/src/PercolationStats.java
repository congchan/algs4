import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/** perform trials independent experiments on an n-by-n grid. */
public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int trials;
    private double[] ps;

    public PercolationStats(int n, int trials) {
        if(n < 1 | trials < 1) {
            throw new IllegalArgumentException();
        }
        this.trials = trials;
        ps = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                // Choose a site uniformly at random among all blocked sites.
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);

                // Open the site.
                perc.open(row, col);
            }
            // The fraction of sites that are opened
            ps[i] = (double) perc.numberOfOpenSites() / (n * n);
        }


    }

    /** sample mean of percolation threshold.   */
    public double mean()  {
        return StdStats.mean(ps);
    }

    /** sample standard deviation of percolation threshold.  */
    public double stddev() {
        return StdStats.stddev(ps);
    }

    /** low  endpoint of 95% confidence interval.  */
    public double confidenceLo() {
        return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    /** high endpoint of 95% confidence interval. */
    public double confidenceHi() {
        return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
    }

    public static void main(String[] args) {   // test client
        int n, trials;
        try {
            // Parse the string argument into an integer value.
            n = Integer.parseInt(args[0]);
            trials = Integer.parseInt(args[1]);

            PercolationStats perStats = new PercolationStats(n, trials);
            System.out.println("mean                    = " + perStats.mean());
            System.out.println("stddev                  = " + perStats.stddev());
            System.out.println("95% confidence interval = [" + perStats.confidenceLo() + ", " + perStats.confidenceHi() + "]");

        } catch (NumberFormatException nfe) {
            System.out.println("The argument must be an integer.");
        }

    }
}
