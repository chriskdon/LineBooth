import linebooth.ImageComponent;
import linebooth.LineBoothState;
import linebooth.PipelineTransformer;
import linebooth.actions.GrayScaleForegroundBackground;
import linebooth.actions.OtsuBinarization;
import linebooth.actions.WinnemollerBinarization;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ImageComponent imageComponent = new ImageComponent();

        BufferedImage background = imageToBufferedImage(getImage("./assets/foreground.jpg"));
        BufferedImage foreground = imageToBufferedImage(getImage("./assets/foreground.jpg"));

        frame.add(imageComponent);
        frame.setSize(background.getWidth(), background.getHeight());

        BufferedImage processed = new PipelineTransformer<LineBoothState>(new LineBoothState(background, foreground))
                .action(new GrayScaleForegroundBackground())             // Change foreground and background to gray
                .action(new WinnemollerBinarization(2f, 1.6f))
                //.action(new OtsuBinarization())
                .result().getOutput();

        imageComponent.setImage(processed);

        frame.setVisible(true);
        frame.setResizable(false);
        frame.setTitle("Line Booth");
    }
}
