package linebooth;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class GrayscaleBufferedImage extends BufferedImage {
    public GrayscaleBufferedImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public byte getGrayPixel(int x, int y) {
        return (byte)(this.getRGB(x, y) & 0xFF);
    }
}
