/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: Seam-carving is a content-aware image resizing technique where
 * the image is reduced in size by one pixel of height (or width) at a time.
 * A vertical seam in an image is a path of pixels connected from the top to the
 * bottom with one pixel in each row; a horizontal seam is a path of pixels
 * connected from the left to the right with one pixel in each column.
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {
    // define the energy of a pixel at the border of the image to be 1000
    static final double BORDERENG = 1000;
    private Canvas canvas;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        canvas = new Canvas(picture);

    }

    // current picture
    public Picture picture() {
        return canvas.picture();
    }

    // width of current picture
    public int width() {
        return canvas.picWidth();
    }

    // height of current picture
    public int height() {
        return canvas.picHeight();
    }

    /**
     * get the enerty of pixel at column x and row y
     */
    public double energy(int x, int y) {
        return canvas.energy(x, y);
    }

    private void validateRowIndex(int row) {
        if (row < 0 || row >= canvas.height())
            throw new IllegalArgumentException(
                    "row index must be between 0 and " + (canvas.height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (col < 0 || col >= canvas.width())
            throw new IllegalArgumentException(
                    "column index must be between 0 and " + (canvas.width() - 1) + ": " + col);
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
        Iterable<Pixel> path = new Stack<Pixel>();
        int[] returnPath = new int[canvas.height()];
        int beginRow = 0;
        int endRow = canvas.height() - 1;
        for (int beginCol = 1; beginCol < canvas.width() - 1; beginCol++) {
            AcyclicSP asp = new AcyclicSP(canvas, beginCol, beginRow);
            for (int endCol = 1; endCol < canvas.width() - 1; endCol++) {
                double candEng = asp.distTo(endCol, endRow);
                if (candEng < minEng) {
                    minEng = candEng;
                    path = asp.pathTo(endCol, endRow);
                }
            }
        }
        int row = 0;
        for (Pixel p : path) {
            returnPath[row++] = p.getCol();
        }
        return returnPath;
    }

    /**
     * transpose the image, call findVerticalSeam(), and transpose it back.
     */
    public int[] findHorizontalSeam() {
        canvas.transpose();
        int[] returnPath = findVerticalSeam();
        canvas.transpose();
        return returnPath;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (isValidHorizontalSeam(seam)) {
            canvas.removeHorizontalSeam(seam);
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (isValidVerticalSeam(seam)) {
            canvas.removeVerticalSeam(seam);
        }
    }

    private boolean isValidHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != canvas.width())
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException();
        }
        return true;
    }

    private boolean isValidVerticalSeam(int[] seam) {
        if (seam == null || seam.length != canvas.height())
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException();
        }
        return true;
    }

    public static void main(String[] args) {

    }
}
