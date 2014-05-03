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
 * <p/>
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class WinnemollerBinarization implements IPipelineAction<LineBoothState> {
    private float scale = 1;
    private float change = 1f;
    private float differenceSensitivity = 1.25f;
    private float sharpenAmount = 0.7f;
    private float thresholdSensitivity = 1.5f;
    private int threshold = 180;

    public void setScale(float x) {
        scale = x;
    }


    /**
     * Constructor
     *
     * @param scale Scale factor for the kernel.
     */
    public WinnemollerBinarization(float scale, float change,
                                   float differenceSensitivity, float sharpenAmount,
                                   float thresholdSensitivity, int threshold) {
        this.scale = scale;
        this.change = change;
        this.differenceSensitivity = differenceSensitivity;
        this.sharpenAmount = sharpenAmount;
        this.thresholdSensitivity = thresholdSensitivity;
        this.threshold = threshold;
    }

    /**
     * Get the centered gauss kernel
     *
     * @return 1D array of the kernel
     */
    private float[] centredGaussKernel(int size, float scale) {
        //if(scale <= 1) { throw new IllegalArgumentException("Scaling factor must be > 1."); }

        float[][] kernel = new float[size][size];

        int radius = (size - 1) / 2;

        final float eulerPart = (float) (1f / (2f * Math.PI * (scale * scale)));

        for (int y = -radius; y < size - radius; y++) {
            for (int x = -radius; x < size - radius; x++) {
                float central = -(((y * y) + (x * x)) / (2f * (scale * scale)));

                kernel[y + radius][x + radius] = eulerPart * (float) Math.exp(central);
            }
        }

        // Utils.print(kernel);

        return Utils.flatten(kernel);
    }

    /**
     * Make value between 0 - 255
     *
     * @param value
     * @return
     */
    private int normalize(int value) {
        if (value >= 255) {
            return 255;
        } else if (value <= 0) {
            return 0;
        } else {
            return value;
        }
    }

    private BufferedImage getL1(BufferedImage src) {
        int size = (int) ((6 * scale) - 1);

        ConvolveOp l1Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale)));
        BufferedImage l1 = l1Op.filter(src, null);

        return l1;
    }

    private BufferedImage getL2(BufferedImage src) {
        int otherSize = (int) ((6 * scale * change) - 1);

        ConvolveOp l2Op = new ConvolveOp(new Kernel(otherSize, otherSize, centredGaussKernel(otherSize, scale*change)));
        BufferedImage l2 = l2Op.filter(src, null);

        return l2;
    }

    /**
     * Calculate the Difference of Gaussians (Concise Computer Vision - pg. 75)
     *
     * @param src
     * @return
     */
    private BufferedImage diffOfGauss(BufferedImage src) {
        BufferedImage l1 = getL1(src);
        BufferedImage l2 = getL2(src);

        BufferedImage dog = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                Color c1 = new Color(l1.getRGB(x, y));
                Color c2 = new Color(l2.getRGB(x, y));

                int bias = 0;

                int red = normalize(c1.getRed() - (int)(c2.getRed() * differenceSensitivity) + bias);
                int green = normalize(c1.getGreen() - (int)(c2.getGreen() * differenceSensitivity) + bias);
                int blue = normalize(c1.getBlue() - (int)(c2.getBlue() * differenceSensitivity) + bias);

                dog.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return dog;
    }

    private BufferedImage addToDog(BufferedImage src) {
        BufferedImage oth = getL1(src);
        BufferedImage dog = diffOfGauss(src);

        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < dog.getWidth(); x++) {
            for (int y = 0; y < dog.getHeight(); y++) {
                Color dogC = new Color(dog.getRGB(x, y));
                Color norm = new Color(oth.getRGB(x, y));

                int bias = 0;
                float t = 1f;

                int red = normalize(norm.getRed() + (int)(dogC.getRed() * t) + bias);
                int green = normalize(norm.getGreen() + (int)(dogC.getGreen() * t) + bias);
                int blue = normalize(norm.getBlue() + (int)(dogC.getBlue() * t) + bias);

                out.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return out;
    }

    private BufferedImage sharpen(BufferedImage src) {
        BufferedImage D = addToDog(src);
        BufferedImage G = getL1(src);

        BufferedImage S =  new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                Color cG = new Color(G.getRGB(x, y));
                Color cD = new Color(D.getRGB(x, y));

                int bias = 0;

                int red = normalize(cG.getRed() + (int)(cD.getRed() * sharpenAmount) + bias);
                int green = normalize(cG.getGreen() + (int)(cD.getGreen() * sharpenAmount) + bias);
                int blue = normalize(cG.getBlue() + (int)(cD.getBlue() * sharpenAmount) + bias);

                S.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        return S;
    }

    private BufferedImage threshold(BufferedImage src) {
        BufferedImage S =  sharpen(src);
        BufferedImage T =  new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                Color c = new Color(S.getRGB(x, y));

                if(c.getRed() >= threshold) {
                    T.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    float bias = 0;

                    int value = (int)Math.tanh(thresholdSensitivity * (c.getRed() - bias));

                    T.setRGB(x, y, new Color(value, value, value).getRGB());
                }
            }
        }

        return T;
    }

    @Override
    public void action(LineBoothState state) {
        state.setOutput(threshold(state.getOutput()));
    }
}
