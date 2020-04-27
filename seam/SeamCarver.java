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

public class SeamCarver {
    private Canvas canvas;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        canvas = new Canvas(picture);
    }

    // current picture, construct a picture from pixels for mutation defend
    public Picture picture() {
        Picture pic = new Picture(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                if (canvas.isTransposed())
                    pic.setRGB(j, i, canvas.getRGB(i, j));
                else
                    pic.setRGB(j, i, canvas.getRGB(j, i));
            }
        }
        return pic;
    }

    // width of current picture
    public int width() {
        if (canvas.isTransposed()) // transpose back
            return canvas.height();
        else
            return canvas.width();
    }

    // height of current picture
    public int height() {
        if (canvas.isTransposed()) // transpose back
            return canvas.width();
        else
            return canvas.height();
    }

    /**
     * get the energy of pixel at column x and row y
     */
    public double energy(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        if (canvas.isTransposed()) { // transpose back
            return canvas.getEnergy(y, x);
        }
        else {
            return canvas.getEnergy(x, y);
        }
        //     canvas.transpose();
        // validateColumnIndex(x);
        // validateRowIndex(y);
        // return canvas.getEnergy(x, y);
    }


    /**
     * Find sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        int[] returnPath = canvas.findVerticalSeam();
        return returnPath;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!canvas.isTransposed())
            validateSeam(seam, canvas.height(), canvas.width() - 1);
        else
            validateSeam(seam, canvas.width(), canvas.height() - 1);

        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        canvas.removeVerticalSeam(seam);
    }

    /**
     * transpose the image, call findVerticalSeam().
     */
    public int[] findHorizontalSeam() {
        if (!canvas.isTransposed())
            canvas.transpose();
        int[] returnPath = canvas.findVerticalSeam();
        return returnPath;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!canvas.isTransposed())
            validateSeam(seam, canvas.width(), canvas.height() - 1);
        else
            validateSeam(seam, canvas.height(), canvas.width() - 1);

        if (!canvas.isTransposed())
            canvas.transpose();
        canvas.removeVerticalSeam(seam);
    }

    private void validateSeam(int[] seam, int validLength, int validRange) {
        if (seam == null)
            throw new IllegalArgumentException("null argument");
        if (seam.length != validLength)
            throw new IllegalArgumentException(
                    "seam length must be " + validLength + ": " + seam.length);
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > validRange)
                throw new IllegalArgumentException(
                        "seam index must be between 0 and " + validRange + ": " + seam[i]);
            if (i < seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException(
                        "seam diff must <= 1 but: (" + seam[i] + ": " + seam[i + 1] + ")");
        }
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

    public static void main(String[] args) {

    }
}
