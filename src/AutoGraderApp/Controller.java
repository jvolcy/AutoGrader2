package AutoGraderApp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* ======================================================================
 * Controller Class
 * This class is the primary store of GUI callback functions.
 * ===================================================================== */
public class Controller implements IAGConstant {
    //---------- FXML GUI control references ----------

    //---------- Misc. Controls ----------
    public TabPane tabMain;
    public AnchorPane anchorPaneMain;
    public Label lblStatus;
    public Label lblMessage;
    public Label lblLanguage;
    private static Label messagePtr;

    //---------- Config Tab ----------
    public ChoiceBox choiceBoxConfigLanguage;
    public Spinner<Integer> spinnerMaxRunTime;
    public Spinner<Integer> spinnerMaxLines;
    public ChoiceBox choiceBoxConfigIncludeSource;
    public ChoiceBox choiceBoxConfigAutoUncompress;
    public ChoiceBox choiceBoxConfigRecursive;
    public TextField txtPython3Interpreter;
    public TextField txtCppCompiler;
    public TextField txtShell;
    public Button btnSettings;
    public Button btnInputSetup;
    public Button btnOutput;
    public Button btnConsole;
    public Button btnSave;
    public Button btnExportHtml;
    public Button btnGradeSummary;

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
    public ChoiceBox cbName;

    //---------- Console Tab ----------
    public ListView listConsole;
    private static ListView consolePtr;

    //---------- Misc members ----------
    private Alert gradingThreadStatusAlert;     //alert box displayed while processing assignments
    private final Double GRADING_TIMELINE_PERIOD = 0.25;        //0.25 second period
    private ReportGenerator reportGenerator;

