package sample;

import javafx.scene.control.Button;
import javafx.scene.web.WebView;

public class Controller {

    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;

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

}
