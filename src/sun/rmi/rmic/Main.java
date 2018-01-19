




package xxxx;






@SuppressWarnings({"deprecation"})
public class Main implements sun.rmi.rmic.Constants {
    String sourcePathArg;
    String sysClassPathArg;
    String extDirsArg;
    String classPathString;
    File destDir;
    int flags;
    long tm;
    Vector classes;
    boolean nowrite;
    boolean nocompile;
    boolean keepGenerated;
    boolean status;
    String[] generatorArgs;
    Vector generators;
    Class environmentClass = BatchEnvironment.class;
    boolean iiopGeneration = false;

    
    String program;

    
    OutputStream out;

    
    public Main(OutputStream out, String program) {
        this.out = out;
        this.program = program;
    }

    
    public void output(String msg) {
        PrintStream out =
            this.out instanceof PrintStream ? (PrintStream)this.out
            : new PrintStream(this.out, true);
        out.println(msg);
    }

    
    public void error(String msg, Object... args) {
        output(getText(msg, args));
    }

    
    public void usage() {
        error("rmic.usage", program);
    }
    
    
    public synchronized boolean compile(String argv[]) {
        if (!parseArgs(argv)) {
            return false;
        }

        if (classes.size() == 0) {
            usage();
            return false;
        }

        return doCompile();
    }

    
    public File getDestinationDir() {
        return destDir;
    }

    
    public boolean parseArgs(String argv[]) {
        sourcePathArg = null;
        sysClassPathArg = null;
        extDirsArg = null;

        classPathString = null;
        destDir = null;
        flags = F_WARNINGS;
        tm = System.currentTimeMillis();
        classes = new Vector();
        nowrite = false;
        nocompile = false;
        keepGenerated = false;
        generatorArgs = getArray("generator.args",true);
        if (generatorArgs == null) {
            return false;
        }
        generators = new Vector();

        
        try {
            argv = CommandLine.parse(argv);
        } catch (FileNotFoundException e) {
            error("rmic.cant.read", e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        
        for (int i = 0 ; i < argv.length ; i++) {
            if (argv[i] != null) {
                if (argv[i].equals("-g")) {
                    flags &= ~F_OPT;
                    flags |= F_DEBUG_LINES | F_DEBUG_VARS;
                    argv[i] = null;
                } else if (argv[i].equals("-O")) {
                    flags &= ~F_DEBUG_LINES;
                    flags &= ~F_DEBUG_VARS;
                    flags |= F_OPT | F_DEPENDENCIES;
                    argv[i] = null;
                } else if (argv[i].equals("-nowarn")) {
                    flags &= ~F_WARNINGS;
                    argv[i] = null;
                } else if (argv[i].equals("-debug")) {
                    flags |= F_DUMP;
                    argv[i] = null;
                } else if (argv[i].equals("-depend")) {
                    flags |= F_DEPENDENCIES;
                    argv[i] = null;
                } else if (argv[i].equals("-verbose")) {
                    flags |= F_VERBOSE;
                    argv[i] = null;
                } else if (argv[i].equals("-nowrite")) {
                    nowrite = true;
                    argv[i] = null;
                } else if (argv[i].equals("-Xnocompile")) {
                    nocompile = true;
                    keepGenerated = true;
                    argv[i] = null;
                } else if (argv[i].equals("-keep") ||
                           argv[i].equals("-keepgenerated")) {
                    keepGenerated = true;
                    argv[i] = null;
                } else if (argv[i].equals("-show")) {
                    error("rmic.option.unsupported", "-show");
                    usage();
                    return false;
                } else if (argv[i].equals("-classpath")) {
                    if ((i + 1) < argv.length) {
                        if (classPathString != null) {
                            error("rmic.option.already.seen", "-classpath");
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        classPathString = argv[++i];
                        argv[i] = null;
                    } else {
                        error("rmic.option.requires.argument", "-classpath");
                        usage();
                        return false;
                    }
                } else if (argv[i].equals("-sourcepath")) {
                    if ((i + 1) < argv.length) {
                        if (sourcePathArg != null) {
                            error("rmic.option.already.seen", "-sourcepath");
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        sourcePathArg = argv[++i];
                        argv[i] = null;
                    } else {
                        error("rmic.option.requires.argument", "-sourcepath");
                        usage();
                        return false;
                    }
                } else if (argv[i].equals("-bootclasspath")) {
                    if ((i + 1) < argv.length) {
                        if (sysClassPathArg != null) {
                            error("rmic.option.already.seen", "-bootclasspath");
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        sysClassPathArg = argv[++i];
                        argv[i] = null;
                    } else {
                        error("rmic.option.requires.argument", "-bootclasspath");
                        usage();
                        return false;
                    }
                } else if (argv[i].equals("-extdirs")) {
                    if ((i + 1) < argv.length) {
                        if (extDirsArg != null) {
                            error("rmic.option.already.seen", "-extdirs");
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        extDirsArg = argv[++i];
                        argv[i] = null;
                    } else {
                        error("rmic.option.requires.argument", "-extdirs");
                        usage();
                        return false;
                    }
                } else if (argv[i].equals("-d")) {
                    if ((i + 1) < argv.length) {
                        if (destDir != null) {
                            error("rmic.option.already.seen", "-d");
                            usage();
                            return false;
                        }
                        argv[i] = null;
                        destDir = new File(argv[++i]);
                        argv[i] = null;
                        if (!destDir.exists()) {
                            error("rmic.no.such.directory", destDir.getPath());
                            usage();
                            return false;
                        }
                    } else {
                        error("rmic.option.requires.argument", "-d");
                        usage();
                        return false;
                    }
                } else {
                    if (!checkGeneratorArg(argv,i)) {
                        usage();
                        return false;
                    }
                }
            }
        }


        
        

        for (int i = 0; i < argv.length; i++) {
            if (argv[i] != null) {
                if (argv[i].startsWith("-")) {
                    error("rmic.no.such.option", argv[i]);
                    usage();
                    return false;
                } else {
                    classes.addElement(argv[i]);
                }
            }
        }


        

        if (generators.size() == 0) {
            addGenerator("default");
        }

        return true;
    }

    
    protected boolean checkGeneratorArg(String[] argv, int currentIndex) {
        boolean result = true;
        if (argv[currentIndex].startsWith("-")) {
            String arg = argv[currentIndex].substring(1).toLowerCase(); 
            for (int i = 0; i < generatorArgs.length; i++) {
                if (arg.equalsIgnoreCase(generatorArgs[i])) {
                    
                    Generator gen = addGenerator(arg);
                    if (gen == null) {
                        return false;    
                    }
                    result = gen.parseArgs(argv,this);
                    break;
                }
            }
        }
        return result;
    }
        
    
    protected Generator addGenerator(String arg) {
                
        Generator gen;
                
        
        
                
        String className = getString("generator.class." + arg);
        if (className == null) {
            error("rmic.missing.property",arg);
            return null;
        }
                
        try {
            gen = (Generator) Class.forName(className).newInstance();
        } catch (Exception e) {
            error("rmic.cannot.instantiate",className);
            return null;
        }

        generators.addElement(gen);
                
        
                
        Class envClass = BatchEnvironment.class;
        String env = getString("generator.env." + arg);         
        if (env != null) {
            try {
                envClass = Class.forName(env);
                 
                

                if (environmentClass.isAssignableFrom(envClass)) {
                             
                    
                                
                    environmentClass = envClass;
                            
                } else {
                                
                    
                    
                                
                    if (!envClass.isAssignableFrom(environmentClass)) {
                                    
                        

                        error("rmic.cannot.use.both",environmentClass.getName(),envClass.getName());
                        return null;
                    }
                }
            } catch (ClassNotFoundException e) {
                error("rmic.class.not.found",env);
                return null;
            }
        }
                
        
        
                
        if (arg.equals("iiop")) {
            iiopGeneration = true;
        }
        return gen;
    }
    
    
    protected String[] getArray(String name, boolean mustExist) {
        String[] result = null;
        String value = getString(name);
        if (value == null) {
            if (mustExist) {
                error("rmic.resource.not.found",name);
                return null;
            } else {
                return new String[0];
            }
        }
                
        StringTokenizer parser = new StringTokenizer(value,", \t\n\r", false);
        int count = parser.countTokens();
        result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = parser.nextToken();
        }

        return result;
    }
        
    
    public BatchEnvironment getEnv() {

        ClassPath classPath =
            BatchEnvironment.createClassPath(classPathString,
                                             sysClassPathArg,
                                             extDirsArg);
        BatchEnvironment result = null;
        try {
            Class[] ctorArgTypes = {OutputStream.class,ClassPath.class,Main.class};
            Object[] ctorArgs = {out,classPath,this};
            Constructor constructor = environmentClass.getConstructor(ctorArgTypes);
            result = (BatchEnvironment) constructor.newInstance(ctorArgs);
            result.reset();
        }
        catch (Exception e) {
            error("rmic.cannot.instantiate",environmentClass.getName());
        }
        return result;
    }
        

    
    public boolean doCompile() {
        
        BatchEnvironment env = getEnv();
        env.flags |= flags;

        
        
        env.majorVersion = 45;
        env.minorVersion = 3;
        
        
        
        String noMemoryErrorString = getText("rmic.no.memory");
        String stackOverflowErrorString = getText("rmic.stack.overflow");

        try {
            
            for (int i = classes.size()-1; i >= 0; i-- ) {
                Identifier implClassName =
                    Identifier.lookup((String)classes.elementAt(i));

                
                implClassName = env.resolvePackageQualifiedName(implClassName);
                
                implClassName = Names.mangleClass(implClassName);

                ClassDeclaration decl = env.getClassDeclaration(implClassName);
                try {
                    ClassDefinition def = decl.getClassDefinition(env);
                    for (int j = 0; j < generators.size(); j++) {
                        Generator gen = (Generator)generators.elementAt(j);
                        gen.generate(env, def, destDir);
                    }
                } catch (ClassNotFound ex) {
                    env.error(0, "rmic.class.not.found", implClassName);
                }

            }

            
            if (!nocompile) {
                compileAllClasses(env);
            }
        } catch (OutOfMemoryError ee) {
            
            
            env.output(noMemoryErrorString);
            return false;
        } catch (StackOverflowError ee) {
            env.output(stackOverflowErrorString);
            return false;
        } catch (Error ee) {
            
            
            
            if (env.nerrors == 0 || env.dump()) {
                ee.printStackTrace();
                env.error(0, "fatal.error");
            }
        } catch (Exception ee) {
            if (env.nerrors == 0 || env.dump()) {
                ee.printStackTrace();
                env.error(0, "fatal.exception");
            }
        }

        env.flushErrors();

        boolean status = true;
        if (env.nerrors > 0) {
            String msg = "";
            if (env.nerrors > 1) {
                msg = getText("rmic.errors", env.nerrors);
            } else {
                msg = getText("rmic.1error");
            }
            if (env.nwarnings > 0) {
                if (env.nwarnings > 1) {
                    msg += ", " + getText("rmic.warnings", env.nwarnings);
                } else {
                    msg += ", " + getText("rmic.1warning");
                }
            }
            output(msg);
            status = false;
        } else {
            if (env.nwarnings > 0) {
                if (env.nwarnings > 1) {
                    output(getText("rmic.warnings", env.nwarnings));
                } else {
                    output(getText("rmic.1warning"));
                }
            }
        }

        
        if (!keepGenerated) {
            env.deleteGeneratedFiles();
        }

        
        if (env.verbose()) {
            tm = System.currentTimeMillis() - tm;
            output(getText("rmic.done_in", Long.toString(tm)));
        }

        
        
        
        
        

        env.shutdown();

        sourcePathArg = null;
        sysClassPathArg = null;
        extDirsArg = null;
        classPathString = null;
        destDir = null;
        classes = null;
        generatorArgs = null;
        generators = null;
        environmentClass = null;
        program = null;
        out = null;

        return status;
    }

    
    public void compileAllClasses (BatchEnvironment env)
        throws ClassNotFound,
               IOException,
               InterruptedException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
        boolean done;

        do {
            done = true;
            for (Enumeration e = env.getClasses() ; e.hasMoreElements() ; ) {
                ClassDeclaration c = (ClassDeclaration)e.nextElement();
                done = compileClass(c,buf,env);
            }
        } while (!done);
    }

    
    public boolean compileClass (ClassDeclaration c,
                                 ByteArrayOutputStream buf,
                                 BatchEnvironment env)
        throws ClassNotFound,
               IOException,
               InterruptedException {
        boolean done = true;
        env.flushErrors();
        sun.tools.javac.SourceClass src;

        switch (c.getStatus()) {
        case CS_UNDEFINED:
            {
                if (!env.dependencies()) {
                    break;
                }
                
            }

        case CS_SOURCE:
            {
                done = false;
                env.loadDefinition(c);
                if (c.getStatus() != CS_PARSED) {
                    break;
                }
                
            }
                        
        case CS_PARSED:
            {
                if (c.getClassDefinition().isInsideLocal()) {
                    break;
                }
                
                
                
                
                

                if (nocompile) {
                    throw new IOException("Compilation required, but -Xnocompile option in effect");
                }

                done = false;

                src = (sun.tools.javac.SourceClass)c.getClassDefinition(env);
                src.check(env);
                c.setDefinition(src, CS_CHECKED);
                
            }

        case CS_CHECKED:
            {
                src = (sun.tools.javac.SourceClass)c.getClassDefinition(env);
                
                if (src.getError()) {
                    c.setDefinition(src, CS_COMPILED);
                    break;
                }
                done = false;
                buf.reset();
                src.compile(buf);
                c.setDefinition(src, CS_COMPILED);
                src.cleanup(env);

                if (src.getError() || nowrite) {
                    break;
                }

                String pkgName = c.getName().getQualifier().toString().replace('.', File.separatorChar);
                String className = c.getName().getFlatName().toString().replace('.', SIGC_INNERCLASS) + ".class";

                File file;
                if (destDir != null) {
                    if (pkgName.length() > 0) {
                        file = new File(destDir, pkgName);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        file = new File(file, className);
                    } else {
                        file = new File(destDir, className);
                    }
                } else {
                    ClassFile classfile = (ClassFile)src.getSource();
                    if (classfile.isZipped()) {
                        env.error(0, "cant.write", classfile.getPath());
                        break;
                    }
                    file = new File(classfile.getPath());
                    file = new File(file.getParent(), className);
                }

                
                try {
                    FileOutputStream out = new FileOutputStream(file.getPath());
                    buf.writeTo(out);
                    out.close();
                    if (env.verbose()) {
                        output(getText("rmic.wrote", file.getPath()));
                    }
                } catch (IOException ee) {
                    env.error(0, "cant.write", file.getPath());
                }
            }
        }
        return done;
    }

    
    public static void main(String argv[]) {
        Main compiler = new Main(System.out, "rmic");
        System.exit(compiler.compile(argv) ? 0 : 1);
    }

    
    public static String getString(String key) {
        if (!resourcesInitialized) {
            initResources();
        }

        
        

        if (resourcesExt != null) {
            try {
                return resourcesExt.getString(key);
            } catch (MissingResourceException e) {}
        }

        try {
            return resources.getString(key);
        } catch (MissingResourceException ignore) {
        }
        return null;
    }

    private static boolean resourcesInitialized = false;
    private static ResourceBundle resources;
    private static ResourceBundle resourcesExt = null;

    private static void initResources() {
        try {
            resources =
                ResourceBundle.getBundle("sun.rmi.rmic.resources.rmic");
            resourcesInitialized = true;
            try {
                resourcesExt =
                    ResourceBundle.getBundle("sun.rmi.rmic.resources.rmicext");
            } catch (MissingResourceException e) {}
        } catch (MissingResourceException e) {
            throw new Error("fatal: missing resource bundle: " +
                            e.getClassName());
        }
    }

    public static String getText(String key) {
        String message = getString(key);
        if (message == null) {
            message = "no text found: \"" + key + "\"";
        }
        return message;
    }

    public static String getText(String key, Object... args )
    {
        String format = getString(key);
        if (format == null) {
            format = "no text found: key = \"" + key + "\", " +
                "arguments = " ;
            
            for (int ctr=0; ctr<args.length; ctr++) {
                if (ctr != 0)
                    format += ", " ;
                format += "\"{" + ctr + "}\"" ;
            }
        }

        return java.text.MessageFormat.format(format, args);
    }
}
