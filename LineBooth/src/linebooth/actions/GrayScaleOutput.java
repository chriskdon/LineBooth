package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class GrayScaleOutput implements IPipelineAction<LineBoothState> {
    @Override
    public void action(LineBoothState state) {
        int[] oData = ((DataBufferInt) state.getOutput().getRaster().getDataBuffer()).getData();

        for (int i = 0; i < state.getWidth() * state.getHeight(); i++) {
            oData[i] = grayscale(oData[i]);
        }
    }


    /**
     * Convert RGB to grayscale.
     *
     * @param argb
     * @return
     */
    private int grayscale(int argb) {
        int g = (int) ((0.21 * ((argb >> 16) & 0xFF)) + (0.71 * ((argb >> 8) & 0xFF)) + (0.07 * (argb & 0xFF)));

        return (argb & 0xFF000000) | (g << 16) | (g << 8) | g;
    }
}
