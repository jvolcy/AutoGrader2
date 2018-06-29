package AutoGrader2;


/* ======================================================================
 * Interface of AG2 constants.
 * ===================================================================== */
public interface IAGConstant {

    //---------- UI Constants ----------
    public final int MIN_STAGE_WIDTH = 600;
    public final int MIN_STAGE_HEIGHT = 400;

    public final int CONFIGURATION_TAB = 0;
    public final int SETUP_INPUT_TAB = 1;
    public final int OUTPUT_TAB = 2;
    public final int CONSOLE_TAB = 3;
    public final int HELP_TAB = 4;

    public final String YES = "Yes";
    public final String NO = "No";

    public final String LANGUAGE_PYTHON3 = "Python 3";
    public final String LANGUAGE_CPP = "C++";

    public final String[] PYTHON_EXTENSIONS = {"py"};
    public final String[] CPP_EXTENSIONS = {"cpp", "c"};
    public final String[] CPP_ALL_EXTENSIONS = {"cpp", "c", "h", "hpp"};
    public final String[] COMPRESSION_EXTENSIONS = {"zip"};

    //---------- AG Options Dictionary Keys ----------
    public final class AG_CONFIG {
        public static final String LANGUAGE = "LANGUAGE";
        public static final String MAX_RUNTIME = "MAX_RUNTIME";
        public static final String MAX_OUTPUT_LINES = "MAX_OUTPUT_LINES";
        public static final String INCLUDE_SOURCE = "INCLUDE_SOURCE";
        public static final String AUTO_UNCOMPRESS = "AUTO_UNCOMPRESS";
        public static final String PROCESS_RECURSIVELY = "PROCESS_RECURSIVELY";
        public static final String PYTHON3_INTERPRETER = "PYTHON3_INTERPRETER";
        public static final String CPP_COMPILER = "CPP_COMPILER";
    }

    //---------- Misc Constants ----------
    public final String CONFIG_FILENAME = "ag2Config.json";
}





/* ======================================================================
 * xxx
 * ===================================================================== */

//----------  ----------
//----------  ----------
//----------  ----------
//----------  ----------
