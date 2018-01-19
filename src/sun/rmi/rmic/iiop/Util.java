




package xxxx;





public final class Util implements sun.rmi.rmic.Constants {


    public static String packagePrefix(){ return PackagePrefixChecker.packagePrefix();}


    
    private static File getOutputDirectoryFor(Identifier theClass,
                                             File rootDir,
                                             BatchEnvironment env,
                                             boolean idl ) {
        File outputDir = null;
        String className = theClass.getFlatName().toString().replace('.', SIGC_INNERCLASS);             
        String qualifiedClassName = className;
        String packagePath = null;
        String packageName = theClass.getQualifier().toString();
        
         
        packageName = 
                correctPackageName(packageName, idl, env.getStandardPackage());
        
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

    public static File getOutputDirectoryForIDL(Identifier theClass,
                                             File rootDir,
                                             BatchEnvironment env) {
        return getOutputDirectoryFor(theClass, rootDir, env, true);
    }

    public static File getOutputDirectoryForStub(Identifier theClass,
                                             File rootDir,
                                             BatchEnvironment env) {
        return getOutputDirectoryFor(theClass, rootDir, env, false);
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

    public static String correctPackageName(
            String p, boolean idl, boolean standardPackage){
        if (idl){
            return p;
        } else {
            if (standardPackage) {
                return p;
            } else {
                return PackagePrefixChecker.correctPackageName(p);
            }
        }
    }

    public static boolean isOffendingPackage(String p){
        return PackagePrefixChecker.isOffendingPackage(p);
    }

    public static boolean hasOffendingPrefix(String p){
        return PackagePrefixChecker.hasOffendingPrefix(p);
    }

}



