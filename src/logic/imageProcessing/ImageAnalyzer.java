package logic.imageProcessing;

import com.sun.istack.internal.Nullable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageAnalyzer {

    public interface PixelProperty {
        boolean meetsRequirement(int pixel);
    }

    public interface PixelReplacer {
        int getReplacement(int originalPixel);
    }

    private Image currentImage;
    private WritableImage replacedImage;

    private PixelReader pixelReader;
    private long pixelCount;

    private long lastReplacedCount;

    public ImageAnalyzer(Image image) {
        this.currentImage = image;
        recalculateImage();
    }

    public long howManyMeetsRequirement(PixelProperty property) {
        long requiredPixelsCount = 0;
        int width = (int) currentImage.getWidth();
        int height = (int) currentImage.getHeight();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (property.meetsRequirement(pixelReader.getArgb(column, row))) {
                    requiredPixelsCount++;
                }
            }
        }
        return requiredPixelsCount;
    }

    public Image getReplacedImage(PixelProperty property, @Nullable PixelProperty ignoredProperty, @Nullable PixelReplacer replacer) {
        int width = (int) currentImage.getWidth();
        int height = (int) currentImage.getHeight();
        WritableImage result = new WritableImage(width, height);
        PixelWriter writer = result.getPixelWriter();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (property.meetsRequirement(pixelReader.getArgb(column, row)) && (ignoredProperty == null || !ignoredProperty.meetsRequirement(pixelReader.getArgb(column, row)))) {
                    if (replacer != null) {
                        writer.setArgb(column, row, replacer.getReplacement(pixelReader.getArgb(column, row)));
                    } else {
                        writer.setArgb(column, row, pixelReader.getArgb(column, row));
                    }
                    lastReplacedCount++;
                } else {
                    writer.setArgb(column, row, pixelReader.getArgb(column, row));
                }
            }
        }
        return result;
    }

    public double percentOfImage(long pixelCount) {
        return ((double) pixelCount) / ((double) this.pixelCount);
    }

    public double percentOfImage(long pixelCount, PixelProperty allRequirement) {
        long othersCount = howManyMeetsRequirement(allRequirement);
        return ((double) pixelCount) / ((double) othersCount);
    }

    private void recalculateImage() {
        pixelReader = currentImage.getPixelReader();
        pixelCount = (long) (currentImage.getHeight() * currentImage.getWidth());
        lastReplacedCount = 0;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
        recalculateImage();
    }

    public long getLastReplacedCount() {
        return lastReplacedCount;
    }
}
