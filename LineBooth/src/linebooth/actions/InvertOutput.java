package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-17.
 */
public class InvertOutput implements IPipelineAction<LineBoothState> {
    @Override
    public void action(LineBoothState state) {
        BufferedImage src = state.getOutput();

        for(int x = 0; x < src.getWidth(); x++) {
            for(int y = 0; y < src.getHeight(); y++) {
                Color c = new Color(src.getRGB(x, y));

                src.setRGB(x, y, new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
            }
        }
    }
}
