package linebooth.image.converters;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public interface ImageToByteArrayConverter {
    public byte[] convert(BufferedImage image);
}
