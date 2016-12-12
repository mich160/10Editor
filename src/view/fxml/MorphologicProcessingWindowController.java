package view.fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import logic.imageProcessing.ImageProcessingUtils;
import logic.imageProcessing.ImageProcessor;
import view.fxml.interfaces.ProcessingSubWindow;

public class MorphologicProcessingWindowController implements ProcessingSubWindow {
    MainWindowController mainWindowController;
    Stage stage;

    private Image originalImage;
    private WritableImage processedImage;
    private ImageProcessor imageProcessor;

    @FXML
    Button dilatationButton;
    @FXML
    Button erosionButton;
    @FXML
    Button openingButton;
    @FXML
    Button closingButton;
    @FXML
    Button thinningButton;
    @FXML
    Button broadingButton;
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
        int[][] lut = imageProcessor.getIterativeBinarizationLUT();
        processedImage = imageProcessor.pointProcessing(lut[ImageProcessor.RED], lut[ImageProcessor.GREEN], lut[ImageProcessor.BLUE]);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
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
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void initializeListeners() {
        dilatationButton.setOnAction(event -> dilateImage());
        erosionButton.setOnAction(event -> erodeImage());
        openingButton.setOnAction(event -> openImage());
        closingButton.setOnAction(event -> closeImage());
        thinningButton.setOnAction(event -> thinImage());
        broadingButton.setOnAction(event -> broadImage());
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void dilateImage() {
        ImageProcessor.MorphOperation dilatation = new ImageProcessor.Dilatation(processedImage);
        processedImage = imageProcessor.morphologicProcessing(dilatation);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void erodeImage() {
        ImageProcessor.MorphOperation erosion = new ImageProcessor.Erosion(processedImage);
        processedImage = imageProcessor.morphologicProcessing(erosion);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void openImage() {
        ImageProcessor.MorphOperation erosion = new ImageProcessor.Erosion(processedImage);
        processedImage = imageProcessor.morphologicProcessing(erosion);
        ImageProcessor.MorphOperation dilatation = new ImageProcessor.Dilatation(processedImage);
        processedImage = imageProcessor.morphologicProcessing(dilatation);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void closeImage() {
        ImageProcessor.MorphOperation dilatation = new ImageProcessor.Dilatation(processedImage);
        processedImage = imageProcessor.morphologicProcessing(dilatation);
        ImageProcessor.MorphOperation erosion = new ImageProcessor.Erosion(processedImage);
        processedImage = imageProcessor.morphologicProcessing(erosion);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void thinImage() {
        ImageProcessor.MorphOperation thinning = new ImageProcessor.Thinning(processedImage);
        processedImage = imageProcessor.morphologicProcessing(thinning);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void broadImage() {
        ImageProcessor.MorphOperation thickening = new ImageProcessor.Thickening(processedImage);
        for (int i = 0; i < thickening.getRotationsCount(); i++) {
            processedImage = imageProcessor.morphologicProcessing(thickening);
            imageProcessor.setBaseImage(processedImage);
            thickening.setImage(processedImage);
            thickening.rotateStructuringElement();
        }
        applyImageToMain();
    }

    private void applyImageToMain() {
        mainWindowController.setCurrentImage(processedImage);
    }

    private void revertImage() {
        mainWindowController.setCurrentImage(originalImage);
    }
}
