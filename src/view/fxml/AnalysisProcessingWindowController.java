package view.fxml;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.imageProcessing.ImageAnalyzer;
import logic.imageProcessing.ImageProcessingUtils;
import view.fxml.interfaces.ProcessingSubWindow;

public class AnalysisProcessingWindowController implements ProcessingSubWindow {
    private static final String GREEN_AREA_CONTENT = "Procent terenów zielonych: %f";

    MainWindowController mainWindowController;
    Application application;
    Stage stage;

    private Image originalImage;
    private Image replacedImage;
    private ImageAnalyzer imageAnalyzer;

    @FXML
    Label greenAreaLabel;
    @FXML
    Button calculateGreenButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    @Override
    public void postInitialization(Image image) {
        originalImage = image;
        replacedImage = ImageProcessingUtils.copyImage(image);
        imageAnalyzer = new ImageAnalyzer(replacedImage);
        initializeListeners();
    }

    @FXML
    @Override
    public void initialize() {

    }

    @Override
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @Override
    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void initializeListeners() {
        calculateGreenButton.setOnAction(event -> analyzeGreenAreas());
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void analyzeGreenAreas() {
        ImageAnalyzer.PixelProperty searched = pixel -> {
            int red = (pixel & (0xff << 16)) >> 16;
            int green = (pixel & (0xff << 8)) >> 8;
            int blue = pixel & 0xff;
            return red < green && blue < green;
        };
        ImageAnalyzer.PixelProperty ignored = pixel -> pixel == 0xff0000ff;
        ImageAnalyzer.PixelReplacer replacer = originalPixel -> 0xffff0000;
        replacedImage = imageAnalyzer.getReplacedImage(searched, ignored, replacer);
        greenAreaLabel.setText(String.format(GREEN_AREA_CONTENT, (imageAnalyzer.percentOfImage(imageAnalyzer.getLastReplacedCount(), pixel -> pixel != 0xff0000ff))));
        imageAnalyzer.setCurrentImage(replacedImage);
        applyImageToMain();
    }

    private void applyImageToMain() {
        mainWindowController.setCurrentImage(replacedImage);
    }

    private void revertImage() {
        mainWindowController.setCurrentImage(originalImage);
    }
}
