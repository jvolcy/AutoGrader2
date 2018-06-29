package AutoGrader2;

import java.io.File;
import java.util.ArrayList;

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
public class GradingEngine {
    /* The assignments list. */
    public ArrayList<Assignment> assignments;
    public ArrayList<File> testDataFiles;
    private String outputFileName;
    public boolean bIncludeSourceInOutput;
    public int maxRunTime;
    public int maxOutputLines;
    public String cppCompiler;
    public String python3Interpreter;
    private SyntaxHighlighter syntaxHighlighter;


    /* ======================================================================
     * GradingEngine Constructor
     * ===================================================================== */
    GradingEngine() {

        //---------- set a few default values ----------
        maxRunTime = 3;     //3 seconds
        maxOutputLines = 100;   //100 lines of output max per program
        bIncludeSourceInOutput = true;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public void setOutputFileName(String fileName) {
        outputFileName = fileName;
        //Assume that all assignments use the same language as assignment 0
        syntaxHighlighter = new SyntaxHighlighter(assignments.get(0).language, outputFileName);
    }

    /* ======================================================================
     * processFiles()
     * processFiles is the central function of the GradingEngine class.
     * This function performs the auto-grading.
     * At a minimum, the caller should specify the following prior to
     * calling processFiles():
     * - an ArrayList of Assignments
     * - a cppCompiler or python3Interpreter, as appropriate
     * - testDataFiles, if required
     * ===================================================================== */
    public void processFiles() {
        syntaxHighlighter.makeHtmlHeader("Hello World",  "This is a test");
    }


    /* ======================================================================
     * dumpAssignments()
     * dumpAssignments is a  debugging function that dumpts the contents
     * of the Assignments array list to the screen.
     * ===================================================================== */
    public void dumpAssignments() {
        Controller.console("[" + assignments.size() + "] assignment(s) found.");

        for (Assignment assignment : assignments) {
            Controller.console("------------------------------------------");
            Controller.console("studentName = " + assignment.studentName);
            Controller.console("assignmentDirectory = " + assignment.assignmentDirectory);

            Controller.console(assignment.assignmentFiles.size() + " assignmentFiles:");
            for (File f : assignment.assignmentFiles) {
                Controller.console("\t" + f.getAbsolutePath());
            }

            Controller.console("language = " + assignment.language);
            Controller.console("grade = %d", assignment.grade);
            Controller.console("instructorComment = " + assignment.instructorComment);
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
