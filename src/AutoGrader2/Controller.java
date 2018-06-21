package AutoGrader2;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

/* ======================================================================
 * Controller Class
 * ===================================================================== */
public class Controller {

    /* ======================================================================
     * xxx
     * ===================================================================== */

    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;
    public TabPane tabMain;
    public ChoiceBox choiceBoxConfigLanguage;
    public ChoiceBox choiceBoxConfigIncludeSource;
    public ChoiceBox choiceBoxConfigAutoUncompress;
    public ChoiceBox choiceBoxConfigRecursive;
    public ListView listTestData;

    public void initialize()
    {
        System.out.println("initialize()");

        choiceBoxConfigLanguage.getItems().add("Python 3");
        choiceBoxConfigLanguage.getItems().add("C++");
        choiceBoxConfigLanguage.setValue("Python 3");

        choiceBoxConfigIncludeSource.getItems().add("Yes");
        choiceBoxConfigIncludeSource.getItems().add("No");
        choiceBoxConfigIncludeSource.setValue("Yes");

        choiceBoxConfigAutoUncompress.getItems().add("Yes");
        choiceBoxConfigAutoUncompress.getItems().add("No");
        choiceBoxConfigAutoUncompress.setValue("Yes");

        choiceBoxConfigRecursive.getItems().add("Yes");
        choiceBoxConfigRecursive.getItems().add("No");
        choiceBoxConfigRecursive.setValue("Yes");

        listTestData.getItems().add("filename1");
        listTestData.getItems().add("filename2");
        listTestData.getItems().add("filename3");

        tabMain.getSelectionModel().select(1);
    }

    public void btnPrevClick()
    {
        System.out.println("'Prev' button clicked.");
    }

    public void menuFileQuit()
    {
        System.out.println("Good bye");
        System.exit(0);
    }

    public void menuSettingsConfig()
    {
        tabMain.getSelectionModel().select(0);
    }

    public void btnInputSetupClick()
    {
        System.out.println("menuInput");
        tabMain.getSelectionModel().select(1);
    }

    public void btnOutputClick()
    {
        System.out.println("menuOutput");
        tabMain.getSelectionModel().select(2);
        wvOutput.getEngine().load("file:///Users/jvolcy/work/Spelman/Projects/data/AGP0202.html");
    }

}



/* ======================================================================
 * xxx
 * ===================================================================== */
