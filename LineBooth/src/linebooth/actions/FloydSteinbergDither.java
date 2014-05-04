package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class FloydSteinbergDither implements IPipelineAction<LineBoothState> {
    private int binarize(int gray) {
        return gray > 128 ? 255 : 0;
    }

    private int gray(int rgb) {
        Color c = new Color(rgb);

        return c.getRed();
    }

    private int getRGB(int gray) {
        if(gray > 255) gray = 255;
        if(gray < 0) gray = 0;

        return new Color(gray, gray, gray, 255).getRGB();
    }


    @Override
    public void action(LineBoothState state) {

        BufferedImage img = state.getOutput();

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int g = gray(img.getRGB(x, y)); // Get the gray value
                int b = binarize(g);            // Get the binarized value

                img.setRGB(x, y, getRGB(b));    // Set the current pixel to the binarized value

                int error = g - b;

                if(x + 1 < img.getWidth()) {
                    img.setRGB(x + 1, y, getRGB(gray(img.getRGB(x + 1, y)) + ((error * 7)/16)));
                }

                if(y + 1 < img.getHeight()) {
                    if(x > 0) {
                        img.setRGB(x - 1, y + 1, getRGB(gray(img.getRGB(x - 1, y + 1)) + ((error * 3)/16)));
                    }

                    img.setRGB(x, y + 1, getRGB(gray(img.getRGB(x, y + 1)) + ((error * 5)/16)));

                    if(x + 1 < img.getWidth()) {
                        img.setRGB(x + 1, y + 1, getRGB(gray(img.getRGB(x + 1, y + 1)) + ((error * 1)/16)));
                    }
                }

            }
        }

    }
}
