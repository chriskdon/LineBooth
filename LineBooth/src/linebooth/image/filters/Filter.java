package linebooth.image.filters;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public interface Filter {
    /**
     * Modifies img
     * @param img
     * @return
     */
    public BufferedImage apply(BufferedImage img);
}
