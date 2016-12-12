package view.fxml;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import view.fxml.interfaces.SubWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ColorWindowController implements SubWindow {
    private interface ColorParser<T> {
        T parseColor(TextField colorTextField);
    }

    private class RGBChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!rgbLock.get()) {
                recalculateValues(false);
                paintColor();
            }
        }
    }

    private class CMYKChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!cmykLock.get()) {
                recalculateValues(true);
                paintColor();
            }
        }
    }

    private class RGBFocusListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                rgbLock.set(false);
                cmykLock.set(true);
            } else {
                rgbLock.set(true);
                cmykLock.set(true);
            }
        }
    }

    private class CMYKFocusListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                rgbLock.set(true);
                cmykLock.set(false);
            } else {
                rgbLock.set(true);
                cmykLock.set(true);
            }
        }
    }

    private final int RED = 0, GREEN = 1, BLUE = 2;
    private final int CYAN = 0, MAGENTA = 1, YELLOW = 2, BLACK = 3;
    private AtomicBoolean rgbLock;
    private AtomicBoolean cmykLock;
    private Color color;
    private Stage stage;
    private MainWindowController mainWindowController;
    private GraphicsContext canvasContext;

    @FXML
    Button cubeButton;
    @FXML
    Button coneButton;
    @FXML
    TextField redTextField;
    @FXML
    TextField greenTextField;
    @FXML
    TextField blueTextField;
    @FXML
    TextField cyanTextField;
    @FXML
    TextField magentaTextField;
    @FXML
    TextField yellowTextField;
    @FXML
    TextField blackTextField;
    @FXML
    Canvas colorCanvas;

    @FXML
    public void initialize() {
        color = Color.BLACK;
        rgbLock = new AtomicBoolean(true);
        cmykLock = new AtomicBoolean(true);
        canvasContext = colorCanvas.getGraphicsContext2D();
        color = Color.BLACK;
        paintColor();
        initializeListeners();
    }

    private void recalculateValues(boolean toRGB) {
        int[] rgb = null;
        double[] cmyk = null;
        if (toRGB) {
            ColorParser<Double> parseCMYK = (TextField textfield) -> {
                try {
                    double color = Double.parseDouble(textfield.getText());
                    if (color < 0.0 || color > 1.0) {
                        throw new NumberFormatException();
                    }
                    return color;
                } catch (NumberFormatException ex) {
                    return 0.0;
                }
            };
            cmyk = new double[]{parseCMYK.parseColor(cyanTextField), parseCMYK.parseColor(magentaTextField), parseCMYK.parseColor(yellowTextField), parseCMYK.parseColor(blackTextField)};
            rgb = getRGB(cmyk);
            redTextField.setText(Integer.toString(rgb[RED]));
            greenTextField.setText(Integer.toString(rgb[GREEN]));
            blueTextField.setText(Integer.toString(rgb[BLUE]));
        } else {
            ColorParser<Integer> parseRGB = (TextField textfield) -> {
                try {
                    int color = Integer.parseInt(textfield.getText());
                    if (color < 0 || color > 255) {
                        throw new NumberFormatException();
                    }
                    return color;
                } catch (NumberFormatException ex) {
                    return 0;
                }
            };
            rgb = new int[]{parseRGB.parseColor(redTextField), parseRGB.parseColor(greenTextField), parseRGB.parseColor(blueTextField)};
            cmyk = getCMYK(rgb);
            cyanTextField.setText(Double.toString(cmyk[CYAN]));
            magentaTextField.setText(Double.toString(cmyk[MAGENTA]));
            yellowTextField.setText(Double.toString(cmyk[YELLOW]));
            blackTextField.setText(Double.toString(cmyk[BLACK]));
        }
        color = Color.rgb(rgb[RED], rgb[GREEN], rgb[BLUE]);
        mainWindowController.setCurrentColor(color);
    }

    private void paintColor() {
        canvasContext.clearRect(0.0, 0.0, colorCanvas.getWidth(), colorCanvas.getHeight());
        canvasContext.setFill(color);
        canvasContext.fillRect(0.0, 0.0, colorCanvas.getWidth(), colorCanvas.getHeight());
    }

    private double[] getCMYK(int[] rgb) {
        double redScaled = rgb[RED] / 255.0;
        double greenScaled = rgb[GREEN] / 255.0;
        double blueScaled = rgb[BLUE] / 255.0;
        double black = Math.min(Math.min(1 - redScaled, 1 - greenScaled), 1 - blueScaled);
        if (Double.compare(black, 1) != 0) {
            double cyan = (1 - redScaled - black) / (1 - black);
            double magenta = (1 - greenScaled - black) / (1 - black);
            double yellow = (1 - blueScaled - black) / (1 - black);
            return new double[]{cyan, magenta, yellow, black};
        }
        return new double[]{0.0, 0.0, 0.0, black};
    }

    private int[] getRGB(double[] cmyk) {
        double red = 1 - Math.min(1, cmyk[CYAN] * (1 - cmyk[BLACK]) + cmyk[BLACK]);
        double green = 1 - Math.min(1, cmyk[MAGENTA] * (1 - cmyk[BLACK]) + cmyk[BLACK]);
        double blue = 1 - Math.min(1, cmyk[YELLOW] * (1 - cmyk[BLACK]) + cmyk[BLACK]);
        return new int[]{(int) (red * 255.0), (int) (green * 255.0), (int) (blue * 255.0)};
    }

    private void initializeListeners() {
        redTextField.textProperty().addListener(new RGBChangeListener());
        greenTextField.textProperty().addListener(new RGBChangeListener());
        blueTextField.textProperty().addListener(new RGBChangeListener());
        cyanTextField.textProperty().addListener(new CMYKChangeListener());
        magentaTextField.textProperty().addListener(new CMYKChangeListener());
        yellowTextField.textProperty().addListener(new CMYKChangeListener());
        blackTextField.textProperty().addListener(new CMYKChangeListener());

        redTextField.focusedProperty().addListener(new RGBFocusListener());
        greenTextField.focusedProperty().addListener(new RGBFocusListener());
        blueTextField.focusedProperty().addListener(new RGBFocusListener());
        cyanTextField.focusedProperty().addListener(new CMYKFocusListener());
        magentaTextField.focusedProperty().addListener(new CMYKFocusListener());
        yellowTextField.focusedProperty().addListener(new CMYKFocusListener());
        blackTextField.focusedProperty().addListener(new CMYKFocusListener());

        coneButton.setOnAction(event -> createConeWindow());
        cubeButton.setOnAction(event -> createBoxWindow());
    }

    private Stage createConeWindow() {
        Stage coneStage = new Stage();
        coneStage.setTitle("HSV Cone");
        coneStage.setMinWidth(300);
        coneStage.setMinHeight(500);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("HSVConeWindow.fxml"));
        try {
            coneStage.setScene(new Scene(fxmlLoader.load()));
            coneStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HSVConeWindowController hsvConeWindowController = fxmlLoader.getController();
        hsvConeWindowController.setStage(coneStage);
        hsvConeWindowController.initializeListeners();
        return coneStage;
    }

    private Stage createBoxWindow() {
        Stage boxStage = new Stage();
        boxStage.setTitle("RGB Cube");
        boxStage.setMinHeight(500);
        boxStage.setMaxHeight(500);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("CubeWindow.fxml"));
        try {
            boxStage.setScene(new Scene(fxmlLoader.load()));
            boxStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CubeWindowController cubeWindowController = fxmlLoader.getController();
        cubeWindowController.setStage(boxStage);
        cubeWindowController.postInitializationRoutine();
        return boxStage;
    }


    @Override
    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    @Override
    public void setMainWindowController(MainWindowController controller) {
        this.mainWindowController = controller;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void postInitialization() {
    }
}
