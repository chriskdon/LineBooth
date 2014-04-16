package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;
import linebooth.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

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
    private int size = 1;

    /**
     * Constructor
     *
     * @param size  Matrix size for the kernel.
     * @param scale Scale factor for the kernel.
     */
    public WinnemollerBinarization(int size, float scale, float change) {
        this.size = size;
        this.scale = scale;
        this.change = change;
    }

    /**
     * Get the centered gauss kernel
     *
     * @return 1D array of the kernel
     */
    private float[] centredGaussKernel(int size, float scale) {
        float[][] kernel = new float[size][size];

        int radius = size/2;

        final float eulerPart = (float)(1f/(2f * Math.PI * (scale*scale)));

        for(int y = -radius; y < size - radius; y++) {
            for(int x = -radius; x < size - radius; x++) {
                float central = -(((y*y) + (x*x))/(2f * (scale*scale)));

                kernel[y + radius][x + radius] = eulerPart * (float)Math.exp(central);
            }
        }

        return Utils.flatten(kernel);
    }

    /**
     * Make value between 0 - 255
     * @param value
     * @return
     */
    private int normalize(int value) {
      return value > 255 ? 255 : value < 0 ? 0 : value;
    }

    private BufferedImage subtract(BufferedImage img, BufferedImage fromImg) {
        BufferedImage diff = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                Color c1 = new Color(fromImg.getRGB(x, y));
                Color c2 = new Color(img.getRGB(x, y));

                int red = normalize(c1.getRed() - c2.getRed());
                int green = normalize(c1.getGreen() - c2.getGreen());
                int blue = normalize(c1.getBlue() - c2.getBlue());

                diff.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return diff;
    }

    /**
     * Calculate the Difference of Gaussians (Concise Computer Vision - pg. 75)
     * @param src
     * @return
     */
    private BufferedImage differenceOfGauss(BufferedImage src) {
        ConvolveOp l1Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale)));
        ConvolveOp l2Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale * change)));

        BufferedImage l1 = l1Op.filter(src, null);
        BufferedImage l2 = l2Op.filter(src, null);

        return subtract(l2, l1);
    }

    @Override
    public LineBoothState action(LineBoothState state) {
        state.setOutput(differenceOfGauss(state.getForeground()));

        return state;
    }
}
