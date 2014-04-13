import linebooth.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * IMPORTANT RESOURCES
 *
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

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ImagePanel imagePanel = new ImagePanel();

        frame.add(imagePanel);
        frame.setSize(500, 500);
        imagePanel.setImage("./assets/test.jpeg");

        BufferedImage img = imagePanel.getImage();

        // Edge Detect Kernel
        float eKernel[] = {
                0, 1, -1,
                0, 0, 0,
                -1, 0, 1
        };


        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, eKernel), ConvolveOp.EDGE_NO_OP, null);

        BufferedImage src = imageToBufferedImage(imagePanel.getImage());
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        imagePanel.setImage(op.filter(src, dest));


        frame.setVisible(true);
    }
}
