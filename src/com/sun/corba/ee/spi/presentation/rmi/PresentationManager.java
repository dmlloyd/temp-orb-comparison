


package com.sun.corba.ee.spi.presentation.rmi ;

import java.io.PrintStream ;

import java.util.Map ;

import java.lang.reflect.Method ;

import javax.rmi.CORBA.Tie ;
import org.glassfish.pfl.basic.proxy.InvocationHandlerFactory;


public interface PresentationManager
{
    
    public interface StubFactoryFactory
    {
        
        String getStubName( String className ) ;

        
        PresentationManager.StubFactory createStubFactory( String className, 
            boolean isIDLStub, String remoteCodeBase, Class<?> expectedClass,
            ClassLoader classLoader);

        
        Tie getTie( Class<?> cls ) ;

        
        boolean createsDynamicStubs() ;
    }

    
    public interface StubFactory
    {
        
        org.omg.CORBA.Object makeStub() ;

        
        String[] getTypeIds() ;
    }

    public interface ClassData 
    {
        
        Class<?> getMyClass() ;

        
        IDLNameTranslator getIDLNameTranslator() ;

        
        String[] getTypeIds() ;

        
        InvocationHandlerFactory getInvocationHandlerFactory() ;

        
        Map<String,Object> getDictionary() ;
    }

    
    ClassData getClassData( Class<?> cls ) ;

    
    DynamicMethodMarshaller getDynamicMethodMarshaller( Method method ) ;

    
    StubFactoryFactory getStubFactoryFactory( boolean isDynamic ) ;

    
    StubFactoryFactory getStaticStubFactoryFactory();

    
    StubFactoryFactory getDynamicStubFactoryFactory();

    
    Tie getTie() ;

    
    String getRepositoryId( java.rmi.Remote impl ) ;

    
    boolean useDynamicStubs() ;

    
    void flushClass( Class<?> cls ) ;

    boolean getDebug() ;

    PrintStream getPrintStream() ;
}
