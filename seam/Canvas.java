/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: a picture with pixels forming a doward DAG, with edges from
 *      pixel (x, y) to pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1)
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class Canvas {
    // define the energy of a pixel at the border of the image to be 1000
    private static final double BORDERENG = 1000;
    private int height;
    private int width;
    private boolean isTransposed;
    private double[][] energies;
    private int[][] rgbs;
    // private Iterable<Pixel> topological;

    public Canvas(Picture picture) {
        height = picture.height();
        width = picture.width();
        isTransposed = false;
        energies = new double[height()][width()];
        rgbs = new int[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                rgbs[i][j] = picture.getRGB(j, i);
            }
        }
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                energies[i][j] = calEnergy(j, i);
            }
        }

        // // get topologicalOrder
        // TopologicalOrder topologicalOrder = new TopologicalOrder(this);
        // topological = topologicalOrder.reversePost();
    }

    /**
     * transpose the Canvas
     */
    public void transpose() {
        energies = transpose(energies);
        rgbs = transpose(rgbs);

        // update dimension
        int tmp = height();
        height = width();
        width = tmp;
        isTransposed = !isTransposed;

        // // update topological order
        // updateTopologicalOrder();
    }

    // // update topological order
    // private void updateTopologicalOrder() {
    //     TopologicalOrder topologicalOrder = new TopologicalOrder(this);
    //     topological = topologicalOrder.reversePost();
    // }

    private double[][] transpose(double[][] m) {
        int dRow = m.length;
        int dCol = m[0].length;
        double[][] tM = new double[dCol][dRow];
        for (int i = 0; i < dRow; i++) {
            for (int j = 0; j < dCol; j++) {
                tM[j][i] = m[i][j];
            }
        }
        return tM;
    }

    private int[][] transpose(int[][] m) {
        int dRow = m.length;
        int dCol = m[0].length;
        int[][] tM = new int[dCol][dRow];
        for (int i = 0; i < dRow; i++) {
            for (int j = 0; j < dCol; j++) {
                tM[j][i] = m[i][j];
            }
        }
        return tM;
    }

    public boolean isTransposed() {
        return isTransposed;
    }

    /**
     * Find sequence of indices for vertical seam, which means
     * find a vertical seam of minimum total energy.
     * 1. The weights are on the vertices instead of the edges.
     * 2. the shortest path from any of the W pixels in the top row to
     * any of the W pixels in the bottom row.
     * 3. DAG, where there is a downward edge from pixel (x, y) to
     * pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1),
     * assuming that the coordinates are in the prescribed range.
     * <p>
     * Execute the topological sort algorithm directly on the pixels;
     * Relax vertices in topological order
     * <p>
     * Returns:
     * an array of length H such that entry y is the column number of
     * the pixel to be removed from row y of the image.
     * Example:  { 3, 4, 3, 2, 2 } represent minimum energy vertical seam
     * are (3, 0), (4, 1), (3, 2), (2, 3), and (2, 4).
     */
    public int[] findVerticalSeam() {
        double minEng = Double.POSITIVE_INFINITY;
        Iterable<Integer> path = new Stack<Integer>();
        int[] returnPath = new int[height()];
        int beginRow = 0;
        int endRow = height() - 1;
        TopologicalOrder tp = new TopologicalOrder(this);
        // AcyclicSP asp;
        for (int beginCol = 1; beginCol < width() - 1; beginCol++) {
            tp.buildSingleSourceSP(beginCol, beginRow);
            // AcyclicSP asp = new AcyclicSP(this, beginCol, beginRow);
            for (int endCol = 1; endCol < width() - 1; endCol++) {
                double candEng = tp.distTo(endCol, endRow);
                if (candEng < minEng) {
                    minEng = candEng;
                    path = tp.pathTo(endCol, endRow);
                }
            }
        }
        int row = 0;
        for (int col : path) {
            returnPath[row++] = col;
        }
        return returnPath;
    }


    /**
     * remove vertical seam, including
     * shift left energy matrix matrix,
     * and replace picture with new pic without the seam
     */
    public void removeVerticalSeam(int[] seam) {
        shiftEnergy(seam);
        shiftRGB(seam);

        width--; // must before update energy as it determines the border
        if (width > 0) {
            updateEnergy(seam);
            // updateTopologicalOrder();
        }
    }

    private void shiftEnergy(int[] seam) {
        for (int i = 0; i < height(); i++) {
            // shift energy
            System.arraycopy(energies[i], seam[i] + 1, energies[i], seam[i],
                             width() - seam[i] - 1);
        }
    }

    private void shiftRGB(int[] seam) {
        for (int i = 0; i < height(); i++) {
            // shift rgb
            System.arraycopy(rgbs[i], seam[i] + 1, rgbs[i], seam[i],
                             width() - seam[i] - 1);
        }
    }

    /**
     * update the energy after the seam has been removed.
     */
    public void updateEnergy(int[] seam) {
        for (int i = 0; i < height(); i++) {
            if (seam[i] < width())
                energies[i][seam[i]] = calEnergy(seam[i], i);
            if (seam[i] > 0)
                energies[i][seam[i] - 1] = calEnergy(seam[i] - 1, i);
            // updateEnergy(seam[i] - 1, i);
            // updateEnergy(seam[i], i);
        }
    }


    // width of current canvas
    public int width() {
        return width;
    }

    // height of current canvas
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double getEnergy(int x, int y) {
        return energies[y][x];
    }


    // get the rgb at column x and row y
    public int getRGB(int x, int y) {
        return rgbs[y][x];
    }

    /**
     * get the adjacent pixels of pixel at column x and row y
     * which are pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1)
     * Special cases: consider the border pixels energy is fixed,
     * relatve adj have only one pixel, which is the one exact below, (x, y + 1)
     *
     * @param x column x
     * @param y row y
     * @return Iterable<Pixel>
     */
    public Iterable<Pixel> adj(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        Queue<Pixel> adjPixels = new Queue<Pixel>();
        if (isValidRowIndex(y + 1)) {
            if (isBorder(x, y) || isBorder(x, y + 1)) {
                // special cases
                adjPixels.enqueue(new Pixel(x, y + 1));
            }
            else {
                for (int i = x - 1; i <= x + 1; i++) {
                    if (isValidColumnIndex(i) && !isBorder(i, y + 1))
                        adjPixels.enqueue(new Pixel(i, y + 1));
                }
            }
        }
        return adjPixels;
    }

    // /**
    //  * get topological order
    //  *
    //  * @return topological
    //  */
    // public Iterable<Pixel> getTopological() {
    //     return topological;
    // }
    //

    /**
     * energy of pixel at column x and row y
     * use dual-gradient energy function
     * The energy of pixel (x,y) is sqrt(Δ^2_x(x,y)+Δ^2_y(x,y))
     */
    public double calEnergy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        if (isBorder(x, y))
            return BORDERENG;
        return Math.sqrt(squareGradX(x, y) + squareGradY(x, y));
    }

    /**
     * the square of the x-gradient
     * Δ^2_x(x,y)=R_x(x,y)^2 + G_x(x,y)^2 + B_x(x,y)^2,
     * the central differences R_x(x,y), G_x(x,y), and B_x(x,y) are the
     * differences in the red, green, and blue components between pixel
     * (x + 1, y) and pixel (x − 1, y)
     */
    private double squareGradX(int x, int y) {
        int rgbL = getRGB(x + 1, y);
        int rgbR = getRGB(x - 1, y);
        return diffRGB(rgbL, rgbR);
    }

    /**
     * the square of the y-gradient
     * Δ^2_y(x,y)=R_y(x,y)^2 + G_y(x,y)^2 + B_y(x,y)^2,
     * the central differences R_y(x,y), G_y(x,y), and B_y(x,y) are the
     * differences in the red, green, and blue components between pixel
     * (x, y + 1) and pixel (x, y − 1)
     */
    private double squareGradY(int x, int y) {
        int rgbU = getRGB(x, y + 1);
        int rgbD = getRGB(x, y - 1);
        return diffRGB(rgbU, rgbD);
    }

    private double diffRGB(int thisRGB, int thatRGB) {
        double diffRed = getRed(thisRGB) - getRed(thatRGB);
        double diffGreen = getGreen(thisRGB) - getGreen(thatRGB);
        double diffBlue = getBlue(thisRGB) - getBlue(thatRGB);
        return Math.pow(diffRed, 2) + Math.pow(diffGreen, 2) + Math.pow(diffBlue, 2);
    }

    private int getRed(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        return r;
    }

    private int getGreen(int rgb) {
        int g = (rgb >> 8) & 0xFF;
        return g;
    }

    private int getBlue(int rgb) {
        int b = rgb & 0xFF;
        return b;
    }

    private void validateRowIndex(int row) {
        if (!isValidRowIndex(row))
            throw new IllegalArgumentException(
                    "row index must be between 0 and " + (height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (!isValidColumnIndex(col))
            throw new IllegalArgumentException(
                    "column index must be between 0 and " + (width() - 1) + ": " + col);
    }

    private boolean isValidRowIndex(int row) {
        return (row >= 0 && row < height());
    }

    private boolean isValidColumnIndex(int col) {
        return (col >= 0 && col < width());
    }

    private boolean isBorder(int col, int row) {
        return (col == 0 || col == width() - 1 || row == 0 || row == height() - 1);
    }

    public static void main(String[] args) {

    }
}
