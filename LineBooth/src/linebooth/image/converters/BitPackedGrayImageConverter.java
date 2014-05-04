package linebooth.image.converters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class BitPackedGrayImageConverter implements ImageToByteArrayConverter {
    public byte[] convert(GrayscaleBufferedImage image) {
        byte[] output = new byte[(int)Math.ceil((image.getHeight()*image.getWidth())/8.0)];

        for(int x = 0, i = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++, i++) {
                for(int bit = 0; bit < 8; bit++) { // Pack the bits
                    // output[i] = (image.getGrayPixel(x, y) > 128 ? 0 : 1);
                }
            }
        }

        return output;
    }

    @Override
    public byte[] convert(BufferedImage image) {
        return convert(new GrayscaleBufferedImage(image));
    }
}
