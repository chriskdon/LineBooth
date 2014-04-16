package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class GrayScaleForegroundBackground implements IPipelineAction<LineBoothState> {
    @Override
    public LineBoothState action(LineBoothState state) {
        BufferedImage background = state.getBackground();
        BufferedImage foreground = state.getForeground();

        for(int x = 0; x < state.getWidth(); x++) {
            for(int y = 0; y < state.getHeight(); y++) {
                background.setRGB(x, y, grayscale(background.getRGB(x, y)));
                foreground.setRGB(x, y, grayscale(foreground.getRGB(x, y)));
            }
        }

        return state;
    }

    /**
     * Convert RGB to grayscale.
     * @param rgb
     * @return
     */
    private int grayscale(int rgb) {
        Color c = new Color(rgb);
        int g = (c.getRed() + c.getGreen() + c.getBlue())/3;

        return new Color(g, g, g).getRGB();
    }
}
