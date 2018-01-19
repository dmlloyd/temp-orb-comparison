


package com.sun.corba.ee.impl.presentation.rmi ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;

import java.io.SerializablePermission;

import com.sun.corba.ee.spi.misc.ORBClassLoader ;

public abstract class StubFactoryDynamicBase extends StubFactoryBase  
{
    protected final ClassLoader loader ;
    
    private static Void checkPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new SerializablePermission(
                    "enableSubclassImplementation"));
        }

        return null;
    }

    private StubFactoryDynamicBase( Void unused, PresentationManager.ClassData classData, 
        ClassLoader loader ) 
    {
        super( classData ) ;

        
        
        if (loader == null) {
            this.loader = ORBClassLoader.getClassLoader() ;
        } else {
            this.loader = loader ;
        }
    }
    
    public StubFactoryDynamicBase( PresentationManager.ClassData classData, 
            ClassLoader loader ) 
    {
    	this(checkPermission(), classData, loader);
    }

    public abstract org.omg.CORBA.Object makeStub() ;
}
