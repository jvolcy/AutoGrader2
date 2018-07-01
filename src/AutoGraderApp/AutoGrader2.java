package AutoGraderApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;

import static AutoGraderApp.Controller.console;

/* ======================================================================
 * AutoGraderApp class
 * ===================================================================== */
public class AutoGrader2 implements IAGConstant {

    //---------- AutoGrader options ----------
    private Dictionary<String, String> ag_config = new Hashtable<String, String>();

    //---------- Configuration File Path ----------
    private String configFileName;

    private GradingEngine gradingEngine;

    /* ======================================================================
     * AutoGraderApp()
     * constructor
     * ===================================================================== */
     AutoGrader2() {

        console("AutoGraderApp constructor...");

        //---------- set the path to the JSON config file ----------
        String cwd = System.getProperty("user.dir");
        configFileName = Paths.get(cwd, CONFIG_FILENAME).toString();
        console("Config file path = '" + configFileName + "'");

        //---------- setup app configurations ----------
        setupConfiguration(configFileName);

        //---------- initialize the grading engine ----------
        gradingEngine = new GradingEngine();

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
    private String autoLocatePython3Interpreter() {
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
    private String autoLocateCppCompiler() {
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
    private String autoLocateShell() {
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
    public String getConfiguration(String key) {
        //console("getting conf. for " + key);
        return ag_config.get(key);
    }

    /* ======================================================================
     * setupConfiguration()
     * This function sets up default AG2 configurations.
     * The values used for the configurations are either hardcoded defaults
     * or generated by probing the system.
     * ===================================================================== */
    private void setupConfiguration(String configFileName) {
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
        loadConfiguration();

    }

    /* ======================================================================
     * loadConfiguration()
     * This function loads the AG2 configurations from a JSON file.
     *
     * configFileName is the full path of the JSON configuration file.
     * ===================================================================== */
    private void loadConfiguration() {

    }

    /* ======================================================================
     * saveConfiguration()
     * This function saves the AG2 configurations to a JSON file.
     *
     * configFileName is the full path of the JSON configuration file.
     * ===================================================================== */
    public void saveConfiguration() {

    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public GradingEngine getGradingEngine() {
        return gradingEngine;
    }

}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
