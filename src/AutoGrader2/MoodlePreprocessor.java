package AutoGrader2;

import java.io.File;
import java.util.*;

/* ======================================================================
 * MoodlePreprocessor
 * Class that pre-processes the assignments downloaded from Moodle.
 * All Moodle downloads must be done using the 'Download all assignments'
 * method with the 'Download submissions in folders' checkbox checked.
 * With this option, every student's submission will be in a separate
 * folder once the downloaded zip file is extracted.  (You must extract
 * the file).
 * MoodlePreprocessor contains functions to extract the student names
 * from the Moodle-assigned subfolder names.  It also recursively
 * identifies all programming files withing these directories.
 * MoodlePreprocessor pre-populates and ArrayList of Assignment objects
 * with the extracted student names, the program directories and the
 * list of programming files contained in each directory.
 * The class also optionally unzips files in the student directory.
 * ===================================================================== */
public class MoodlePreprocessor implements IAGConstant
{
    //---------- assignments dictionary ----------
    /* The key to the assignment files dictionary is the student's name.  The
    values are a list of files (.py, .cpp, .h, etc.) included with
    the particular student's submission.   Subdirectories are
    optionally recursively searched until at least one file of the
    specified language (Python or C++) is found.  compressed  files
    will be optionally uncompressed ahead of the recursive search. */
    //private static Map<String, ArrayList<String>> assignmentFiles = new HashMap<String, ArrayList<String>>();
    //private static Map<String, Assignment> assignments = new HashMap<String, Assignment>();
    private ArrayList<Assignment> assignments;

    /* The key to the assignment directories dictionary is the student's
    * name.  The value is the directory that holds the student's
    * submission.  The student's name is derived from the assignment
    * directory. */
    //private static Map<String, String> assignmentDirectories = new HashMap<String, String>();

    private String TopAssignmentsDirectory;

    /* ======================================================================
     * MoodlePreprocessor()
     * Class constructor accepts the top-level assignment directory
     * and the assignment language.
     * The language can be either LANGUAGE_PYTHON3 or LANGUAGE_CPP
     * ===================================================================== */
    MoodlePreprocessor(String sourceDirectory, String language, Boolean processRecursively, Boolean autoUncompress)
    {
        TopAssignmentsDirectory = sourceDirectory;

        //---------- Allocate assignments ArrayList ----------
        assignments = new ArrayList<Assignment>();

        /*Steps
        * 1) get the name of all subdirectories inside the assignmentDirectory
        * 2) extract the student names from all these subdirectories.  Eliminate
        * subdirectories that do not sport a valid student name.  These student
        * names will be the keys to the dictionary.
        * 3) within each student directory search for the appropriate files.
        * these would be .zip, .py, .cpp and .h files, depending on the language.
        * 4) add the files of interest to the assignments dictionary under the
        * key that matches the student's name.
        * */


        //---------- verify that TopAssignmentsDirectory is a valid directory ----------
        File f = new File(TopAssignmentsDirectory);
        if (f.isDirectory() == false)
        {
            System.out.println("'" + TopAssignmentsDirectory + "' is not a valid source directory.");
            return;     //do nothing else
        }

        //----------  ----------
        prepareStudentDirectories();

        //----------  ----------
        for (Assignment assignment : assignments)
        {
            assignment.assignmentFiles = new ArrayList<File>();
            String[] extensions = {"zip", "py", "cpp", "h"};
            getAssignmentFiles(assignment.assignmentFiles, assignment.assignmentDirectory, true, extensions);
            /*for (File file : assignment.assignmentFiles)
            {
                System.out.println(file);
            }*/
        }

        //----------  ----------
        //----------  ----------
        //----------  ----------
        //----------  ----------
        //----------  ----------

    }

    /* ======================================================================
     * overloaded MoodlePreprocessor() constructor with default
     * auto-compress argument.
     * ===================================================================== */
    MoodlePreprocessor(String sourceDirectory, String language, Boolean processRecursively)
    {
        this(sourceDirectory, language, processRecursively, true);
    }

