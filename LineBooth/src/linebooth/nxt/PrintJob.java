package linebooth.nxt;

import linebooth.image.converters.BitPackedImage;

public class PrintJob {
    public static final int FOREGROUND_IMAGE = 2;
    public static final int BACKGROUND_IMAGE = 3;

    private final int imageType;
    private final BitPackedImage image;

    public PrintJob(int imageType, BitPackedImage image) {
        this.imageType = imageType;
        this.image = image;
    }

    public int getImageType() {
        return imageType;
    }

    public BitPackedImage getImage() {
        return image;
    }
}
