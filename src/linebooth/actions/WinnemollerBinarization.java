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
    private int size = 1;
    private int otherSize = 1;
    private float sensitivity = 1.5f;

    /**
     * Constructor
     *
     * @param size  Matrix size for the kernel.
     * @param scale Scale factor for the kernel.
     */
    public WinnemollerBinarization(int size, int otherSize, float scale, float change, float sensitivity) {
        this.size = size;
        this.otherSize = otherSize;
        this.scale = scale;
        this.change = change;
        this.sensitivity = sensitivity;
    }

    /**
     * Get the centered gauss kernel
     *
     * @return 1D array of the kernel
     */
    private float[] centredGaussKernel(int size, float scale) {
        //if(scale <= 1) { throw new IllegalArgumentException("Scaling factor must be > 1."); }

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

    private BufferedImage getL1(BufferedImage src) {
        ConvolveOp l1Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale)));
        BufferedImage l1 = l1Op.filter(src, null);

        return l1;
    }

    private BufferedImage getL2(BufferedImage src) {
        ConvolveOp l2Op = new ConvolveOp(new Kernel(otherSize, otherSize, centredGaussKernel(otherSize, scale * change)));
        BufferedImage l2 = l2Op.filter(src, null);

        return l2;
    }

    /**
     * Calculate the Difference of Gaussians (Concise Computer Vision - pg. 75)
     * @param src
     * @return
     */
    private BufferedImage dogEdgeDetector(BufferedImage src) {
        BufferedImage l1 = getL1(src);
        BufferedImage l2 = getL2(src);

        BufferedImage dog = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < src.getWidth(); x++) {
            for(int y = 0; y < src.getHeight(); y++) {
                Color c1 = new Color(l1.getRGB(x, y));
                Color c2 = new Color(l2.getRGB(x, y));

                int red = normalize(c1.getRed() - (int)((c2.getRed()*sensitivity)));
                int green = normalize(c1.getGreen() - (int)((c2.getGreen()*sensitivity)));
                int blue = normalize(c1.getBlue() - (int)((c2.getBlue()*sensitivity)));

                dog.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return dog;
    }

    @Override
    public LineBoothState action(LineBoothState state) {
        BufferedImage dog = dogEdgeDetector(state.getForeground());

        state.setOutput(dog);

        return state;
    }
}
