package view.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import logic.imageProcessing.ImageProcessingUtils;
import logic.imageProcessing.ImageProcessor;
import view.customFXClasses.ErrorUtils;
import view.fxml.interfaces.ProcessingSubWindow;

import java.util.Optional;

public class BinarizationProcessingWindowController implements ProcessingSubWindow {
    MainWindowController mainWindowController;
    Stage stage;

    private Image originalImage;
    private WritableImage processedImage;
    private ImageProcessor imageProcessor;

    @FXML
    Button manualBinarizationButton;
    @FXML
    Button entropyBinarizationButton;
    @FXML
    Button percentBinarizationButton;
    @FXML
    Button minimumBinarizationButton;
    @FXML
    Button meanBinarizationButton;
    @FXML
    Button fuzzyBinarizationButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    @Override
    public void postInitialization(Image image) {
        originalImage = image;
        processedImage = ImageProcessingUtils.copyImage(image);
        imageProcessor = new ImageProcessor(processedImage);
        processedImage = imageProcessor.pointProcessing(originalValues -> {
            int value = (originalValues[ImageProcessor.RED] + originalValues[ImageProcessor.GREEN] + originalValues[ImageProcessor.BLUE]) / 3;
            return new int[]{value, value, value};
        });
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
        initializeListeners();
    }

    @Override
    @FXML
    public void initialize() {

    }

    private void initializeListeners() {
        manualBinarizationButton.setOnAction(event -> manualBinarization());
        entropyBinarizationButton.setOnAction(event -> entropyBinarization());
        percentBinarizationButton.setOnAction(event -> percentBinarization());
        minimumBinarizationButton.setOnAction(event -> minimumBinarization());
        meanBinarizationButton.setOnAction(event -> meanBinarization());
        fuzzyBinarizationButton.setOnAction(event -> fuzzyBinarization());
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void manualBinarization() {
        Optional<String> threshold = createTextInputDialog("Próg", "Ustawienie progu", "Wprowadź próg binaryzacji: ").showAndWait();
        if (threshold.isPresent()) {
            String thresholdStr = threshold.get();
            if (ErrorUtils.isValidChannelValue(thresholdStr)) {
                int[][] lut = imageProcessor.getCustomBinarizationLUT(Integer.parseInt(thresholdStr));
                binarize(lut);
            } else {
                ErrorUtils.viewChannelInvalidValueAlert();
            }
        }
    }

    private void entropyBinarization() {
        int[][] lut = imageProcessor.getEntropyBinarizationLUT();
        binarize(lut);
    }

    private void percentBinarization() {
        Optional<String> percent = createTextInputDialog("Próg procentowy", "Ustawienie progu", "Wprowadź procent binaryzacji: ").showAndWait();
        if (percent.isPresent()) {
            String percentStr = percent.get();
            if (ErrorUtils.isValidPercentValue(percentStr)) {
                int[][] lut = imageProcessor.getPercentBinarizationLUT(Double.parseDouble(percentStr) / 100.0);
                binarize(lut);
            } else {
                ErrorUtils.viewPercentInvalidValueAlert();
            }
        }
    }

    private void minimumBinarization() {
        int[][] lut = imageProcessor.getMinimumErrorBinarizationLUT();
        binarize(lut);
    }

    private void meanBinarization() {
        int[][] lut = imageProcessor.getIterativeBinarizationLUT();
        binarize(lut);
    }

    private void fuzzyBinarization() {
        int[][] lut = imageProcessor.getFuzzyMinimumErrorBinarizationLUT();
        binarize(lut);
    }

    private void applyImageToMain() {
        mainWindowController.setCurrentImage(processedImage);
    }

    private void revertImage() {
        mainWindowController.setCurrentImage(originalImage);
    }

    private void binarize(int[][] lut) {
        processedImage = imageProcessor.pointProcessing(lut[ImageProcessor.RED], lut[ImageProcessor.GREEN], lut[ImageProcessor.BLUE]);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private TextInputDialog createTextInputDialog(String title, String header, String content) {
        TextInputDialog result = new TextInputDialog();
        result.setTitle(title);
        result.setHeaderText(header);
        result.setHeaderText(content);
        return result;
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
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
