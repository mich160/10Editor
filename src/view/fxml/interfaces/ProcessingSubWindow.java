package view.fxml.interfaces;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.fxml.MainWindowController;

public interface ProcessingSubWindow {
    void postInitialization(Image image);

    MainWindowController getMainWindowController();

    void setMainWindowController(MainWindowController controller);

    Stage getStage();

    void setStage(Stage stage);

    void initialize();
}
