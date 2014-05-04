package linebooth.image.converters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class GrayscaleImageToIntArrayConverter implements IntArrayConverter {
    public int[][] convert(GrayscaleBufferedImage image) {
        int[][] output = new int[image.getHeight()][image.getWidth()];

        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                output[y][x] = (image.getGrayPixel(x, y) > 128 ? 0 : 1);
            }
        }

        return output;
    }

    @Override
    public int[][] convert(BufferedImage image) {
        return convert(new GrayscaleBufferedImage(image));
    }
}
