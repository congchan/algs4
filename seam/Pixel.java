/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Pixel {

    private int col;
    private int row;
    // private int rgb;

    public Pixel(int col, int row) {
        this.row = row;
        this.col = col;
        // this.rgb = rgb;
    }

    // public void setRGB(int rgb) {
    //     this.rgb = rgb;
    // }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    // public int getRGB() {
    //     return rgb;
    // }

    public static void main(String[] args) {

    }
}
