




package sun.rmi.rmic;

import java.io.File;
import sun.tools.java.Identifier;



public class Util implements sun.rmi.rmic.Constants {

    
    public static File getOutputDirectoryFor(Identifier theClass,
                                             File rootDir,
                                             BatchEnvironment env) {
        
        File outputDir = null;
        String className = theClass.getFlatName().toString().replace('.', SIGC_INNERCLASS);             
        String qualifiedClassName = className;
        String packagePath = null;
        String packageName = theClass.getQualifier().toString();
                
        if (packageName.length() > 0) {
            qualifiedClassName = packageName + "." + className;
            packagePath = packageName.replace('.', File.separatorChar);
        }

        
        
        if (rootDir != null) {
                    
            
                
            if (packagePath != null) {
                    
                
                            
                outputDir = new File(rootDir, packagePath);
                            
                
                            
                ensureDirectory(outputDir,env);
                    
            } else {
                    
                
                    
                outputDir = rootDir;
            }               
        } else {
                    
            
                    
            String workingDirPath = System.getProperty("user.dir");
            File workingDir = new File(workingDirPath);
                    
            
                    
            if (packagePath == null) {
                        
                
               
                outputDir = workingDir;
                        
            } else {
                        
                
                            
                outputDir = new File(workingDir, packagePath);
                                    
                
                                    
                ensureDirectory(outputDir,env);
            }
        }

        
            
        return outputDir;
    }
 
    private static void ensureDirectory (File dir, BatchEnvironment env) {
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                env.error(0,"rmic.cannot.create.dir",dir.getAbsolutePath());
                throw new InternalError();
            }
        }
    }
}

