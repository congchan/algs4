/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Pixel {

    private int col;
    private int row;
    private int rgb;

    public Pixel(int col, int row, int rgb) {
        this.row = row;
        this.col = col;
        this.rgb = rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getRgb() {
        return rgb;
    }

    public static void main(String[] args) {

    }
}
