package linebooth.nxt;

public class PrintJob {
    public static final int FOREGROUND_IMAGE = 2;
    public static final int BACKGROUND_IMAGE = 3;

    private final int imageType;
    private final byte[] image;

    public PrintJob(int imageType, byte[] image) {
        this.imageType = imageType;
        this.image = image;
    }

    public int getImageType() {
        return imageType;
    }

    public byte[] getImage() {
        return image;
    }
}
