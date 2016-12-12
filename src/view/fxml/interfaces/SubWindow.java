package view.fxml.interfaces;

import javafx.stage.Stage;
import view.fxml.MainWindowController;

public interface SubWindow {
    MainWindowController getMainWindowController();

    void setMainWindowController(MainWindowController controller);

    Stage getStage();

    void setStage(Stage stage);

    void postInitialization();

    void initialize();
}
