


package xxxx;



public class RemoteClass implements sun.rmi.rmic.RMIConstants {

    
    public static RemoteClass forClass(BatchEnvironment env,
                                       ClassDefinition implClassDef)
    {
        RemoteClass rc = new RemoteClass(env, implClassDef);
        if (rc.initialize()) {
            return rc;
        } else {
            return null;
        }
    }

    
    public ClassDefinition getClassDefinition() {
        return implClassDef;
    }

    
    public Identifier getName() {
        return implClassDef.getName();
    }

    
    public ClassDefinition[] getRemoteInterfaces() {
        return (ClassDefinition[]) remoteInterfaces.clone();
    }

    
    public Method[] getRemoteMethods() {
        return (Method[]) remoteMethods.clone();
    }

    
    public long getInterfaceHash() {
        return interfaceHash;
    }

    
    public String toString() {
        return "remote class " + implClassDef.getName().toString();
    }

    
    private BatchEnvironment env;

    
    private ClassDefinition implClassDef;

    
    private ClassDefinition[] remoteInterfaces;

    
    private Method[] remoteMethods;

    
    private long interfaceHash;

    
    private ClassDefinition defRemote;
    private ClassDefinition defException;
    private ClassDefinition defRemoteException;

    
    private RemoteClass(BatchEnvironment env, ClassDefinition implClassDef) {
        this.env = env;
        this.implClassDef = implClassDef;
    }

    
    private boolean initialize() {
        
        if (implClassDef.isInterface()) {
            env.error(0, "rmic.cant.make.stubs.for.interface",
                      implClassDef.getName());
            return false;
        }

        
        try {
            defRemote =
                env.getClassDeclaration(idRemote).getClassDefinition(env);
            defException =
                env.getClassDeclaration(idJavaLangException).
                getClassDefinition(env);
            defRemoteException =
                env.getClassDeclaration(idRemoteException).
                getClassDefinition(env);
        } catch (ClassNotFound e) {
            env.error(0, "rmic.class.not.found", e.name);
            return false;
        }

        
        Vector remotesImplemented =     
            new Vector();
        for (ClassDefinition classDef = implClassDef;
             classDef != null;)
            {
                try {
                    ClassDeclaration[] interfaces = classDef.getInterfaces();
                    for (int i = 0; i < interfaces.length; i++) {
                        ClassDefinition interfaceDef =
                            interfaces[i].getClassDefinition(env);
                        
                        if (!remotesImplemented.contains(interfaceDef) &&
                            defRemote.implementedBy(env, interfaces[i]))
                            {
                                remotesImplemented.addElement(interfaceDef);
                                
                                if (env.verbose()) {
                                    System.out.println("[found remote interface: " +
                                                       interfaceDef.getName() + "]");
                                    
                                }
                            }
                    }

                    
                    if (classDef == implClassDef && remotesImplemented.isEmpty()) {
                        if (defRemote.implementedBy(env,
                                                    implClassDef.getClassDeclaration()))
                            {
                                
                                env.error(0, "rmic.must.implement.remote.directly",
                                          implClassDef.getName());
                            } else {
                                
                                env.error(0, "rmic.must.implement.remote",
                                          implClassDef.getName());
                            }
                        return false;
                    }

                    
                    classDef = (classDef.getSuperClass() != null ?
                                classDef.getSuperClass().getClassDefinition(env) :
                                null);

                } catch (ClassNotFound e) {
                    env.error(0, "class.not.found", e.name, classDef.getName());
                    return false;
                }
            }

        

        
        Hashtable methods = new Hashtable();
        boolean errors = false;
        for (Enumeration enumeration = remotesImplemented.elements();
             enumeration.hasMoreElements();)
            {
                ClassDefinition interfaceDef =
                    (ClassDefinition) enumeration.nextElement();
                if (!collectRemoteMethods(interfaceDef, methods))
                    errors = true;
            }
        if (errors)
            return false;

        
        remoteInterfaces = new ClassDefinition[remotesImplemented.size()];
        remotesImplemented.copyInto(remoteInterfaces);

        
        String[] orderedKeys = new String[methods.size()];
        int count = 0;
        for (Enumeration enumeration = methods.elements();
             enumeration.hasMoreElements();)
            {
                Method m = (Method) enumeration.nextElement();
                String key = m.getNameAndDescriptor();
                int i;
                for (i = count; i > 0; --i) {
                    if (key.compareTo(orderedKeys[i - 1]) >= 0) {
                        break;
                    }
                    orderedKeys[i] = orderedKeys[i - 1];
                }
                orderedKeys[i] = key;
                ++count;
            }
        remoteMethods = new Method[methods.size()];
        for (int i = 0; i < remoteMethods.length; i++) {
            remoteMethods[i] = (Method) methods.get(orderedKeys[i]);
            
            if (env.verbose()) {
                System.out.print("[found remote method <" + i + ">: " +
                                 remoteMethods[i].getOperationString());
                ClassDeclaration[] exceptions =
                    remoteMethods[i].getExceptions();
                if (exceptions.length > 0)
                    System.out.print(" throws ");
                for (int j = 0; j < exceptions.length; j++) {
                    if (j > 0)
                        System.out.print(", ");
                    System.out.print(exceptions[j].getName());
                }
                System.out.println("]");
            }
            
        }

        
        interfaceHash = computeInterfaceHash();

        return true;
    }

    
    private boolean collectRemoteMethods(ClassDefinition interfaceDef,
                                         Hashtable table)
    {
        if (!interfaceDef.isInterface()) {
            throw new Error(
                            "expected interface, not class: " + interfaceDef.getName());
        }

        

        boolean errors = false;

        
    nextMember:
        for (MemberDefinition member = interfaceDef.getFirstMember();
             member != null;
             member = member.getNextMember())
            {
                if (member.isMethod() &&
                    !member.isConstructor() && !member.isInitializer())
                    {
                        
                        ClassDeclaration[] exceptions = member.getExceptions(env);
                        boolean hasRemoteException = false;
                        for (int i = 0; i < exceptions.length; i++) {
                            
                            try {
                                if (defRemoteException.subClassOf(
                                                                  env, exceptions[i]))
                                    {
                                        hasRemoteException = true;
                                        break;
                                    }
                            } catch (ClassNotFound e) {
                                env.error(0, "class.not.found", e.name,
                                          interfaceDef.getName());
                                continue nextMember;
                            }
                        }
                        
                        if (!hasRemoteException) {
                            env.error(0, "rmic.must.throw.remoteexception",
                                      interfaceDef.getName(), member.toString());
                            errors = true;
                            continue nextMember;
                        }

                        
                        try {
                            MemberDefinition implMethod = implClassDef.findMethod(
                                                                                  env, member.getName(), member.getType());
                            if (implMethod != null) {           
                                exceptions = implMethod.getExceptions(env);
                                for (int i = 0; i < exceptions.length; i++) {
                                    if (!defException.superClassOf(
                                                                   env, exceptions[i]))
                                        {
                                            env.error(0, "rmic.must.only.throw.exception",
                                                      implMethod.toString(),
                                                      exceptions[i].getName());
                                            errors = true;
                                            continue nextMember;
                                        }
                                }
                            }
                        } catch (ClassNotFound e) {
                            env.error(0, "class.not.found", e.name,
                                      implClassDef.getName());
                            continue nextMember;
                        }

                        
                        Method newMethod = new Method(member);
                        
                        String key = newMethod.getNameAndDescriptor();
                        Method oldMethod = (Method) table.get(key);
                        if (oldMethod != null) {
                            newMethod = newMethod.mergeWith(oldMethod);
                            if (newMethod == null) {
                                errors = true;
                                continue nextMember;
                            }
                        }
                        table.put(key, newMethod);
                    }
            }

        
        try {
            ClassDeclaration[] superDefs = interfaceDef.getInterfaces();
            for (int i = 0; i < superDefs.length; i++) {
                ClassDefinition superDef =
                    superDefs[i].getClassDefinition(env);
                if (!collectRemoteMethods(superDef, table))
                    errors = true;
            }
        } catch (ClassNotFound e) {
            env.error(0, "class.not.found", e.name, interfaceDef.getName());
            return false;
        }

        return !errors;
    }

    
    private long computeInterfaceHash() {
        long hash = 0;
        ByteArrayOutputStream sink = new ByteArrayOutputStream(512);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            DataOutputStream out = new DataOutputStream(
                                                        new DigestOutputStream(sink, md));

            out.writeInt(INTERFACE_HASH_STUB_VERSION);
            for (int i = 0; i < remoteMethods.length; i++) {
                MemberDefinition m = remoteMethods[i].getMemberDefinition();
                Identifier name = m.getName();
                Type type = m.getType();

                out.writeUTF(name.toString());
                
                out.writeUTF(type.getTypeSignature());

                ClassDeclaration exceptions[] = m.getExceptions(env);
                sortClassDeclarations(exceptions);
                for (int j = 0; j < exceptions.length; j++) {
                    out.writeUTF(Names.mangleClass(
                                                   exceptions[j].getName()).toString());
                }
            }
            out.flush();

            
            byte hashArray[] = md.digest();
            for (int i = 0; i < Math.min(8, hashArray.length); i++) {
                hash += ((long) (hashArray[i] & 0xFF)) << (i * 8);
            }
        } catch (IOException e) {
            throw new Error(
                            "unexpected exception computing intetrface hash: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw new Error(
                            "unexpected exception computing intetrface hash: " + e);
        }

