package linebooth.image.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Merge images.
 * <p/>
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class MergeImagesOperation implements BinaryOperation {
    /**
     * Merge two images. Whereever there are transparent pixels in the foreground show the background.
     *
     * @param foreground
     * @param background
     * @return The merged images.
     */
    @Override
    public BufferedImage apply(BufferedImage foreground, BufferedImage background) {

        if (foreground.getWidth() != background.getWidth() || foreground.getHeight() != background.getHeight()) {
            throw new IllegalArgumentException("Foreground and Background must have the same dimensions.");
        }

        int[] fData = ((DataBufferInt) foreground.getRaster().getDataBuffer()).getData();
        int[] bData = ((DataBufferInt) background.getRaster().getDataBuffer()).getData();

        BufferedImage out = new BufferedImage(foreground.getWidth(), foreground.getHeight(), foreground.getType());
        int[] outData = ((DataBufferInt) out.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < outData.length; i++) {
            if (((fData[i] >> 24) & 0xFF) == 0) {   // Is the foreground pixel 100% transparent
                outData[i] = bData[i];              // Take background
            } else {
                outData[i] = fData[i];              // Take foreground
            }
        }

        return out;
    }
}
