package AutoGrader2;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/* ======================================================================
 * Controller Class
 * This class is the primary store of GUI callback functions.
 * ===================================================================== */
public class Controller implements IAGConstant {
    //---------- FXML GUI control references ----------

    //---------- Misc. Controls ----------
    public TabPane tabMain;
    public AnchorPane anchorPaneMain;

    //---------- Config Tab ----------
    public ChoiceBox choiceBoxConfigLanguage;
    public TextField txtMaxRunTime;
    public TextField txtMaxLines;
    public ChoiceBox choiceBoxConfigIncludeSource;
    public ChoiceBox choiceBoxConfigAutoUncompress;
    public ChoiceBox choiceBoxConfigRecursive;
    public TextField txtPython3Interpreter;
    public TextField txtCppCompiler;
    public Button btnSettings;
    public Button btnInputSetup;
    public Button btnOutput;

    //---------- Input/Setup Tab ----------
    public ListView listTestData;
    public Button btnStart;
    public CheckBox checkNoTestData;
    public Button btnAdd;
    public Button btnRemove;
    public TextField txtSourceDirectory;

    //---------- Output Tab ----------
    public WebView wvOutput;
    public Button btnPrev;
    public Button btnNext;

    //---------- Console Tab ----------
    public ListView listConsole;
    public static ListView consolePtr;

    //---------- Misc members ----------
    private GradingEngine gradingEngine;

    /* ======================================================================
     * initialize()
     * Called automatically upon creation of the GUI
     * ===================================================================== */
    public void initialize() {

        //---------- set the static pointer to the console ----------
        consolePtr = listConsole;

        //---------- populate the different choice box configuration options ----------

        //---------- setup "language" config options ----------
        choiceBoxConfigLanguage.getItems().add(LANGUAGE_PYTHON3);
        choiceBoxConfigLanguage.getItems().add(LANGUAGE_CPP);

        //---------- setup "include source" config options ----------
        choiceBoxConfigIncludeSource.getItems().add(YES);
        choiceBoxConfigIncludeSource.getItems().add(NO);

        //---------- setup "auto uncompress" config options ----------
        choiceBoxConfigAutoUncompress.getItems().add(YES);
        choiceBoxConfigAutoUncompress.getItems().add(NO);

        //---------- setup "recursive" config options ----------
        choiceBoxConfigRecursive.getItems().add(YES);
        choiceBoxConfigRecursive.getItems().add(NO);


        //---------- update the different configuration fields with the actual user-specified values ----------

        //---------- set the "language" value  ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.LANGUAGE) != null)
            choiceBoxConfigLanguage.setValue(AutoGrader2.getConfiguration(AG_CONFIG.LANGUAGE));
        else
            choiceBoxConfigLanguage.setValue(LANGUAGE_PYTHON3);

        //---------- set the "max run time" value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.MAX_RUNTIME) != null)
            txtMaxRunTime.setText(AutoGrader2.getConfiguration(AG_CONFIG.MAX_RUNTIME));
        else
            txtMaxRunTime.setText("0");

        //---------- set the "max output lines" value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES) != null)
            txtMaxLines.setText(AutoGrader2.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES));
        else
            txtMaxLines.setText("200");

        //---------- set the "include source" value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.INCLUDE_SOURCE) != null)
            choiceBoxConfigIncludeSource.setValue(AutoGrader2.getConfiguration(AG_CONFIG.INCLUDE_SOURCE));
        else
            choiceBoxConfigIncludeSource.setValue(YES);

