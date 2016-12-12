package view.fxml;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import view.customFXClasses.ConeRenderer;

public class HSVConeWindowController {
    private Stage stage;

    private int currentOffset = 0;

    @FXML
    Canvas coneCanvas;
    GraphicsContext coneCanvasContext;

    @FXML
    public void initialize() {
        coneCanvasContext = coneCanvas.getGraphicsContext2D();
        drawCone(currentOffset);
    }

    private void clear() {
        coneCanvasContext.clearRect(0, 0, coneCanvas.getWidth(), coneCanvas.getHeight());
    }

    private void drawCone(int offset) {
        coneCanvasContext.drawImage(ConeRenderer.getConeBody(400, 800, offset), 0, 10, 200, 400);
        coneCanvasContext.drawImage(ConeRenderer.getConeBase(400, 90 + offset), 0, 0, 200, 20);
    }

    public void initializeListeners() {
        stage.getScene().setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.RIGHT) {
                clear();
                currentOffset -= 5;
                drawCone(currentOffset);
            } else if (keyCode == KeyCode.LEFT) {
                clear();
                currentOffset += 5;
                drawCone(currentOffset);
            }
        });

    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
