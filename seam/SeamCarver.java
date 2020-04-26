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
        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        Picture pic = new Picture(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                pic.setRGB(j, i, canvas.getRgb(j, i));
            }
        }
        return pic;
    }

    // width of current picture
    public int width() {
        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        return canvas.width();
    }

    // height of current picture
    public int height() {
        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        return canvas.height();
    }

    /**
     * get the enerty of pixel at column x and row y
     */
    public double energy(int x, int y) {
        if (canvas.isTransposed()) // transpose back
            canvas.transpose();
        return canvas.getEnergy(x, y);
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
            validateSeam(seam, canvas.height());
        else
            validateSeam(seam, canvas.width());

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
            validateSeam(seam, canvas.width());
        else
            validateSeam(seam, canvas.height());

        if (!canvas.isTransposed())
            canvas.transpose();
        canvas.removeVerticalSeam(seam);
    }

    private void validateSeam(int[] seam, int validLength) {
        if (seam == null || seam.length != validLength)
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1
                    || seam[i] < 0 || seam[i] >= validLength)
                throw new IllegalArgumentException();
        }
    }

    private boolean isValidHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != canvas.width())
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1
                    || seam[i] < 0 || seam[i] >= canvas.width())
                throw new IllegalArgumentException();
        }
        return true;
    }

    private boolean isValidVerticalSeam(int[] seam) {
        if (seam == null || seam.length != canvas.height())
            throw new IllegalArgumentException();
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1
                    || seam[i] < 0 || seam[i] >= canvas.height())
                throw new IllegalArgumentException();
        }
        return true;
    }

    public static void main(String[] args) {

    }
}
