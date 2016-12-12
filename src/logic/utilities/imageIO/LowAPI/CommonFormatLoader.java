package logic.utilities.imageIO.LowAPI;

import javafx.scene.image.Image;
import logic.utilities.imageIO.ImageFormatLoader;

import java.io.File;
import java.io.IOException;

public class CommonFormatLoader implements ImageFormatLoader {
    @Override
    public Image loadImage(File file) throws IOException {
        return new Image(file.toURI().toString());
    }

    @Override
    public Image loadImage(String filename) throws IOException {
        return loadImage(new File(filename));
    }
}
