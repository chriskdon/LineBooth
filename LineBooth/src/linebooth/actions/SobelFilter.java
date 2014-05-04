package linebooth.actions;

import linebooth.GrayscaleBufferedImage;
import linebooth.GrayscaleFilter;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-17.
 */
public class SobelFilter extends GrayscaleFilter {
    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img) {
        float[] x = {
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
        };

        float[] y = {
                1, 2, 1,
                0, 0, 0,
                -1, -2, -1
        };

        ConvolveOp convolveX = new ConvolveOp(new Kernel(3, 3, x));
        ConvolveOp convolveY = new ConvolveOp(new Kernel(3, 3, y));

        return new GrayscaleBufferedImage(convolveY.filter(convolveX.filter(img, null), null), false);
    }
}
