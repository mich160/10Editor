package view.fxml;

import com.sun.istack.internal.Nullable;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.utilities.imageIO.ImageLoader;
import logic.utilities.imageIO.ImageSaver;
import view.customFXClasses.ErrorUtils;
import view.customFXClasses.ShapesCanvas;
import view.fxml.interfaces.ProcessingSubWindow;
import view.fxml.interfaces.SubWindow;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainWindowController {
    private class CommandInterpreter {
        static final String LINE_CMD = "Line", RECTANGLE_CMD = "Rect", CIRCLE_CMD = "Circle";

        private double[] getParameters(String args) {
            String[] paramsToParse = args.split(",");
            double[] params = new double[paramsToParse.length];
            for (int i = 0; i < paramsToParse.length; i++) {
                params[i] = Double.parseDouble(paramsToParse[i]);
            }
            return params;
        }

        void executeCommand(String command) {
            int bracketIndex = command.indexOf('(');
            String name = command.substring(0, command.indexOf('('));
            String args = command.substring(bracketIndex + 1, command.length() - 1);
            double[] params = null;
            try {
                params = getParameters(args);
            } catch (NumberFormatException ex) {
                ErrorUtils.viewWrongCommandAlert();
            }
            switch (name) {
                case LINE_CMD:
                    canvas.addLine(params[0], params[1], params[2], params[3]);
                    break;
                case RECTANGLE_CMD:
                    canvas.addRectangle(params[0], params[1], params[2], params[3]);
                    break;
                case CIRCLE_CMD:
                    canvas.addCircle(params[0], params[1], params[3]);
                    break;
                default:
                    ErrorUtils.viewWrongCommandAlert();
                    break;
            }
        }
    }

    private Application application;
    private Stage mainStage;
    private ShapesCanvas canvas;
    private GraphicsContext colorCanvasContext;

    private ImageLoader imageLoader;
    private ImageSaver imageSaver;
    private FileChooser openFileChooser;
    private FileChooser saveFileChooser;
    private CommandInterpreter commandInterpreter;

    private Stage colorStage;
    private Stage pointProcessingStage;
    private Stage filterProcessingStage;
    private Stage histogramProcessingStage;
    private Stage binarizationProcessingStage;
    private Stage morphologicProcessingStage;
    private Stage analysisProcessingStage;
    private Stage editShapeStage;

    @FXML
    private Canvas colorCanvas;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem pointMenuItem;
    @FXML
    private MenuItem filterMenuItem;
    @FXML
    private MenuItem histogramMenuItem;
    @FXML
    private MenuItem binarizationMenuItem;
    @FXML
    private MenuItem morphologicMenuItem;
    @FXML
    private MenuItem analysisMenuItem;
    @FXML
    private AnchorPane canvasPane;
    @FXML
    private Button lineButton;
    @FXML
    private Button circleButton;
    @FXML
    private Button rectangleButton;
    @FXML
    private Button bezierButton;
    @FXML
    private Button commandButton;
    @FXML
    private Button editButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button selectButton;

    @FXML
    public void initialize() {
        imageLoader = new ImageLoader();
        imageSaver = new ImageSaver();
        commandInterpreter = new CommandInterpreter();
        canvas = new ShapesCanvas(canvasPane);
        canvas.setDrawnShapeType(ShapesCanvas.DrawnShape.LINE);
        canvasPane.getChildren().add(canvas);
        colorCanvasContext = colorCanvas.getGraphicsContext2D();
        updateColorCanvas(canvas.getCurrentColor());
    }

    public void postInitialization() {
        initializeListeners();
    }

    private void initializeListeners() {
        lineButton.setOnAction(event -> {
            canvas.setDrawnShapeType(ShapesCanvas.DrawnShape.LINE);
            canvas.setMode(ShapesCanvas.Mode.DRAW);
        });
        rectangleButton.setOnAction(event -> {
            canvas.setDrawnShapeType(ShapesCanvas.DrawnShape.RECTANGLE);
            canvas.setMode(ShapesCanvas.Mode.DRAW);
        });
        circleButton.setOnAction(event -> {
            canvas.setDrawnShapeType(ShapesCanvas.DrawnShape.CIRCLE);
            canvas.setMode(ShapesCanvas.Mode.DRAW);
        });
        bezierButton.setOnAction(event -> {
            canvas.setDrawnShapeType(ShapesCanvas.DrawnShape.BEZIER);
            canvas.setMode(ShapesCanvas.Mode.DRAW);
        });
        selectButton.setOnAction(event -> canvas.setMode(ShapesCanvas.Mode.EDIT));
        editButton.setOnAction(event -> {
            editShapeStage = createSubWindow(editShapeStage, "EditShapeWindow.fxml", "Edytuj kształt");
            editShapeStage.setOnCloseRequest(request -> disposeEditWindow());
            canvas.setMode(ShapesCanvas.Mode.EDIT);
        });
        commandButton.setOnAction(event -> showCommandInput());
        clearButton.setOnAction(event -> canvas.clear());
        colorCanvas.setOnMouseClicked(event -> {
            if (colorStage != null) {
                colorStage.close();
            }
            colorStage = createSubWindow(colorStage, "ColorWindow.fxml", "Kolor");
            colorStage.setOnCloseRequest(request -> disposeColorWindow());
        });
        mainStage.setOnCloseRequest(event -> closeAllSubWindows());
        openMenuItem.setOnAction(event -> loadUserImage());
        saveMenuItem.setOnAction(event -> saveCurrentImage());
        pointMenuItem.setOnAction(event -> {
            if (pointProcessingStage != null) {
                disposePointProcessingWindow();
            }
            pointProcessingStage = createProcessingSubWindow(pointProcessingStage, "PointProcessingWindow.fxml", "Przetwarzanie punktowe");
            pointProcessingStage.setOnCloseRequest(event1 -> disposePointProcessingWindow());
        });
        filterMenuItem.setOnAction(event -> {
            if (filterProcessingStage != null) {
                disposeFilterProcessingWindow();
            }
            filterProcessingStage = createProcessingSubWindow(filterProcessingStage, "FilterProcessingWindow.fxml", "Przetwarzanie macierzowe");
            filterProcessingStage.setOnCloseRequest(event1 -> disposeFilterProcessingWindow());
        });
        histogramMenuItem.setOnAction(event -> {
            if (histogramProcessingStage != null) {
                disposeHistogramProcessingWindow();
            }
            histogramProcessingStage = createProcessingSubWindow(histogramProcessingStage, "HistogramProcessingWindow.fxml", "Histogram");
            histogramProcessingStage.setOnCloseRequest(event1 -> disposeHistogramProcessingWindow());
        });
        binarizationMenuItem.setOnAction(event -> {
            if (binarizationProcessingStage != null) {
                disposeBinarizationProcessingWindow();
            }
            binarizationProcessingStage = createProcessingSubWindow(binarizationProcessingStage, "BinarizationProcessingWindow.fxml", "Binaryzacja");
            binarizationProcessingStage.setOnCloseRequest(event1 -> disposeBinarizationProcessingWindow());
        });
        morphologicMenuItem.setOnAction(event -> {
            if (morphologicProcessingStage != null) {
                disposeMorphologicProcessingWindow();
            }
            morphologicProcessingStage = createProcessingSubWindow(morphologicProcessingStage, "MorphologicProcessingWindow.fxml", "Morfologia");
            morphologicProcessingStage.setOnCloseRequest(event1 -> disposeMorphologicProcessingWindow());
        });
        analysisMenuItem.setOnAction(event -> {
            if (analysisProcessingStage != null) {
                disposeAnalysisProcessingWindow();
            }
            analysisProcessingStage = createProcessingSubWindow(analysisProcessingStage, "AnalysisProcessingWindow.fxml", "Analiza");
            analysisProcessingStage.setOnCloseRequest(event1 -> disposeAnalysisProcessingWindow());
        });
    }

    private Stage createSubWindow(@Nullable Stage stageReference, String fxmlPath, String title) {
        if (stageReference == null) {
            Stage result = new Stage();
            result.setTitle(title);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlPath));
            try {
                result.setScene(new Scene(fxmlLoader.load()));
                result.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SubWindow subwindow = fxmlLoader.getController();
            subwindow.setMainWindowController(this);
            subwindow.setStage(result);
            subwindow.postInitialization();
            return result;
        }
        return stageReference;
    }

    private Stage createProcessingSubWindow(@Nullable Stage stageReference, String fxmlPath, String title) {
        if (stageReference == null) {
            Stage result = new Stage();
            result.setTitle(title);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlPath));
            try {
                result.setScene(new Scene(fxmlLoader.load()));
                result.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProcessingSubWindow subwindow = fxmlLoader.getController();
            subwindow.setMainWindowController(this);
            subwindow.setStage(result);
            subwindow.postInitialization(getCurrentImage());
            return result;
        }
        return stageReference;
    }

    private void showCommandInput() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Wprowadzanie komendy");
        dialog.setHeaderText("Rect(x,y,w,h); Circle(x,y,r); Line(x,y,x,y)");
        dialog.setContentText("Wprowadź komendę");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            commandInterpreter.executeCommand(result.get());
        }
    }

    private void saveCurrentImage() {
        if (saveFileChooser == null) {
            saveFileChooser = new FileChooser();
            saveFileChooser.setTitle("Zapisz plik");
            saveFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            saveFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Wszystkie", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
        }
        File image = saveFileChooser.showSaveDialog(mainStage);
        if (image != null) {
            try {
                imageSaver.saveImage(getCurrentImage(), image);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Błąd");
                alert.setTitle("Błąd zapisu");
                alert.setContentText(e.getMessage());
                alert.show();
                e.printStackTrace();
            }
        }
    }

    private void loadUserImage() {
        if (openFileChooser == null) {
            openFileChooser = new FileChooser();
            openFileChooser.setTitle("Otwórz plik");
            openFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            openFileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Wszystkie", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", ".jpg"),
                    new FileChooser.ExtensionFilter("PPM", ".ppm"),
                    new FileChooser.ExtensionFilter("BMP", ".bmp"),
                    new FileChooser.ExtensionFilter("PNG", ".png"));
        }
        File image = openFileChooser.showOpenDialog(mainStage);
        if (image != null) {
            try {
                setCurrentImage(imageLoader.loadImage(image));
                enableMenus();
                closeAllSubWindows();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Błąd");
                alert.setTitle("Błąd pliku");
                alert.setContentText(e.getMessage());
                alert.show();
                e.printStackTrace();
            }
        }
    }

    private void enableMenus() {
        pointMenuItem.setDisable(false);
        filterMenuItem.setDisable(false);
        saveMenuItem.setDisable(false);
        histogramMenuItem.setDisable(false);
        binarizationMenuItem.setDisable(false);
        morphologicMenuItem.setDisable(false);
        analysisMenuItem.setDisable(false);
    }

    private void closeAllSubWindows() {
        if (colorStage != null) {
            disposeColorWindow();
        }
        if (editShapeStage != null) {
            disposeEditWindow();
        }
        if (pointProcessingStage != null) {
            disposePointProcessingWindow();
        }
        if (filterProcessingStage != null) {
            disposeFilterProcessingWindow();
        }
        if (histogramProcessingStage != null) {
            disposeHistogramProcessingWindow();
        }
        if (binarizationProcessingStage != null) {
            disposeBinarizationProcessingWindow();
        }
        if (morphologicProcessingStage != null) {
            disposeMorphologicProcessingWindow();
        }
        if (analysisProcessingStage != null) {
            disposeAnalysisProcessingWindow();
        }
    }

    private void disposeColorWindow() {
        colorStage = null;
    }

    private void disposeEditWindow() {
        editShapeStage = null;
    }

    private void disposePointProcessingWindow() {
        pointProcessingStage.close();
        pointProcessingStage = null;
    }

    private void disposeFilterProcessingWindow() {
        filterProcessingStage.close();
        filterProcessingStage = null;
    }

    private void disposeHistogramProcessingWindow() {
        histogramProcessingStage.close();
        histogramProcessingStage = null;
    }

    private void disposeBinarizationProcessingWindow() {
        binarizationProcessingStage.close();
        binarizationProcessingStage = null;
    }

    private void disposeMorphologicProcessingWindow() {
        morphologicProcessingStage.close();
        morphologicProcessingStage = null;
    }

    private void disposeAnalysisProcessingWindow() {
        analysisProcessingStage.close();
        analysisProcessingStage = null;
    }

    private void updateColorCanvas(Color color) {
        colorCanvasContext.setFill(color);
        colorCanvasContext.fillRect(0.0, 0.0, colorCanvas.getWidth(), colorCanvas.getHeight());
    }

    //getters and setters
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public ShapesCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(ShapesCanvas canvas) {
        this.canvas = canvas;
    }

    public Image getCurrentImage() {
        Image result = canvas.snapshot(null, null);
        canvas.clear();
        canvas.loadImage(result);
        return result;
    }

    public void setCurrentImage(Image image) {
        canvas.loadImage(image);
    }

    public Color getCurrentColor() {
        return canvas.getCurrentColor();
    }

    public void setCurrentColor(Color currentColor) {
        canvas.setCurrentColor(currentColor);
        updateColorCanvas(currentColor);
    }
}
