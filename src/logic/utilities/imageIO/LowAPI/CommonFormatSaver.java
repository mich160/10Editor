package logic.utilities.imageIO.LowAPI;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import logic.utilities.imageIO.IOUtils;
import logic.utilities.imageIO.ImageFormatSaver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CommonFormatSaver implements ImageFormatSaver {
    @Override
    public void saveImage(Image image, File file) throws IOException {
        BufferedImage awtImage = convertToBufferedImg(image);
        String extension = IOUtils.getExtension(file);
        ImageIO.write(awtImage, extension, file);
    }

    @Override
    public void saveImage(Image image, String filename) throws IOException {
        saveImage(image, new File(filename));
    }

    private BufferedImage convertToBufferedImg(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        BufferedImage result = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {
                result.setRGB(column, row, pixelReader.getArgb(column, row) & 0xffffff);
            }
        }
        return result;
    }
}
