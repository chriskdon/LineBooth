import linebooth.ImageComponent;
import linebooth.LineBoothState;
import linebooth.PipelineTransformer;
import linebooth.actions.GrayScaleForegroundBackground;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;

/**
 * IMPORTANT RESOURCES
 * <p/>
 * http://en.wikipedia.org/wiki/Kernel_(image_processing)
 * http://www.jhlabs.com/ip/blurring.html
 * https://developer.apple.com/library/mac/documentation/performance/Conceptual/vImage/Art/kernel_convolution.jpg
 */

public class Main {

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bufImg = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics graph = bufImg.getGraphics();
        graph.drawImage(im, 0, 0, null);
        graph.dispose();

        return bufImg;
    }

    /**
     * Get an image from a file.
     *
     * @param path
     * @return
     */
    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception ex) {
            throw new RuntimeException("Could not load: " + path);
        }
    }

    /**
     * Calculate the Gauss kernel based on a scale.
     * <p/>
     * L(x, y, scale)
     *
     * @param length
     * @param scale
     * @return
     */
    public static float[][] CenteredGaussKernel(int length, double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale must be > 0");
        }
        if (length < 1) {
            throw new IllegalArgumentException("Length must be >= 1");
        }

        float[][] kernel = new float[length][length];
        double sumTotal = 0;

        int kernelRadius = length / 2;
        double distance = 0;

        float calculatedEuler = 1.0f / (float) (2.0 * Math.PI * Math.pow(scale, 2));

        for (int filterY = -kernelRadius; filterY <= kernelRadius; filterY++) {
            for (int filterX = -kernelRadius; filterX <= kernelRadius; filterX++) {
                distance = -((filterX * filterX) + (filterY * filterY)) / (2 * (scale * scale));

                kernel[filterY + kernelRadius][filterX + kernelRadius] = (float) (calculatedEuler * Math.exp(distance));

                sumTotal += kernel[filterY + kernelRadius][filterX + kernelRadius];
            }
        }

     /*   for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                kernel[y][x] = (float) (kernel[y][x] * (1.0 / sumTotal));
            }
        }*/

        return kernel;
    }

    /**
     * Difference of Gauss
     * <p/>
     * D[scale, scalingFactor](x, y) = L(x, y, scale) - L(x, y, scale * scalingFactor)
     *
     * @param matrixSize
     * @param scale
     * @param scalingFactor
     * @return
     */
    public static BufferedImage DifferenceOfGauss(BufferedImage src, int matrixSize, float scale, float scalingFactor) {
        if (scalingFactor <= 1) {
            throw new IllegalArgumentException("Scaling factor must be > 1");
        }

        ConvolveOp k1Conv = new ConvolveOp(new Kernel(matrixSize, matrixSize, flatten(CenteredGaussKernel(matrixSize, scale))));
        ConvolveOp k2Conv = new ConvolveOp(new Kernel(matrixSize, matrixSize, flatten(CenteredGaussKernel(matrixSize, scalingFactor * scale))));

        // L(x, y, scale)
        BufferedImage i1 = k1Conv.filter(src, null);
        BufferedImage i2 = k2Conv.filter(src, null);

        return i1;
    }

    public static float[] flatten(float[][] array) {
        float[] flat = new float[array.length * array.length];

        int x = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                flat[x++] = array[i][j];
            }
        }

        return flat;
    }

    public static BufferedImage convertToGrayscale(BufferedImage src) {
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < src.getWidth(); x++) {
            for(int y = 0; y < src.getHeight(); y++) {
                Color c = new Color(src.getRGB(x, y));

                int g = (c.getRed() + c.getBlue() + c.getGreen())/3;

                img.setRGB(x, y, new Color(g, g, g).getRGB());
            }
        }

        return img;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ImageComponent imageComponent = new ImageComponent();

        BufferedImage background = convertToGrayscale(getImage("./assets/background.jpg"));
        BufferedImage foreground = convertToGrayscale(getImage("./assets/foreground.jpg"));

        frame.add(imageComponent);
        frame.setSize(background.getWidth(), background.getHeight());


        BufferedImage processed = new PipelineTransformer<LineBoothState>(new LineBoothState(background, foreground))
                                        .action(new GrayScaleForegroundBackground()) // Change foreground and background to gray
                                        .result().getBackground();

        imageComponent.setImage(processed);

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setTitle("Line Booth");
    }
}
