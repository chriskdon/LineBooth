package linebooth.actions;

import linebooth.GrayscaleBufferedImage;
import linebooth.GrayscaleFilter;

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
public class WinnemollerBinarization extends GrayscaleFilter {
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
            l1 = new GrayscaleBufferedImage(l1Op.filter(src, null), false);
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
        int[] l1Data = getL1().getPixelData();
        int[] l2Data = getL2().getPixelData();

        GrayscaleBufferedImage dog = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        int[] dogData = dog.getPixelData();

        for (int i = 0; i < dogData.length; i++) {
            int c1 = GrayscaleBufferedImage.getGrayPixel(l1Data[i]);
            int c2 = GrayscaleBufferedImage.getGrayPixel(l2Data[i]);

            int bias = 0;

            GrayscaleBufferedImage.setGrayPixel(dogData, i, (c1 - (int)(c2 * differenceSensitivity) + bias));
        }

        return dog;
    }

    private GrayscaleBufferedImage addToDog() {
        int[] othData = getL1().getPixelData();
        int[] dogData = diffOfGauss().getPixelData();

        GrayscaleBufferedImage out = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        int[] oData = out.getPixelData();

        for (int i = 0; i < oData.length; i++) {
            int norm = GrayscaleBufferedImage.getGrayPixel(othData[i]);
            int dogC = GrayscaleBufferedImage.getGrayPixel(dogData[i]);

            int bias = 0;
            float t = 1f;

            GrayscaleBufferedImage.setGrayPixel(oData, i, (norm + (int) (dogC * t) + bias));
        }

        return out;
    }

    private GrayscaleBufferedImage sharpen(GrayscaleBufferedImage src) {
        int[] dData = addToDog().getPixelData();
        int[] gData = getL1().getPixelData();

        GrayscaleBufferedImage S = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        int[] sData = S.getPixelData();

        for (int i = 0; i < src.getWidth() * src.getHeight(); i++) {
            int cG = GrayscaleBufferedImage.getGrayPixel(gData[i]);
            int cD = GrayscaleBufferedImage.getGrayPixel(dData[i]);

            int bias = 0;

            GrayscaleBufferedImage.setGrayPixel(sData, i, (cG + (int) (cD * sharpenAmount) + bias));
        }

        return S;
    }

    private GrayscaleBufferedImage threshold(GrayscaleBufferedImage src) {
        int[] sData = sharpen(src).getPixelData();

        GrayscaleBufferedImage T = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        int[] tData = T.getPixelData();

        for(int i = 0; i < tData.length; i++) {
            int c = GrayscaleBufferedImage.getGrayPixel(sData[i]);

            if (c >= threshold) {
                GrayscaleBufferedImage.setGrayPixel(tData, i, 255);
            } else {
                float bias = 0;

                int value = (int) Math.tanh(thresholdSensitivity * (c - bias));

                GrayscaleBufferedImage.setGrayPixel(tData, i, value);
            }
        }

        return T;
    }

    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img) {
        reset();
        src = img;

        return threshold(img);
    }
}
