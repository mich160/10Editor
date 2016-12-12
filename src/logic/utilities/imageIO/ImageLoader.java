package logic.utilities.imageIO;

import javafx.scene.image.Image;
import logic.utilities.imageIO.LowAPI.CommonFormatLoader;
import logic.utilities.imageIO.LowAPI.PPMLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader implements ImageFormatLoader {
    private final static Map<String, ImageFormatLoader> imageLoaders = new HashMap<>();

    static {
        imageLoaders.put("ppm", new PPMLoader());
        ImageFormatLoader commonLoader = new CommonFormatLoader();
        imageLoaders.put("jpg", commonLoader);
        imageLoaders.put("png", commonLoader);
        imageLoaders.put("bmp", commonLoader);
    }

    public Image loadImage(File image) throws IOException {
        if (!imageLoaders.containsKey(IOUtils.getExtension(image))) {
            throw new IOException("Unrecognized extension!");
        }
        return imageLoaders.get(IOUtils.getExtension(image)).loadImage(image);
    }

    @Override
    public Image loadImage(String filename) throws IOException {
        return loadImage(new File(filename));
    }

    public String[] getValidExtensions() {
        return (String[]) imageLoaders.keySet().stream().toArray();
    }

}
