import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by kubasub on 2014-04-15.
 */
public class ForegroundExtractor {
    private BufferedImage bg;
    private BufferedImage fg;
    private BufferedImage output;

    private HashMap<String, PixelState> opaquePixels = new HashMap<String, PixelState>(100000); //only used for removeCluster method
    private HashMap<String, PixelState> transparentPixels = new HashMap<String, PixelState>(100000); //only used for removeCluster method

    public ForegroundExtractor() throws IOException {
        bg = ImageIO.read(new File("library_bg.jpg"));
        fg = ImageIO.read(new File("library_fg.jpg"));
        output = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    public void extractForegroundByHSV() throws IOException {

        //compare the background and foreground's pixels
        for (int width = 20; width < output.getWidth() - 20; width++) {
            for (int height = 20; height < output.getHeight() - 20; height++) {

                if (radiusMatchByHSBThresholds(width, height, 0.17f, 0.1f, 0.2f, 1)) {
                    output.setRGB(width, height, new Color(0, 0, 0, 0).getRGB()); //set transparent on match
                } else {
                    output.setRGB(width, height, fg.getRGB(width, height));
                }
            }
        }
    }

    public void removeOpaqueClusters(int clusterSizeThreshold) {
        for (int width = 20; width < output.getWidth() - 20; width++) {
            for (int height = 20; height < output.getHeight() - 20; height++) {

                removeCluster(width, height, clusterSizeThreshold);
            }
            System.out.println(width);
        }
        opaquePixels.clear();
    }

    private void addTransparentClusters(int clusterSizeThreshold) {
        System.out.println("STARTED ADDING CLUSTERS");
        for (int width = 20; width < output.getWidth() - 20; width++) {
            for (int height = 20; height < output.getHeight() - 20; height++) {

                addCluster(width, height, clusterSizeThreshold);
            }
            System.out.println(width);
        }
        transparentPixels.clear();
    }

    //group the transparent and non-transparent pixels in the output image
//        final BufferedImage outputCopy = output.getSubimage(0, 0, output.getWidth(), output.getHeight());
//        for(int width=20; width<output.getWidth()-20; width++) {
//            for(int height=20; height<output.getHeight()-20; height++) {
//
//                if(radiusTransparentBySegmentation(output, width, height, 0)) {
//                    output.setRGB(width, height, new Color(0,0,0,0).getRGB()); //set transparent on match
//                } else {
//                    output.setRGB(width, height, fg.getRGB(width, height));
//                }
//            }
//        }

    /**
     * Tries to match a foreground image pixel to the background.
     * <p/>
     * the larger color threshold --> more cut out
     * the larger the radius --> the more cut out
     */
    private boolean radiusMatchByRGBThreshold(int centerX, int centerY, int colorThresholdFactor, int pixelRadius) {
        //int gridLength = 2*pixelRadius + 1; //the number of pixels to loops through in a row/column

        //colour values for the foreground pixel
        int fRGB = fg.getRGB(centerX, centerY);
        int fR = (fRGB >> 16) & 0x0ff;
        int fG = (fRGB >> 8) & 0x0ff;
        int fB = (fRGB) & 0x0ff;

        for (int x = -pixelRadius; x <= pixelRadius; x++) {
            for (int y = -pixelRadius; y <= pixelRadius; y++) {
                int bRGB = bg.getRGB(centerX + pixelRadius, centerY + pixelRadius);
                int bR = (bRGB >> 16) & 0x0ff;
                int bG = (bRGB >> 8) & 0x0ff;
                int bB = (bRGB) & 0x0ff;

                if ((Math.abs(bR - fR) + Math.abs(bG - fG) + Math.abs(bB - fB)) / 3 < colorThresholdFactor) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean radiusMatchByHSBThresholds(int centerX, int centerY, float hueThreshold, float saturationThreshold, float brightnessThreshold, int pixelRadius) {
        //colour values for the foreground pixel
        int fRGB = fg.getRGB(centerX, centerY);
        int fR = (fRGB >> 16) & 0x0ff;
        int fG = (fRGB >> 8) & 0x0ff;
        int fB = (fRGB) & 0x0ff;
        float[] fHSB = Color.RGBtoHSB(fR, fG, fB, null);
        float fHue = fHSB[0];
        float fSat = fHSB[1];
        float fBri = fHSB[2];

        for (int x = -pixelRadius; x <= pixelRadius; x++) {
            for (int y = -pixelRadius; y <= pixelRadius; y++) {
                int bRGB = bg.getRGB(centerX + pixelRadius, centerY + pixelRadius);
                int bR = (bRGB >> 16) & 0x0ff;
                int bG = (bRGB >> 8) & 0x0ff;
                int bB = (bRGB) & 0x0ff;
                float[] bHSB = Color.RGBtoHSB(bR, bG, bB, null);
                float bHue = bHSB[0];
                float bSat = bHSB[1];
                float bBri = bHSB[2];

                if ((Math.abs(fHue - bHue) <= hueThreshold) &&
                        Math.abs(fSat - bSat) <= saturationThreshold &&
                        Math.abs(fBri - bBri) <= brightnessThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks a grid of pixels surrounding a pixel to see if it should be removed.
     * <p/>
     * It makes the pixel transparent if surrounding pixels are transparent and it adds a pixel back from the
     * foreground image if the surrounding pixels are not transparent.
     * <p/>
     * It creates a static copy of the current output image to compare against.
     */
    private boolean radiusTransparentBySegmentation(BufferedImage outputCopy, int centerX, int centerY, int pixelRadius) {
        //colour values for the foreground pixel
//        int fRGB = fg.getRGB(centerX, centerY);
//        int fA = (fRGB>>24) & 0xff;

        int numberOfTransparentPixels = 0;
        int numberOfPixels = (int) Math.pow((2 * pixelRadius + 1), 2);

        for (int x = -pixelRadius; x <= pixelRadius; x++) {
            for (int y = -pixelRadius; y <= pixelRadius; y++) {
                if (x != centerX && y != centerY) {
                    int oRGB = outputCopy.getRGB(centerX + pixelRadius, centerY + pixelRadius);
                    int oA = (oRGB >> 24) & 0xff;

                    if (oA == 0) { //if the pixel is transparent
                        numberOfTransparentPixels++;
                    }
                }
            }
        }

        //return whether or not more than half of the pixels are transparent
        return ((double) numberOfTransparentPixels / numberOfPixels) >= 0.5;
    }

    private void removeCluster(int centerX, int centerY, int clusterSizeThreshold) {
        HashMap<String, PixelState> clusterNodesHashMap = new HashMap<String, PixelState>(clusterSizeThreshold);
        Stack<PixelState> nodeList = new Stack<PixelState>();
        int clusterSize = 0;
        boolean opaqueCenter = ((output.getRGB(centerX, centerY) >> 24) & 0xff) != 0;
        PixelState centerPixel = new PixelState(centerX, centerY, opaqueCenter);


        if (centerPixel.isOpaque() && !hasOpaquePixelBeenConsideredBefore(centerPixel)) {
            nodeList.push(centerPixel);
            clusterNodesHashMap.put(centerPixel.getPixelPositionString(), centerPixel);
            opaquePixels.put(centerPixel.getPixelPositionString(), centerPixel);
            clusterSize++;
        }

        while (!nodeList.isEmpty()) {

            PixelState currentNode = nodeList.peek();

            if (currentNode.hasAvailableDirections()) {
                int curX = currentNode.getX();
                int curY = currentNode.getY();

                PixelState newPixel = null;
                switch (currentNode.getCurrentDirection()) {
                    case 0: //go north
                        if (curY - 1 >= 0) {
                            newPixel = new PixelState(curX, curY - 1, ((output.getRGB(curX, curY - 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 1: //go east
                        if (curX + 1 < output.getWidth()) {
                            newPixel = new PixelState(curX + 1, curY, ((output.getRGB(curX + 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 2: //go south
                        if (curY + 1 < output.getHeight()) {
                            newPixel = new PixelState(curX, curY + 1, ((output.getRGB(curX, curY + 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 3: //go west
                        if (curX - 1 >= 0) {
                            newPixel = new PixelState(curX - 1, curY, ((output.getRGB(curX - 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                }
                //Determine whether to add the new pixel
                if (newPixel != null && newPixel.isOpaque()) {
                    //Add pixel if it hasn't already been considered
                    String newPixelString = newPixel.getPixelPositionString();
                    if (!clusterNodesHashMap.containsKey(newPixelString)) {
                        nodeList.push(newPixel);
                        clusterNodesHashMap.put(newPixelString, newPixel);
                        opaquePixels.put(newPixelString, newPixel);
                        clusterSize++;
                    }
                }

                //if(clusterSize > clusterSizeThreshold) break; //break if we have a sufficiently large cluster

                currentNode.incrementDirection();
            } else {
                nodeList.pop();
            }
        }

        //make all pixels transparent if the cluster is not large enough
        if (clusterSize < clusterSizeThreshold) {
            for (PixelState pixel : clusterNodesHashMap.values()) {
                output.setRGB(pixel.getX(), pixel.getY(), new Color(0, 0, 0, 0).getRGB());
                opaquePixels.remove(pixel);
            }
        }
    }

    private void addCluster(int startX, int startY, int clusterSizeThreshold) {
        HashMap<String, PixelState> clusterNodesHashMap = new HashMap<String, PixelState>(clusterSizeThreshold);
        Stack<PixelState> nodeList = new Stack<PixelState>();
        int clusterSize = 0;
        boolean opaqueStart = ((output.getRGB(startX, startY) >> 24) & 0xff) != 0;
        PixelState startPixel = new PixelState(startX, startY, opaqueStart);


        if (!startPixel.isOpaque() && !hasTransparentPixelBeenConsideredBefore(startPixel)) {
            nodeList.push(startPixel);
            clusterNodesHashMap.put(startPixel.getPixelPositionString(), startPixel);
            transparentPixels.put(startPixel.getPixelPositionString(), startPixel);
            clusterSize++;
        }

        while (!nodeList.isEmpty()) {

            PixelState currentNode = nodeList.peek();

            if (currentNode.hasAvailableDirections()) {
                int curX = currentNode.getX();
                int curY = currentNode.getY();

                PixelState newPixel = null;
                switch (currentNode.getCurrentDirection()) {
                    case 0: //go north
                        if (curY - 1 >= 0) {
                            newPixel = new PixelState(curX, curY - 1, ((output.getRGB(curX, curY - 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 1: //go east
                        if (curX + 1 < output.getWidth()) {
                            newPixel = new PixelState(curX + 1, curY, ((output.getRGB(curX + 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 2: //go south
                        if (curY + 1 < output.getHeight()) {
                            newPixel = new PixelState(curX, curY + 1, ((output.getRGB(curX, curY + 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 3: //go west
                        if (curX - 1 >= 0) {
                            newPixel = new PixelState(curX - 1, curY, ((output.getRGB(curX - 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                }
                //Determine whether to add the new pixel
                if (newPixel != null && !newPixel.isOpaque()) {
                    //Add pixel if it hasn't already been considered
                    String newPixelString = newPixel.getPixelPositionString();
                    if (!clusterNodesHashMap.containsKey(newPixelString)) {
                        nodeList.push(newPixel);
                        clusterNodesHashMap.put(newPixelString, newPixel);
                        transparentPixels.put(newPixelString, newPixel);
                        clusterSize++;
                    }
                }

                //if(clusterSize > clusterSizeThreshold) break; //break if we have a sufficiently large cluster

                currentNode.incrementDirection();
            } else {
                nodeList.pop();
            }
        }

        //make all pixels transparent if the cluster is not large enough
        if (clusterSize < clusterSizeThreshold) {
            for (PixelState pixel : clusterNodesHashMap.values()) {
                int x = pixel.getX();
                int y = pixel.getY();
                output.setRGB(x, y, fg.getRGB(x,y));
                transparentPixels.remove(pixel);
            }
        }
    }


    private boolean hasOpaquePixelBeenConsideredBefore(PixelState pixel) {
        String pixelPositionString = pixel.getPixelPositionString();

        return opaquePixels.containsKey(pixelPositionString);
    }

    private boolean hasTransparentPixelBeenConsideredBefore(PixelState pixel) {
        String pixelPositionString = pixel.getPixelPositionString();

        return transparentPixels.containsKey(pixelPositionString);
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    ////////EDGE DETECT
    public void edgeDetect() throws IOException {
        // Edge Detect Kernel
        float eKernelY[] = {
                -1, 0, 1,
                -2, 0, 2,
                -1, 0, 1
        };

        float eKernelX[] = {
                1, 2, 1,
                0, 0, 0,
                -1, -2, -1
        };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, eKernelX), ConvolveOp.EDGE_NO_OP, null);

        BufferedImage src = imageToBufferedImage(fg);
        output = new BufferedImage(fg.getWidth(), fg.getHeight(), BufferedImage.TYPE_INT_RGB);
        op.filter(src, output);

        outputImage();
    }

    public BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bufImg = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics graph = bufImg.getGraphics();
        graph.drawImage(im, 0, 0, null);
        graph.dispose();

        return bufImg;
    }

    /////OUTPUT IMAGE
    private void outputImage() throws IOException {
        File outputFile = new File("library_output.png");
        ImageIO.write(output, "png", outputFile);
    }

    public static void main(String[] args) throws IOException {
        ForegroundExtractor kk = new ForegroundExtractor();
        kk.extractForegroundByHSV();
        kk.removeOpaqueClusters(10000);
        kk.addTransparentClusters(10000);
        kk.outputImage();
    }

}
