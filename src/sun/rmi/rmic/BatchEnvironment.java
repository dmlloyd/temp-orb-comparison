

















package xxxx;




@SuppressWarnings({"deprecation"})
public class BatchEnvironment extends sun.tools.javac.BatchEnvironment {

    
    private Main main;

    
    public static ClassPath createClassPath(String classPathString) {
        ClassPath[] paths = classPaths(null, classPathString, null, null);
        return paths[1];
    }

    
    public static ClassPath createClassPath(String classPathString,
                                            String sysClassPathString,
                                            String extDirsString)
    {
        ClassPath[] paths = classPaths(null,
                                       classPathString,
                                       sysClassPathString,
                                       extDirsString);
        return paths[1];
    }

    
    public BatchEnvironment(OutputStream out, ClassPath path, Main main) {
        super(out, path);
        this.main = main;
    }

    
    public Main getMain() {
        return main;
    }

    
    public ClassPath getClassPath() {
        return sourcePath;
    }

    
    private Vector generatedFiles = new Vector();

    
    public void addGeneratedFile(File file) {
        generatedFiles.addElement(file);
    }

    
    public void deleteGeneratedFiles() {
        synchronized(generatedFiles) {
            Enumeration enumeration = generatedFiles.elements();
            while (enumeration.hasMoreElements()) {
                File file = (File) enumeration.nextElement();
                file.delete();
            }
            generatedFiles.removeAllElements();
        }
    }

    
    public void shutdown() {
        main = null;
        generatedFiles = null;
        super.shutdown();
    }

    
    public String errorString(String err,
                              Object arg0, Object arg1, Object arg2)
    {
        if (err.startsWith("rmic.") || err.startsWith("warn.rmic.")) {
            String result =  Main.getText(err,
                                          (arg0 != null ? arg0.toString() : null),
                                          (arg1 != null ? arg1.toString() : null),
                                          (arg2 != null ? arg2.toString() : null));

            if (err.startsWith("warn.")) {
                result = "warning: " + result;
            }
            return result;
        } else {
            return super.errorString(err, arg0, arg1, arg2);
        }
    }
    public void reset() {
    }
}
