package linebooth.image.converters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class GrayImagePacker implements BitPacker {
    public BitPackedImage convert(GrayscaleBufferedImage image) {
        BitPackedImage packed = new BitPackedImage(image.getHeight(), image.getWidth());

        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                packed.setPixel(x, y, image.getGrayPixel(x, y) < 128);
            }
        }

        return packed;
    }

    @Override
    public BitPackedImage convert(BufferedImage image) {
        return convert(new GrayscaleBufferedImage(image));
    }
}
