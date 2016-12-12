package logic.utilities.imageIO;

import java.io.File;

public class IOUtils {
    public static String getExtension(File file) {
        String filename = file.getName();
        return (filename.substring(filename.lastIndexOf('.') + 1));
    }
}