        return hash;
    }

    
    private void sortClassDeclarations(ClassDeclaration[] decl) {
        for (int i = 1; i < decl.length; i++) {
            ClassDeclaration curr = decl[i];
            String name = Names.mangleClass(curr.getName()).toString();
            int j;
            for (j = i; j > 0; j--) {
                if (name.compareTo(
                                   Names.mangleClass(decl[j - 1].getName()).toString()) >= 0)
                    {
                        break;
                    }
                decl[j] = decl[j - 1];
            }
            decl[j] = curr;
        }
    }


    
    public class Method implements Cloneable {

        
        public MemberDefinition getMemberDefinition() {
            return memberDef;
        }

        
        public Identifier getName() {
            return memberDef.getName();
        }

        
        public Type getType() {
            return memberDef.getType();
        }

        
        public ClassDeclaration[] getExceptions() {
            return (ClassDeclaration[]) exceptions.clone();
        }

        
        public long getMethodHash() {
            return methodHash;
        }

        
        public String toString() {
            return memberDef.toString();
        }

        
        public String getOperationString() {
            return memberDef.toString();
        }

        
        public String getNameAndDescriptor() {
            return memberDef.getName().toString() +
                memberDef.getType().getTypeSignature();
        }

        
        private MemberDefinition memberDef;

        
        private long methodHash;

        
        private ClassDeclaration[] exceptions;

        
        
         Method(MemberDefinition memberDef) {
            this.memberDef = memberDef;
            exceptions = memberDef.getExceptions(env);
            methodHash = computeMethodHash();
        }

        
        protected Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new Error("clone failed");
            }
        }

        
        private Method mergeWith(Method other) {
            if (!getName().equals(other.getName()) ||
                !getType().equals(other.getType()))
                {
                    throw new Error("attempt to merge method \"" +
                                    other.getNameAndDescriptor() + "\" with \"" +
                                    getNameAndDescriptor());
                }

            Vector legalExceptions = new Vector();
            try {
                collectCompatibleExceptions(
                                            other.exceptions, exceptions, legalExceptions);
                collectCompatibleExceptions(
                                            exceptions, other.exceptions, legalExceptions);
            } catch (ClassNotFound e) {
                env.error(0, "class.not.found", e.name,
                          getClassDefinition().getName());
                return null;
            }

            Method merged = (Method) clone();
            merged.exceptions = new ClassDeclaration[legalExceptions.size()];
            legalExceptions.copyInto(merged.exceptions);

            return merged;
        }

        
        private void collectCompatibleExceptions(ClassDeclaration[] from,
                                                 ClassDeclaration[] with,
                                                 Vector list)
            throws ClassNotFound
        {
            for (int i = 0; i < from.length; i++) {
                ClassDefinition exceptionDef = from[i].getClassDefinition(env);
                if (!list.contains(from[i])) {
                    for (int j = 0; j < with.length; j++) {
                        if (exceptionDef.subClassOf(env, with[j])) {
                            list.addElement(from[i]);
                            break;
                        }
                    }
                }
            }
        }

        
        private long computeMethodHash() {
            long hash = 0;
            ByteArrayOutputStream sink = new ByteArrayOutputStream(512);
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                DataOutputStream out = new DataOutputStream(
                                                            new DigestOutputStream(sink, md));

                String methodString = getNameAndDescriptor();
                
                if (env.verbose()) {
                    System.out.println("[string used for method hash: \"" +
                                       methodString + "\"]");
                }
                
                out.writeUTF(methodString);

                
                out.flush();
                byte hashArray[] = md.digest();
                for (int i = 0; i < Math.min(8, hashArray.length); i++) {
                    hash += ((long) (hashArray[i] & 0xFF)) << (i * 8);
                }
            } catch (IOException e) {
                throw new Error(
                                "unexpected exception computing intetrface hash: " + e);
            } catch (NoSuchAlgorithmException e) {
                throw new Error(
                                "unexpected exception computing intetrface hash: " + e);
            }

            return hash;
        }
    }
}
