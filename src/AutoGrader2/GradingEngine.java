package AutoGrader2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static AutoGrader2.Controller.console;

/* ======================================================================
 * structure to hold the results returned by the shellExec() function.
 * ===================================================================== */
class shellExecResult {
    Boolean bTimedOut;
    Boolean bMaxLinesExceeded;
    String output;
}

/* ======================================================================
 * GradingEngine class
 * This class compiles and executes (or interprets) student assignments.
 * The central function of the class is processFiles().
 * At a minimum, the caller should specify the following prior to
 * calling processFiles:
 * - an ArrayList of Assignments
 * - a cppCompiler or python3Interpreter, as appropriate
 * - testDataFiles, if required
 * ===================================================================== */
public class GradingEngine implements IAGConstant {
    /* The assignments list. */
    public ArrayList<Assignment> assignments;
    public ArrayList<File> testDataFiles;
    private String outputFileName;
    private String outputDirectory;
    public boolean bIncludeSourceInOutput;
    public int maxRunTime;
    public int maxOutputLines;
    public String cppCompiler;
    public String python3Interpreter;
    public String shell;
    private ReportGenerator reportGenerator;


    /* ======================================================================
     * GradingEngine Constructor
     * ===================================================================== */
    GradingEngine() {

        //---------- set a few default values ----------
        maxRunTime = 3;     //3 seconds
        maxOutputLines = 100;   //100 lines of output max per program
        bIncludeSourceInOutput = true;
        setOutputFileName("AG_output.html");
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public void setOutputFileName(String fileName) {
        outputFileName = fileName;
        File f = new File(fileName);
        outputDirectory =  f.getParent();
        console("output directory = " + outputDirectory);
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public String getOutputFileName() {
        return outputFileName;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    private String fileNameFromPathName(String pathName)
    {
        //extract the filename from the path name
        File f = new File(pathName);
        return f.getName();
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    private String readFromFile(String filepath) {
        try {
            String text = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
            return text;
        } catch (Exception e) {
            console(e.getMessage());
        }
        return null;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    private String readFromFile(File file) {
        return readFromFile(file.getAbsolutePath());
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    private String readFromFile(String filepath, int maxLines) {
        //initialize the output string
        String text = "";

        //attempt to read the file line by line
        try {
            BufferedReader b = new BufferedReader(new FileReader(new File(filepath)));

            String readLine;    //individual lines in the file
            int numLines = 0;   //line #

            //read the file line by-line until we are out of lines or have
            //reached the max allowed
            while ((readLine = b.readLine()) != null && numLines < maxLines) {
                text += readLine + "\n";
                numLines++;
            }
        } catch (Exception e) {
            console(e.getMessage());
        }
        return text;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    private String readFromFile(File file, int maxLines) {
        return readFromFile(file.getAbsolutePath(), maxLines);
    }

    /* ======================================================================
     * This function attempts to determine the PID of a running process
     * based on any substring, the token, found on the command line used
     * to launch the the process.  The token should be as unique a
     * substring as possible.  A good substring would be the name of a
     * file that is a part of the command-line argument.
     * The function uses the "ps -eo pid,command" command to list the
     * PID and associated command-line used to launch the different
     * running processes on the system.  The returned ArraList is a list
     * of PIDs for all commands that match the token.
     * ===================================================================== */
    private ArrayList<Integer> getPidFromToken(String token) {
        ArrayList<Integer> pids = new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec("ps -eo pid,command");
            p.waitFor(1, TimeUnit.SECONDS);

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                //check for the token
                if (line.contains(token)) {
                    String pidStr = line.trim().split(" ")[0];
                    pids.add( Integer.valueOf(pidStr) );
                    console(pidStr + "-->" + line.trim().split(" ")[1]);
                }
            }
        } catch (Exception e) {
            console(e.getMessage());
        }
        return pids;
    }


    /* ======================================================================
     * shellExec()
     * function that executes a command inside a shell.  The function
     * returns the output of the shell session which is a combination of
     * the stdout and stderr.
     * The identifyingToken is an optional string that is part of
     * the command to be executed.  This should be as unique as string
     * as possible.  It is used to determine the ID of the launched
     * process by searching the output of "ps -eo pid,command" which
     * lists PID and the command line used to launch the corresponding
     * process.
     * ===================================================================== */
    private shellExecResult shellExec(String args, int timeout_sec, int maxOutputLines, String identifyingToken) {
        shellExecResult execResult = new shellExecResult();
        execResult.bTimedOut = false;

        //create a temp file to capture program output
        File tmpFile = new File(outputDirectory + "/TEMP.AG2");

        //delete any previous temp file in the output directory.
        try {
            //delete the temporary file if it exists, ignoring any "file not found" errors.
            tmpFile.delete();
        } catch (Exception e) {
            //ignore errors from the delete operation
        }

        //form the command string, redirecting both stdout and stderr to the temporary file
        String[] cmd = {shell, "-c", args + " >> \"" + tmpFile.getPath() + "\" 2>&1"};

        //concatenate the cmd array string for display on the console
        String cmdStr = "";
        for (String s : cmd) {
            cmdStr += s + " ";
        }
        console("Executing " + args + "\n  as  " + cmdStr );

        try {
            //attempt to execute the command
            Process p;
            p = Runtime.getRuntime().exec(cmd);

            //attempt to detect the PIDs of the launched shell and command
            ArrayList<Integer> pids = getPidFromToken(identifyingToken);
            console("Started process(es) %s", pids.toString());

            //wait no more than the specified timeout for the process to complete.
            //a timeout of zero means wait indefinitely.
            if (timeout_sec > 0)
                p.waitFor(timeout_sec, TimeUnit.SECONDS);
            else
                p.waitFor();

            //check if the process is still alive.  If it is, set the timeout
            //flag and attempt to forcefully terminate it.
            if (p.isAlive()) {
                execResult.bTimedOut = true;
                console ("Killing process %s. Run time exceeds max value of %d seconds.", p.toString(), maxRunTime);

                //attempt to destroy the process using the Java supplied methods
                p.destroy();
                p.destroyForcibly();

                //force the issue by killing all identified processes with
                //a unix kill command
                for (Integer pid : pids) {
                    String killCmd = "kill -9 " + pid.toString();
                    console(killCmd);
                    Runtime.getRuntime().exec(killCmd);
                }
            }
        } catch (Exception e) {
            console(e.getMessage());
        }

        //read in the output of the executed command from the temp file
        execResult.output = readFromFile(tmpFile, maxOutputLines);

        //delete the tmp file
        tmpFile.delete();

        return execResult;

    }

    /* ======================================================================
     * execAssignment()
     *
     * ===================================================================== */
    private void execAssignment(Assignment assignment) {

        if (assignment.testFiles == null) return;

        int numTests = assignment.testFiles.size();

        //create arrays to hold test results
        assignment.runtimeErrors = new String[numTests];
        assignment.progOutputs = new String[numTests];

        //create a results structure for the calls to shellExec()
        shellExecResult execResult = new shellExecResult();

        int maxRunTime = Integer.valueOf(AutoGrader2.getConfiguration(AG_CONFIG.MAX_RUNTIME));
        int maxLines = Integer.valueOf(AutoGrader2.getConfiguration(AG_CONFIG.MAX_OUTPUT_LINES));

        //********* TEMP ******** For python, there should only be on assignment file;  for C++, it doesn't matter
        String sourceFile = assignment.assignmentFiles.get(0).getAbsolutePath();

        //run the code for each test case
        for (int i = 0; i< numTests; i++) {

            String dataFileName = assignment.testFiles.get(i);
            String cmd = "\"" + AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER) + "\" " +
                    "\"" + sourceFile + "\"" + " < \"" + dataFileName + "\"";

            execResult = shellExec(cmd, maxRunTime, maxLines, assignment.assignmentFiles.get(0).getAbsolutePath());

            //store the output in the assignment object
            assignment.progOutputs[i] = execResult.output;

            //store any runtime errors in the assignment object
            if (execResult.bTimedOut) {
                assignment.runtimeErrors[i] = "Maximum execution time of " + maxRunTime
                        + " seconds exceeded.  Process forcefully terminated... output may be lost.\"";
            }
        }
/*

        dataFileName = "/Users/jvolcy/work/Spelman/Projects/data/data3.txt";
        String sourceFile = "/Users/jvolcy/work/Spelman/Projects/data/P0104 - Solution.py";
        String tmpFileName = outputFileName + ".AG2";

        String[] cmd = {
                shell, "-c",
                AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER) + " " +
                        "\"" + sourceFile + "\"" +
                        " < \"" + dataFileName +
                        "\" >> \"" + tmpFileName + "\" 2>&1"};

        //concatenate the cmd array string for display on the console
        String cmdStr = "";
        for (String s : cmd) {
            cmdStr += s;
        }
        console(cmdStr);

        try {
            Process p;
            //p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "cal 1988"});
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor(Long.valueOf(AutoGrader2.getConfiguration(AG_CONFIG.MAX_RUNTIME)), TimeUnit.SECONDS);

            //attempt to destroy the process
            if (p.isAlive()) {
                console("killing process...");
                p.destroy();
                p.waitFor(1, TimeUnit.SECONDS);

                //after 1 second, if the process is still alive attempt to forcibly destroy it
                if (p.isAlive()) {
                    p.destroyForcibly();
                }
            }
        }
        catch (Exception e) {
            console(e.getMessage());
        }
        */
    }

    /* ======================================================================
     * processAssignments()
     * processFiles is the central function of the GradingEngine class.
     * This function performs the auto-grading.
     * At a minimum, the caller should specify the following prior to
     * calling processFiles():
     * - an ArrayList of Assignments
     * - a cppCompiler or python3Interpreter, as appropriate
     * - testDataFiles, if required
     * ===================================================================== */
    public void processAssignments() {

        /*
        String filename = fileNameFromPathName(outputFileName);
        String outputFileNameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));

        String dataFileName = "/Users/jvolcy/work/Spelman/Projects/data/data.txt";
        String sourceFile = "/Users/jvolcy/work/Spelman/Projects/data/P0104 - Solution.py";
        String cmd = "\"" + AutoGrader2.getConfiguration(AG_CONFIG.PYTHON3_INTERPRETER) + "\" " +
                "\"" + sourceFile + "\"" + " < \"" + dataFileName + "\"";

        shellExecResult output = shellExec(cmd, maxRunTime, maxOutputLines, sourceFile);
        console(output.output);
        */
        for (Assignment assignment : assignments) {
            execAssignment(assignment);
        }

        //reportGenerator = new ReportGenerator("AutoGrader 2", outputFileNameWithoutExtension, assignments, outputFileName);
        //reportGenerator.generateReport();
    }


    /* ======================================================================
     * dumpAssignments()
     * dumpAssignments is a  debugging function that dumps the contents
     * of the Assignments array list to the screen.
     * ===================================================================== */
    public void dumpAssignments() {
        console("[" + assignments.size() + "] assignment(s) found.");

        for (Assignment assignment : assignments) {
            console("------------------------------------------");
            console("studentName = " + assignment.studentName);
            console("assignmentDirectory = " + assignment.assignmentDirectory);
            console("language = " + assignment.language);

            console(assignment.assignmentFiles.size() + " assignmentFiles:");
            for (File f : assignment.assignmentFiles) {
                console("\t" + f.getAbsolutePath());
            }

            if (assignment.testFiles != null)           //TEMP*******
            for (int i=0; i<assignment.testFiles.size(); i++) {
                console("Errors: %s", assignment.runtimeErrors[i]);
                console("Output: %s", assignment.progOutputs[i]);
            }

            console("grade = %d", assignment.grade);
            console("instructorComment = " + assignment.instructorComment);
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
