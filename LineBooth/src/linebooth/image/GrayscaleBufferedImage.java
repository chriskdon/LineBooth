package linebooth.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-03.
 */
public class GrayscaleBufferedImage extends BufferedImage {
    private int[] pixelData;

    public GrayscaleBufferedImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);

        pixelData = ((DataBufferInt) getRaster().getDataBuffer()).getData();
    }

    public GrayscaleBufferedImage(BufferedImage img) {
        this(img.getWidth(), img.getHeight());

        int[] iData;

        if (img.getType() == BufferedImage.TYPE_INT_ARGB || img.getType() == BufferedImage.TYPE_INT_RGB) {
            iData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        } else {
            BufferedImage o = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    o.setRGB(x, y, img.getRGB(x, y));
                }
            }

            iData = ((DataBufferInt) o.getRaster().getDataBuffer()).getData();
        }

        int[] oData = getPixelData();

        if (img instanceof GrayscaleBufferedImage) { // Already grayscale
            for (int i = 0; i < getWidth() * getHeight(); i++) {
                oData[i] = iData[i];
            }
        } else {
            for (int i = 0; i < getWidth() * getHeight(); i++) {
                oData[i] = grayscale(iData[i]);
            }
        }
    }

    @Override
    public synchronized void setRGB(int x, int y, int rgb) {
        super.setRGB(x, y, grayscale(rgb));
    }

    public int[] getPixelData() {
        return pixelData;
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

    public synchronized int getGrayPixel(int x, int y) {
        return this.getRGB(x, y) & 0xFF;
    }

    public synchronized int getGrayPixel(int i) {
        return getPixelData()[i] & 0xFF;
    }

    private int normalize(int value) {
        if (value > 255) {
            value = 255;
        }
        if (value < 0) {
            value = 0;
        }

        return value;
    }

    public synchronized void setGrayPixel(int i, int value) {
        value = normalize(value);

        getPixelData()[i] = new Color(value, value, value).getRGB();
    }

    public synchronized void setGrayPixel(int x, int y, int value) {
        value = normalize(value);

        super.setRGB(x, y, new Color(value, value, value).getRGB());
    }

    public synchronized void plusGrayPixel(int x, int y, int value) {
        setGrayPixel(x, y, getGrayPixel(x, y) + value);
    }
}