    /* ======================================================================
     * overloaded MoodlePreprocessor() constructor with default recursion
     * and auto-compress arguments.
     * ===================================================================== */
    MoodlePreprocessor(String sourceDirectory, String language)
    {
        this(sourceDirectory, language, true, true);
    }


    /* ======================================================================
     * extractStudentNameFromMoodleDirectoryName()
     * ===================================================================== */
    private String extractStudentNameFromMoodleDirectoryName(String moodleDirectoryName)
    {
        String[] data = moodleDirectoryName.split("_");
        if (data.length < 2)    //this is an error condition; not a valid Moodle directory name
            return null;

        return data[0];
    }

To do : recursion should only be happening if we don't find a source file in an upper directory!!!
    In other words, search recursively until you find a file.


    /* ======================================================================
     * getAssignmentFiles
     * ===================================================================== */
    private void getAssignmentFiles(ArrayList<File> files, String directory, boolean bRecursive, String[] extensions)
    {
        if (files == null)
        {
            System.out.println("'ArrayList<File> files' cannot be null!");
            return;
        }

        File[] filesInCurrentDirectory = getFilesInDirectory(directory);
        for (File file : filesInCurrentDirectory)
        {
            /* there are 3 cases to consider:
            * 1) the file is a directory and we want to find files recursively.
            * In this case, we call getAssignmentFiles() recursively.
            * 2) the file is a directory but wa are not interested in recursively
            * searching for files.  In this case, we simply ignore the file.
            * 3) the file is not a directory.  In this we add the file to out
            * list of files. */
            if (file.isDirectory() && bRecursive)
            {
                getAssignmentFiles(files, file.toString(), true, extensions);
            }

            if (file.isFile())
            {
                for (String extension : extensions)
                {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
                    if ( fileExtension.equals(extension))
                    {
                        //System.out.println(file.getName());
                        files.add(file);
                    }
                    /*
                    else
                    {
                        System.out.println(fileExtension + "!=" + extension);
                    }*/
                }
            }
        }
    }

    /* ======================================================================
     * getSubDirectories()
     * given the path to a directory, this function returns an array of
     * all files and sub-directories found in the directory
     * ===================================================================== */
    private File[] getFilesInDirectory(String directoryPath)
    {
        //---------- get all directories in the provided directory ----------
        File folder = new File(directoryPath);
        return folder.listFiles();
    }

    /* ======================================================================
     * prepareStudentDirectories()
     * ===================================================================== */
    private void prepareStudentDirectories()
    {
        //---------- get all directories in the top level directory ----------
        File[] listOfFiles = getFilesInDirectory(TopAssignmentsDirectory);

        /* for directories that don't conform to Moodle names, we will label
        them "anonymous 1", "anonymous 2", etc... */
        int anonymousCounter = 1;

        //---------- go through the list and identify subdirectories ----------
        for (File file: listOfFiles)
        {
            if (file.isDirectory())     //check that it is a directory
            {
                Assignment newAssignment = new Assignment();

                //attempt to extract the student's name
                String studentName = extractStudentNameFromMoodleDirectoryName( file.getName() );
                newAssignment.assignmentDirectory = file.toString();

                if (studentName != null)
                {
                    //if successful, add to the assignmentDirectories dictionary
                    //assignmentDirectories.put(studentName, file.toString());
                    newAssignment.studentName = studentName;
                }
                else
                {
                    newAssignment.studentName = "Anonymous " + anonymousCounter;
                    anonymousCounter++;
                }

                //add this assignment to the assignments ArrayList
                assignments.add(newAssignment);
            }
        }

        System.out.println(assignments);
        /*
        for (String key : assignmentDirectories.keySet() )
        {
            System.out.println("assignmentDirectories["+key+"] = "+assignmentDirectories.get(key));
        }
        */
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
    public ArrayList<Assignment> getAssignments()
    {
        return assignments;
    }

    /* ======================================================================
     * xxx
     * ===================================================================== */
}




/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
