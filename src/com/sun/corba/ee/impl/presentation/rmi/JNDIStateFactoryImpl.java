


package com.sun.corba.ee.impl.presentation.rmi ;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.presentation.rmi.StubAdapter;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;
import javax.rmi.PortableRemoteObject;
import java.lang.reflect.Field;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;






public class JNDIStateFactoryImpl implements StateFactory {

    @SuppressWarnings("WeakerAccess")
    public JNDIStateFactoryImpl() { }

    
    public Object getStateToBind(Object orig, Name name, Context ctx,
        Hashtable<?,?> env) throws NamingException 
    {
        if (orig instanceof org.omg.CORBA.Object) {
            return orig;
        }

        if (!(orig instanceof Remote)) {
            return null;
        }

        ORB orb = getORB( ctx ) ; 
        if (orb == null) {
            
            
            return null ;
        }

        Remote stub;

        try {
            stub = PortableRemoteObject.toStub( (Remote)orig ) ;
        } catch (Exception exc) {
            Exceptions.self.noStub( exc ) ;
            
            
            
            return null ;
        }

        if (StubAdapter.isStub( stub )) {
            try {
                StubAdapter.connect( stub, orb ) ; 
            } catch (Exception exc) {
                Exceptions.self.couldNotConnect( exc ) ;

                if (!(exc instanceof java.rmi.RemoteException)) {
                    
                    
                    return null ;
                }

                
                
            }
        }

        return stub ;
    }

    
    
    
    
    
    
    private ORB getORB( Context ctx ) {
        try {
            return (ORB) getOrbField(ctx).get( ctx ) ;
        } catch (Exception exc) {
            Exceptions.self.couldNotGetORB( exc, ctx );
            return null;
        }
    }

    private ConcurrentMap<Class<?>, Field> orbFields = new ConcurrentHashMap<>();

    private Field getOrbField(Context ctx) {
        Field orbField = orbFields.get(ctx.getClass());
        if (orbField != null) return orbField;

        orbField = AccessController.doPrivileged((PrivilegedAction<Field>) () -> getField(ctx.getClass(), "_orb"));

        orbFields.put(ctx.getClass(), orbField);
        return orbField;
    }

    private Field getField(Class<?> aClass, String fieldName) {
        try {
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            if (aClass.getSuperclass() == null)
                return null;
            else
                return getField(aClass.getSuperclass(), fieldName);
        }
    }
}
