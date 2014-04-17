package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;
import linebooth.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Concise Computer Vision - (pg. 170)
 *
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class WinnemollerBinarization implements IPipelineAction<LineBoothState> {
    private float scale = 0;
    private float change = 1;

    /**
     * Constructor

     * @param scale Scale factor for the kernel.
     */
    public WinnemollerBinarization(float scale, float change) {
        this.scale = scale;
        this.change = change;
    }

    /**
     * Get the centered gauss kernel
     *
     * @return 1D array of the kernel
     */
    private float[] centredGaussKernel(int size, float scale, float multiplier) {
        //if(scale <= 1) { throw new IllegalArgumentException("Scaling factor must be > 1."); }

        float[][] kernel = new float[size][size];

        int radius = size/2;

        final float eulerPart = (float)(1f/(2f * Math.PI * (scale*scale)));

        for(int y = -radius; y < size - radius; y++) {
            for(int x = -radius; x < size - radius; x++) {
                float central = -(((y*y) + (x*x))/(2f * (scale*scale)));

                kernel[y + radius][x + radius] = multiplier * eulerPart * (float)Math.exp(central);
            }
        }

        Utils.print(kernel);

        return Utils.flatten(kernel);
    }

    /**
     * Make value between 0 - 255
     * @param value
     * @return
     */
    private int normalize(int value) {
      if(value >= 255) {
          return 255;
      } else if(value <= 0) {
          return 0;
      } else {
          return value;
      }
    }

    private BufferedImage getL1(BufferedImage src) {
        int size = (int)((6*scale) - 1);

        ConvolveOp l1Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale, 1)));
        BufferedImage l1 = l1Op.filter(src, null);

        return l1;
    }

    private BufferedImage getL2(BufferedImage src) {
        int otherSize = (int)((6*scale*change) - 1);

        ConvolveOp l2Op = new ConvolveOp(new Kernel(otherSize, otherSize, centredGaussKernel(otherSize, scale, change)));
        BufferedImage l2 = l2Op.filter(src, null);

        return l2;
    }

    /**
     * Calculate the Difference of Gaussians (Concise Computer Vision - pg. 75)
     * @param src
     * @return
     */
    private BufferedImage diffOfGauss(BufferedImage src) {
        BufferedImage l1 = getL1(src);
        BufferedImage l2 = getL2(src);

        BufferedImage dog = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < src.getWidth(); x++) {
            for(int y = 0; y < src.getHeight(); y++) {
                Color c1 = new Color(l1.getRGB(x, y));
                Color c2 = new Color(l2.getRGB(x, y));

                int red = normalize(c2.getRed() - (c1.getRed()));
                int green = normalize(c2.getGreen() - (c1.getGreen()));
                int blue = normalize(c2.getBlue() - (c1.getBlue()));

                dog.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return dog;
    }

    @Override
    public LineBoothState action(LineBoothState state) {
        BufferedImage dog = diffOfGauss(state.getForeground());

        state.setOutput(dog);

        return state;
    }
}
