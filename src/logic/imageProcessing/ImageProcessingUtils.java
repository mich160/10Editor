package logic.imageProcessing;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageProcessingUtils {
    public static WritableImage copyImage(Image image) {
        WritableImage result = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = result.getPixelWriter();
        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {
                pixelWriter.setArgb(column, row, pixelReader.getArgb(column, row));
            }
        }
        return result;
    }
}
