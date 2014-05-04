package linebooth.image.filters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Dithering Filter
 *
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class FloydSteinbergDitherFilter extends GrayscaleFilter {
    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img, GrayscaleBufferedImage dest) {
        dest = new GrayscaleBufferedImage(img);

        for (int x = 0; x < dest.getWidth(); x++) {
            for (int y = 0; y < dest.getHeight(); y++) {
                int g = dest.getGrayPixel(x, y);                     // Get the gray value
                int b = (g > 128 ? 255 : 0);                                // Get the binarized value

                dest.setGrayPixel(x, y, b);                          // Set the current pixel to the binarized value

                int error = g - b;

                if(x + 1 < dest.getWidth()) {                        // Right Pixel
                    dest.plusGrayPixel(x + 1, y, (error * 7) >> 4);
                }

                if(y + 1 < dest.getHeight()) {
                    int y1 = y + 1;
                    if(x > 0) {                                     // Bottom Left Pixel
                        dest.plusGrayPixel(x - 1, y1, (error * 3) >> 4);
                    }

                    dest.plusGrayPixel(x, y1, (error * 5) >> 4);     // Bottom Center Pixel

                    if(x + 1 < dest.getWidth()) {                    // Bottom Right Pixel
                        dest.plusGrayPixel(x + 1, y1, (error * 1) >> 4);
                    }
                }
            }
        }

        return dest;
    }
}
