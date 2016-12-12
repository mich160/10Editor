package view.fxml;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import logic.imageProcessing.ImageProcessingUtils;
import logic.imageProcessing.ImageProcessor;
import view.fxml.interfaces.ProcessingSubWindow;

public class FilterProcessingWindowController implements ProcessingSubWindow {
    MainWindowController mainWindowController;
    Application application;
    Stage stage;

    private Image originalImage;
    private WritableImage processedImage;
    private ImageProcessor imageProcessor;

    @FXML
    Button smoothenButton;
    @FXML
    Button medianButton;
    @FXML
    Button sobelButton;
    @FXML
    Button sharpenButton;
    @FXML
    Button blurButton;
    @FXML
    Button applyCustomFilterButton;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;
    @FXML
    TextArea customFilterTextArea;

    @FXML
    public void initialize() {
        customFilterTextArea.setTooltip(new Tooltip("Macierz liczb w formacie: a,b,c;d,e,f;g,h,j;"));
    }

    public void postInitialization(Image originalImage) {
        this.originalImage = originalImage;
        this.processedImage = ImageProcessingUtils.copyImage(originalImage);
        imageProcessor = new ImageProcessor(processedImage);
        initializeListeners();
    }

    private void initializeListeners() {
        smoothenButton.setOnAction(event -> smoothenImage());
        medianButton.setOnAction(event -> medianSmoothenImage());
        sobelButton.setOnAction(event -> sobelImage());
        sharpenButton.setOnAction(event -> sharpenImage());
        blurButton.setOnAction(event -> blurImage());
        applyCustomFilterButton.setOnAction(event -> customImage());
        okButton.setOnAction(event -> {
            applyImageToMain();
            stage.close();
        });
        cancelButton.setOnAction(event -> {
            revertImage();
            stage.close();
        });
    }

    private void smoothenImage() {
        processedImage = imageProcessor.convolutionProcessing(ImageProcessor.AVERAGE_FILTER);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void medianSmoothenImage() {
        processedImage = imageProcessor.convolutionProcessing(ImageProcessor.MEDIAN_FILTER_3X3);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void sobelImage() {
        processedImage = imageProcessor.convolutionProcessing(ImageProcessor.SOBEL_FILTER);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void sharpenImage() {
        processedImage = imageProcessor.convolutionProcessing(ImageProcessor.HIGHPASS_FILTER);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();

    }

    private void blurImage() {
        processedImage = imageProcessor.convolutionProcessing(ImageProcessor.GAUSSIAN_FILTER);
        imageProcessor.setBaseImage(processedImage);
        applyImageToMain();
    }

    private void customImage() {
        try {
            processedImage = imageProcessor.convolutionProcessing(new ImageProcessor.ConvolutionFilter(parseCustomFilterArea()));
            imageProcessor.setBaseImage(processedImage);
            applyImageToMain();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nieprawidłowa macierz");
            alert.setContentText("Macierz powinna być macierzą kwadratową z nieparzystą długością boku");
            alert.show();
        }
    }

    private double[][] parseCustomFilterArea() throws Exception {
        String[] values = customFilterTextArea.getText().split(";|,");
        int size = (int) Math.ceil(Math.sqrt(values.length));
        int currentValue = 0;
        double[][] result = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                try {
                    result[row][column] = Double.parseDouble(values[currentValue++]);
                } catch (NumberFormatException ex) {
                    throw new Exception(ex);
                }
            }
        }
        return result;
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
}
