package logic.utilities.imageIO;

import javafx.scene.image.Image;
import logic.utilities.imageIO.LowAPI.CommonFormatSaver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageSaver implements ImageFormatSaver {
    private static Map<String, ImageFormatSaver> imageSavers = new HashMap<>();

    static {
        CommonFormatSaver commonFormatSaver = new CommonFormatSaver();
        imageSavers.put("jpg", commonFormatSaver);
        imageSavers.put("png", commonFormatSaver);
        imageSavers.put("bmp", commonFormatSaver);
    }

    @Override
    public void saveImage(Image image, File file) throws IOException {
        String extension = IOUtils.getExtension(file);
        if (!imageSavers.containsKey(extension)) {
            throw new IOException("Unrecognized extension!");
        }
        imageSavers.get(extension).saveImage(image, file);
    }

    @Override
    public void saveImage(Image image, String filename) throws IOException {
        saveImage(image, new File(filename));
    }
}
