package view.fxml;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import logic.imageProcessing.ImageProcessor;
import view.fxml.interfaces.ProcessingSubWindow;

import java.util.function.UnaryOperator;

import static logic.imageProcessing.ImageProcessingUtils.copyImage;
import static view.customFXClasses.ErrorUtils.viewChannelInvalidValueAlert;

public class PointProcessingWindowController implements ProcessingSubWindow {
    private MainWindowController mainWindowController;
    private Application application;
    private Stage stage;

    private Image originalImage;
    private WritableImage processedImage;
    private ImageProcessor imageProcessor;
    private boolean brightnessAffected;
    private UnaryOperator<Integer> brightness;

    @FXML
    private CheckBox redCheckBox;
    @FXML
    private CheckBox greenCheckBox;
    @FXML
    private CheckBox blueCheckBox;
    @FXML
    private TextField valueTextField;
    @FXML
    private Button addButton;
    @FXML
    private Button subtractButton;
    @FXML
    private Button multiplyButton;
    @FXML
    private Button divideButton;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private Button grayscaleAvgButton;
    @FXML
    private Button grayscaleWeightedAvgButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        brightnessAffected = false;
    }

    private void initializeListeners() {
        addButton.setOnAction(event -> processAdd());
        subtractButton.setOnAction(event -> processSubtract());
        multiplyButton.setOnAction(event -> processMultiply());
        divideButton.setOnAction(event -> processDivide());
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            processSetBrightness(newValue);
        });
        grayscaleAvgButton.setOnAction(event -> processGrayscaleAvg());
        grayscaleWeightedAvgButton.setOnAction(event -> processGrayscaleWeightedAvg());
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void processAdd() {
        if (isValueANumber()) {
            int[] rLUT, gLUT, bLUT;
            UnaryOperator<Integer> addition = integer -> integer + Integer.parseInt(valueTextField.getText());
            if (redCheckBox.isSelected()) {
                rLUT = ImageProcessor.buildLUT(addition);
            } else {
                rLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (greenCheckBox.isSelected()) {
                gLUT = ImageProcessor.buildLUT(addition);
            } else {
                gLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (blueCheckBox.isSelected()) {
                bLUT = ImageProcessor.buildLUT(addition);
            } else {
                bLUT = ImageProcessor.IDENTITY_LUT;
            }
            processedImage = imageProcessor.pointProcessing(rLUT, gLUT, bLUT);
            imageProcessor.setBaseImage(processedImage);
            applyImageToMain();
        } else {
            viewChannelInvalidValueAlert();
        }
    }

    private void processSubtract() {
        if (isValueANumber()) {
            int[] rLUT, gLUT, bLUT;
            UnaryOperator<Integer> subtraction = integer -> integer - Integer.parseInt(valueTextField.getText());
            if (redCheckBox.isSelected()) {
                rLUT = ImageProcessor.buildLUT(subtraction);
            } else {
                rLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (greenCheckBox.isSelected()) {
                gLUT = ImageProcessor.buildLUT(subtraction);
            } else {
                gLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (blueCheckBox.isSelected()) {
                bLUT = ImageProcessor.buildLUT(subtraction);
            } else {
                bLUT = ImageProcessor.IDENTITY_LUT;
            }
            processedImage = imageProcessor.pointProcessing(rLUT, gLUT, bLUT);
            imageProcessor.setBaseImage(processedImage);
            applyImageToMain();
        } else {
            viewChannelInvalidValueAlert();
        }
    }

    private void processMultiply() {
        if (isValueANumber()) {
            int[] rLUT, gLUT, bLUT;
            UnaryOperator<Double> multiplication = aDouble -> aDouble * Double.parseDouble(valueTextField.getText());
            if (redCheckBox.isSelected()) {
                rLUT = ImageProcessor.buildLUTDoubleOp(multiplication);
            } else {
                rLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (greenCheckBox.isSelected()) {
                gLUT = ImageProcessor.buildLUTDoubleOp(multiplication);
            } else {
                gLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (blueCheckBox.isSelected()) {
                bLUT = ImageProcessor.buildLUTDoubleOp(multiplication);
            } else {
                bLUT = ImageProcessor.IDENTITY_LUT;
            }
            processedImage = imageProcessor.pointProcessing(rLUT, gLUT, bLUT);
            imageProcessor.setBaseImage(processedImage);
            applyImageToMain();
        } else {
            viewChannelInvalidValueAlert();
        }
    }

    private void processDivide() {
        if (!isValueANumber()) {
            viewChannelInvalidValueAlert();
        } else if (Double.compare(Double.parseDouble(valueTextField.getText()), 0.0) == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Zła wartość liczbowa");
            alert.setContentText("Wartość nie może być zerem.");
            alert.show();
        } else {
            int[] rLUT, gLUT, bLUT;
            UnaryOperator<Double> division = aDouble -> aDouble / Double.parseDouble(valueTextField.getText());
            if (redCheckBox.isSelected()) {
                rLUT = ImageProcessor.buildLUTDoubleOp(division);
            } else {
                rLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (greenCheckBox.isSelected()) {
                gLUT = ImageProcessor.buildLUTDoubleOp(division);
            } else {
                gLUT = ImageProcessor.IDENTITY_LUT;
            }
            if (blueCheckBox.isSelected()) {
                bLUT = ImageProcessor.buildLUTDoubleOp(division);
            } else {
                bLUT = ImageProcessor.IDENTITY_LUT;
            }
            processedImage = imageProcessor.pointProcessing(rLUT, gLUT, bLUT);
            imageProcessor.setBaseImage(processedImage);
            applyImageToMain();
        }
    }

    private void processSetBrightness(Number newValue) {
        brightnessAffected = true;
        brightness = integer -> (int) (integer + (255.0 / 50.0 * newValue.doubleValue() - 255.0));
        applyImageToMain();
    }

    private void processGrayscaleAvg() {
        processedImage = imageProcessor.pointProcessing(originalValues -> {
            int average = (int) (((double) originalValues[ImageProcessor.RED] + (double) originalValues[ImageProcessor.GREEN] + (double) originalValues[ImageProcessor.BLUE]) / 3.0);
            return new int[]{average, average, average};
        });
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void processGrayscaleWeightedAvg() {
        processedImage = imageProcessor.pointProcessing(originalValues -> {
            int average = (int) (0.21 * (double) originalValues[ImageProcessor.RED] + 0.71 * (double) originalValues[ImageProcessor.GREEN] + 0.07 * (double) originalValues[ImageProcessor.BLUE]);
            return new int[]{average, average, average};
        });
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void applyImageToMain() {
        if (brightnessAffected) {
            int[] lut = ImageProcessor.buildLUT(brightness);
            mainWindowController.setCurrentImage(imageProcessor.pointProcessing(lut, lut, lut));
        } else {
            mainWindowController.setCurrentImage(processedImage);
        }
    }

    private void revertImage() {
        mainWindowController.setCurrentImage(originalImage);
    }

    private boolean isValueANumber() {
        try {
            Double.parseDouble(valueTextField.getText());
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public void postInitialization(Image mainImage) {
        originalImage = mainImage;
        processedImage = copyImage(originalImage);
        imageProcessor = new ImageProcessor(processedImage);
        initializeListeners();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }


    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Image getOriginalImage() {
        return originalImage;
    }
}
