package AutoGrader2;

import java.io.File;
import java.util.ArrayList;

/* ======================================================================
 * GradingEngine class
 * ===================================================================== */
public class GradingEngine
{
    /* The assignments list. */
    public ArrayList<Assignment> assignments;

    /* ======================================================================
     * GradingEngine Constructor
     * ===================================================================== */
    GradingEngine()
    {

    }

    /* ======================================================================
     * dumpAssignments()
     * dumpAssignments is a  debugging function that dumpts the contents
     * of the Assignments array list to the screen.
     * ===================================================================== */
    public void dumpAssignments()
    {
        System.out.println("[" + assignments.size() + "] assignment(s).");

        for (Assignment assignment : assignments)
        {
            System.out.print("studentName = ");
            System.out.println(assignment.studentName);

            System.out.print("assignmentDirectory = ");
            System.out.println(assignment.assignmentDirectory);

            System.out.println(assignment.assignmentFiles.size() + " assignmentFiles:");
            for (File f : assignment.assignmentFiles)
            {
                System.out.print("\t");
                System.out.println(f.getAbsolutePath());
            }

            System.out.print("grade = ");
            System.out.println(assignment.grade);

            System.out.print("instructorComment = ");
            System.out.println(assignment.instructorComment);
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
