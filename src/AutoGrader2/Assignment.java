package AutoGrader2;

import java.io.File;
import java.util.ArrayList;

/* ======================================================================
 * Assignment
 * ===================================================================== */
public class Assignment {
    //---------- general members ----------
    public String studentName;
    public String assignmentDirectory;
    public ArrayList<File> assignmentFiles;
    public String language;
    //---------- code analysis members ----------
    //public Integer linesOfCode;
    //public Integer numCommenbts;
    //public Integer nunDocStrings;
    //public Integer numFunctions;
    //public Integer numClasses;
    //---------- errors ----------
    public String compilerErrors;
    public String[] runtimeErrors;     //there are as many entries as we have test cases
    //---------- processing/execution members ----------
    public ArrayList<String> testFiles;         //the length of this list is the # of test cases
    public String[] progOutputs;     //there are as many entries as we have test cases
    //---------- grading members ----------
    public Integer grade;
    public String instructorComment;

    Assignment() {
        //set the default student name, grade, instructor comment etc.
        /*
        studentName = null;
        assignmentDirectory = null;
        linesOfCode = null;
        numCommenbts = null;
        nunDocStrings = null;
        numFunctions = null;
        numClasses = null;
        grade = null;
        instructorComment = null;

        runtimeErrors = new ArrayList<>(); */
        //testFiles = new ArrayList<>();
        /*
        testResults = new ArrayList<>();
        //initialize the assignmentFiles ArrayList
        //assignmentFiles = new ArrayList<File>();
        */
    }
}


/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
