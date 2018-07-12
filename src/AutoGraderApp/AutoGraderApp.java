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
    public static final String appName = "Spelman AutoGrader 2";
    public static final String version = "2.0";
    public static final String copyrightText = "copyright 2016-2018";
    public static final String credits = "J Volcy";

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


    /* ======================================================================
     * Help HTML string
     * ===================================================================== */
    public static final String HelpHtml = "<!DOCTYPE html PUBLIC \\\"-//W3C//DTD HTML 4.01//EN\\\" \\\"http://www.w3.org/TR/html4/strict.dtd\\\">\n" +
            "<html><head><meta content=\\\"text/html; charset=ISO-8859-1\\\" http-equiv=\\\"content-type\\\"><title>AutoGrader 2.0 Help</title></head><body>\n" +
            "<div style=\\\"text-align: center; color: rgb(51, 51, 255);\\\"><big style=\\\"font-weight: bold;\\\">Spelman AutoGrader 2.0</big><br>\n" +
            "</div>\n" +
            "&nbsp;<br>\n" +
            "<big style=\\\"color: rgb(51, 51, 255);\\\">Introduction</big><br>\n" +
            "The Spelman AutoGrader 2 program is designed to help grade Python and\n" +
            "C++ programs submitted through Moodle.&nbsp; To use the program,\n" +
            "perform a 'download all submissions' of the target assignment from\n" +
            "Moodle.&nbsp; Extract the downloaded zip file.&nbsp; This will create a\n" +
            "directory on disk that holds all student submissions.&nbsp; We will\n" +
            "call this directory our 'top-level directory' (TLD).&nbsp; The TLD\n" +
            "should contain as many sub-directories as there are submitted\n" +
            "assignments.&nbsp; The names of these sub-directories should be\n" +
            "formatted as &#8220;student name_assignment&#8221;.&nbsp; If your TLD does not\n" +
            "contain sub-directories, please see the section on <a href=\\\"#Moodle_Download_Settings\\\">Moodle Download Settings</a>.<br>\n" +
            "&nbsp;<br>\n" +
            "The individual student directories may contain program files (*.cpp,\n" +
            "*.h, *.py, etc) zip files (*.zip) and subdirectories.&nbsp; The\n" +
            "AutoGrader first searches for zip files.&nbsp; If any are found and the\n" +
            "auto-uncompress option is selected, these are uncompressed into a\n" +
            "subdirectory with the same name as the zip file.&nbsp; If none are\n" +
            "found, the AutoGrader will search for programming files.&nbsp; This\n" +
            "search continues recursively through sub-directories until a\n" +
            "programming file is found.&nbsp; If a programming language is\n" +
            "specified, the AutoGrader looks for programming files for that language\n" +
            "only.&nbsp; If the programming language is set to &#8220;Auto&#8221; (recommended),\n" +
            "the AutoGrader searches for any programming files and attempts to\n" +
            "classify the submission as a Python or C++ program.<br>\n" +
            "&nbsp;<br>\n" +
            "For C++, one (and only one) of the multiple source files should contain\n" +
            "a main().&nbsp; For Python, the top-level module must be identified\n" +
            "among the multiple .py files.&nbsp; By default, AutoGrader assumes that\n" +
            "top-level Python modules are named 'main.py'.&nbsp; For this reason, it\n" +
            "is advisable to instruct students to name their top-level Python\n" +
            "modules 'main.py' when submitting multi-file projects.<br>\n" +
            "&nbsp;<br>\n" +
            "&nbsp;<br>\n" +
            "<big style=\\\"color: rgb(51, 51, 255);\\\">Test Data Files</big><br>\n" +
            "Xxx<br>\n" +
            "&nbsp;<br>\n" +
            "<big style=\\\"color: rgb(51, 51, 255);\\\">Configuration Options</big><br>\n" +
            "Xxx<br>\n" +
            "&nbsp;<br>\n" +
            "&nbsp;<br>\n" +
            "&nbsp;<br>\n" +
            "<big style=\\\"color: rgb(51, 51, 255);\\\"><a name=\\\"Moodle_Download_Settings\\\"></a>Moodle Download Settings</big><br>\n" +
            "If the downloaded student submissions are not in individual folders under the top-level directory, follow the instructions below<br>\n" +
            "1.&nbsp;&nbsp;&nbsp;&nbsp; Verify that you are using Moodle 3.1 or later<br>\n" +
            "2.&nbsp;&nbsp;&nbsp;&nbsp; Visit the assignment and click on 'View/grade all submissions'<br>\n" +
            "3.&nbsp;&nbsp;&nbsp;&nbsp; Scroll to the very bottom of the page and\n" +
            "verify that the 'Download submissions in folders' option is checked\n" +
            "(You will only need to do this once.&nbsp; Moodle will remember your\n" +
            "choice.)<br>\n" +
            "4.&nbsp;&nbsp;&nbsp;&nbsp; Under 'Grading action' select 'Download all submissions'.<br>\n" +
            "\n" +
            "</body></html>";
}



/* ======================================================================
 * To Do
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
