package logic.utilities.imageIO;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;

public interface ImageFormatLoader {
    Image loadImage(File file) throws IOException;

    Image loadImage(String filename) throws IOException;
}
