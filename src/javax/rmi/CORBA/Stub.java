



package javax.rmi.CORBA;






public abstract class Stub extends ObjectImpl
    implements java.io.Serializable {

    private static final long serialVersionUID = 1087775603798577179L;

    
    private transient StubDelegate stubDelegate = null;
    private static Class<?> stubDelegateClass = null;
    private static final String StubClassKey = "javax.rmi.CORBA.StubClass";

    static {
        Object stubDelegateInstance = (Object) createDelegateIfSpecified(StubClassKey);
        if (stubDelegateInstance != null)
            stubDelegateClass = stubDelegateInstance.getClass();
    }


    
    public int hashCode() {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            return stubDelegate.hashCode(this);
        }

        return 0;
    }

    
    public boolean equals(java.lang.Object obj) {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            return stubDelegate.equals(this, obj);
        }

        return false;
    }

    
    public String toString() {


        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        String ior;
        if (stubDelegate != null) {
            ior = stubDelegate.toString(this);
            if (ior == null) {
                return super.toString();
            } else {
                return ior;
            }
        }
        return super.toString();
    }

    
    public void connect(ORB orb) throws RemoteException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.connect(this, orb);
        }

    }

    
    private void readObject(java.io.ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.readObject(this, stream);
        }

    }

    
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {

        if (stubDelegate == null) {
            setDefaultDelegate();
        }

        if (stubDelegate != null) {
            stubDelegate.writeObject(this, stream);
        }
    }

    private void setDefaultDelegate() {
        if (stubDelegateClass != null) {
            try {
                 stubDelegate = (javax.rmi.CORBA.StubDelegate) stubDelegateClass.newInstance();
            } catch (Exception ex) {
            
            
            
            }
        }
    }

    
    
    
    
    private static Object createDelegateIfSpecified(String classKey) {
        String className = (String)
            AccessController.doPrivileged(new GetPropertyAction(classKey));
        if (className == null) {
            Properties props = getORBPropertiesFile();
            if (props != null) {
                className = props.getProperty(classKey);
            }
        }

        if (className == null) {
            return new com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl();
        }

        try {
            return loadDelegateClass(className).newInstance();
        } catch (ClassNotFoundException ex) {
            INITIALIZE exc = new INITIALIZE( "Cannot instantiate " + className);
            exc.initCause( ex ) ;
            throw exc ;
        } catch (Exception ex) {
            INITIALIZE exc = new INITIALIZE( "Error while instantiating" + className);
            exc.initCause( ex ) ;
            throw exc ;
        }

    }

    private static Class<?> loadDelegateClass( String className )  throws ClassNotFoundException
    {
        try {
            return Class.forName(className, false, Stub.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            
        }

        try {
            return RMIClassLoader.loadClass((String) null, className);
        } catch (MalformedURLException e) {
            String msg = "Could not load " + className + ": " + e.toString();
            ClassNotFoundException exc = new ClassNotFoundException( msg ) ;
            throw exc ;
        }
    }

    
    private static Properties getORBPropertiesFile () {
        return (Properties) AccessController.doPrivileged(new GetORBPropertiesFileAction());
    }

}
