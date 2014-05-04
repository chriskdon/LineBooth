package linebooth.image.filters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public abstract class GrayscaleFilter implements Filter {
    public abstract GrayscaleBufferedImage apply(GrayscaleBufferedImage img);

    @Override
    public BufferedImage apply(BufferedImage img) {
        return apply(new GrayscaleBufferedImage(img));
    }

    @Override
    public String toString() {
        return "Grayscale Filter";
    }
}

