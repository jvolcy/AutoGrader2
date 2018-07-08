package AutoGraderApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static AutoGraderApp.Controller.console;

/* ======================================================================
 * AutoGraderApp class
 * ===================================================================== */
public class AutoGraderApp extends Application implements IAGConstant {

    public static AutoGrader2 autoGrader;
    public static final String version = "2.0";

    /* ======================================================================
     * start()
     * This function is automatically called after the primary stage
     * has been created.
     * This is a good place to customize the appearance of the stage.
     * ===================================================================== */
    @Override
    public void start(Stage primaryStage) throws Exception {
        console("start...");

        //Parent root = FXMLLoader.load(getClass().getResource("AutoGraderApp.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoGraderApp.fxml"));
        Parent root = loader.load();

        // Get the Controller from the FXMLLoader
        //Controller controller = loader.getController();
        //controller.setGradingEngine(autoGrader.getGradingEngine());
        //controller.setAutoGraderRef(autoGrader);

        primaryStage.setTitle("Spelman AutoGrader 2.0");
        primaryStage.setScene(new Scene(root, MIN_STAGE_WIDTH, MIN_STAGE_HEIGHT));
        primaryStage.setMinWidth(MIN_STAGE_WIDTH);
        primaryStage.setMinHeight(MIN_STAGE_HEIGHT);

        primaryStage.show();
    }

    /* ======================================================================
     * main()
     * Entry point into the application.
     * ===================================================================== */
    public static void main(String[] args) {

        console("main...");
        autoGrader = new AutoGrader2();

        console("launching...");
        //---------- start the GUI ----------
        launch(args);

        //---------- Commit the AG options to the JSON file ----------
        autoGrader.saveConfiguration();

        console("Exiting main()...");
    }
}

/* ======================================================================
 * To Do
 * C++ support
 * #1 Need to set the CWD on the Java grading thread
 * #2 Add document to serialized output
 * JSON configuration
 * AG directives (add appropriate entries to Assignment class prior to release)
 * ===================================================================== */


/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
