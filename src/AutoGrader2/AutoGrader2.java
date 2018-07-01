package AutoGrader2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;

import static AutoGrader2.Controller.console;

/* ======================================================================
 * AutoGrader2 class
 * ===================================================================== */
public class AutoGrader2 extends Application implements IAGConstant {

    //---------- AutoGrader options ----------
    private static Dictionary<String, String> ag_config = new Hashtable<String, String>();

    //---------- Configuration File Path ----------
    private static String configFileName = null;

    private static GradingEngine gradingEngine;

    /* ======================================================================
     * start()
     * This function is automatically called after the primary stage
     * has been created.
     * This is a good place to customize the appearance of the stage.
     * ===================================================================== */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("AutoGrader2.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AutoGrader2.fxml"));
        Parent root = loader.load();

        // Get the Controller from the FXMLLoader
        Controller controller = loader.getController();
        controller.setGradingEngine(gradingEngine);

        primaryStage.setTitle("Spelman AutoGrader 2.0");
        primaryStage.setScene(new Scene(root, MIN_STAGE_WIDTH, MIN_STAGE_HEIGHT));
        primaryStage.setMinWidth(MIN_STAGE_WIDTH);
        primaryStage.setMinHeight(MIN_STAGE_HEIGHT);

        primaryStage.show();
    }

    /* ======================================================================
     * autoLocatePython3Interpreter()
     * This function attempts to automatically find the path to an
     * installed python3 interpreter on the current system.  The function
     * assumes that the 'which' command is available on the system.  That
     * is, it assumes that we are on a *nix system or a Windows system
     * with cygwin installed.  The function also assumes that the
     * interpreter is called, aliased or linked as 'python3'.
     * The function first uses the 'which python3' system command to
     * check for the p3 interpreter in the current search path.  If that
     * fails, the function specifically checks the path "/usr/local/bin/python3".
     * If a p3 interpreter is found, its path is returned.  If not,
     * the function returns null.
     * ===================================================================== */
    private static String autoLocatePython3Interpreter() {
        String python3Path = null;
        try {
            Runtime r = Runtime.getRuntime();

            //first, use "which python3" to try to find a Python 3 interpreter
            Process p = r.exec(new String[]{"which", "python3"});
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            python3Path = b.readLine();
            b.close();

            /* if "which python3" did not yield a suitable interpreter, check for one at
             /usr/local/bin/python3. */
            if (python3Path == null) {
                final String FALLBACK_PYTHON3_PATH = "/usr/local/bin/python3";

                if (Files.isRegularFile(Paths.get(FALLBACK_PYTHON3_PATH))) {
                    python3Path = FALLBACK_PYTHON3_PATH;
                }
            }

        } catch (Exception e) {
            console("", e);
        }

        return python3Path;

    }

    /* ======================================================================
     * autoLocateCppCompiler()
     * This function attempts to automatically find the path to an
     * installed C++ compiler on the current system.  The function
     * assumes that the 'which' command is available on the system.  That
     * is, it assumes that we are on a *nix system or a Windows system
     * with cygwin installed.  The function also assumes that the
     * interpreter is called, aliased or linked as 'g++' or 'c++'.
     * If a c++ compiler is found, its path is returned.  If not,
     * the function returns null.
     * ===================================================================== */
    private static String autoLocateCppCompiler() {
        String cppPath = null;
        try {
            Runtime r = Runtime.getRuntime();

            //use "which g++" to try to find a c++ compiler
            Process p = r.exec(new String[]{"which", "g++"});
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            cppPath = b.readLine();
            b.close();

            /* if "which g++" did not yield a suitable compiler, check for 'c++' */
            if (cppPath == null) {
                //use "which c++" to try to find a Python 3 interpreter
                p = r.exec(new String[]{"which", "c++"});
                p.waitFor();
                b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                cppPath = b.readLine();
                b.close();
            }

        } catch (Exception e) {
            //console("", e);
            console(e.getMessage());
        }

        return cppPath;

    }

    /* ======================================================================
     * autoLocateShell()
     * ===================================================================== */
    private static String autoLocateShell() {
        String shellPath = null;
        try {
            Runtime r = Runtime.getRuntime();

            //use "which bash" to try to find a shell interpreter
            Process p = r.exec(new String[]{"which", "bash"});
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            shellPath = b.readLine();
            b.close();

            /* if "which bash" did not yield a suitable shell, check for 'sh' */
            if (shellPath == null) {
                //use "which sh" to try to find a shell interpreter
                p = r.exec(new String[]{"which", "sh"});
                p.waitFor();
                b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                shellPath = b.readLine();
                b.close();
            }

        } catch (Exception e) {
            //console("", e);
            console(e.getMessage());
        }

        return shellPath;

    }

    /* ======================================================================
     * getConfiguration()
     * This function returns the value for the supplied configuration key.
     * If the key is not in the configuraiton dictionary ag_config, the
     * function returns null.  All configuration keys and values are strings.
     * ===================================================================== */
    public static String getConfiguration(String key) {
        //console("getting conf. for " + key);
        return ag_config.get(key);
    }

    /* ======================================================================
     * setupConfiguration()
     * This function sets up default AG2 configurations.
     * The values used for the configurations are either hardcoded defaults
     * or generated by probing the system.
     * ===================================================================== */
    private static void setupConfiguration(String configFileName) {
        //---------- auto-locate python3 interpreter ----------
        String python3Path = autoLocatePython3Interpreter();
        if (python3Path == null)
            console("No auto-detected python3 interpreter.");
        else
            console("Found a Python3 interpreter at '" + python3Path + "'");

        //---------- auto-locate c++ compiler ----------
        String cppPath = autoLocateCppCompiler();
        if (cppPath == null)
            console("No auto-detected c++ compiler.");
        else
            console("Found a c++ compiler at '" + cppPath + "'");

        //---------- auto-locate shell interpreter ----------
        String shellPath = autoLocateShell();
        if (shellPath == null)
            console("No auto-detected shell interpreter.");
        else
            console("Found a shell interpreter at '" + shellPath + "'");

        //---------- Generate or set the default AG options ----------
        ag_config.put(AG_CONFIG.LANGUAGE, LANGUAGE_PYTHON3);
        ag_config.put(AG_CONFIG.MAX_RUNTIME, "3");
        ag_config.put(AG_CONFIG.MAX_OUTPUT_LINES, "200");
        ag_config.put(AG_CONFIG.INCLUDE_SOURCE, YES);
        ag_config.put(AG_CONFIG.AUTO_UNCOMPRESS, YES);
        ag_config.put(AG_CONFIG.PROCESS_RECURSIVELY, YES);
        ag_config.put(AG_CONFIG.PYTHON3_INTERPRETER, python3Path);
        ag_config.put(AG_CONFIG.CPP_COMPILER, cppPath);
        ag_config.put(AG_CONFIG.SHELL, shellPath);

        //---------- Overwrite the default AG options with data from the JSON file ----------
        loadConfiguration(configFileName);

    }

    /* ======================================================================
     * loadConfiguration()
     * This function loads the AG2 configurations from a JSON file.
     *
     * configFileName is the full path of the JSON configuration file.
     * ===================================================================== */
    private static void loadConfiguration(String configFileName) {

    }

    /* ======================================================================
     * saveConfiguration()
     * This function saves the AG2 configurations to a JSON file.
     *
     * configFileName is the full path of the JSON configuration file.
     * ===================================================================== */
    private static void saveConfiguration(String configFileName) {

    }

    /* ======================================================================
     * main()
     * Entry point into the application.
     * ===================================================================== */
    public static void main(String[] args) {
        //---------- set the path to the JSON config file ----------
        String cwd = System.getProperty("user.dir");
        configFileName = Paths.get(cwd, CONFIG_FILENAME).toString();
        console("Config file path = '" + configFileName + "'");

        //---------- setup app configurations ----------
        setupConfiguration(configFileName);

        //---------- initialize the grading engine ----------
        gradingEngine = new GradingEngine();

        //---------- start the GUI ----------
        launch(args);

        //---------- Commit the AG options to the JSON file ----------
        saveConfiguration("");

        console("Exiting main()...");
    }
}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
