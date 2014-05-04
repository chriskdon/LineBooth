package linebooth.image.extractor;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public interface Extractor {
    public BufferedImage extract(BufferedImage foreground, BufferedImage background, BufferedImage dest);
}
