package sample;

import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;

public class Controller {

    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;
    public TabPane tabMain;

    public void btnPrevClick()
    {
        System.out.println("'Prev' button clicked.");
        wvOutput.getEngine().load("file:///Users/jvolcy/work/Spelman/Projects/data/AGP0202.html");
    }

    public void menuFileQuit()
    {
        System.out.println("Good bye");
        System.exit(0);
    }

    public void menuInput()
    {
        System.out.println("menuInput");
        tabMain.getSelectionModel().select(0);
    }

    public void menuOutput()
    {
        System.out.println("menuOutput");
        tabMain.getSelectionModel().select(1);
    }

}
