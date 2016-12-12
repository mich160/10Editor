package logic.imageProcessing;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import logic.utilities.MathUtils;

import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class ImageProcessor {
    public static final int RED = 0, GREEN = 1, BLUE = 2;
    public final static int[] IDENTITY_LUT = IntStream.range(0, 256).toArray();
    public static final ConvolutionFilter AVERAGE_FILTER = new ConvolutionFilter(new double[][]
            {
                    {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0},
                    {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0},
                    {1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0}});
    public static final ConvolutionFilter HIGHPASS_FILTER = new ConvolutionFilter(new double[][]
            {
                    {-1.0, -1.0, -1.0},
                    {-1.0, 9.0, -1.0},
                    {-1.0, -1.0, -1.0}
            }, 1.0);
    public static final ConvolutionFilter GAUSSIAN_FILTER = new ConvolutionFilter(new double[][]
            {
                    {0.00000065, 0.00002252, 0.00018870, 0.00038329, 0.00018870, 0.00002252, 0.00000065,},
                    {0.00002252, 0.00077853, 0.00652409, 0.01325159, 0.00652409, 0.00077853, 0.00002252,},
                    {0.00018870, 0.00652409, 0.05467199, 0.11104863, 0.05467199, 0.00652409, 0.00018870,},
                    {0.00038329, 0.01325159, 0.11104863, 0.22555973, 0.11104863, 0.01325159, 0.00038329,},
                    {0.00018870, 0.00652409, 0.05467199, 0.11104863, 0.05467199, 0.00652409, 0.00018870,},
                    {0.00002252, 0.00077853, 0.00652409, 0.01325159, 0.00652409, 0.00077853, 0.00002252,},
                    {0.00000065, 0.00002252, 0.00018870, 0.00038329, 0.00018870, 0.00002252, 0.00000065}
            });

    public static final MedianFilter MEDIAN_FILTER_3X3 = new MedianFilter(3);

    public static final SobelFilter SOBEL_FILTER = new SobelFilter();

    private static final double[][] SOBEL_HORIZONTAL_KERNEL = new double[][]
            {
                    {-1.0, -2.0, -1.0},
                    {0.0, 0.0, 0.0},
                    {1.0, 2.0, 1.0}};
    private static final double[][] SOBEL_VERTICAL_KERNEL = new double[][]
            {
                    {-1.0, 0.0, 1.0},
                    {-2.0, 0.0, 2.0},
                    {-1.0, 0.0, 1.0}};

    public interface LUTBuilder {
        int[] getLUT(int[] colorArray);
    }

    public interface PixelOperation {//R/G/B -> nR/nG/nB

        int[] getValues(int[] originalValues);
    }

    public interface ImageFilter {
        int getPixel(PixelReader pixelReader, int pixelX, int pixelY, int imageWidth, int imageHeight);
    }

    public interface MorphOperation {
        int getPixel(int x, int y);

        int getRotationsCount();

        void rotateStructuringElement();

        Image getImage();

        void setImage(Image image);
    }

    public interface StructuringElement {
        void setTo(int x, int y);

        boolean any();

        boolean all();

        void rotate();

        void setInverted(boolean inverted);

        boolean getInverted();

        void setImage(Image image);

        Image getImage();
    }

    public static class CrossStructuringElement implements StructuringElement {
        private Image image;
        private PixelReader pixelReader;
        private double imageWidth;
        private double imageHeight;

        private int currentX;
        private int currentY;
        private boolean inverted;

        public CrossStructuringElement(Image image) {
            this.image = image;
            this.pixelReader = image.getPixelReader();
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            this.inverted = false;
        }

        public CrossStructuringElement(CrossStructuringElement another) {
            this.image = another.image;
            this.pixelReader = another.pixelReader;
            this.imageWidth = another.imageWidth;
            this.imageHeight = another.imageHeight;
            this.currentX = another.currentX;
            this.currentY = another.currentY;
        }

        @Override
        public void setTo(int x, int y) {
            this.currentX = x;
            this.currentY = y;
        }

        @Override
        public boolean any() {
            int left = 1;//zakladam ze piksele nie sa czarne
            int right = 1;
            int up = 1;
            int down = 1;
            int middle = pixelReader.getArgb(currentX, currentY) & 0xff;
            if (currentX - 1 >= 0) {
                left = pixelReader.getArgb(currentX - 1, currentY) & 0xff;
            }
            if (currentX + 1 < imageWidth) {
                right = pixelReader.getArgb(currentX + 1, currentY) & 0xff;
            }
            if (currentY - 1 >= 0) {
                up = pixelReader.getArgb(currentX, currentY - 1) & 0xff;
            }
            if (currentY + 1 < imageHeight) {
                down = pixelReader.getArgb(currentX, currentY + 1) & 0xff;
            }
            if (inverted) {
                return !(left == 0 || right == 0 || up == 0 || down == 0 || middle == 0);
            } else {
                return left == 0 || right == 0 || up == 0 || down == 0 || middle == 0;
            }
        }

        @Override
        public boolean all() {
            int left = 1;
            int right = 1;
            int up = 1;
            int down = 1;
            int middle = pixelReader.getArgb(currentX, currentY) & 0xff;
            if (currentX - 1 >= 0) {
                left = pixelReader.getArgb(currentX - 1, currentY) & 0xff;
            }
            if (currentX + 1 < imageWidth) {
                right = pixelReader.getArgb(currentX + 1, currentY) & 0xff;
            }
            if (currentY - 1 >= 0) {
                up = pixelReader.getArgb(currentX, currentY - 1) & 0xff;
            }
            if (currentY + 1 < imageHeight) {
                down = pixelReader.getArgb(currentX, currentY + 1) & 0xff;
            }
            if (inverted) {
                return !(left == 0 && right == 0 && up == 0 && down == 0 && middle == 0);
            } else return left == 0 && right == 0 && up == 0 && down == 0 && middle == 0;
        }

        @Override
        public void rotate() {
            return;
        }

        @Override
        public void setInverted(boolean inverted) {
            this.inverted = inverted;
        }

        @Override
        public boolean getInverted() {
            return inverted;
        }

        @Override
        public void setImage(Image image) {
            this.pixelReader = image.getPixelReader();
            this.imageHeight = image.getHeight();
            this.imageWidth = image.getWidth();
            this.image = image;
        }

        @Override
        public Image getImage() {
            return this.image;
        }
    }

    public static class ThickStructuringElement implements StructuringElement {
        private static byte[][][] structuringElements = {
                {
                        {1, 1, 2},
                        {1, 0, 2},
                        {1, 2, 0}
                },
                {
                        {2, 1, 1},
                        {2, 0, 1},
                        {0, 2, 1}
                },
                {
                        {1, 1, 1},
                        {2, 0, 1},
                        {0, 2, 2}
                },
                {
                        {0, 2, 2},
                        {2, 0, 1},
                        {1, 1, 1}
                },
                {
                        {0, 2, 1},
                        {2, 0, 1},
                        {2, 1, 1}
                },
                {
                        {1, 2, 0},
                        {1, 0, 2},
                        {1, 1, 0}
                },
                {
                        {2, 2, 0},
                        {1, 0, 2},
                        {1, 1, 1}
                },
                {
                        {1, 1, 1},
                        {1, 0, 2},
                        {2, 2, 0}
                }

        };

        private Image image;
        private PixelReader pixelReader;
        private double imageWidth;
        private double imageHeight;

        private int currentX;
        private int currentY;
        private boolean inverted;
        private byte[][] currentElement;
        private int currentElementIndex;

        public ThickStructuringElement(Image image) {
            this.image = image;
            this.pixelReader = image.getPixelReader();
            this.imageWidth = image.getWidth();
            this.imageHeight = image.getHeight();
            this.currentX = 0;
            this.currentY = 0;
            this.inverted = false;
            this.currentElement = structuringElements[0];
        }

        @Override
        public void setTo(int x, int y) {
            currentX = x;
            currentY = y;
        }

        @Override
        public boolean any() {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    int imageX = currentX + (column - 1);
                    int imageY = currentY + (row - 1);
                    if (imageX >= 0 && imageX < imageWidth && imageY >= 0 && imageY < imageHeight) {
                        if (currentElement[row][column] == 0 && pixelReader.getArgb(imageX, imageY) == 0xffffffff) {
                            return true;
                        } else if (currentElement[row][column] == 1 && pixelReader.getArgb(imageX, imageY) == 0xff000000) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean all() {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    int imageX = currentX + (column - 1);
                    int imageY = currentY + (row - 1);
                    if (imageX >= 0 && imageX < imageWidth && imageY >= 0 && imageY < imageHeight) {
                        if (currentElement[row][column] == 0 && pixelReader.getArgb(imageX, imageY) != 0xffffffff) {
                            return false;
                        } else if (currentElement[row][column] == 1 && pixelReader.getArgb(imageX, imageY) != 0xff000000) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void rotate() {
            currentElementIndex = (currentElementIndex + 1) % 8;
            currentElement = structuringElements[currentElementIndex];
        }

        @Override
        public void setInverted(boolean inverted) {
            this.inverted = inverted;
        }

        @Override
        public boolean getInverted() {
            return this.inverted;
        }

        @Override
        public void setImage(Image image) {
            this.pixelReader = image.getPixelReader();
            this.imageHeight = image.getHeight();
            this.imageWidth = image.getWidth();
            this.image = image;
        }

        @Override
        public Image getImage() {
            return this.image;
        }
    }

    public static class Dilatation implements MorphOperation {
        private StructuringElement structuringElement;

        public Dilatation(Image image) {
            this.structuringElement = new CrossStructuringElement(image);
        }

        @Override
        public int getPixel(int x, int y) {
            structuringElement.setTo(x, y);
            if (structuringElement.any()) {
                return 0xff000000;
            }
            return 0xffffffff;
        }

        @Override
        public int getRotationsCount() {
            return 1;
        }

        @Override
        public void rotateStructuringElement() {
            structuringElement.rotate();
        }

        @Override
        public Image getImage() {
            return structuringElement.getImage();
        }

        @Override
        public void setImage(Image image) {
            structuringElement.setImage(image);
        }
    }

    public static class Erosion implements MorphOperation {
        private StructuringElement structuringElement;

        public Erosion(Image image) {
            this.structuringElement = new CrossStructuringElement(image);
            this.structuringElement.setInverted(true);
        }

        @Override
        public int getPixel(int x, int y) {
            structuringElement.setTo(x, y);
            if (structuringElement.all()) {
                return 0xffffffff;
            } else return 0xff000000;
        }

        @Override
        public int getRotationsCount() {
            return 1;
        }

        @Override
        public void rotateStructuringElement() {
            structuringElement.rotate();
        }

        @Override
        public Image getImage() {
            return structuringElement.getImage();
        }

        @Override
        public void setImage(Image image) {
            structuringElement.setImage(image);
        }
    }

    public static class Thinning implements MorphOperation {
        private StructuringElement structuringElement;
        private PixelReader pixelReader;

        public Thinning(Image image) {
            this.structuringElement = new CrossStructuringElement(image);
            this.pixelReader = image.getPixelReader();
        }

        @Override
        public int getPixel(int x, int y) {
            structuringElement.setTo(x, y);
            if (structuringElement.all()) {
                return 0xffffffff;
            } else {
                return pixelReader.getArgb(x, y);
            }
        }

        @Override
        public int getRotationsCount() {
            return 1;
        }

        @Override
        public void rotateStructuringElement() {
            structuringElement.rotate();
        }

        @Override
        public Image getImage() {
            return structuringElement.getImage();
        }

        @Override
        public void setImage(Image image) {
            this.pixelReader = image.getPixelReader();
            structuringElement.setImage(image);
        }
    }

    public static class Thickening implements MorphOperation {
        private StructuringElement structuringElement;
        private PixelReader pixelReader;

        public Thickening(Image image) {
            this.structuringElement = new ThickStructuringElement(image);
            this.pixelReader = image.getPixelReader();
        }

        @Override
        public int getPixel(int x, int y) {
            structuringElement.setTo(x, y);
            if (structuringElement.all()) {
                return 0xff000000;
            }
            return pixelReader.getArgb(x, y);
        }

        @Override
        public int getRotationsCount() {
            return 8;
        }

        @Override
        public void rotateStructuringElement() {
            structuringElement.rotate();
        }

        public void setImage(Image image) {
            this.pixelReader = image.getPixelReader();
            this.structuringElement.setImage(image);
        }

        public Image getImage() {
            return this.structuringElement.getImage();
        }
    }

    public static class MedianFilter implements ImageFilter {

        private int[] redVector;
        private int[] greenVector;
        private int[] blueVector;
        private int size;

        public MedianFilter(int size) {
            this.size = size;
            redVector = new int[size * size];
            greenVector = new int[size * size];
            blueVector = new int[size * size];
        }

        @Override
        public int getPixel(PixelReader pixelReader, int pixelX, int pixelY, int imageWidth, int imageHeight) {
            int vectorPosition = 0;
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    int imageX = pixelX + column - size / 2;
                    int imageY = pixelY + row - size / 2;
                    imageX = MathUtils.constraintValue(imageX, 0, imageWidth - 1);
                    imageY = MathUtils.constraintValue(imageY, 0, imageHeight - 1);
                    int pixelRGB = pixelReader.getArgb(imageX, imageY);
                    redVector[vectorPosition] = (pixelRGB & (0xff << 16)) >> 16;
                    greenVector[vectorPosition] = (pixelRGB & (0xff << 8)) >> 8;
                    blueVector[vectorPosition] = (pixelRGB & 0xff);
                    vectorPosition++;
                }
            }
            Arrays.sort(redVector);
            Arrays.sort(greenVector);
            Arrays.sort(blueVector);
            return (0xff << 24) | (redVector[size / 2] << 16) | (greenVector[size / 2] << 8) | blueVector[size / 2];
        }

    }

    public static class SobelFilter implements ImageFilter {

        public SobelFilter() {

        }

        @Override
        public int getPixel(PixelReader pixelReader, int pixelX, int pixelY, int imageWidth, int imageHeight) {
            double gX = 0.0;
            double gY = 0.0;
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    int imageX = pixelX + column - 3 / 2;
                    int imageY = pixelY + row - 3 / 2;
                    imageX = MathUtils.constraintValue(imageX, 0, imageWidth - 1);
                    imageY = MathUtils.constraintValue(imageY, 0, imageHeight - 1);
                    int currentPixel = pixelReader.getArgb(imageX, imageY);
                    int grayscaleValue = (((currentPixel & (0xff << 16)) >> 16) + ((currentPixel & (0xff << 8)) >> 8) + ((currentPixel & 0xff))) / 3;
                    gX += grayscaleValue * SOBEL_VERTICAL_KERNEL[row][column];
                    gY += grayscaleValue * SOBEL_HORIZONTAL_KERNEL[row][column];
                }
            }
            int grayscaleResult = ((int) Math.sqrt(gX * gX + gY * gY));
            return (0xff << 24) | (grayscaleResult << 16) | (grayscaleResult << 8) | grayscaleResult;
        }
    }

    public static class ConvolutionFilter implements ImageFilter {
        private double[][] kernel;
        double divisor;
        int kernelWidth;
        int kernelHeight;

        public ConvolutionFilter(double[][] kernel) {
            this.kernel = kernel;
            divisor = getDivisorFromKernel(kernel);
            kernelHeight = kernel.length;
            kernelWidth = kernel[0].length;
        }

        public ConvolutionFilter(double[][] kernel, double divisor) {
            this.kernel = kernel;
            this.divisor = divisor;
            kernelHeight = kernel.length;
            kernelWidth = kernel[0].length;
        }

        private double getDivisorFromKernel(double[][] kernel) {
            double divisor = 0.0;
            for (int row = 0; row < kernelHeight; row++) {
                for (int column = 0; column < kernelWidth; column++) {
                    divisor += kernel[row][column];
                }
            }
            return divisor;
        }

        @Override
        public int getPixel(PixelReader pixelReader, int pixelX, int pixelY, int imageWidth, int imageHeight) {
            double red = 0.0;
            double green = 0.0;
            double blue = 0.0;
            for (int row = 0; row < kernelHeight; row++) {
                for (int column = 0; column < kernelWidth; column++) {
                    double currentKernelValue = kernel[row][column];
                    int imageX = pixelX + column - kernelHeight / 2;
                    int imageY = pixelY + row - kernelWidth / 2;
                    imageX = MathUtils.constraintValue(imageX, 0, imageWidth - 1);
                    imageY = MathUtils.constraintValue(imageY, 0, imageHeight - 1);
                    int pixelRGB = pixelReader.getArgb(imageX, imageY);
                    red += ((double) ((pixelRGB & 0xff << 16) >> 16)) * currentKernelValue;
                    green += ((double) ((pixelRGB & 0xff << 8) >> 8)) * currentKernelValue;
                    blue += ((double) (pixelRGB & 0xff)) * currentKernelValue;
                }
            }
            red = MathUtils.constraintValue(red, 0.0, 255.0);
            green = MathUtils.constraintValue(green, 0.0, 255.0);
            blue = MathUtils.constraintValue(blue, 0.0, 255.0);
            return (0xff << 24) | (((int) red) << 16) | ((int) green << 8) | ((int) blue);
        }
    }

    public static int[] buildLUT(LUTBuilder builder) {
        return builder.getLUT(new int[256]);
    }

    //8bitint -> 8bitint
    public static int[] buildLUT(UnaryOperator<Integer> operation) {//if result exceeds 255 or is below 0 then its locked on 255/0
        int[] result = new int[256];
        for (int i = 0; i < 256; i++) {
            result[i] = MathUtils.constraintValue(operation.apply(i), 0, 255);
        }
        return result;
    }

    public static int[] buildLUTDoubleOp(UnaryOperator<Double> operation) {
        int[] result = new int[256];
        for (int i = 0; i < 256; i++) {
            result[i] = (int) MathUtils.constraintValue(operation.apply((double) i), 0.0, 255.0);
        }
        return result;
    }

    private Image baseImage;
    private PixelReader imageReader;
    private ImageHistogram imageHistogram;//lazy initialization

    public ImageProcessor(Image baseImage) {
        this.baseImage = baseImage;
        this.imageReader = baseImage.getPixelReader();
    }

    public WritableImage pointProcessing(int[] rLUT, int[] gLUT, int[] bLUT) {
        WritableImage result = new WritableImage((int) baseImage.getWidth(), (int) baseImage.getHeight());
        PixelWriter imageWriter = result.getPixelWriter();
        for (int row = 0; row < baseImage.getHeight(); row++) {
            for (int column = 0; column < baseImage.getWidth(); column++) {
                int basePixel = imageReader.getArgb(column, row);
                int baseRed = (basePixel & (0xff << 16)) >> 16;
                int baseGreen = (basePixel & (0xff << 8)) >> 8;
                int baseBlue = (basePixel & (0xff));
                imageWriter.setArgb(column, row, (0xff << 24) | (rLUT[baseRed] << 16) | (gLUT[baseGreen] << 8) | (bLUT[baseBlue]));
            }
        }
        return result;
    }

    public WritableImage pointProcessing(PixelOperation pixelOperation) {
        WritableImage result = new WritableImage((int) baseImage.getWidth(), (int) baseImage.getHeight());
        PixelWriter imageWriter = result.getPixelWriter();
        for (int row = 0; row < baseImage.getHeight(); row++) {
            for (int column = 0; column < baseImage.getWidth(); column++) {
                int basePixel = imageReader.getArgb(column, row);
                int[] baseRGB = new int[3];
                baseRGB[RED] = (basePixel & (0xff << 16)) >> 16;
                baseRGB[GREEN] = (basePixel & (0xff << 8)) >> 8;
                baseRGB[BLUE] = (basePixel & (0xff));
                int[] resultPixels = pixelOperation.getValues(baseRGB);
                imageWriter.setArgb(column, row, (0xff << 24 | resultPixels[RED] << 16 | resultPixels[GREEN] << 8 | resultPixels[BLUE]));
            }
        }
        return result;
    }

    public WritableImage convolutionProcessing(ImageFilter filter) {
        int baseImageWidth = (int) baseImage.getWidth();
        int baseImageHeight = (int) baseImage.getHeight();
        WritableImage result = new WritableImage(baseImageWidth, baseImageHeight);
        PixelWriter imageWriter = result.getPixelWriter();
        for (int row = 0; row < baseImageHeight; row++) {
            for (int column = 0; column < baseImageWidth; column++) {
                imageWriter.setArgb(column, row, filter.getPixel(imageReader, column, row, baseImageWidth, baseImageHeight));
            }
        }
        return result;
    }

    public WritableImage morphologicProcessing(MorphOperation operation) {
        int baseImageWidth = (int) baseImage.getWidth();
        int baseImageHeight = (int) baseImage.getHeight();
        WritableImage result = null;
        result = new WritableImage(baseImageWidth, baseImageHeight);
        PixelWriter imageWriter = result.getPixelWriter();
        for (int row = 0; row < baseImageHeight; row++) {
            for (int column = 0; column < baseImageWidth; column++) {
                imageWriter.setArgb(column, row, operation.getPixel(column, row));
            }
        }
        return result;
    }

    public int[][] histogramLinearStretchLUT() {
        getImageHistogram();
        UnaryOperator<Double> stretchRed = aDouble -> (aDouble - imageHistogram.getRedMin()) / (double) (imageHistogram.getRedMax() - imageHistogram.getRedMin()) * 256;
        UnaryOperator<Double> stretchGreen = aDouble -> (aDouble - imageHistogram.getGreenMin()) / (double) (imageHistogram.getGreenMax() - imageHistogram.getGreenMin()) * 256;
        UnaryOperator<Double> stretchBlue = aDouble -> (aDouble - imageHistogram.getBlueMin()) / (double) (imageHistogram.getBlueMax() - imageHistogram.getBlueMin()) * 256;
        int[] rLUT = ImageProcessor.buildLUTDoubleOp(stretchRed);
        int[] gLUT = ImageProcessor.buildLUTDoubleOp(stretchGreen);
        int[] bLUT = ImageProcessor.buildLUTDoubleOp(stretchBlue);
        return new int[][]{rLUT, gLUT, bLUT};
    }

    public int[][] histogramEqualizeLUT() {
        getImageHistogram();
        long[] cumulativeRed = imageHistogram.getRedCumulative();
        long[] cumulativeGreen = imageHistogram.getGreenCumulative();
        long[] cumulativeBlue = imageHistogram.getBlueCumulative();
        UnaryOperator<Double> equalizeRed = aDouble -> cumulativeRed[aDouble.intValue()] * 255.0 / (double) imageHistogram.getPixelCount();
        UnaryOperator<Double> equalizeGreen = aDouble -> cumulativeGreen[aDouble.intValue()] * 255.0 / (double) imageHistogram.getPixelCount();
        UnaryOperator<Double> equalizeBlue = aDouble -> cumulativeBlue[aDouble.intValue()] * 255.0 / (double) imageHistogram.getPixelCount();
        int[] rLUT = ImageProcessor.buildLUTDoubleOp(equalizeRed);
        int[] gLUT = ImageProcessor.buildLUTDoubleOp(equalizeGreen);
        int[] bLUT = ImageProcessor.buildLUTDoubleOp(equalizeBlue);
        return new int[][]{rLUT, gLUT, bLUT};
    }

    public int[][] getCustomBinarizationLUT(int threshold) {
        int thresholdValid = (threshold > 255 || threshold < 0) ? 127 : threshold;
        int[] lut = getBinarizationLUT(thresholdValid);
        return new int[][]{lut, lut, lut};
    }

    public int[][] getPercentBinarizationLUT(double percent) {
        getImageHistogram();
        int selection;
        int limit = (int) (percent * imageHistogram.getPixelCount());
        for (selection = 0; selection < 256; selection++) {
            if (imageHistogram.getRedCumulative()[selection] >= limit) {
                break;
            }
        }
        int[] lut = getBinarizationLUT(selection);
        return new int[][]{lut, lut, lut};
    }

    public int[][] getIterativeBinarizationLUT() {
        getImageHistogram();
        int thresholdCurrent = 0;
        int thresholdPrevious = 0;
        double[] numerators = new double[256];
        for (int i = 0; i < 256; i++) {
            numerators[i] = i * imageHistogram.getRedHistogram()[i];
        }
        do {
            thresholdPrevious = thresholdCurrent;
            double tBNumerator = 0.0;
            double tBDenominator = imageHistogram.getRedCumulative()[thresholdCurrent];
            double tWNumerator = 0.0;
            double tWDenominator = imageHistogram.getRedCumulative()[255] - imageHistogram.getRedCumulative()[thresholdCurrent];
            for (int i = 0; i <= thresholdCurrent; i++) {
                tBNumerator += numerators[i];
            }
            for (int j = thresholdCurrent + 1; j < 256; j++) {
                tWNumerator += numerators[j];
            }
            double tB = 0.0;
            if (Double.compare(tBDenominator, 0.0) != 0) {
                tB = tBNumerator / tBDenominator / 2.0;
            }
            double tW = 0.0;
            if (Double.compare(tWDenominator, 0.0) != 0) {
                tW = tWNumerator / tWDenominator / 2.0;
            }
            thresholdCurrent = (int) (tB + tW);
        } while (thresholdCurrent != thresholdPrevious);
        int[] lut = getBinarizationLUT(thresholdCurrent);
        return new int[][]{lut, lut, lut};
    }

    public int[][] getMinimumErrorBinarizationLUT() {
        getImageHistogram();
        int thresholdMaximum = 0;
        double sigmaMaximum = 0.0;
        double[] kpkCumulative = new double[256];
        double kpkSum = 0.0;
        for (int i = 0; i < 256; i++) {
            kpkSum += i * imageHistogram.getRedProbability(i);
            kpkCumulative[i] = kpkSum;
        }
        for (int i = 0; i < 256; i++) {
            double objectProbability = (double) imageHistogram.getRedCumulative()[i] / imageHistogram.getPixelCount();
            double backgroundProbability = 1.0 - objectProbability;
            double miObject = kpkCumulative[i] / objectProbability;
            double miBackground = (kpkCumulative[255] - kpkCumulative[i]) / backgroundProbability;
            double sigmaSquare = objectProbability * backgroundProbability * Math.pow(miObject - miBackground, 2.0);
            if (sigmaSquare > sigmaMaximum) {
                sigmaMaximum = sigmaSquare;
                thresholdMaximum = i;
            }
        }
        int[] lut = getBinarizationLUT(thresholdMaximum);
        return new int[][]{lut, lut, lut};
    }


    public int[][] getEntropyBinarizationLUT() {
        getImageHistogram();
        int selection;
        int maxSelection = 0;
        double maxEntropy = 0.0;
        for (selection = 0; selection < 255; selection++) {
            double objectProbability = (double) imageHistogram.getRedCumulative()[selection] / imageHistogram.getPixelCount();
            double backgroundProbability = 1.0 - objectProbability;
            double objectEntropy = 0.0;
            double backgroundEntropy = 0.0;
            for (int i = 0; i <= selection; i++) {
                double redProbability = imageHistogram.getRedProbability(i);
                if (Double.compare(redProbability, 0) != 0) {
                    objectEntropy -= imageHistogram.getRedProbability(i) / objectProbability * MathUtils.log2(imageHistogram.getRedProbability(i) / objectProbability);
                }
            }
            for (int i = selection + 1; i < 256; i++) {
                double redProbability = imageHistogram.getRedProbability(i);
                if (Double.compare(redProbability, 0) != 0) {
                    backgroundEntropy -= imageHistogram.getRedProbability(i) / backgroundProbability * MathUtils.log2(imageHistogram.getRedProbability(i) / backgroundProbability);
                }
            }
            double entropy = objectEntropy + backgroundEntropy;
            if (entropy > maxEntropy) {
                maxSelection = selection;
                maxEntropy = entropy;
            }
        }
        int[] lut = getBinarizationLUT(maxSelection);
        return new int[][]{lut, lut, lut};
    }

    public int[][] getFuzzyMinimumErrorBinarizationLUT() {
        getImageHistogram();
        int minimum = 255;
        int maximum = 0;
        for (int i = 0; i < 256; i++) {
            if (imageHistogram.getRedHistogram()[i] > 0) {
                minimum = i;
                break;
            }
        }
        for (int i = 255; i >= 0; i--) {
            if (imageHistogram.getRedHistogram()[i] > 0) {
                maximum = i;
                break;
            }
        }
        double[] iCumulatives = new double[256];
        for (int i = 0; i < 256; i++) {
            iCumulatives[i] = i * imageHistogram.getRedHistogram()[i];
        }
        int C = maximum - minimum;
        double minimumEntropy = fuzzinessEntropy(0, C, iCumulatives);
        int threshold = 0;
        for (int i = 1; i < 255; i++) {
            double entropy = fuzzinessEntropy(i, C, iCumulatives);
            if (entropy < minimumEntropy) {
                minimumEntropy = entropy;
                threshold = i;
            }
        }
        int[] lut = getBinarizationLUT(threshold);
        return new int[][]{lut, lut, lut};
    }

    public int getMedianThreshold() {
        getImageHistogram();
        int result = 127;
        long maxValue = imageHistogram.getPixelCount() / 2;
        for (int i = 0; i < 256; i++) {
            if (imageHistogram.getRedCumulative()[i] >= maxValue) {
                result = i;
                break;
            }
        }
        return result;
    }

    public Image getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(Image baseImage) {
        this.baseImage = baseImage;
        this.imageReader = baseImage.getPixelReader();
        this.imageHistogram = null;
    }

    public ImageHistogram getImageHistogram() {
        if (imageHistogram == null) {
            imageHistogram = new ImageHistogram(baseImage);
        }
        return imageHistogram;
    }

    private int[] getBinarizationLUT(int threshold) {
        UnaryOperator<Integer> thresholding = integer -> {
            if (integer < threshold) {
                return 0;
            } else return 255;
        };
        return buildLUT(thresholding);
    }

    private double membershipValue(int pixelValue, int threshold, int C, double miZero, double miOne) {
        if (pixelValue <= threshold) {
            return 1 / (1 + Math.abs(pixelValue - miZero) / C);
        }
        return 1 / (1 + Math.abs(pixelValue - miOne) / C);
    }

    private double fuzzinessEntropy(int threshold, int C, double[] iCumulatives) {
        double miZero = 0.0;
        for (int i = 0; i <= threshold; i++) {
            miZero += iCumulatives[i];
        }
        if (threshold != 0) {
            miZero /= (double) imageHistogram.getRedCumulative()[threshold];
        }
        double miOne = 0.0;
        for (int i = threshold + 1; i < 256; i++) {
            miOne += iCumulatives[i];
        }
        if (threshold != 255) {
            miOne /= (double) (imageHistogram.getRedCumulative()[255] - imageHistogram.getRedCumulative()[threshold]);
        }
        double entropy = 0.0;
        for (int i = 0; i < 256; i++) {
            entropy += MathUtils.shannons(membershipValue(i, threshold, C, miZero, miOne)) * (double) imageHistogram.getRedHistogram()[i];
        }
        entropy /= (double) imageHistogram.getPixelCount();
        return entropy;
    }
}
