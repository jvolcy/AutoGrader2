package AutoGraderApp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;

import static AutoGraderApp.Controller.console;

/* ======================================================================
 * AGDocument class
 * ===================================================================== */
class AGDocument implements java.io.Serializable {
    public String assignmentName;
    public GradingEngine gradingEngine;
    public String htmlReport;
    public File moodleDirectory;
    public ArrayList<File> testDataFiles;         //the length of this list is the # of test cases

}

/* ======================================================================
 * AutoGraderApp class
 * ===================================================================== */
public class AutoGrader2 implements IAGConstant {

    //---------- AutoGrader options ----------
    private Dictionary<String, String> ag_config = new Hashtable<String, String>();

    //---------- Configuration File Path ----------
    private String configFileName;

    private AGDocument agDocument;
    //private GradingEngine gradingEngine;

    /* ======================================================================
     * AutoGraderApp()
     * constructor
     * ===================================================================== */
     AutoGrader2() {
        console("AutoGraderApp constructor...");

        //---------- set the path to the JSON config file ----------
        // v. 2.1.1, json file now hidden and stored in user.home instead of user.dir
        //"user.dir" = the current working directory
        //"user.home" = the user's home directory
        String cwd = System.getProperty("user.home");
        configFileName = Paths.get(cwd, CONFIG_FILENAME).toString();
        console("Config file path = '" + configFileName + "'");

        //---------- setup app configurations ----------
        setupConfiguration();

        //---------- initialize the grading engine ----------
         agDocument = new AGDocument();
         agDocument.gradingEngine = new GradingEngine();
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
            console("autoLocatePython3Interpreter(): " + e.toString());
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

            /* if still not found, check for 'cpp' */
            if (cppPath == null) {
                //use "which c++" to try to find a Python 3 interpreter
                p = r.exec(new String[]{"which", "cpp"});
                p.waitFor();
                b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                cppPath = b.readLine();
                b.close();
            }

        } catch (Exception e) {
            //console("", e);
            console("autoLocateCppCompiler(): " + e.toString());
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
            console("autoLocateShell(): " + e.toString());
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
     * getConfiguration()
     * This function returns the value for the supplied configuration key.
     * If the key is not in the configuraiton dictionary ag_config, the
     * function returns null.  All configuration keys and values are strings.
     * ===================================================================== */
    public String setConfiguration(String key, String value) {
        //console("setting conf. for " + key);
        return ag_config.put(key, value);
    }

    /* ======================================================================
     * setupConfiguration()
     * This function sets up default AG2 configurations.
     * The values used for the configurations are either hardcoded defaults
     * or generated by probing the system.
     * ===================================================================== */
    private void setupConfiguration() {
        //---------- auto-locate python3 interpreter ----------
        String python3Path = autoLocatePython3Interpreter();

        if (python3Path == null) {
            python3Path = "";   //2.1.1
            console("No auto-detected python3 interpreter.");
        }
        else
            console("Found a Python3 interpreter at '" + python3Path + "'");

        //---------- auto-locate c++ compiler ----------
        String cppPath = autoLocateCppCompiler();
        if (cppPath == null) {
            cppPath = "";       //2.1.1
            console("No auto-detected c++ compiler.");
        }
        else
            console("Found a c++ compiler at '" + cppPath + "'");

        //---------- auto-locate shell interpreter ----------
        String shellPath = autoLocateShell();
        if (shellPath == null) {
            shellPath = "";     //2.1.1
            console("No auto-detected shell interpreter.");
        }
        else
            console("Found a shell interpreter at '" + shellPath + "'");

        //---------- Generate or set the default AG options ----------
        ag_config.put(AG_CONFIG.LANGUAGE, LANGUAGE_AUTO);
        ag_config.put(AG_CONFIG.MAX_RUNTIME, "3");
        ag_config.put(AG_CONFIG.MAX_OUTPUT_LINES, "100");
        ag_config.put(AG_CONFIG.INCLUDE_SOURCE, YES);
        ag_config.put(AG_CONFIG.AUTO_UNCOMPRESS, YES);
        ag_config.put(AG_CONFIG.PROCESS_RECURSIVELY, YES);
        ag_config.put(AG_CONFIG.PYTHON3_INTERPRETER, python3Path);
        ag_config.put(AG_CONFIG.CPP_COMPILER, cppPath);
        ag_config.put(AG_CONFIG.SHELL, shellPath);
        ag_config.put(AG_CONFIG.ENABLE_MOSS, DISABLED);
        ag_config.put(AG_CONFIG.MOSS_USERID, "");
        ag_config.put(AG_CONFIG.MOSS_MAX_MATCHES, "10");
        ag_config.put(AG_CONFIG.MOSS_NUM_MATCH_FILES, "250");
        ag_config.put(AG_CONFIG.MOSS_USE_MOSS, NO);
        ag_config.put(AG_CONFIG.MOSS_SERVER, MossClient.DEFAULT_MOSS_SERVER);
        ag_config.put(AG_CONFIG.MOSS_PORT, String.valueOf(MossClient.DEFAULT_MOSS_PORT));

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
        // read and parsing the config file
        try {
            Object obj = new JSONParser().parse(new FileReader(configFileName));

            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;

            // getting firstName and lastName
            //String firstName = (String) jo.get("firstName");
            //String lastName = (String) jo.get("lastName");

            //read the configuration
            ArrayList<String> configKeys = Collections.list(ag_config.keys());

            for (String key : configKeys) {
                //if the key is not currently specified in the file, use the default.
                //also, if the key's value is null, use the default.
                if (jo.containsKey(key) && (jo.get(key) != ""))
                    ag_config.put(key, (String)jo.get(key));
                    console("ag_config("+key+") = " + ag_config.get(key));
            }
            /*
            ag_config.put(AG_CONFIG.LANGUAGE, (String) jo.get(AG_CONFIG.LANGUAGE));
            ag_config.put(AG_CONFIG.MAX_RUNTIME, (String) jo.get(AG_CONFIG.MAX_RUNTIME));
            ag_config.put(AG_CONFIG.MAX_OUTPUT_LINES, (String) jo.get(AG_CONFIG.MAX_OUTPUT_LINES));
            ag_config.put(AG_CONFIG.INCLUDE_SOURCE, (String) jo.get(AG_CONFIG.INCLUDE_SOURCE));
            ag_config.put(AG_CONFIG.AUTO_UNCOMPRESS, (String) jo.get(AG_CONFIG.AUTO_UNCOMPRESS));
            ag_config.put(AG_CONFIG.PROCESS_RECURSIVELY, (String) jo.get(AG_CONFIG.PROCESS_RECURSIVELY));
            ag_config.put(AG_CONFIG.PYTHON3_INTERPRETER, (String) jo.get(AG_CONFIG.PYTHON3_INTERPRETER));
            ag_config.put(AG_CONFIG.CPP_COMPILER, (String) jo.get(AG_CONFIG.CPP_COMPILER));
            ag_config.put(AG_CONFIG.SHELL, (String) jo.get(AG_CONFIG.SHELL));
            ag_config.put(AG_CONFIG.MOSS_SERVER, (String) jo.get(AG_CONFIG.MOSS_SERVER));
            ag_config.put(AG_CONFIG.MOSS_PORT, (String) jo.get(AG_CONFIG.MOSS_PORT));

             */
        }
        catch (Exception e) {
            console("loadConfiguration(): " + e.toString());
        }
    }

    /* ======================================================================
     * saveConfiguration()
     * This function saves the AG2 configurations to a JSON file.
     *
     * configFileName is the full path of the JSON configuration file.
     * ===================================================================== */
    public void saveConfiguration() {

        console("Saving " + configFileName);
        // creating JSONObject
        JSONObject jo = new JSONObject();

        //putting data to JSONObject
        //note that this will overwrite existing keys, but will not delete unknown keys
        //that may exist in a different version of the json file
        ArrayList<String> configKeys = Collections.list(ag_config.keys());

        for (String key : configKeys) {
            jo.put(key, ag_config.get(key));
        }

        /*
        jo.put(AG_CONFIG.LANGUAGE, ag_config.get(AG_CONFIG.LANGUAGE));
        jo.put(AG_CONFIG.MAX_RUNTIME, ag_config.get(AG_CONFIG.MAX_RUNTIME));
        jo.put(AG_CONFIG.MAX_OUTPUT_LINES, ag_config.get(AG_CONFIG.MAX_OUTPUT_LINES));
        jo.put(AG_CONFIG.INCLUDE_SOURCE, ag_config.get(AG_CONFIG.INCLUDE_SOURCE));
        jo.put(AG_CONFIG.AUTO_UNCOMPRESS, ag_config.get(AG_CONFIG.AUTO_UNCOMPRESS));
        jo.put(AG_CONFIG.PROCESS_RECURSIVELY, ag_config.get(AG_CONFIG.PROCESS_RECURSIVELY));
        jo.put(AG_CONFIG.PYTHON3_INTERPRETER, ag_config.get(AG_CONFIG.PYTHON3_INTERPRETER));
        jo.put(AG_CONFIG.CPP_COMPILER, ag_config.get(AG_CONFIG.CPP_COMPILER));
        jo.put(AG_CONFIG.SHELL, ag_config.get(AG_CONFIG.SHELL));
         */

        // writing JSON to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(configFileName))) {
            bw.write(jo.toJSONString());
            bw.close();
        } catch (IOException e) {
            console("saveConfiguration(): " +  e.toString());
        }
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public AGDocument getAgDocument() {
        return agDocument;
    }

    /* ======================================================================
     * menuFileOpen()
     * Callback for File->Open
     * ===================================================================== */
    public void deSerializeFromDisk(String fileName) throws Exception {
        //De-serialization
        // Reading the object from a file
        FileInputStream file = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(file);

        // Method for deserialization of object
        agDocument = (AGDocument) in.readObject();

        in.close();
        file.close();

        console("Grading Engine successfully de-serialized.");
    }


    /* ======================================================================
     * menuFileSave()
     * Callback for File->Save
     * ===================================================================== */
    public void serializeToDisk (String fileName) throws Exception {
        // Serialization
        //Saving of object in a file
        FileOutputStream file = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
        out.writeObject(agDocument);

        out.close();
        file.close();

        console("Grading Engine successfully serialized.");
    }

}



/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
