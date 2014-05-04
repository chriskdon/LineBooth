package linebooth.image.filters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public abstract class GrayscaleFilter implements Filter {
    public abstract GrayscaleBufferedImage apply(GrayscaleBufferedImage img, GrayscaleBufferedImage dest);

    @Override
    public BufferedImage apply(BufferedImage img, BufferedImage dest) {
        GrayscaleBufferedImage out = null;

        if (dest != null && !(dest instanceof GrayscaleBufferedImage)) {
            out = new GrayscaleBufferedImage(dest);
        }

        return apply(new GrayscaleBufferedImage(img), out);
    }

    @Override
    public String toString() {
        return "Grayscale Filter";
    }
}

