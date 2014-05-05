package linebooth.image.converters;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class BitPackedImage {
    private byte[] packedImage;
    private int rows, columns;

    public BitPackedImage(int rows, int columns, byte[] packedImage) {
        this.rows = rows;
        this.columns = columns;
        this.packedImage = packedImage;
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
}
