package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-17.
 */
public class Sobel implements IPipelineAction<LineBoothState> {
    @Override
    public LineBoothState action(LineBoothState state) {
        float[] x = {
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
        };

        float[] y = {
            1, 2, 1,
            0, 0, 0,
            -1, -2, -1
        };

        ConvolveOp convolveX = new ConvolveOp(new Kernel(3, 3, x));
        ConvolveOp convolveY = new ConvolveOp(new Kernel(3, 3, y));

        state.setOutput(convolveY.filter(convolveX.filter(state.getOutput(), null), null));

        return state;
    }
}
