





package xxxx;



public abstract class Generator implements      sun.rmi.rmic.Generator,
                                                sun.rmi.rmic.iiop.Constants {
        
    protected boolean alwaysGenerate = false;
    protected BatchEnvironment env = null;
    protected ContextStack contextStack = null; 
    private boolean trace = false;
    protected boolean idl = false;  
    
    
    public boolean parseArgs(String argv[], Main main) {
        for (int i = 0; i < argv.length; i++) {
            if (argv[i] != null) {
                if (argv[i].equalsIgnoreCase("-always") ||
                    argv[i].equalsIgnoreCase("-alwaysGenerate")) {
                    alwaysGenerate = true;
                    argv[i] = null;
                } else if (argv[i].equalsIgnoreCase("-xtrace")) {
                    trace = true;
                    argv[i] = null;
                }
            }
        }
        return true;
    }
    
    
    protected abstract boolean parseNonConforming(ContextStack stack);
    
    
    protected abstract CompoundType getTopType(ClassDefinition cdef, ContextStack stack);
    
    
    protected abstract OutputType[] getOutputTypesFor(CompoundType topType,
                                                      HashSet alreadyChecked);
        
    
    protected abstract String getFileNameExtensionFor(OutputType outputType);
        
    
    protected abstract void writeOutputFor(OutputType outputType,
                                                HashSet alreadyChecked,
                                                IndentingWriter writer) throws IOException;
   
    
    protected abstract boolean requireNewInstance();
       
    
    public boolean requiresGeneration (File target, Type theType) {
    
        boolean result = alwaysGenerate;
        
        if (!result) {
        
            
            
            
            
            ClassFile baseFile;
            ClassPath path = env.getClassPath();
            String className = theType.getQualifiedName().replace('.',File.separatorChar);
            
            
            
            baseFile = path.getFile(className + ".source");
            
            if (baseFile == null) {
                
                
                
                baseFile = path.getFile(className + ".class");
            }

            
            
            if (baseFile != null) {
               
                
                        
                long baseFileMod = baseFile.lastModified();
        
                
                
                
                
                String targetName = IDLNames.replace(target.getName(),".java",".class");  
                String parentPath = target.getParent();
                File targetFile = new File(parentPath,targetName);

                
                
                if (targetFile.exists()) {
                    
                    
                    
                    long targetFileMod = targetFile.lastModified();
                    
                    
                    
                    result = targetFileMod < baseFileMod;
                    
                } else {
                    
                    
      
                    result = true;
                }   
            } else {
                
                
                
                result = true;
            }
        }

        return result;
    }
    
    
    protected Generator newInstance() {
        Generator result = null;
        try {
            result = (Generator) getClass().newInstance();
        }
        catch (Exception e){} 
        
        return result;
    }

    
    protected Generator() {
    }
    
    
    public void generate(sun.rmi.rmic.BatchEnvironment env, ClassDefinition cdef, File destDir) {
                
        this.env = (BatchEnvironment) env;
        contextStack = new ContextStack(this.env);
        contextStack.setTrace(trace);
                
        
        
        
        
        this.env.setParseNonConforming(parseNonConforming(contextStack));
                
        
                
        CompoundType topType = getTopType(cdef,contextStack);
        if (topType != null) {
                        
            Generator generator = this;
                        
            
                        
            if (requireNewInstance()) {
                                
                                
                                
                                
                generator = newInstance();
            }

            
                        
            generator.generateOutputFiles(topType, this.env, destDir);
        }
    }
        
    
    protected void generateOutputFiles (CompoundType topType,
                                        BatchEnvironment env,
                                        File destDir) {
                
        
                
        HashSet alreadyChecked = env.alreadyChecked;
                
        
                
        OutputType[] types = getOutputTypesFor(topType,alreadyChecked);
                
        
                
        for (int i = 0; i < types.length; i++) {
            OutputType current = types[i];
            String className = current.getName(); 
            File file = getFileFor(current,destDir);
            boolean sourceFile = false;
                        
            
                        
            if (requiresGeneration(file,current.getType())) {
                
                
                        
                if (file.getName().endsWith(".java")) {
                    sourceFile = compileJavaSourceFile(current);
                        
                                
                                
                    if (sourceFile) {
                        env.addGeneratedFile(file);
                    }
                }
                        
                
                        
                try {
                   IndentingWriter out = new IndentingWriter(
                                                              new OutputStreamWriter(new FileOutputStream(file)),INDENT_STEP,TAB_SIZE);
        
                    long startTime = 0;
                    if (env.verbose()) {
                        startTime = System.currentTimeMillis();
                    }

                    writeOutputFor(types[i],alreadyChecked,out);
                    out.close();

                    if (env.verbose()) {
                        long duration = System.currentTimeMillis() - startTime;
                        env.output(Main.getText("rmic.generated", file.getPath(), Long.toString(duration)));
                    }
                    if (sourceFile) {
                        env.parseFile(new ClassFile(file));
                    }
                } catch (IOException e) {
                    env.error(0, "cant.write", file.toString());
                    return;
                }
            } else {
                
                
                
                if (env.verbose()) {
                    env.output(Main.getText("rmic.previously.generated", file.getPath()));
                }
            }
        }
    }
        
    
    protected File getFileFor(OutputType outputType, File destinationDir) {
        
        
        Identifier id = getOutputId(outputType);
        File packageDir = null;
        if(idl){
            packageDir = Util.getOutputDirectoryForIDL(id,destinationDir,env);
        } else {
            packageDir = Util.getOutputDirectoryForStub(id,destinationDir,env);
        }
        String classFileName = outputType.getName() + getFileNameExtensionFor(outputType);
        return new File(packageDir, classFileName);
    }
                                
    
    protected Identifier getOutputId (OutputType outputType) {
        return outputType.getType().getIdentifier();
    }

    
    protected boolean compileJavaSourceFile (OutputType outputType) {
        return true;
    }
            
    
    
    

    public class OutputType {
        private String name;
        private Type type;

        public OutputType (String name, Type type) {
            this.name = name;
            this.type = type;
        }
        
        public String getName() {
            return name;
        }
        
        public Type getType() {
            return type;
        }
    }
}

