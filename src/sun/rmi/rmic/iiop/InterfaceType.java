




package xxxx;



public abstract class InterfaceType extends CompoundType {
        
    
    
    
        
    
    public void print ( IndentingWriter writer,
                        boolean useQualifiedNames,
                        boolean useIDLNames,
                        boolean globalIDLNames) throws IOException {
                                                        
        if (isInner()) {
            writer.p("// " + getTypeDescription() + " (INNER)");
        } else {
            writer.p("// " + getTypeDescription() + "");
        }
        writer.pln(" (" + getRepositoryID() + ")\n");
        printPackageOpen(writer,useIDLNames);
                
        if (!useIDLNames) {
            writer.p("public ");
        }
                
        writer.p("interface " + getTypeName(false,useIDLNames,false));
        printImplements(writer,"",useQualifiedNames,useIDLNames,globalIDLNames);
        writer.plnI(" {");
        printMembers(writer,useQualifiedNames,useIDLNames,globalIDLNames);
        writer.pln();
        printMethods(writer,useQualifiedNames,useIDLNames,globalIDLNames);
        writer.pln();

        if (useIDLNames) {
            writer.pOln("};");
        } else {
            writer.pOln("}");
        }
        printPackageClose(writer,useIDLNames);
    }
        
    
    
    

    
    protected InterfaceType(ContextStack stack, int typeCode, ClassDefinition classDef) {
        super(stack,typeCode,classDef); 
            
        if ((typeCode & TM_INTERFACE) == 0 || ! classDef.isInterface()) {
            throw new CompilerError("Not an interface");
        }
    }
    
    
    protected InterfaceType(ContextStack stack,
                            ClassDefinition classDef,
                            int typeCode) {
        super(stack,classDef,typeCode);
        
        if ((typeCode & TM_INTERFACE) == 0 || ! classDef.isInterface()) {
            throw new CompilerError("Not an interface");
        }
    }
}
