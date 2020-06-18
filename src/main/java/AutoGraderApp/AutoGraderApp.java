package AutoGraderApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class AutoGraderApp extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        /*** works
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
         ****/


        Parent root = FXMLLoader.load(getClass().getResource("AutoGraderApp.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
        //Parent root = loadFXML("primary");
        //Parent root = loader.load();

        // Get the Controller from the FXMLLoader
        //Controller controller = loader.getController();
        //controller.setGradingEngine(autoGrader.getGradingEngine());
        //controller.setAutoGraderRef(autoGrader);

        //primaryStage.setTitle(appName);
        primaryStage.setScene(new Scene(root, 640, 480));
        //primaryStage.setMinWidth(MIN_STAGE_WIDTH);
        //primaryStage.setMinHeight(MIN_STAGE_HEIGHT);

        //primaryStage.getIcons().add(new Image("/ag2_icon.png"));
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(xloadFXML(fxml));
    }

    private static Parent xloadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AutoGraderApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}