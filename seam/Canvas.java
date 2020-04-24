/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: a picture with pixels forming a doward DAG, with edges from
 *      pixel (x, y) to pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1)
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;

public class Canvas {
    // define the energy of a pixel at the border of the image to be 1000
    static final double BORDERENG = 1000;
    private Picture picture;
    private int height;
    private int width;
    private boolean isTransposed;
    private double[][] energies;
    private Pixel[][] pixels;
    private Iterable<Pixel> topological;

    public Canvas(Picture picture) {
        this.picture = new Picture(picture);
        height = picture.height();
        width = picture.width();
        isTransposed = false;
        energies = new double[height()][width()];
        pixels = new Pixel[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                energies[i][j] = calEnergy(j, i);
                pixels[i][j] = new Pixel(j, i);
            }
        }
        // get topologicalOrder
        TopologicalOrder topologicalOrder = new TopologicalOrder(this);
        topological = topologicalOrder.reversePost();
    }

    /**
     * transpose the Canvas
     */
    public void transpose() {
        energies = transpose(energies, height, width);
        pixels = transpose(pixels, height, width);

        // update dimension
        int tmp = height;
        height = width;
        width = tmp;
        isTransposed = !isTransposed;

        // update topological order
        TopologicalOrder topologicalOrder = new TopologicalOrder(this);
        topological = topologicalOrder.reversePost();
    }

    private Pixel[][] transpose(Pixel[][] m, int dx, int dy) {
        assert dx == m.length;
        assert dy == m[0].length;
        Pixel[][] tM = new Pixel[dy][dx];
        for (int i = 0; i < dx; i++) {
            for (int j = 0; j < dy; j++) {
                tM[j][i] = new Pixel(i, j);
            }
        }
        return tM;
    }

    private double[][] transpose(double[][] m, int dx, int dy) {
        assert dx == m.length;
        assert dy == m[0].length;
        double[][] tM = new double[dy][dx];
        for (int i = 0; i < dx; i++) {
            for (int j = 0; j < dy; j++) {
                tM[j][i] = m[i][j];
            }
        }
        return tM;
    }

    /**
     * remove vertical seam, including
     * shift left energy matrix, pixels matrix,
     * and replace picture with new pic without the seam
     */
    public void removeVerticalSeam(int[] seam) {
        // double[][] newEngs = new double[height()][width() - 1];
        // Pixel[][] newPixels = new Pixel[height()][width() - 1];
        Picture newPic = new Picture(width() - 1, height());
        for (int i = 0; i < height(); i++) {
            // shift energy
            // System.arraycopy(energies[i], 0, newEngs[i], 0, seam[i]);
            System.arraycopy(energies[i], seam[i] + 1, energies[i], seam[i],
                             width() - seam[i] - 1);

            // shift pixels
            // System.arraycopy(pixels[i], 0, newPixels[i], 0, seam[i]);
            System.arraycopy(pixels[i], seam[i] + 1, pixels[i], seam[i],
                             width() - seam[i] - 1);

            // remove the actual picture seam
            for (int j = 0; j < seam[i]; j++) {
                newPic.setRGB(j, i, picture.getRGB(j, i));
            }
            for (int j = seam[i] + 1; j < width(); j++) {
                newPic.setRGB(j - 1, i, picture.getRGB(j, i));
            }
        }
        picture = newPic;
        width--;

        updateVerticalEnergy(seam);
    }

    /**
     * update the energy after the vertical seam has been removed.
     */
    public void updateVerticalEnergy(int[] seam) {
        for (int i = 0; i < height(); i++) {
            updateEnergy(seam[i] - 1, i);
            updateEnergy(seam[i], i);
        }
    }

    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // width of current canvas
    public int width() {
        return width;
    }

    // height of current canvas
    public int height() {
        return height;
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int picWidth() {
        return picture.width();
    }

    // height of current picture
    public int picHeight() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return energies[y][x];
    }

    /**
     * get the pixel at column x and row y
     *
     * @param x column x
     * @param y row y
     * @return Pixel
     */
    public Pixel getPixel(int x, int y) {
        // if (isTransposed) {
        //     return pixel(y, x);
        // }
        return pixel(x, y);
    }

    private Pixel pixel(int x, int y) {
        return pixels[y][x];
    }


    /**
     * get the vertical adjacent pixels of pixel at column x and row y
     * which are pixels (x − 1, y + 1), (x, y + 1), and (x + 1, y + 1)
     * Special cases: consider the border pixels energy is fixed,
     * relatve adj have only one pixel, which is the one exact below, (x, y + 1)
     *
     * @param x column x
     * @param y row y
     * @return Iterable<Pixel>
     */
    public Iterable<Pixel> verticalAdj(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        Queue<Pixel> adjPixels = new Queue<Pixel>();
        if (isValidRowIndex(y + 1)) {
            if (isBorder(x, y) || isBorder(x, y + 1)) {
                // special cases
                adjPixels.enqueue(pixels[y + 1][x]);
            }
            else {
                for (int i = x - 1; i <= x + 1; i++) {
                    if (isValidColumnIndex(i))
                        adjPixels.enqueue(pixels[y + 1][i]);
                }
            }
        }
        return adjPixels;
    }


    /**
     * get the horizontal adjacent pixels of pixel at column x and row y
     * which are pixels (x + 1, y - 1), (x + 1, y), and (x + 1, y + 1)
     * Special cases: consider the border pixels energy is fixed,
     * relatve adj have only one pixel, which is the one exact below, (x, y + 1)
     *
     * @param x column x
     * @param y row y
     * @return Iterable<Pixel>
     */
    public Iterable<Pixel> horizontalAdj(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        Queue<Pixel> adjPixels = new Queue<Pixel>();
        if (isValidRowIndex(x + 1)) {
            if (isBorder(x, y) || isBorder(x + 1, y)) {
                // special cases
                adjPixels.enqueue(pixels[y][x + 1]);
            }
            else {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (isValidRowIndex(j))
                        adjPixels.enqueue(pixels[j][x + 1]);
                }
            }
        }
        return adjPixels;
    }


    /**
     * The actual adj pixels depends on whether the it is transposed or not.
     */
    public Iterable<Pixel> adj(int col, int row) {
        // if (isTransposed) {
        //     return adjacent(row, col);
        // }
        return adjacent(col, row);
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
    private Iterable<Pixel> adjacent(int x, int y) {
        validateColumnIndex(x);
        validateRowIndex(y);
        Queue<Pixel> adjPixels = new Queue<Pixel>();
        if (isValidRowIndex(y + 1)) {
            if (isBorder(x, y) || isBorder(x, y + 1)) {
                // special cases
                adjPixels.enqueue(pixels[y + 1][x]);
            }
            else {
                for (int i = x - 1; i <= x + 1; i++) {
                    if (isValidColumnIndex(i))
                        adjPixels.enqueue(pixels[y + 1][i]);
                }
            }
        }
        return adjPixels;
    }

    /**
     * get topological order
     *
     * @return topological
     */
    public Iterable<Pixel> getTopological() {
        return topological;
    }

    /**
     * update the energy at column x and row y
     */
    private void updateEnergy(int x, int y) {
        energies[y][x] = calEnergy(x, y);
    }


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
        int rgbL = picture.getRGB(x + 1, y);
        int rgbR = picture.getRGB(x - 1, y);
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
        int rgbU = picture.getRGB(x, y + 1);
        int rgbD = picture.getRGB(x, y - 1);
        return diffRGB(rgbU, rgbD);
    }

    private double diffRGB(int thisRGB, int thatRGB) {
        double diffRed = getRed(thisRGB) - getRed(thatRGB);
        double diffGreen = getGreen(thisRGB) - getGreen(thatRGB);
        double diffBlue = getBlue(thisRGB) - getBlue(thatRGB);
        return diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue;
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
