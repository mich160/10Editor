package view.customFXClasses;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class TextureFactory {
    private static final int red = 0, green = 1, blue = 2;
    private static final int leftUpper = 0, rightUpper = 1, leftLower = 2;

    private static class RGBColor {
        int red, green, blue;

        public RGBColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public RGBColor subtract(RGBColor color) {
            return new RGBColor(red - color.red, green - color.green, blue - color.blue);
        }

        public int getRGB() {
            return (red << 16) | (green << 8) | (blue);
        }

        public int getARGB() {
            return (0xff << 24) | (red << 16) | (green << 8) | (blue);
        }

        public int getRed() {
            return red;
        }

        public void setRed(int red) {
            this.red = red;
        }

        public int getGreen() {
            return green;
        }

        public void setGreen(int green) {
            this.green = green;
        }

        public int getBlue() {
            return blue;
        }

        public void setBlue(int blue) {
            this.blue = blue;
        }

    }

    public static RGBColor[][] facesRGB = new RGBColor[6][];

    static {
        facesRGB[0] = new RGBColor[]{new RGBColor(255, 0, 255), new RGBColor(255, 0, 0), new RGBColor(0, 0, 255)};//0 - 0,0   1 - x,0   2 - 0,y
        facesRGB[1] = new RGBColor[]{new RGBColor(255, 255, 255), new RGBColor(255, 0, 255), new RGBColor(0, 255, 255)};
        facesRGB[2] = new RGBColor[]{new RGBColor(255, 255, 0), new RGBColor(255, 255, 255), new RGBColor(0, 255, 0)};
        facesRGB[3] = new RGBColor[]{new RGBColor(255, 0, 0), new RGBColor(255, 255, 0), new RGBColor(0, 0, 0)};
        facesRGB[4] = new RGBColor[]{new RGBColor(255, 0, 0), new RGBColor(255, 0, 255), new RGBColor(255, 255, 0)};
        facesRGB[5] = new RGBColor[]{new RGBColor(0, 0, 255), new RGBColor(0, 0, 0), new RGBColor(0, 255, 255)};
    }

    public static Image getRGBFaces(int sideLength) {
        WritableImage result = new WritableImage(sideLength * 2, sideLength * 3);
        int xStart = 0, yStart = 0;
        for (RGBColor[] faceRGB : facesRGB) {
            drawFace(faceRGB, result, xStart, yStart, sideLength);
            xStart += sideLength;
            if (xStart == sideLength * 2) {
                xStart = 0;
                yStart += sideLength;
            }
        }
        return result;
    }

    private static void drawFace(RGBColor[] values, WritableImage image, int xStart, int yStart, int sideLength) {
        PixelWriter pixelWriter = image.getPixelWriter();
        double[] xDeltaColor = new double[3];
        double[] yDeltaColor = new double[3];
        xDeltaColor[red] = (values[rightUpper].red - values[leftUpper].red) / (double) sideLength;
        xDeltaColor[green] = (values[rightUpper].green - values[leftUpper].green) / (double) sideLength;
        xDeltaColor[blue] = (values[rightUpper].blue - values[leftUpper].blue) / (double) sideLength;
        yDeltaColor[red] = (values[leftLower].red - values[leftUpper].red) / (double) sideLength;
        yDeltaColor[green] = (values[leftLower].green - values[leftUpper].green) / (double) sideLength;
        yDeltaColor[blue] = (values[leftLower].blue - values[leftUpper].blue) / (double) sideLength;
        boolean redX = false;
        boolean greenX = false;
        boolean blueX = false;
        redX = Double.compare(xDeltaColor[red], 0.0) != 0;
        greenX = Double.compare(xDeltaColor[green], 0.0) != 0;
        blueX = Double.compare(xDeltaColor[blue], 0.0) != 0;
        for (int row = xStart; row < xStart + sideLength; row++) {
            for (int column = yStart; column < yStart + sideLength; column++) {
                int redPart, greenPart, bluePart;
                if (redX) {
                    redPart = scaleColor(xDeltaColor[red], values[leftUpper].red, xStart, row);
                } else {
                    redPart = scaleColor(yDeltaColor[red], values[leftUpper].red, yStart, column);
                }
                if (greenX) {
                    greenPart = scaleColor(xDeltaColor[green], values[leftUpper].green, xStart, row);
                } else {
                    greenPart = scaleColor(yDeltaColor[green], values[leftUpper].green, yStart, column);
                }
                if (blueX) {
                    bluePart = scaleColor(xDeltaColor[blue], values[leftUpper].blue, xStart, row);
                } else {
                    bluePart = scaleColor(yDeltaColor[blue], values[leftUpper].blue, yStart, column);
                }
                int color = (0xff << 24) | (redPart << 16) | (greenPart << 8) | bluePart;
                pixelWriter.setArgb(row, column, color);
            }
        }
    }

    private static int scaleColor(double deltaC, int leftUpperValue, int startCoordinate, int currentCoordinate) {
        if (Double.compare(deltaC, 0.0) == 0) {
            return leftUpperValue;
        }
        return (int) (leftUpperValue + (currentCoordinate - startCoordinate) * deltaC);
    }
}