        //---------- set the "auto uncompress" value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS) != null)
            choiceBoxConfigAutoUncompress.setValue(AutoGrader2.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS));
        else
            choiceBoxConfigAutoUncompress.setValue(YES);

        //---------- set the "recursive process" value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.PROCESS_RECURSIVELY) != null)
            choiceBoxConfigRecursive.setValue(AutoGrader2.getConfiguration(AG_CONFIG.PROCESS_RECURSIVELY));
        else
            choiceBoxConfigRecursive.setValue(YES);

        //---------- set the python3 interpreter path value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER) != null)
            txtPython3Interpreter.setText(AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER));
        else
            txtPython3Interpreter.setText("");

        //---------- set the c++ compiler path value ----------
        if (AutoGrader2.getConfiguration(AG_CONFIG.CPP_COMPILER) != null)
            txtCppCompiler.setText(AutoGrader2.getConfiguration(AG_CONFIG.CPP_COMPILER));
        else
            txtCppCompiler.setText("");

        //configure the "Test Data" list view to allow multiple selections
        listTestData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listTestData.getItems().add("filename1");
        listTestData.getItems().add("filename2");
        listTestData.getItems().add("filename3");

        setStartButtonStatus();

        //set the main tab to the input/setup tab by invoking the btnInputSetupClick callback
        btnInputSetupClick();

        //disable the Output button
        btnOutput.setDisable(true);

        //************* TEMP **************
        txtSourceDirectory.setText("/Users/jvolcy/Downloads/201709-94470-Homework 7b, P0502 - Number Pyramid, due 1021 (will count as Lab 5)-259033");

    }

    /* ======================================================================
     * setGradingEngine()
     * sets a reference to the GradingEngine object (this is the model in
     * the MVC paradigm).
     * ===================================================================== */
    public void setGradingEngine(GradingEngine e) {
        //---------- set a reference to the grading engine ----------
        gradingEngine = e;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public static void console (String format, Object... arguments) {
        String formattedOutput = String.format(format, arguments);

        //if the GUI is not yet up, the consolePtr hasn't been set yet:
        //dump the output to the screen.
        if (consolePtr == null) {
            System.out.println("[x]" + formattedOutput);
        }
        else{
            System.out.println("[c]" + formattedOutput);
            consolePtr.getItems().add(formattedOutput);
        }
    }


    /* ======================================================================
     * btnPrevClick()
     * The "Prev" and "Next" buttons are on the Output tab.  These are
     * navigation buttons to move back and forth through the list of
     * graded assignments.  These buttons work in conjunction with the
     * student name drop-down list.
     * ===================================================================== */
    public void btnPrevClick() {
        console("'Prev' button clicked.\n");
    }


    /* ======================================================================
     * menuFileQuit()
     * Callback for File->Quit
     * ===================================================================== */
    public void menuFileQuit() {
        console("Good bye\n");

        /* get and close the app's stage.  This will quit the GUI and return
        control to AutoGrader2.main(). */
        Stage stage = (Stage) anchorPaneMain.getScene().getWindow();
        stage.close();

    }

    /* ======================================================================
     * menuSettingsConfig()
     * Callback for Settings->Config
     * This function sets the main tab to the CONFIGURATION_TAB.
     * ===================================================================== */
    public void menuSettingsConfig() {
        btnSettingsClick();
    }

    /* ======================================================================
     * btnSettingsClick()
     * Callback for Settings button.
     * This function sets the main tab to the CONFIGURATION_TAB.
     * ===================================================================== */
    public void btnSettingsClick() {
        tabMain.getSelectionModel().select(CONFIGURATION_TAB);
        btnSettings.setTextFill(Paint.valueOf("blue"));
        btnInputSetup.setTextFill(Paint.valueOf("black"));
        btnOutput.setTextFill(Paint.valueOf("black"));
    }

    /* ======================================================================
     * btnInputSetupClick()
     * Callback for the Input/Setup button.
     * This function sets the main tab to the SETUP_INPUT_TAB.
     * ===================================================================== */
    public void btnInputSetupClick() {
        tabMain.getSelectionModel().select(SETUP_INPUT_TAB);
        btnSettings.setTextFill(Paint.valueOf("black"));
        btnInputSetup.setTextFill(Paint.valueOf("blue"));
        btnOutput.setTextFill(Paint.valueOf("black"));
    }

    /* ======================================================================
     * btnOutputClick()
     * Callback for the Output button.
     * This function sets the main tab to the OUTPUT_TAB.
     * ===================================================================== */
    public void btnOutputClick() {
        tabMain.getSelectionModel().select(OUTPUT_TAB);
        btnSettings.setTextFill(Paint.valueOf("black"));
        btnInputSetup.setTextFill(Paint.valueOf("black"));
        btnOutput.setTextFill(Paint.valueOf("blue"));
        wvOutput.getEngine().load("file:///Users/jvolcy/work/Spelman/Projects/data/AGP0202.html");
    }

    /* ======================================================================
     * btnConsoleClick()
     * ===================================================================== */
    public void btnConsoleClick(){
        tabMain.getSelectionModel().select(CONSOLE_TAB);
        btnSettings.setTextFill(Paint.valueOf("black"));
        btnInputSetup.setTextFill(Paint.valueOf("black"));
        btnOutput.setTextFill(Paint.valueOf("black"));
    }

    /* ======================================================================
     * btnTestDataRemoveClick()
     * Callback for "Remove" button associated with the Test Data list view.
     * Clicking this button is equivalent to pressing the delete or
     * backspace key to remove selected test data files from the list.
     * ===================================================================== */
    public void btnTestDataRemoveClick() {
        /* Because we can't safely iterate through a list while we are deleting
         * items from it, we create a copy of the selected items in the list,
         * then we iterate through this non-changing list.  Note that it is
         * not possible to carry out this process using indexes instead of
         * the contents of the list because, again, the indexes would change
         * as we modify the list. */
        Object[] selectedItems = listTestData.getSelectionModel().getSelectedItems().toArray();

        //go through the list of selected items and delete each one
        for (Object selectedItem : selectedItems) {
            listTestData.getItems().remove(selectedItem);
        }

        //adjust the status of the Start button
        setStartButtonStatus();

    }

    /* ======================================================================
     * listTestDataKeyPressed()
     * Callback for the test data list that is invoked when a key is
     * pressed.  We check for the 'delete' and 'backspace' keys and use
     * these to invoke the function that deletes selected items from the
     * list view.
     * ===================================================================== */
    public void listTestDataKeyPressed(KeyEvent e) {
        /* Check if the user pressed either delete or backspace.  If so, delete
         * all selected list entries. */
        if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
            btnTestDataRemoveClick();
        }
    }

    /* ======================================================================
     * setStartButtonStatus()
     * This function enables and disables the "Start" button based on the
     * status of the "No Test Data" checkbox and the test data list view.
     * The button is enabled only if at least one test data file is
     * specified or the "No Test Data" checkbox is checked.  Otherwise,
     * the "Start" button should be disabled.  Call this function on
     * startup, after the checkbox's status changes and after items have
     * been removed form the list view.
     * Also, we set the background color of the list view to light red
     * or light green to indicate a missing input.
     * ===================================================================== */
    private void setStartButtonStatus() {
        if (checkNoTestData.isSelected() || listTestData.getItems().size() != 0) {
            btnStart.setDisable(false);
            listTestData.setStyle("-fx-background-color: #f0fff0; -fx-border-style: solid; -fx-border-color: #a0a0a0; -fx-border-width: 1");
        } else {
            btnStart.setDisable(true);
            listTestData.setStyle("-fx-background-color: #fff0f0; -fx-border-style: solid; -fx-border-color: #a0a0a0; -fx-border-width: 1");
        }
    }


    /* ======================================================================
     * checkNoTestDataClick()
     * Callback for "No Test Data" check box on the Input/Setup tab.
     * When no test data is required, we disable the "Add" button the
     * "Remove" button as well as the list view.
     * ===================================================================== */
    public void checkNoTestDataClick() {
        if (checkNoTestData.isSelected()) {
            btnAdd.setDisable(true);
            btnRemove.setDisable(true);
            listTestData.setDisable(true);
        } else {
            btnAdd.setDisable(false);
            btnRemove.setDisable(false);
            listTestData.setDisable(false);
        }
        setStartButtonStatus();
    }


    /* ======================================================================
     * btnAddClick()
     * Callback for the "Add" button on the Input/Setup tab.  The add button
     * is used to add test files to the test data list.
     * ===================================================================== */
    public void btnAddClick() {
        //get the app's stage
        Stage stage = (Stage) anchorPaneMain.getScene().getWindow();

        //open the file chooser dialog box
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Test Data File");
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        //save the selected file objects in the test data list view
        for (File file : files) {
            listTestData.getItems().add(file);
        }
    }


    /* ======================================================================
     * btnSourceDirectoryClick()
     * Callback for "Source Director" button on Input/Setup Tab.
     * ===================================================================== */
    public void btnSourceDirectoryClick() {
        //get the app's stage
        Stage stage = (Stage) anchorPaneMain.getScene().getWindow();

        //open the file chooser dialog box
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Assignments Directory");
        File directory = directoryChooser.showDialog(stage);
        //console(directory.getCanonicalPath()\n);
        txtSourceDirectory.setText(directory.getAbsolutePath());
    }


    /* ======================================================================
     * tabMainOnKeyPressed()
     * Callback for KeyPressed event on the main tab.  We capture and
     * consume this event to eliminate keyboard navigation through the
     * tap pages.  Only the Settings/Input/Output buttons should be used
     * to navigate through he pages.
     * ===================================================================== */
    public void tabMainOnKeyPressed(Event e) {
        /* consume key press events so that they do not go on to effect
        navigation controls on the main tab. */
        e.consume();
    }


    /* ======================================================================
     * btnStart()
     * Callback for 'Start' button on Input/Setup tab
     * ===================================================================== */
    public void btnStart() {
        MoodlePreprocessor mpp = new MoodlePreprocessor(txtSourceDirectory.getText(),
                AutoGrader2.getConfiguration(AG_CONFIG.LANGUAGE),
                AutoGrader2.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS).equals(IAGConstant.YES));

        //---------- Configure the Grading Engine ----------
        gradingEngine.assignments = mpp.getAssignments();

        gradingEngine.dumpAssignments();
        gradingEngine.cppCompiler = AutoGrader2.getConfiguration(AG_CONFIG.CPP_COMPILER);
        gradingEngine.python3Interpreter = AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER);
        gradingEngine.setOutputFileName("/Users/jvolcy/work/Spelman/Projects/data/AGtest.html");

        gradingEngine.processFiles();

   }

}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
