package logic.utilities.imageIO.LowAPI;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import logic.utilities.imageIO.ImageFormatLoader;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PPMLoader implements ImageFormatLoader {
    @Override
    public Image loadImage(File file) throws IOException {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            Image result = loadImage(inputStream);
            inputStream.close();
            return result;
        } catch (NotPPMFileException | CorruptedPPMFileException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public Image loadImage(String filename) throws IOException {
        return loadImage(new File(filename));
    }

    public class NotPPMFileException extends Exception {
        public NotPPMFileException(String message) {
            super(message);
        }
    }

    public class CorruptedPPMFileException extends Exception {
        public CorruptedPPMFileException(String message) {
            super(message);
        }
    }

    private enum State {
        width,
        height,
        maxValue,
        data
    }

    private static Set<Character> whiteChars = new HashSet<>();
    private static Set<Character> digits = new HashSet<>();
    private static String P6 = "P6", P3 = "P3";

    static {
        whiteChars.add((char) 10);
        whiteChars.add((char) 13);
        whiteChars.add((char) 9);
        whiteChars.add((char) 32);

        digits.add('0');
        digits.add('1');
        digits.add('2');
        digits.add('3');
        digits.add('4');
        digits.add('5');
        digits.add('6');
        digits.add('7');
        digits.add('8');
        digits.add('9');
    }

    public PPMLoader() {

    }

    public Image loadImage(InputStream stream) throws IOException, NotPPMFileException, CorruptedPPMFileException {
        WritableImage result = null;
        PixelWriter resultWriter = null;
        State currentState = State.width;
        StringBuilder buffer = new StringBuilder();
        String type = readString(stream);
        int width = 0;
        int height = 0;
        int maxValue = 255;
        boolean parsingInt = false;
        if (!type.equals(P3) && !type.equals(P6)) {
            throw new NotPPMFileException("This is not a PPM file!");
        }
        while (stream.available() > 0) {
            if (currentState == State.data) {
                break;
            }
            int current = stream.read();
            if (current == '#') {
                readLine(stream);
            } else if (digits.contains((char) current)) {
                if (!parsingInt) {
                    parsingInt = true;
                }
                buffer.append((char) current);
            } else if (whiteChars.contains((char) current)) {
                if (parsingInt) {
                    parsingInt = false;
                    if (currentState == State.height) {
                        height = Integer.parseInt(buffer.toString());
                        result = new WritableImage(width, height);
                        resultWriter = result.getPixelWriter();
                        currentState = State.maxValue;
                    } else if (currentState == State.width) {
                        width = Integer.parseInt(buffer.toString());
                        currentState = State.height;
                    } else if (currentState == State.maxValue) {
                        maxValue = Integer.parseInt(buffer.toString());
                        currentState = State.data;
                    }
                    buffer.delete(0, buffer.length());
                }
            }
        }
        if (type.equals(P3)) {
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    if (stream.available() == 0) {
                        throw new CorruptedPPMFileException("File is corrupted!");
                    }
                    int red = scaleTo8Bit(readInt(stream), maxValue);
                    int green = scaleTo8Bit(readInt(stream), maxValue);
                    int blue = scaleTo8Bit(readInt(stream), maxValue);
                    resultWriter.setArgb(column, row, getRGB(red, green, blue));
                }
            }
        } else {
            boolean extended = maxValue >= 256;
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    if (stream.available() == 0) {
                        throw new CorruptedPPMFileException("File is corrupted!");
                    }
                    if (!extended) {
                        int red = scaleTo8Bit(stream.read(), maxValue);
                        int green = scaleTo8Bit(stream.read(), maxValue);
                        int blue = scaleTo8Bit(stream.read(), maxValue);
                        resultWriter.setArgb(column, row, getRGB(red, green, blue));
                    } else {
                        int hRed = stream.read();
                        int lRed = stream.read();
                        int hGreen = stream.read();
                        int lGreen = stream.read();
                        int hBlue = stream.read();
                        int lBlue = stream.read();
                        int red = (hRed << 8) | lRed;
                        int green = (hGreen << 8) | lGreen;
                        int blue = (hBlue << 8) | lBlue;
                        resultWriter.setArgb(column, row, getRGB(red, green, blue));
                    }
                }
            }
        }
        return result;
    }

    private int[] readLine(InputStream stream) throws IOException {
        LinkedList<Integer> buffer = new LinkedList<>();
        char current;
        while (stream.available() > 0) {
            current = (char) stream.read();
            if (current != 10 && current != 13) {
                buffer.add((int) current);
            }
            if (current == 10) {
                break;
            }
        }
        return bufferToArray(buffer);
    }

    private int readInt(InputStream stream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char current;
        boolean intEncountered = false;
        while (stream.available() > 0) {
            current = (char) stream.read();
            if (current == '#') {
                readLine(stream);
            } else if (!intEncountered && digits.contains(current)) {
                intEncountered = true;
                buffer.append(current);
            } else if (intEncountered) {
                if (digits.contains(current)) {
                    buffer.append(current);
                } else {
                    break;
                }
            }
        }
        return Integer.parseInt(buffer.toString());
    }

    private String readString(InputStream stream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        char current;
        boolean stringEncountered = false;
        while (stream.available() > 0) {
            current = (char) stream.read();
            if (!stringEncountered && !whiteChars.contains(current)) {
                stringEncountered = true;
                buffer.append(current);
            } else if (stringEncountered) {
                if (!whiteChars.contains(current)) {
                    buffer.append(current);
                } else {
                    break;
                }
            }
        }
        return buffer.toString();
    }

    private int[] bufferToArray(List<Integer> buffer) {
        int[] result = new int[buffer.size()];
        int index = 0;
        for (int i : buffer) {
            result[index++] = i;
        }
        return result;
    }

    private int scaleTo8Bit(int value, int maxValue) {
        if (maxValue != 255) {
            return (int) ((double) value / (double) maxValue * 255.0);
        }
        return value;
    }

    private int getARGB(int alpha, int red, int green, int blue) {
        return ((0xff) << 24 | (red << 16) | (green << 8) | blue);
    }

    private int getRGB(int red, int green, int blue) {
        return getARGB(0xff, red, green, blue);
    }
}
