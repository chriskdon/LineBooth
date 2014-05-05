package linebooth.image.converters;

import linebooth.image.GrayscaleBufferedImage;

import java.awt.image.BufferedImage;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-05-04.
 */
public class GrayImagePacker implements BitPacker {
    public BitPackedImage convert(GrayscaleBufferedImage image) {
        byte[] output = new byte[(int)Math.ceil((image.getHeight()*image.getWidth())/8.0)];

        for(int i = 0, byteNum = 0, bit = 7; i < image.getPixelData().length; i++) {
            output[byteNum] |= (byte) ((image.getGrayPixel(i) > 128 ? 0 : 1) << bit);

            bit--;
            if(bit < 0) { bit = 7; byteNum++; } // New Byte
        }

        return new BitPackedImage(image.getHeight(), image.getWidth(), output);
    }

    @Override
    public BitPackedImage convert(BufferedImage image) {
        return convert(new GrayscaleBufferedImage(image));
    }
}