  /* ======================================================================
     * initialize()
     * Called automatically upon creation of the GUI
     * ===================================================================== */
    public void initialize() {

        //---------- set the static pointer to the console ----------
        consolePtr = listConsole;
        messagePtr = lblMessage;

        //---------- populate the different choice box configuration options ----------

        //---------- setup "language" config options ----------
        choiceBoxConfigLanguage.getItems().add(LANGUAGE_AUTO);
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
        if ( AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE) != null)
            choiceBoxConfigLanguage.setValue(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE));
        else
            choiceBoxConfigLanguage.setValue(LANGUAGE_AUTO);

        //---------- set the "max run time" value ----------
        spinnerMaxRunTime.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.MAX_RUNTIME) != null)
           spinnerMaxRunTime.getValueFactory().setValue(Integer.valueOf(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.MAX_RUNTIME)));

       //---------- set the "max output lines" value ----------
        spinnerMaxLines.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES) != null)
            spinnerMaxLines.getValueFactory().setValue(Integer.valueOf(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES)));

        //---------- set the "include source" value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.INCLUDE_SOURCE) != null)
            choiceBoxConfigIncludeSource.setValue(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.INCLUDE_SOURCE));
        else
            choiceBoxConfigIncludeSource.setValue(YES);

        //---------- set the "auto uncompress" value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS) != null)
            choiceBoxConfigAutoUncompress.setValue(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS));
        else
            choiceBoxConfigAutoUncompress.setValue(YES);

        //---------- set the "recursive process" value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.PROCESS_RECURSIVELY) != null)
            choiceBoxConfigRecursive.setValue(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.PROCESS_RECURSIVELY));
        else
            choiceBoxConfigRecursive.setValue(YES);

        //---------- set the python3 interpreter path value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER) != null)
            txtPython3Interpreter.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER));
        else
            txtPython3Interpreter.setText("");

        //---------- set the c++ compiler path value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.CPP_COMPILER) != null)
            txtCppCompiler.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.CPP_COMPILER));
        else
            txtCppCompiler.setText("");

        //---------- set the shell interpreter path value ----------
        if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.SHELL) != null)
            txtShell.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.SHELL));
        else
            txtShell.setText("");

        //---------- configure the "Test Data" list view to allow multiple selections ----------
        listTestData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listTestData.getItems().add("/Users/jvolcy/work/Spelman/Projects/data/data.txt");  //TEMP******\n");
        listTestData.getItems().add("/Users/jvolcy/work/Spelman/Projects/data/data2.txt");  //TEMP******\n");

        setStartButtonStatus();

        //set the main tab to the input/setup tab by invoking the btnInputSetupClick callback
        btnInputSetupClick();

        //disable the Output button
        btnOutput.setDisable(true);

        //---------- Initialize Misc. controls ----------
        lblLanguage.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE));

        lblMessage.setText("AutoGrader " + AutoGraderApp.version);

        //************* TEMP **************
        txtSourceDirectory.setText("/Users/jvolcy/Downloads/201709-94470-Homework 7b, P0502 - Number Pyramid, due 1021 (will count as Lab 5)-259033");

        cbName.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                cbNameClick();
            }
        });

        //---------- Configure the grading thread monitoring thread ----------
        gradingThreadMonitor.setCycleCount(Timeline.INDEFINITE);

        //---------- Configure the grading thread status alert pop-up dialog ----------
        gradingThreadStatusAlert = new Alert(Alert.AlertType.INFORMATION);
        gradingThreadStatusAlert.setTitle("Auto Grader");
        //gradingThreadStatusAlert.setHeaderText("Processing...");
        gradingThreadStatusAlert.setContentText("Click Cancel to abort.");
        gradingThreadStatusAlert.getButtonTypes().setAll(ButtonType.CANCEL);

    }
    /* ======================================================================
     * configChanged()
     * function called whenever the configuration tab is selected or
     * unselected
     * ===================================================================== */
    public void configChanged() {
        //if (tabMain.getSelectionModel().isSelected(IAGConstant.CONFIGURATION_TAB)) return;
        try {
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.LANGUAGE, choiceBoxConfigLanguage.getSelectionModel().getSelectedItem().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.MAX_RUNTIME, spinnerMaxRunTime.getValue().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.MAX_OUTPUT_LINES, spinnerMaxLines.getValue().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.INCLUDE_SOURCE, choiceBoxConfigIncludeSource.getSelectionModel().getSelectedItem().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.AUTO_UNCOMPRESS, choiceBoxConfigAutoUncompress.getSelectionModel().getSelectedItem().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.PROCESS_RECURSIVELY, choiceBoxConfigRecursive.getSelectionModel().getSelectedItem().toString());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.PYTHON3_INTERPRETER, txtPython3Interpreter.getText());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.CPP_COMPILER, txtCppCompiler.getText());
            AutoGraderApp.autoGrader.setConfiguration(AG_CONFIG.SHELL, txtShell.getText());

            lblLanguage.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE));
            /*
            if (AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE).equals(IAGConstant.LANGUAGE_AUTO))
                lblLanguage.setText("");
            else
                lblLanguage.setText(AutoGraderApp.autoGrader.getConfiguration(AG_CONFIG.LANGUAGE));
            */
        }
        catch (Exception e) {
            console("", e);
        }
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public static void console (String format, Object... arguments) {
        String formattedOutput = String.format(format, arguments);

        //if the GUI is not yet up, the consolePtr hasn't been set yet:
        //dump the output to the screen.
        try {
            consolePtr.getItems().add(formattedOutput);
            System.out.println("[c]" + formattedOutput);
        }
        catch (Exception e) {
            System.out.println("[x]" + formattedOutput);
        }
        /*
        if (consolePtr == null) {
            System.out.println("[x]" + formattedOutput);
        }
        else{
            System.out.println("[c]" + formattedOutput);
            consolePtr.getItems().add(formattedOutput);
        }
        */
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public static void message (String msg) {

        //if the GUI is not yet up, the messagePtr hasn't been set yet:
        //dump the output to the screen.
        try {
            messagePtr.setText(msg);
        }
        catch (Exception e) {
            console("[m]" + msg);
        }

    }

    /* ======================================================================
     * cbNameClick()
     * ===================================================================== */
    public void cbNameClick() {
        try {
            wvOutput.getEngine().executeScript("document.getElementById(\""
                    + cbName.getSelectionModel().getSelectedItem().toString()
                    + "\").scrollIntoView();");
        }
        catch (Exception e){
            //trap any calls made before the wvOutput engine is assigned a document
            console(e.toString());
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
        cbName.getSelectionModel().selectPrevious();
        /*
        Element inputField = wvOutput.getEngine().getDocument().getElementById("Lea grade");
        console(inputField.getAttribute("value") +";"+ inputField.getAttribute("name") +";"
                + inputField.getAttribute("text") +";"+ inputField.getAttribute("put") +";"+
                inputField.getAttribute("post") +";"+ inputField.getTagName() +";"+ inputField.getTextContent());
        inputField.setAttribute("value", "This is the new value");

        wvOutput.getEngine().executeScript("document.getElementById(\"Lea grade\").value=\"Sample Grade\"");
        wvOutput.getEngine().executeScript("document.getElementById(\"Lea comment\").value=\"Sample Comment\"");
        Object x = wvOutput.getEngine().executeScript("document.getElementById(\"Lea comment\").value");
        System.out.println(x.toString());
*/
        saveGrades();
        AutoGraderApp.autoGrader.getGradingEngine().dumpAssignments();
        //reportGenerator.writeReportToFile(txtSourceDirectory.getText() + ".html");

    }

    /* ======================================================================
     * btnNextClick()
     * The "Prev" and "Next" buttons are on the Output tab.  These are
     * navigation buttons to move back and forth through the list of
     * graded assignments.  These buttons work in conjunction with the
     * student name drop-down list.
     * ===================================================================== */
    public void btnNextClick() {
        cbName.getSelectionModel().selectNext();
    }

    /* ======================================================================
     * menuFileOpen()
     * Callback for File->Open
     * ===================================================================== */
    public void menuFileOpen() {
        // Deserialization
        AutoGraderApp.autoGrader.deSerializeGradingEngineFromDisk(txtSourceDirectory.getText() + "/object.AG");
        doPostGradingProcessing();
    }

    /* ======================================================================
     * menuFileSave()
     * Callback for File->Save
     * ===================================================================== */
    public void menuFileSave() {
        // Serialization
        AutoGraderApp.autoGrader.serializeGradingEngineToDisk(txtSourceDirectory.getText() + "/object.AG");
    }

    /* ======================================================================
     * menuFileExportHtml()
     * Callback for File->Export HTML
     * ===================================================================== */
    public void menuFileExportHtml() {

    }

    /* ======================================================================
     * menuFileQuit()
     * Callback for File->Quit
     * ===================================================================== */
    public void menuFileQuit() {
        console("Good bye\n");

        /* get and close the app's stage.  This will quit the GUI and return
        control to AutoGraderApp.main(). */
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
        btnConsole.setTextFill(Paint.valueOf("black"));

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
        btnConsole.setTextFill(Paint.valueOf("black"));
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
        btnConsole.setTextFill(Paint.valueOf("black"));
        //wvOutput.getEngine().load("file://" + AutoGraderApp.autoGrader.getGradingEngine().getOutputFileName());
    }

    /* ======================================================================
     * btnConsoleClick()
     * ===================================================================== */
    public void btnConsoleClick(){
        tabMain.getSelectionModel().select(CONSOLE_TAB);
        btnSettings.setTextFill(Paint.valueOf("black"));
        btnInputSetup.setTextFill(Paint.valueOf("black"));
        btnOutput.setTextFill(Paint.valueOf("black"));
        btnConsole.setTextFill(Paint.valueOf("blue"));
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
        /*
        if (checkNoTestData.isSelected() || listTestData.getItems().size() != 0) {
            btnStart.setDisable(false);
            listTestData.setStyle("-fx-background-color: #f0fff0; -fx-border-style: solid; -fx-border-color: #a0a0a0; -fx-border-width: 1");
        } else {
            btnStart.setDisable(true);
            listTestData.setStyle("-fx-background-color: #fff0f0; -fx-border-style: solid; -fx-border-color: #a0a0a0; -fx-border-width: 1");
        }
        */
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

        if (files == null) return;
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
     * btnSaveClick()
     * ===================================================================== */
    public void btnSaveClick() {

        menuFileSave();
        //----------  ----------
        //----------  ----------
    }


    /* ======================================================================
     * btnExportHtmlClick()
     * ===================================================================== */
    public void btnExportHtmlClick() {

        //----------  ----------
        //----------  ----------
    }


    /* ======================================================================
     * btnGradeSummaryClick()
     * ===================================================================== */
    public void btnGradeSummaryClick() {

        //----------  ----------
        //----------  ----------
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
     * selectMainPythonFile()
     * This function creates a dialog box that permits the user to
     * select the top-level python script when multiple python files are
     * detected.  The function then populates the primaryAssignmentFile
     * member of the Assignment object accordingly.
     * ===================================================================== */
    private void selectMainPythonFile(Assignment assignment) {
        //create a list of choices that
        List<String> choices = new ArrayList<>();;

        for (File f : assignment.assignmentFiles) {
            choices.add(f.getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);

        dialog.setTitle(assignment.studentName);
        dialog.setHeaderText("More than one python source was found with "
                + assignment.studentName
                + "'s submission.\nPlease select the top level python file from the list.\n"
                + "Select [Cancel] to skip this submission (not graded).");
        dialog.setContentText("Primary source:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            for (File f : assignment.assignmentFiles) {
                if (result.get().equals(f.getName())) {
                    console(f.getAbsolutePath());
                    assignment.primaryAssignmentFile = f;
                    return;
                }
            }
        }
        else
        {
            assignment.primaryAssignmentFile = null;
            System.out.println("no choice");
        }
    }


    /* ======================================================================
     * btnStart()
     * Callback for 'Start' button on Input/Setup tab
     * ===================================================================== */
    public void btnStart() {

        //---------- create aliases ----------
        // Create aliases for the autoGrader and gradingEngine members of
        // AutoGraderApp.  These are used extensively below.
        AutoGrader2 autoGrader = AutoGraderApp.autoGrader;
        GradingEngine gradingEngine = autoGrader.getGradingEngine();

        //---------- Invoke the Moodle file pre-processor ----------
        message("Pre-processing Moodle files...");

        // The pre-processor extracts assignment files and student names
        // from the downloaded and uncompressed Moodle submissions download.
        MoodlePreprocessor mpp = new MoodlePreprocessor(txtSourceDirectory.getText(),
                autoGrader.getConfiguration(AG_CONFIG.LANGUAGE),
                autoGrader.getConfiguration(AG_CONFIG.AUTO_UNCOMPRESS).equals(YES));

        //---------- handle multiple Python files ----------
        /* for Python files, if there are more than 1 source files, the user must
        * identify the primary file.  The function selectMainPythonFile()
        * removes all python source files apart from the user-specified
        * primary file. */
        for (Assignment assignment : mpp.getAssignments()) {
            //python only: we need to check for the # of programming files found
            //only 1 can be the primary
            if (assignment.language == IAGConstant.LANGUAGE_PYTHON3) {
                if (assignment.assignmentFiles.size() > 1) {
                    selectMainPythonFile(assignment);
                } else if (assignment.assignmentFiles.size() == 1) {
                    //make the lone assignment file the primary
                    assignment.primaryAssignmentFile = assignment.assignmentFiles.get(0);
                } else {
                    /* Getting here implies that no programming files were found. */
                    console("*** Assertion failure: No programming files were found.  The" +
                            "Moodle pre-preprocessor should not be in the Assignments list. ***");
                }
            }
        }
        //---------- Xfer Assignments to the Grading Engine ----------
        /* The Moodle pre-preocessor creates the Assignments files
         * array.  It also populates the student name and possibly
         * other details of each assignment.  This array list
         * becomes the assignment list for the grading engine. */
        gradingEngine.assignments = mpp.getAssignments();

        //---------- Configure the Grading Engine ----------
        gradingEngine.setCppCompiler(autoGrader.getConfiguration(AG_CONFIG.CPP_COMPILER));
        gradingEngine.setPython3Interpreter(autoGrader.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER));
        gradingEngine.setShellInterpreter(autoGrader.getConfiguration(AG_CONFIG.SHELL));
        gradingEngine.setTempOutputDirectory(txtSourceDirectory.getText());
        //gradingEngine.setOutputFileName(txtSourceDirectory.getText() + ".html");
        gradingEngine.setMaxOutputLines(Integer.valueOf(autoGrader.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES)));
        gradingEngine.setMaxRunTime(Integer.valueOf(autoGrader.getConfiguration(AG_CONFIG.MAX_RUNTIME)));

        //---------- Initialize the student name choice box ----------
        // This ChoiceBox appears on the output tab and contains student names.
        // Along with the "Prev" and "Next" buttons, t is used to navigate
        // the output HTML.
        cbName.getItems().clear();

        //---------- Add test files to the Assignment objects ----------
        /* Here, we add test files from the 'listTestData' ListView.
         * While we are going through the list of assignments,
         * also take advantage and populate the student names
         * ChoiceBox (cbName). */
        for (Assignment assignment : gradingEngine.assignments) {
            //populate the names ChoiceBox with the student names
            cbName.getItems().add(assignment.studentName);

            //initialize the test files array list
            assignment.testFiles = new ArrayList<>();

            //add the test files from the 'ListTestData' ListView
            for (Object s : listTestData.getItems()) {
                assignment.testFiles.add(s.toString());
            }

        }

        //select the first name on the student name list by default
        cbName.getSelectionModel().selectFirst();

        //disable the 'Start' button
        btnStart.setDisable(true);

        //---------- invoke the grader ----------
        message("Processing assignments...");
        lblStatus.setText("Working...");

        gradingEngine.processAssignments();

        //---------- start the grading thread monitor ----------
        /* this is a periodic function that runs in the context of
        * the UI thread.  It's main purpose is to update the message
        * label as the processing progresses. The function also
        * closes out any cleanup that needs to occur
        * post-processing. */
        gradingThreadMonitor.play();

        //---------- Display the grading status alert ----------
        gradingThreadStatusAlert.setHeaderText("Processing " + gradingEngine.getProcessingStatus().progress + " assignments.");
        gradingThreadStatusAlert.showAndWait();

        //---------- Check for usr abort ----------
        /* If we are here, then the status alert has closed.  This
        would be the result of either the user closing it, or the
        grading timeline closing it programmatically.  Check to see
        if the grading process is still running.  If it is, the
        user clicked 'Cancel' and we should abort the grading process. */
        if (gradingEngine.getProcessingStatus().bRunning) {
            System.out.println("Aborting...");
            lblStatus.setText("Aborting...");
            gradingEngine.abortGrading();
        }

        //---------- post-processing ----------

        /* post-processing of the auto-grading operation will be completed.
         * in the function doPostGradingProcessing().  This function
         * is called as the final operation of the gradingThreadMonitor
         * Timeline. */
    }

    /* ======================================================================
     * doPostGradingProcessing()
     * This function is called after the grading engine has completed its
     * work.  The function's primary goal is to invoke the report generator
     * and display its output.
     * ===================================================================== */
    private void doPostGradingProcessing() {

        //enable the Output button
        btnOutput.setDisable(false);

        reportGenerator = new ReportGenerator("AutoGrader 2.0",         //title
                txtSourceDirectory.getText(),       //header text
                AutoGraderApp.autoGrader.getGradingEngine().assignments);   //assignments

        reportGenerator.generateReport();

        wvOutput.getEngine().loadContent(reportGenerator.getDocument());
        //wvOutput.getEngine().load("file://" + AutoGraderApp.autoGrader.getGradingEngine().getOutputFileName());

        //switch to the output tab
        btnOutputClick();

        //dump the assignments to the console  ************ TEMP **************
        AutoGraderApp.autoGrader.getGradingEngine().dumpAssignments();

        //reportGenerator.writeReportToFile(txtSourceDirectory.getText() + ".html");

    }

    /* ======================================================================
     * xferGradesFromWebViewToAssignmentObject()
     * retrieves the instructor-entered grades and comments for the
     * given assignment and transfers them to the Assignment object.
     * ===================================================================== */
    void xferGradesFromWebViewToAssignmentObject(Assignment assignment) {
        String gradeId = assignment.studentName + ReportGenerator.HTML_GRADE_ID_SUFFIX;
        String commentId = assignment.studentName + ReportGenerator.HTML_COMMENT_ID_SUFFIX;

        //set the grade
        try {
            Object oGrade = wvOutput.getEngine().executeScript("document.getElementById(\"" + gradeId + "\").value");
            assignment.grade = Integer.valueOf(oGrade.toString());
        }
        catch (Exception e) {
            //if the id is not found or the conversion from str->int fails, set the grade to null
            console(assignment.studentName + " : " + e.toString());
            assignment.grade = null;
        }

        //set the comment
        try {
            Object oComment = wvOutput.getEngine().executeScript("document.getElementById(\"" + commentId + "\").value");
            assignment.instructorComment = oComment.toString();
        }
        catch (Exception e) {
            //if the id is not found, set the comment to null
            assignment.instructorComment = null;
        }

    }


    /* ======================================================================
     * xferGradesFromAssignmentObjectToWebView()
     * transfers the grade and comment fields of the supplied assignment
     * to the corresponding fields in the web view.
     *
     *         ****This function has not been tested ****
     * ===================================================================== */
    void xferGradesFromAssignmentObjectToWebView(Assignment assignment) {
        String gradeId = assignment.studentName + ReportGenerator.HTML_GRADE_ID_SUFFIX;
        String commentId = assignment.studentName + ReportGenerator.HTML_COMMENT_ID_SUFFIX;

        //set the grade and comment on the webview
        try {
            wvOutput.getEngine().executeScript("document.getElementById(\"" + gradeId + "\").value =\"" + assignment.grade.toString()+"\"");
            wvOutput.getEngine().executeScript("document.getElementById(\"" + commentId + "\").value =\"" + assignment.instructorComment+"\"");
        } catch (Exception e) {
            console(assignment.studentName + " : " + e.toString());
        }
    }


    /* ======================================================================
     * saveGrades()
     * saves the instructor grades and comments to the Assignment
     * array list.
     * ===================================================================== */
    void saveGrades() {
        for (Assignment assignment : AutoGraderApp.autoGrader.getGradingEngine().assignments) {
            xferGradesFromWebViewToAssignmentObject(assignment);
        }
    }


    /* ======================================================================
     * gradingThreadMonitor
     * This timeline is essentially a periodic function that executes with
     * a period of 0.25 sec.  The function has multiple purposes and is
     * intended to run only while the grading operation is in process.  The
     * function begins with a call to gradingThreadMonitor.start() in the
     * btnStart() handler.  It monitors the ProcessingStatus structure
     * updated by the grading engine while processing assignments.  The
     * timer auto-terminates when the processing reports to be complete.
     * That is, when ProcessingStatus.bRunning is false.  The function
     * performs a few post-processing tasks like enabling the output
     * button and switching to the output tab.
     * ===================================================================== */
    private Timeline gradingThreadMonitor = new Timeline(new KeyFrame(Duration.seconds(GRADING_TIMELINE_PERIOD), new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            //create a ProcessingStatus object to monitor the status of the grading processor
            ProcessingStatus ps = AutoGraderApp.autoGrader.getGradingEngine().getProcessingStatus();

            /* we will keep running so long as the grading engine is still
            * processing. This is indicated by the bRunning member of the
            * ProcessingStatus object being "true". */
            if (ps.bRunning) {
                //update the lblMessage text
                message("Processing submission for " + ps.message + "...");
                /* calculate the % complete and update the pop-up alert initiated
                * by btnClick. */
                gradingThreadStatusAlert.setHeaderText("Processing submissions..." +
                        100*(ps.progress-ps.startVal)/(ps.endVal-ps.startVal) + "%.\n[" +
                ps.message + "]");

                lblStatus.setText("Working..." + 100*(ps.progress-ps.startVal)/(ps.endVal-ps.startVal) + "%");

                /* our work is done for this invocation.  The timeline will
                * execute again in 0.25 seconds. */
                return;
            }
            else {
                /* If we are here, ps.bRunning is false, meaning that the
                * the grading engine is done.  We will self-terminate
                * the current timeline, close the status alert dialog,
                * and do other post-processing things. */
                gradingThreadMonitor.stop();        //end the current timeline
                gradingThreadStatusAlert.close();   //close the alert dialog

                message("Processing Done.");    //update the lblMessage
                lblStatus.setText("Ready.");

                //re-enable the 'Start' button
                btnStart.setDisable(false);

                //call the post-processing function
                doPostGradingProcessing();
            }
        }
    }));


}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
