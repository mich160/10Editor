package view.fxml;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.imageProcessing.ImageHistogram;
import logic.imageProcessing.ImageProcessingUtils;
import logic.imageProcessing.ImageProcessor;
import view.fxml.interfaces.ProcessingSubWindow;

public class HistogramProcessingWindowController implements ProcessingSubWindow {
    MainWindowController mainWindowController;
    Application application;
    Stage stage;

    private Image originalImage;
    private WritableImage processedImage;
    private ImageProcessor imageProcessor;
    private GraphicsContext histogramCanvasContext;

    @FXML
    Canvas histogramCanvas;
    @FXML
    CheckBox redCheckBox;
    @FXML
    CheckBox greenCheckBox;
    @FXML
    CheckBox blueCheckBox;
    @FXML
    Button stretchHistogramButton;
    @FXML
    Button equalizeHistogramButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;


    @FXML
    public void initialize() {
        histogramCanvasContext = histogramCanvas.getGraphicsContext2D();
    }

    public void postInitialization(Image image) {
        originalImage = image;
        processedImage = ImageProcessingUtils.copyImage(originalImage);
        imageProcessor = new ImageProcessor(processedImage);
        drawHistogram();
        initializeListeners();
    }

    private void initializeListeners() {
        redCheckBox.setOnAction(event -> drawHistogram());
        greenCheckBox.setOnAction(event -> drawHistogram());
        blueCheckBox.setOnAction(event -> drawHistogram());
        stretchHistogramButton.setOnAction(event -> {
            stretchHistogram();
            drawHistogram();
        });
        equalizeHistogramButton.setOnAction(event -> {
            equalizeHistogram();
            drawHistogram();
        });
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void drawHistogram() {
        ImageHistogram histogram = imageProcessor.getImageHistogram();
        long maxValue = histogram.getHistogramMaxValue();
        int canvasLRPadding = (int) (0.05 * histogramCanvas.getWidth());
        int canvasTBPadding = (int) (0.05 * histogramCanvas.getHeight());
        int oY = (int) (histogramCanvas.getHeight() - canvasTBPadding);
        double valueA = ((double) (canvasTBPadding - oY)) / ((double) maxValue);
        double argumentA = (histogramCanvas.getWidth() - 2 * canvasLRPadding) / 255.0;
        drawAxes(histogram, maxValue, canvasLRPadding, canvasTBPadding, canvasLRPadding, oY);
        if (redCheckBox.isSelected()) {
            drawRedHistogram(histogram, oY, valueA, (double) oY, argumentA, (double) canvasLRPadding);
        }
        if (greenCheckBox.isSelected()) {
            drawGreenHistogram(histogram, oY, valueA, (double) oY, argumentA, (double) canvasLRPadding);
        }
        if (blueCheckBox.isSelected()) {
            drawBlueHistogram(histogram, oY, valueA, (double) oY, argumentA, (double) canvasLRPadding);
        }
    }

    private void drawAxes(ImageHistogram histogram, long maxValue, int lrPadding, int tbPadding, int oX, int oY) {
        histogramCanvasContext.clearRect(0, 0, histogramCanvas.getWidth(), histogramCanvas.getHeight());
        histogramCanvasContext.setStroke(Color.BLACK);
        histogramCanvasContext.setLineWidth(1.0);
        histogramCanvasContext.setFill(Color.BLACK);
        histogramCanvasContext.setFont(Font.font(13));
        histogramCanvasContext.strokeLine(lrPadding - 0.5, tbPadding - 0.5, oX - 0.5, oY - 0.5);
        histogramCanvasContext.strokeLine(oX - 0.5, oY - 0.5, histogramCanvas.getWidth() - lrPadding - 0.5, oY - 0.5);
        histogramCanvasContext.fillText(Long.toString(maxValue), lrPadding, tbPadding);
        histogramCanvasContext.fillText(Integer.toString(0), oX, oY + tbPadding * 0.8);
        histogramCanvasContext.fillText(Integer.toString(255), histogramCanvas.getWidth() - 2 * lrPadding, oY + tbPadding * 0.9);
    }

    private void drawRedHistogram(ImageHistogram histogram, int oY, double valueA, double valueB, double argumentA, double argumentB) {
        histogramCanvasContext.setStroke(Color.RED);
        for (int i = 0; i < 256; i++) {
            double beginX = argumentA * i + argumentB - 0.5;
            double beginY = oY - 0.5;
            double endY = valueA * histogram.getRedHistogram()[i] + valueB - 0.5;
            histogramCanvasContext.strokeLine(beginX, beginY, beginX, endY);
        }
    }

    private void drawGreenHistogram(ImageHistogram histogram, int oY, double valueA, double valueB, double argumentA, double argumentB) {
        histogramCanvasContext.setStroke(Color.GREEN);
        for (int i = 0; i < 256; i++) {
            double beginX = argumentA * i + argumentB - 0.5;
            double beginY = oY - 0.5;
            double endY = valueA * histogram.getGreenHistogram()[i] + valueB - 0.5;
            histogramCanvasContext.strokeLine(beginX, beginY, beginX, endY);
        }
    }

    private void drawBlueHistogram(ImageHistogram histogram, int oY, double valueA, double valueB, double argumentA, double argumentB) {
        histogramCanvasContext.setStroke(Color.BLUE);
        for (int i = 0; i < 256; i++) {
            double beginX = argumentA * i + argumentB - 0.5;
            double beginY = oY - 0.5;
            double endY = valueA * histogram.getBlueHistogram()[i] + valueB - 0.5;
            histogramCanvasContext.strokeLine(beginX, beginY, beginX, endY);
        }
    }

    private void stretchHistogram() {
        int[][] luts = imageProcessor.histogramLinearStretchLUT();
        processedImage = imageProcessor.pointProcessing(luts[ImageProcessor.RED], luts[ImageProcessor.GREEN], luts[ImageProcessor.BLUE]);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void equalizeHistogram() {
        int[][] luts = imageProcessor.histogramEqualizeLUT();
        processedImage = imageProcessor.pointProcessing(luts[ImageProcessor.RED], luts[ImageProcessor.GREEN], luts[ImageProcessor.BLUE]);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void applyImageToMain() {
        mainWindowController.setCurrentImage(processedImage);
    }

    private void revertImage() {
        mainWindowController.setCurrentImage(originalImage);
    }

    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
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

    public WritableImage getProcessedImage() {
        return processedImage;
    }
}
