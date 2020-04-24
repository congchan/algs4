/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class weightedPixelImage {
    private double[][] energies;
    private int[][] pixels;

    public weightedPixelImage(double[][] energies, int[][] pixels) {
        this.energies = energies;
        this.pixels = pixels;
    }

    private class Pixel {
        int col;
        int row;

        public Pixel(int x, int y) {
            col = x;
            row = y;
        }


        public Pixel[] adj() {

        }
    }

    public static void main(String[] args) {

    }
}
