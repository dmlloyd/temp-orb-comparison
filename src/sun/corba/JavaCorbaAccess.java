

package xxxx;


public interface JavaCorbaAccess {
    public ValueHandlerImpl newValueHandlerImpl();
    public Class<?> loadClass(String className) throws ClassNotFoundException;
}
