package linebooth.image.filters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class OtsuBinarizationFilter extends GrayscaleFilter {
    /**
     * Get a grayscale histogram for an image.
     *
     * @param src
     * @return
     */
    private int[] histogram(GrayscaleBufferedImage src) {
        int[] h = new int[256]; // Grayscale histogram

        for (int i = 0; i < src.getPixelData().length; i++) {
            h[src.getGrayPixel(i)]++;
        }

        return h;
    }

    private int getOtsuThreshold(GrayscaleBufferedImage src) {
        int[] histo = histogram(src);

        int total = src.getHeight() * src.getWidth();

        float sum = 0;
        for (int i = 0; i < 256; i++) sum += i * histo[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histo[i];
            if (wB == 0) continue;
            wF = total - wB;

            if (wF == 0) break;

            sumB += (float) (i * histo[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }

    private GrayscaleBufferedImage binarize(GrayscaleBufferedImage src, GrayscaleBufferedImage dest) {
        if (dest == null) {
            dest = new GrayscaleBufferedImage(src.getWidth(), src.getHeight());
        }

        int threshold = getOtsuThreshold(src);

        for (int i = 0; i < dest.getPixelData().length; i++) {
            if (src.getGrayPixel(i) < threshold) {
                dest.setGrayPixel(i, 0);
            } else {
                dest.setGrayPixel(i, 255);
            }
        }

        return dest;
    }

    @Override
    public GrayscaleBufferedImage apply(GrayscaleBufferedImage img, GrayscaleBufferedImage dest) {
        return binarize(img, dest);
    }
}
