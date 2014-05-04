package linebooth;

import java.awt.image.BufferedImage;
import java.lang.management.BufferPoolMXBean;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public interface IFilter {
    /**
     * Modifies img
     * @param img
     * @return
     */
    public BufferedImage apply(BufferedImage img);
}
