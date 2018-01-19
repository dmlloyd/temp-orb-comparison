




package sun.rmi.rmic;



public interface Generator {

    
    public boolean parseArgs(String argv[], Main main);
    
    
    public void generate(BatchEnvironment env, ClassDefinition cdef, File destDir);
}
