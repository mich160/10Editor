package program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.fxml.MainWindowController;

public class Program extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Grafika");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../view/fxml/MainWindow.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.show();

        MainWindowController controller = fxmlLoader.getController();
        controller.setApplication(this);
        controller.setMainStage(primaryStage);
        controller.postInitialization();
    }
}
