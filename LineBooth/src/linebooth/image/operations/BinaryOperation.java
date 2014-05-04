package linebooth.image.operations;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public interface BinaryOperation {
    public BufferedImage apply(BufferedImage a, BufferedImage b);
}
