package sample;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

public class Controller {

    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;
    public TabPane tabMain;
    public ChoiceBox choiceBox1;

    public void initialize()
    {
        System.out.println("initialize()");
        choiceBox1.getItems().add("item1");
        choiceBox1.getItems().add("item2");
        choiceBox1.getItems().add("item3");
        choiceBox1.setValue("item2");
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
