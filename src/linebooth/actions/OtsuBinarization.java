package linebooth.actions;

import linebooth.IPipelineAction;
import linebooth.LineBoothState;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class OtsuBinarization implements IPipelineAction<LineBoothState> {
    /**
     * Get a grayscale histogram for an image.
     *
     * @param src
     * @return
     */
    private int[] histogram(BufferedImage src) {
        int[] h = new int[256]; // Grayscale histogram

        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                h[new Color(src.getRGB(x, y)).getRed()]++; // Red has a gray value same as all the other channels
            }
        }

        return h;
    }

    private int getOtsuThreshold(BufferedImage src) {
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

    private void binarize(BufferedImage src, BufferedImage dest) {
        int threshold = getOtsuThreshold(src);

        for(int x = 0; x < src.getWidth(); x++) {
            for(int y = 0; y < src.getHeight(); y++) {
                int gray = new Color(src.getRGB(x, y)).getRed();

                if(gray > threshold) {
                    dest.setRGB(x, y, new Color(255, 255, 255).getRGB());
                } else {
                    dest.setRGB(x, y, new Color(0,0,0).getRGB());
                }
            }
        }
    }

    @Override
    public LineBoothState action(LineBoothState state) {
        BufferedImage src = state.getForeground();

        binarize(src, state.getOutput());

        return state;
    }
}
