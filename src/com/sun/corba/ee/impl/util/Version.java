



package xxxx;

public class Version {

    public static final String  PROJECT_NAME = "RMI-IIOP";
    public static final String  VERSION = "1.0";
    public static final String  BUILD = "0.0";
    public static final String  BUILD_TIME = "unknown";
    public static final String  FULL = PROJECT_NAME + " " + VERSION + " (" 
        + BUILD_TIME + ")";
    
    public static String asString () {
        return FULL;
    }
    
    public static void main (String[] args) {
        System.out.println(FULL);
    }
}
