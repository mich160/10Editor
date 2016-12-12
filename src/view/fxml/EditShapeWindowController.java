package view.fxml;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.utilities.ValidationUtils;
import view.customFXClasses.ShapesCanvas;
import view.fxml.interfaces.SubWindow;

import java.util.concurrent.atomic.AtomicBoolean;

public class EditShapeWindowController implements SubWindow {
    private static final String angleFieldName = "kąt", xFieldName = "x", yFieldName = "y", xScaleFieldName = "skala x", yScaleFieldName = "skala y";

    private class ScaleChangeListener implements ChangeListener<String> {
        private TextField thisBox;
        private TextField otherBox;

        public ScaleChangeListener(TextField thisBox, TextField otherBox) {
            this.thisBox = thisBox;
            this.otherBox = otherBox;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (symmetricCheckBox.isSelected() && !scaleLocked.get()) {
                scaleLocked.set(true);
                otherBox.setText(thisBox.getText());
                scaleLocked.set(false);
            }
        }
    }

    private MainWindowController mainWindowController;
    private Stage stage;

    private ShapesCanvas canvas;
    private AtomicBoolean scaleLocked;

    @FXML
    private TextField angleTextField;
    @FXML
    private TextField xTextField;
    @FXML
    private TextField yTextField;
    @FXML
    private TextField xScaleTextField;
    @FXML
    private TextField yScaleTextField;
    @FXML
    private CheckBox symmetricCheckBox;
    @FXML
    private Button translateButton;
    @FXML
    private Button scaleButton;
    @FXML
    private Button rotateButton;

    @Override
    public MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    @Override
    public void setMainWindowController(MainWindowController controller) {
        mainWindowController = controller;
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
        canvas = mainWindowController.getCanvas();
        initializeListeners();
    }

    @FXML
    @Override
    public void initialize() {
        scaleLocked = new AtomicBoolean(false);
    }

    private void initializeListeners() {
        translateButton.setOnAction(event -> translateShape());
        scaleButton.setOnAction(event -> scaleShape());
        rotateButton.setOnAction(event -> rotateShape());
        xScaleTextField.textProperty().addListener(new ScaleChangeListener(xScaleTextField, yScaleTextField));
        yScaleTextField.textProperty().addListener(new ScaleChangeListener(yScaleTextField, xScaleTextField));
    }

    private void translateShape() {
        boolean allValid = true;
        allValid = allValid && ValidationUtils.isValidDouble(xTextField.getText());
        if (!allValid) {
            viewWrongValueAlert(xFieldName);
            return;
        }
        allValid = allValid && ValidationUtils.isValidDouble(yTextField.getText());
        if (!allValid) {
            viewWrongValueAlert(yFieldName);
            return;
        }
        canvas.translateSelectedShape(Double.parseDouble(xTextField.getText()), Double.parseDouble(yTextField.getText()));
    }

    private void rotateShape() {
        if (!ValidationUtils.isValidDouble(angleTextField.getText())) {
            viewWrongValueAlert(angleFieldName);
            return;
        }
        if (!ValidationUtils.isValidDouble(xTextField.getText()) || !ValidationUtils.isValidDouble(yTextField.getText())) {
            canvas.rotateSelectedShape(Double.parseDouble(angleTextField.getText()));
        } else {
            canvas.rotateSelectedShape(Double.parseDouble(xTextField.getText()), Double.parseDouble(yTextField.getText()), Double.parseDouble(angleTextField.getText()));
        }
    }

    private void scaleShape() {
        boolean allValid = true;
        allValid = allValid && ValidationUtils.isValidDouble(xScaleTextField.getText());
        if (!allValid) {
            viewWrongValueAlert(xScaleFieldName);
            return;
        }
        allValid = allValid && ValidationUtils.isValidDouble(yScaleTextField.getText());
        if (!allValid) {
            viewWrongValueAlert(yScaleFieldName);
            return;
        }
        if (!ValidationUtils.isValidDouble(xTextField.getText()) || !ValidationUtils.isValidDouble(yTextField.getText())) {
            canvas.scaleSelectedShape(Double.parseDouble(xScaleTextField.getText()), Double.parseDouble(yScaleTextField.getText()));
        } else {
            canvas.scaleSelectedShape(Double.parseDouble(xTextField.getText()), Double.parseDouble(yTextField.getText()), Double.parseDouble(xScaleTextField.getText()), Double.parseDouble(yScaleTextField.getText()));
        }
    }

    private void viewWrongValueAlert(String valueType) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Błąd!");
        alert.setHeaderText("Zła wartość pola");
        alert.setContentText("Zła wartość pola " + valueType + '.');
        alert.show();
    }
}
