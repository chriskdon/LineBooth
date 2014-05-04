package linebooth.actions;

import linebooth.GrayscaleBufferedImage;
import linebooth.GrayscaleFilter;

/**
 * Dithering Filter
 *
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class FloydSteinbergDitherFilter extends GrayscaleFilter {
    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int g = img.getGrayPixel(x, y);                     // Get the gray value
                int b = GrayscaleBufferedImage.binarize(g);         // Get the binarized value

                img.setGrayPixel(x, y, b);                          // Set the current pixel to the binarized value

                int error = g - b;

                if(x + 1 < img.getWidth()) {                        // Right Pixel
                    img.plusGrayPixel(x + 1, y, (error * 7) >> 4);
                }

                if(y + 1 < img.getHeight()) {
                    int y1 = y + 1;
                    if(x > 0) {                                     // Bottom Left Pixel
                        img.plusGrayPixel(x - 1, y1, (error * 3) >> 4);
                    }

                    img.plusGrayPixel(x, y1, (error * 5) >> 4);     // Bottom Center Pixel

                    if(x + 1 < img.getWidth()) {                    // Bottom Right Pixel
                        img.plusGrayPixel(x + 1, y1, (error * 1) >> 4);
                    }
                }
            }
        }

        return img;
    }
}
