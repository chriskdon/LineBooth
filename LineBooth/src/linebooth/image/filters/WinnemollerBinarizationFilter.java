package linebooth.image.filters;

import linebooth.image.GrayscaleBufferedImage;

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
public class WinnemollerBinarizationFilter extends GrayscaleFilter {
    private float scale = 1;
    private float change = 1f;
    private float differenceSensitivity = 1.25f;
    private float sharpenAmount = 0.7f;
    private float thresholdSensitivity = 1.5f;
    private int threshold = 180;
    private BufferedImage src;
    private GrayscaleBufferedImage l1, l2;

    /**
     * Constructor
     *
     * @param scale Scale factor for the kernel.
     */
    public WinnemollerBinarizationFilter(float scale, float change,
                                         float differenceSensitivity, float sharpenAmount,
                                         float thresholdSensitivity, int threshold) {
        this.scale = scale;
        this.change = change;
        this.differenceSensitivity = differenceSensitivity;
        this.sharpenAmount = sharpenAmount;
        this.thresholdSensitivity = thresholdSensitivity;
        this.threshold = threshold;
    }

    public void setScale(float x) {
        scale = x;
    }

    public void setChange(float x) {
        change = x;
    }

    private void reset() {
        src = l1 = l2 = null;
    }

    /**
     * Get the centered gauss kernel
     *
     * @return 1D array of the kernel
     */
    private float[] centredGaussKernel(int size, float scale) {
        float[] kernel = new float[size * size];

        int radius = (size - 1) / 2;

        final float eulerPart = (float) (1f / (2f * Math.PI * (scale * scale)));

        int i = 0;
        for (int y = -radius; y < size - radius; y++) {
            for (int x = -radius; x < size - radius; x++) {
                float central = -(((y * y) + (x * x)) / (2f * (scale * scale)));

                kernel[i++] = eulerPart * (float) Math.exp(central);
            }
        }

        return kernel;
    }

    private GrayscaleBufferedImage getL1() {
        if (l1 == null) {
            int size = (int) ((6 * scale) - 1);

            ConvolveOp l1Op = new ConvolveOp(new Kernel(size, size, centredGaussKernel(size, scale)));
            l1 = new GrayscaleBufferedImage(l1Op.filter(src, null));
        }

        return l1;
    }

    private GrayscaleBufferedImage getL2() {
        if (l2 == null) {
            int otherSize = (int) ((6 * scale * change) - 1);

            ConvolveOp l2Op = new ConvolveOp(new Kernel(otherSize, otherSize, centredGaussKernel(otherSize, scale * change)));
            l2 = new GrayscaleBufferedImage(l2Op.filter(src, null));
        }

        return l2;
    }

    /**
     * Calculate the Difference of Gaussians (Concise Computer Vision - pg. 75)
     *
     * @return
     */
    private GrayscaleBufferedImage diffOfGauss() {
        GrayscaleBufferedImage l1 = getL1();
        GrayscaleBufferedImage l2 = getL2();

        GrayscaleBufferedImage dog = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());


        for (int i = 0; i < dog.getPixelData().length; i++) {
            int c1 = l1.getGrayPixel(i);
            int c2 = l2.getGrayPixel(i);

            int bias = 0;

            dog.setGrayPixel(i, (c1 - (int) (c2 * differenceSensitivity) + bias));
        }

        return dog;
    }

    private GrayscaleBufferedImage addToDog() {
        GrayscaleBufferedImage oth = getL1();
        GrayscaleBufferedImage dog = diffOfGauss();

        GrayscaleBufferedImage out = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());

        for (int i = 0; i < out.getPixelData().length; i++) {
            int norm = oth.getGrayPixel(i);
            int dogC = dog.getGrayPixel(i);

            int bias = 0;
            float t = 1f;

            out.setGrayPixel(i, (norm + (int) (dogC * t) + bias));
        }

        return out;
    }

    private GrayscaleBufferedImage sharpen(GrayscaleBufferedImage src) {
        GrayscaleBufferedImage d = addToDog();
        GrayscaleBufferedImage g = getL1();

        GrayscaleBufferedImage s = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());

        for (int i = 0; i < s.getPixelData().length; i++) {
            int cG = g.getGrayPixel(i);
            int cD = d.getGrayPixel(i);

            int bias = 0;

            s.setGrayPixel(i, (cG + (int) (cD * sharpenAmount) + bias));
        }

        return s;
    }

    private GrayscaleBufferedImage threshold(GrayscaleBufferedImage src, GrayscaleBufferedImage dest) {
        GrayscaleBufferedImage s = sharpen(src);


        GrayscaleBufferedImage t;
        if (dest == null) {
            t = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        } else {
            t = dest;
        }

        for (int i = 0; i < t.getPixelData().length; i++) {
            int c = s.getGrayPixel(i);

            if (c >= threshold) {
                t.setGrayPixel(i, 255);
            } else {
                float bias = 0;

                t.setGrayPixel(i, (int) Math.tanh(thresholdSensitivity * (c - bias)));
            }
        }

        return t;
    }

    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img, GrayscaleBufferedImage dest) {
        reset();
        src = img;

        return threshold(img, dest);
    }
}
