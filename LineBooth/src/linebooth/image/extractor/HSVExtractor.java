package linebooth.image.extractor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Stack;

public class HSVExtractor implements Extractor {

    @Override
    public BufferedImage extract(BufferedImage foreground, BufferedImage background, BufferedImage dest) {
        BufferedImage output = dest;

        if (output == null) {
            output = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        int clusterThreshold = output.getHeight() * output.getWidth() / 3;  //Threshold is 1/3 of the pixels in the image

        extractByHSV(foreground, background, output);

        removeOpaqueClusters(output, clusterThreshold);
        addTransparentClusters(output, foreground, clusterThreshold);

        return output;
    }

    public void extractByHSV(BufferedImage foreground, BufferedImage background, BufferedImage output) {
        float hueThreshold = 0.17f;
        float saturationThreshold = 0.1f;
        float brightnessThreshold = 0.2f;
        int pixelRadius = 1;

        for (int width = pixelRadius; width < output.getWidth() - pixelRadius; width++) {
            for (int height = pixelRadius; height < output.getHeight() - pixelRadius; height++) {

                if (radiusMatchByHSBThresholds(foreground, background, width, height, hueThreshold, saturationThreshold, brightnessThreshold, pixelRadius)) {
                    output.setRGB(width, height, new Color(0, 0, 0, 0).getRGB()); //set transparent on match
                } else {
                    output.setRGB(width, height, foreground.getRGB(width, height));
                }
            }
        }
    }

    private boolean radiusMatchByHSBThresholds(BufferedImage foreground, BufferedImage background, int centerX, int centerY, float hueThreshold, float saturationThreshold, float brightnessThreshold, int pixelRadius) {
        //colour values for the foreground pixel
        int fRGB = foreground.getRGB(centerX, centerY);
        int fR = (fRGB >> 16) & 0x0ff;
        int fG = (fRGB >> 8) & 0x0ff;
        int fB = (fRGB) & 0x0ff;
        float[] fHSB = Color.RGBtoHSB(fR, fG, fB, null);
        float fHue = fHSB[0];
        float fSat = fHSB[1];
        float fBri = fHSB[2];

        for (int x = -pixelRadius; x <= pixelRadius; x++) {
            for (int y = -pixelRadius; y <= pixelRadius; y++) {
                int bRGB = background.getRGB(centerX + pixelRadius, centerY + pixelRadius);
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

    public void removeOpaqueClusters(BufferedImage output, int clusterSizeThreshold) {
        HashMap<String, PixelState> opaquePixels = new HashMap<String, PixelState>(10000); //only used for removeCluster method

        for (int width = 20; width < output.getWidth() - 20; width++) {
            for (int height = 20; height < output.getHeight() - 20; height++) {

                removeCluster(output, width, height, clusterSizeThreshold, opaquePixels);
            }
        }
        opaquePixels.clear();
    }

    private void addTransparentClusters(BufferedImage output, BufferedImage foreground, int clusterSizeThreshold) {
        HashMap<String, PixelState> transparentPixels = new HashMap<String, PixelState>(10000); //only used for removeCluster method

        for (int width = 20; width < output.getWidth() - 20; width++) {
            for (int height = 20; height < output.getHeight() - 20; height++) {

                addCluster(output, foreground, width, height, clusterSizeThreshold, transparentPixels);
            }
        }
        transparentPixels.clear();
    }

    private void removeCluster(BufferedImage image, int centerX, int centerY, int clusterSizeThreshold, HashMap<String, PixelState> opaquePixels) {
        HashMap<String, PixelState> clusterNodesHashMap = new HashMap<String, PixelState>(clusterSizeThreshold);
        Stack<PixelState> nodeList = new Stack<PixelState>();
        int clusterSize = 0;
        boolean opaqueCenter = ((image.getRGB(centerX, centerY) >> 24) & 0xff) != 0;
        PixelState centerPixel = new PixelState(centerX, centerY, opaqueCenter);


        if (centerPixel.isOpaque() && !hasOpaquePixelBeenConsideredBefore(opaquePixels, centerPixel)) {
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
                            newPixel = new PixelState(curX, curY - 1, ((image.getRGB(curX, curY - 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 1: //go east
                        if (curX + 1 < image.getWidth()) {
                            newPixel = new PixelState(curX + 1, curY, ((image.getRGB(curX + 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 2: //go south
                        if (curY + 1 < image.getHeight()) {
                            newPixel = new PixelState(curX, curY + 1, ((image.getRGB(curX, curY + 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 3: //go west
                        if (curX - 1 >= 0) {
                            newPixel = new PixelState(curX - 1, curY, ((image.getRGB(curX - 1, curY) >> 24) & 0xff) != 0);
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
                image.setRGB(pixel.getX(), pixel.getY(), new Color(0, 0, 0, 0).getRGB());
                opaquePixels.remove(pixel);
            }
        }
    }

    private void addCluster(BufferedImage image, BufferedImage original, int startX, int startY, int clusterSizeThreshold, HashMap<String, PixelState> transparentPixels) {
        HashMap<String, PixelState> clusterNodesHashMap = new HashMap<String, PixelState>(clusterSizeThreshold);
        Stack<PixelState> nodeList = new Stack<PixelState>();
        int clusterSize = 0;
        boolean opaqueStart = ((image.getRGB(startX, startY) >> 24) & 0xff) != 0;
        PixelState startPixel = new PixelState(startX, startY, opaqueStart);


        if (!startPixel.isOpaque() && !hasTransparentPixelBeenConsideredBefore(transparentPixels, startPixel)) {
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
                            newPixel = new PixelState(curX, curY - 1, ((image.getRGB(curX, curY - 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 1: //go east
                        if (curX + 1 < image.getWidth()) {
                            newPixel = new PixelState(curX + 1, curY, ((image.getRGB(curX + 1, curY) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 2: //go south
                        if (curY + 1 < image.getHeight()) {
                            newPixel = new PixelState(curX, curY + 1, ((image.getRGB(curX, curY + 1) >> 24) & 0xff) != 0);
                        }
                        break;
                    case 3: //go west
                        if (curX - 1 >= 0) {
                            newPixel = new PixelState(curX - 1, curY, ((image.getRGB(curX - 1, curY) >> 24) & 0xff) != 0);
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

        //add color back to the pixels if the cluster is small
        if (clusterSize < clusterSizeThreshold) {
            for (PixelState pixel : clusterNodesHashMap.values()) {
                int x = pixel.getX();
                int y = pixel.getY();
                image.setRGB(x, y, original.getRGB(x, y));
                transparentPixels.remove(pixel);
            }
        }
    }

    private boolean hasOpaquePixelBeenConsideredBefore(HashMap<String, PixelState> opaquePixels, PixelState pixel) {
        String pixelPositionString = pixel.getPixelPositionString();

        return opaquePixels.containsKey(pixelPositionString);
    }

    private boolean hasTransparentPixelBeenConsideredBefore(HashMap<String, PixelState> transparentPixels, PixelState pixel) {
        String pixelPositionString = pixel.getPixelPositionString();

        return transparentPixels.containsKey(pixelPositionString);
    }
}
