package linebooth.image.converters;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class BitPackedImage {
    private byte[] packedImage;
    private int rows, columns;

    public BitPackedImage(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.packedImage = new byte[(int)Math.ceil((rows*columns)/8.0)];
        ;
    }

    public byte[] getPackedImage() {
        return packedImage;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getPixel(int x, int y) {
        int base = (getColumns() * y) + x;
        //System.out.printf("Rows: %d, Cols: %d => (%d,%d)", getRows(), getColumns(), x, y);
        return (getPackedImage()[base / 8] >> ((base - 1)%8)) & 0x1; // Pixel value
    }

    public void setPixel(int x, int y, boolean isBlack) {
        if(isBlack) {
            int base = (getColumns() * y) + x;

            getPackedImage()[base / 8] |= 1 << ((base - 1) % 8);
        }
    }
}
