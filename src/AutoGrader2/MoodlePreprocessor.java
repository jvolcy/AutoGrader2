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
    private String language;
    private String[] extensions;

    /* ======================================================================
     * MoodlePreprocessor()
     * Class constructor accepts the top-level assignment directory
     * and the assignment language.
     * The language can be either LANGUAGE_PYTHON3 or LANGUAGE_CPP
     * ===================================================================== */
    MoodlePreprocessor(String sourceDirectory, String language, /*Boolean processRecursively,*/ Boolean autoUncompress)
    {
        TopAssignmentsDirectory = sourceDirectory;

        //---------- set the language and corresponding file extensions ----------
        setLanguage(language);

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
        if ( !f.isDirectory() )
        {
            System.out.println("'" + TopAssignmentsDirectory + "' is not a valid source directory.");
            return;     //do nothing else
        }

        /* initialize 'assignments' array list of type 'ArrayList<Assignment>' and
        populate .studentName and .assignmentDirectory fields */
        prepareStudentDirectories();


        //----------  ----------
        prepareAssignmentFiles();


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
    /*
    MoodlePreprocessor(String sourceDirectory, String language, Boolean processRecursively)
    {
        this(sourceDirectory, language, processRecursively, true);
    }
    */

    /* ======================================================================
     * overloaded MoodlePreprocessor() constructor with default recursion
     * and auto-compress arguments.
     * ===================================================================== */
    MoodlePreprocessor(String sourceDirectory, String language)
    {
        this(sourceDirectory, language, true);
    }


    /* ======================================================================
     * setLanguage()
     * This method sets the programming language and the file extensions
     * list.
     * ===================================================================== */
    private void setLanguage(String language_)
    {

        //---------- set the default file extensions ----------
        if (language_.equals(IAGConstant.LANGUAGE_CPP))
        {
            this.language = language_;
            extensions = IAGConstant.CPP_EXTENSIONS;
        }
        else if (language_.equals(IAGConstant.LANGUAGE_PYTHON3))
        {
            this.language = language_;
            extensions = IAGConstant.PYTHON_EXTENSIONS;
        }
        else
        {
            this.language = null;
            extensions = null;
        }
    }


    /* ======================================================================
     * getFilesInDirectory()
     * given the path to a directory, this function returns an array of
     * all files and sub-directories found in the directoryPath
     * ===================================================================== */
    private ArrayList<File> getFilesInDirectory(String directoryPath, boolean omitHiddenFiles)
    {
        //---------- get all directories in the provided directory ----------
        ArrayList<File> filesInFolder = new ArrayList<File>();

        File folder = new File(directoryPath);
        for (File f : folder.listFiles())
        {
            if (!f.isHidden())
                filesInFolder.add(f);
        }
        return filesInFolder;
    }


    /* ======================================================================
     * getFileExtension()
     * returns the extension of the given filename.
     * ===================================================================== */
    private String getFileExtension(String fileName)
    {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /* ======================================================================
     * getFileExtension()
     * overloaded version of the getFileExtension() method that accepts
     * a File object as an argument and returns the string extension of the
     * corresponding file.
     * ===================================================================== */
    private String getFileExtension(File f)
    {
        String fileName = f.getName();
        return getFileExtension(fileName);
    }

    /* ======================================================================
     * getFileNameWithoutExtension()
     * returns the extension of the given filename.
     * ===================================================================== */
    private String stripFileExtension(String fileName)
    {
        int locationOfDot = fileName.lastIndexOf('.');
        return fileName.substring(0, locationOfDot);
    }


    /* ======================================================================
     * xxx
     * ===================================================================== */

    /* ======================================================================
     * findProgrammingFiles()
     * This function returns an array list of files in the specified
     * directory with file extension matching any of the extensions on
     * the 'extensions' list.
     * ===================================================================== */
    private ArrayList<File> findFilesByExtension(String directory, String[] fileExtensions)
    {
        //---------- create an empty list of files ----------
        ArrayList<File> programmingFiles = new ArrayList<File>();

        //---------- retrieve a list of all non-hidden files in the directory ----------
        ArrayList<File> progFiles = getFilesInDirectory(directory, true);

        //---------- go through each file in the directory ----------
        for (File f : progFiles)
        {
            //---------- we will ignore subdirectories ----------
            if (f.isFile())
            {
                //---------- retrieve the extension on the file ----------
                String f_extension = getFileExtension(f).toLowerCase();
                //if fileExtensions.contains();
                if (Arrays.asList(fileExtensions).contains(f_extension))
                for (String ext : fileExtensions)
                {
                    //---------- if the extension on the file matches
                    // one of the extension in the extensions list, add
                    // it to the programming files list. ----------
                    if (f_extension.equals(ext.toLowerCase()))
                        programmingFiles.add(f);
                }

            }
        }

        return programmingFiles;
    }


    /* ======================================================================
     * findAssignmentFiles()
     *
     * 1) beginning with the provided directory, search for program files
     * (py or cpp).  If any are found, add them to the assignmentFiles list.
     * We are done.
     * 2) If none are found, search for a zip file.  If any are found, unzip
     * unzip them to new folders.
     * 3) Search for subdirectories in the assignmentDirectory.  These may
     * already have existed or may have been created in step 2.
     * 4) In either case, for each sub-directory found, repeat step 1
     * with the subdirectory as the new assignmentDirectory.
     * 5) At this point, no assignment files were found, return an empty
     * ArrayList.
     * ===================================================================== */
    private void findAssignmentFiles(ArrayList<File> files, String directory)
    {
        ArrayList<File> programmingFiles = findFilesByExtension(directory, extensions);

        //---------- Step 1 ----------
        if (programmingFiles.size() > 0)
        {
            files = programmingFiles;
            return;
        }

        //---------- Step 2: search for a zip file ----------
        ArrayList<File> compressedFiles = findFilesByExtension(directory, COMPRESSION_EXTENSIONS);
        for (File cFile : compressedFiles)
        {
            //uncompress each zip file
            String cmd = "unzip \""+cFile.getName()+"\" -u -d \"" + stripFileExtension(cFile.getName()) + "\"";
            System.out.println(cmd);



        }

//----------  ----------
//----------  ----------
    }



    /* ======================================================================
     * prepareAssignmentFiles()
     *
     * ===================================================================== */
    private void prepareAssignmentFiles()
    {
        for (Assignment assignment : assignments)
        {
            //---------- initialize the assignmentFiles array list ----------
            assignment.assignmentFiles = new ArrayList<File>();

            //---------- call the recursive findAssignmentFiles method ----------
            findAssignmentFiles(assignment.assignmentFiles, assignment.assignmentDirectory);

       }

    }


    /* ======================================================================
     * getAssignmentFiles()  XXXXXX
     * ===================================================================== */
    private void getAssignmentFiles(ArrayList<File> files, String directory, boolean bRecursive, String[] extensions)
    {
        if (files == null)
        {
            System.out.println("'ArrayList<File> files' cannot be null!");
            return;
        }

        ArrayList<File> filesInCurrentDirectory = getFilesInDirectory(directory, true);
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
                    String fileExtension = getFileExtension(file);
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
     * extractStudentNameFromMoodleDirectoryName()
     * ===================================================================== */
    private String extractStudentNameFromMoodleDirectoryName(String moodleDirectoryName)
    {
        String[] data = moodleDirectoryName.split("_");
        if (data.length < 2)    //this is an error condition; not a valid Moodle directory name
            return null;

        return data[0];
    }

    /* ======================================================================
     * prepareStudentDirectories()
     * This function performs the first step to building the 'assignments'
     * ArrayList.  The function searches the TopAssignmentsDirectory for
     * the sub-directories of student submissions.  It is assumed that all
     * directories under the TopAssignmentsDirectory corresponds to a
     * student submission.  These names of these directories should be
     * Moodle-formatted as "student name_assignment description".  This
     * function extracts the student name from the corresponding directory
     * names.  The function then creates a new "Assignment" object that is
     * added to the "assignemnts" ArrayList. Finally, the .studentName and
     * .assignmentDirectory fields of the newly created "Assignment" object
     * is populated.
     * In the case where the directory name does not conform to the
     * expected Moodle naming scheme, student names of "Anonymous 1",
     * "Anonymous 2", etc. are used.
     * At the conclusion of this function, the "assignments" ArrayList
     * should be populated with as many entries as there are sub-directories
     * under the TopAssignmentsDirectory.  Each entry should have a valid
     * .studentName and .assignmentDirectory value.
     * ===================================================================== */
    private void prepareStudentDirectories()
    {
        //---------- get all directories in the top level directory ----------
        ArrayList<File> listOfFiles = getFilesInDirectory(TopAssignmentsDirectory, true);

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

        //System.out.println(assignments);
        /*
        for (String key : assignmentDirectories.keySet() )
        {
            System.out.println("assignmentDirectories["+key+"] = "+assignmentDirectories.get(key));
        }
        */
    }

    /* ======================================================================
     * getAssignments()
     * This function returns the list of assignments.
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
