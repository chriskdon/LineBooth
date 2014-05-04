package linebooth.image.extractor;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class SubtractionExtractor implements Extractor {
    private int normalize(int value) {
        if(value > 255) { value = 255; }
        if(value < 0) { value = 0; }

        return value;
    }

    @Override
    public BufferedImage extract(BufferedImage foreground, BufferedImage background, BufferedImage dest) {
        if(dest == null) {
            dest = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        for(int x = 0; x < dest.getWidth(); x++) {
            for(int y = 0; y < dest.getHeight(); y++) {
                Color f = new Color(foreground.getRGB(x, y));
                Color b = new Color(background.getRGB(x, y));

                int red = normalize(f.getRed() - b.getRed());
                int green = normalize(f.getGreen() - b.getGreen());
                int blue = normalize(f.getBlue() - b.getBlue());

                dest.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return dest;
    }
}
