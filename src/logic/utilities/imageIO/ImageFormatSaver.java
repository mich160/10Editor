package logic.utilities.imageIO;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;

public interface ImageFormatSaver {
    void saveImage(Image image, File file) throws IOException;

    void saveImage(Image image, String filename) throws IOException;
}
