package linebooth.image.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class SkinFilter implements Filter {
    private int max(int x, int y, int z) {
        return Math.max(Math.max(x, y), z);
    }

    private int min(int x, int y, int z) {
        return Math.min(Math.min(x, y), z);
    }

    @Override
    public BufferedImage apply(BufferedImage img, BufferedImage dest) {
        if(dest == null) {
            dest = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color c = new Color(img.getRGB(x, y));

                if (c.getRed() > 95 && c.getGreen() > 40 && c.getBlue() > 20) {
                    int max = max(c.getRed(), c.getGreen(), c.getBlue());
                    int min = min(c.getRed(), c.getGreen(), c.getBlue());

                    if (max - min > 15) {
                       if(Math.abs(c.getRed() - c.getGreen()) > 15 && c.getRed() > c.getGreen() && c.getRed() > c.getBlue()) {
                           dest.setRGB(x, y, c.getRGB());
                       }
                    }
                } else {
                    dest.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        return dest;
    }
}
