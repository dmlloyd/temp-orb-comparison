




package sun.rmi.rmic;

import java.io.File;
import sun.tools.java.ClassDefinition;


public interface Generator {

    
    public boolean parseArgs(String argv[], Main main);
    
    
    public void generate(BatchEnvironment env, ClassDefinition cdef, File destDir);
}
