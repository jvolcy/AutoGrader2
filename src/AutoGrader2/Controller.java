package AutoGrader2;

import com.sun.jna.StringArray;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;

import java.util.Collection;
import java.util.List;

/* ======================================================================
 * Controller Class
 * This class is the primary store of GUI callback functions.
 * ===================================================================== */
public class Controller implements IAGConstant {


    //---------- FXML GUI control references ----------
    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;
    public TabPane tabMain;
    public ChoiceBox choiceBoxConfigLanguage;
    public ChoiceBox choiceBoxConfigIncludeSource;
    public ChoiceBox choiceBoxConfigAutoUncompress;
    public ChoiceBox choiceBoxConfigRecursive;
    public ListView listTestData;

    /* ======================================================================
     * initialize()
     * Called automatically upon creation of the GUI
     * ===================================================================== */
    public void initialize() {
        System.out.println("initialize()");

        choiceBoxConfigLanguage.getItems().add("Python 3");
        choiceBoxConfigLanguage.getItems().add("C++");
        choiceBoxConfigLanguage.setValue("Python 3");

        choiceBoxConfigIncludeSource.getItems().add(YES);
        choiceBoxConfigIncludeSource.getItems().add(NO);
        choiceBoxConfigIncludeSource.setValue("Yes");

        choiceBoxConfigAutoUncompress.getItems().add(YES);
        choiceBoxConfigAutoUncompress.getItems().add(NO);
        choiceBoxConfigAutoUncompress.setValue(YES);

        choiceBoxConfigRecursive.getItems().add(YES);
        choiceBoxConfigRecursive.getItems().add(NO);
        choiceBoxConfigRecursive.setValue(YES);

        //configure the "Test Data" list view to allow multiple selections
        listTestData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listTestData.getItems().add("filename1");
        listTestData.getItems().add("filename2");
        listTestData.getItems().add("filename3");

        tabMain.getSelectionModel().select(SETUP_INPUT_TAB);
    }

    /* ======================================================================
     * btnPrevClick()
     * The "Prev" and "Next" buttons are on the Output tab.  These are
     * navigation buttons to move back and forth through the list of
     * graded assignments.  These buttons work in conjunction with the
     * student name drop-down list.
     * ===================================================================== */
    public void btnPrevClick()
    {
        System.out.println("'Prev' button clicked.");
    }


    /* ======================================================================
     * menuFileQuit()
     * Callback for File->Quit
     * ===================================================================== */
    public void menuFileQuit()
    {
        System.out.println("Good bye");
        System.exit(0);
    }

    /* ======================================================================
     * menuSettingsConfig()
     * Callback for Settings->Config
     * This function sets the main tab to the CONFIGURATION_TAB.
     * ===================================================================== */
    public void menuSettingsConfig()
    {
        tabMain.getSelectionModel().select(CONFIGURATION_TAB);
    }

    /* ======================================================================
     * btnInputSetupClick()
     * Callback for the Input/Setup button.
     * This function sets the main tab to the SETUP_INPUT_TAB.
     * ===================================================================== */
    public void btnInputSetupClick()
    {
        System.out.println("menuInput");
        tabMain.getSelectionModel().select(SETUP_INPUT_TAB);
    }

    /* ======================================================================
     * btnOutputClick()
     * Callback for the Output button.
     * This function sets the main tab to the OUTPUT_TAB.
     * ===================================================================== */
    public void btnOutputClick()
    {
        System.out.println("menuOutput");
        tabMain.getSelectionModel().select(OUTPUT_TAB);
        wvOutput.getEngine().load("file:///Users/jvolcy/work/Spelman/Projects/data/AGP0202.html");
    }

    /* ======================================================================
     * btnTestDataRemoveClick()
     * Callback for "Remove" button associated with the Test Data list view.
     * Clicking this button is equivalent to pressing the delete or
     * backspace key to remove selected test data files from the list.
     * ===================================================================== */
    public void btnTestDataRemoveClick()
    {
        /* Because we can't safely iterate through a list while we are deleting
         * items from it, we create a copy of the selected items in the list,
         * then we iterate through this non-changing list.  Note that it is
         * not possible to carry out this process using indexes instead of
         * the contents of the list because, again, the indexes would change
         * as we modify the list. */
        Object[] selectedItems = listTestData.getSelectionModel().getSelectedItems().toArray();

        //go through the list of selected items and delete each one
        for (Object selectedItem : selectedItems)
        {
            listTestData.getItems().remove(selectedItem);
        }

    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public void listTestDataKeyPressed(KeyEvent e)
    {
        /* Check if the user pressed either delete or backspace.  If so, delete
        * all selected list entries. */
        if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE)
        {
            btnTestDataRemoveClick();
        }

    }
}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
