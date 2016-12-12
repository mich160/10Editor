package view.customFXClasses;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ConeRenderer {

    public static Image getConeBase(int sideLength, int offset) {//off 90
        WritableImage result = new WritableImage(sideLength, sideLength);
        PixelWriter pixelWriter = result.getPixelWriter();
        double translation = sideLength / 2;
        double middleX = sideLength / 2;
        double middleY = sideLength / 2;
        for (int row = 0; row < sideLength; row++) {
            for (int column = 0; column < sideLength; column++) {
                if (Math.pow(row - translation, 2) + Math.pow(column - translation, 2) <= Math.pow(translation, 2)) {
                    double alpha = Math.toDegrees(getAngleBetweenPoints(middleX, middleY, row, column)) + offset;
                    double saturation = getDistanceBetweenPoints(middleX, middleY, row, column) / translation;
                    Color currentColor = Color.hsb(alpha, saturation, 1);
                    int red = (int) (currentColor.getRed() * 255);
                    int green = (int) (currentColor.getGreen() * 255);
                    int blue = (int) (currentColor.getBlue() * 255);
                    int argb = (0xff << 24) | (red << 16) | (green << 8) | blue;
                    pixelWriter.setArgb(column, row, argb);
                }
            }
        }
        return result;
    }

    public static Image getConeBody(int width, int height, int offset) {
        WritableImage result = new WritableImage(width, height);
        PixelWriter pixelWriter = result.getPixelWriter();
        double middlePointX = width / 2;
        double middlePointY = 0.0;
        double endingPointX = middlePointX;
        double endingPointY = height;
        double leftSideTangent = endingPointY / endingPointX;
        double rightSideTangent = endingPointY / (endingPointX - width);
        double rightSideConstant = -1 * rightSideTangent * width;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (row <= leftSideTangent * column && row <= rightSideTangent * column + rightSideConstant) {
                    double value = 1.0 - (double) row / height;
                    double yValue = Math.sqrt(-1 * Math.pow(column, 2) + 2 * middlePointX * column);
                    double hue = Math.toDegrees(getAngleBetweenPoints(middlePointX, middlePointY, column, yValue)) + offset;
                    Color currentColor = Color.hsb(hue, 1.0, value);
                    int red = (int) (currentColor.getRed() * 255);
                    int green = (int) (currentColor.getGreen() * 255);
                    int blue = (int) (currentColor.getBlue() * 255);
                    int argb = (0xff << 24) | (red << 16) | (green << 8) | blue;
                    pixelWriter.setArgb(width - column - 1, row, argb);
                }
            }
        }
        return result;
    }

    public static double getAngleBetweenPoints(double ax, double ay, double bx, double by) {
        double deltaX = bx - ax;
        double deltaY = by - ay;
        return Math.atan2(deltaY, deltaX);
    }

    public static double getDistanceBetweenPoints(double ax, double ay, double bx, double by) {
        return Math.sqrt(Math.pow(bx - ax, 2) + Math.pow(by - ay, 2));
    }
}
