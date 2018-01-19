


package sun.rmi.rmic;



public class Names {

    
    static final public Identifier stubFor(Identifier name) {
        return Identifier.lookup(name + "_Stub");
    }

    
    static final public Identifier skeletonFor(Identifier name) {
        return Identifier.lookup(name + "_Skel");
    }

    
    static final public Identifier mangleClass(Identifier className) {
        if (!className.isInner())
            return className;

        
        Identifier mangled = Identifier.lookup(
                                               className.getFlatName().toString()
                                               .replace('.', sun.tools.java.Constants.SIGC_INNERCLASS));
        if (mangled.isInner())
            throw new Error("failed to mangle inner class name");

        
        return Identifier.lookup(className.getQualifier(), mangled);
    }
}
