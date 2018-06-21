package AutoGrader2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/* ======================================================================
 * AutoGrader2 class
 * ===================================================================== */
public class AutoGrader2 extends Application implements IAGConstant {

    /* ======================================================================
     * start()
     * This function is automatically called after the primary stage
     * has been created.
     * This is a good place to customize the appearance of the stage.
     * ===================================================================== */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("AutoGrader2.fxml"));
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
        launch(args);
    }
}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
