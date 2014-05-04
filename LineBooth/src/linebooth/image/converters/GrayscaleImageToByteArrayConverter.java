package linebooth.image.converters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class GrayscaleImageToByteArrayConverter implements ByteArrayConverter {
    public byte[] convert(GrayscaleBufferedImage image) {
        byte[] output = new byte[ image.getPixelData().length];

        for(int i = 0; i < output.length; i++) {
            output[i] = (byte)(image.getGrayPixel(i) > 128 ? 1 : 0);
        }

        return output;
    }

    @Override
    public byte[] convert(BufferedImage image) {
        return convert(new GrayscaleBufferedImage(image));
    }
}
