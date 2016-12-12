package logic.imageProcessing;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class ImageHistogram {
    private long pixelCount;
    private long maxValue;
    private int redMax, greenMax, blueMax;
    private int redMin, greenMin, blueMin;
    private long[] redHistogram;
    private long[] greenHistogram;
    private long[] blueHistogram;
    private long[] redCumulative;
    private long[] greenCumulative;
    private long[] blueCumulative;

    private ImageHistogram() {
        redHistogram = new long[256];
        greenHistogram = new long[256];
        blueHistogram = new long[256];
    }

    public ImageHistogram(Image image) {
        this();
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();
        pixelCount = imageHeight * imageWidth;
        PixelReader pixelReader = image.getPixelReader();
        for (int row = 0; row < imageHeight; row++) {
            for (int column = 0; column < imageWidth; column++) {
                int pixel = pixelReader.getArgb(column, row);
                int red = (pixel & (0xff << 16)) >> 16;
                int green = (pixel & (0xff << 8)) >> 8;
                int blue = pixel & 0xff;
                redMax = Math.max(red, redMax);
                greenMax = Math.max(green, greenMax);
                blueMax = Math.max(blue, blueMax);
                redMin = Math.min(red, redMin);
                greenMin = Math.min(green, greenMin);
                blueMin = Math.min(blue, blueMin);
                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;
            }
        }
    }

    public int getRedMax() {
        return redMax;
    }

    public int getGreenMax() {
        return greenMax;
    }

    public int getBlueMax() {
        return blueMax;
    }

    public int getRedMin() {
        return redMin;
    }

    public int getGreenMin() {
        return greenMin;
    }

    public int getBlueMin() {
        return blueMin;
    }

    public long[] getRedHistogram() {
        return redHistogram;
    }

    public long[] getGreenHistogram() {
        return greenHistogram;
    }

    public long[] getBlueHistogram() {
        return blueHistogram;
    }

    public double getRedProbability(int value) {
        return ((double) redHistogram[value]) / ((double) pixelCount);
    }

    public double getGreenProbability(int value) {
        return ((double) greenHistogram[value]) / ((double) pixelCount);
    }

    public double getBlueProbability(int value) {
        return ((double) blueHistogram[value]) / ((double) pixelCount);
    }

    public long getPixelCount() {
        return pixelCount;
    }

    public long getHistogramMaxValue() {
        if (maxValue <= 0) {
            for (int i = 0; i < 256; i++) {
                maxValue = Math.max(Math.max(Math.max(maxValue, redHistogram[i]), greenHistogram[i]), blueHistogram[i]);
            }
        }
        return maxValue;
    }

    private void calculateCumulatives() {
        redCumulative = new long[256];
        greenCumulative = new long[256];
        blueCumulative = new long[256];
        long redSum = 0;
        long greenSum = 0;
        long blueSum = 0;
        for (int i = 0; i < 256; i++) {
            redSum += redHistogram[i];
            greenSum += greenHistogram[i];
            blueSum += blueHistogram[i];
            redCumulative[i] = redSum;
            greenCumulative[i] = greenSum;
            blueCumulative[i] = blueSum;
        }
    }

    public long[] getRedCumulative() {
        if (redCumulative == null) {
            calculateCumulatives();
        }
        return redCumulative;
    }

    public long[] getGreenCumulative() {
        if (greenCumulative == null) {
            calculateCumulatives();
        }
        return greenCumulative;
    }

    public long[] getBlueCumulative() {
        if (blueCumulative == null) {
            calculateCumulatives();
        }
        return blueCumulative;
    }
}
