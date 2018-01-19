


package com.sun.corba.ee.impl.presentation.rmi;

import javax.rmi.CORBA.Tie ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager;

import com.sun.corba.ee.impl.util.PackagePrefixChecker;
import com.sun.corba.ee.impl.util.Utility;

import com.sun.corba.ee.spi.misc.ORBClassLoader;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;

import com.sun.corba.ee.impl.javax.rmi.CORBA.Util;

public class StubFactoryFactoryStaticImpl extends 
    StubFactoryFactoryBase 
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public PresentationManager.StubFactory createStubFactory(
        String className, boolean isIDLStub, String remoteCodeBase, Class 
        expectedClass, ClassLoader classLoader)
    {
        String stubName = null ;

        if (isIDLStub) {
            stubName = Utility.idlStubName(className);
        } else {
            stubName =
                Utility.stubNameForCompiler(className);
        }

        ClassLoader expectedTypeClassLoader = 
            (expectedClass == null ? classLoader : 
            expectedClass.getClassLoader());

        
        
        
        
        
        

        String firstStubName = stubName ;
        String secondStubName = stubName ;

        if (PackagePrefixChecker.hasOffendingPrefix(stubName)) {
            firstStubName =
                PackagePrefixChecker.packagePrefix() + stubName;
        } else {
            secondStubName =
                PackagePrefixChecker.packagePrefix() + stubName;
        }

        Class<?> clz = null;

        try {
            clz = Util.getInstance().loadClass( firstStubName, remoteCodeBase, 
                expectedTypeClassLoader ) ;
        } catch (ClassNotFoundException e1) {
            
            wrapper.classNotFound1( e1, firstStubName ) ;
            try {
                clz = Util.getInstance().loadClass( secondStubName, remoteCodeBase, 
                    expectedTypeClassLoader ) ;
            } catch (ClassNotFoundException e2) {
                throw wrapper.classNotFound2( e2, secondStubName ) ;
            }
        }

        
        
        
        
        if ((clz == null) || 
            ((expectedClass != null) && !expectedClass.isAssignableFrom(clz))) {
            try {
                clz = ORBClassLoader.loadClass(className);
            } catch (Exception exc) {
                
                throw new IllegalStateException("Could not load class " +
                    stubName, exc) ;
            }
        }

        return new StubFactoryStaticImpl( clz ) ;
    }

    public Tie getTie( Class cls )
    {
        Class<?> tieClass = null ;
        String className = Utility.tieName(cls.getName());

        
        try {
            try {
                
                
                tieClass = Utility.loadClassForClass(className, Util.getInstance().getCodebase(cls), 
                    null, cls, cls.getClassLoader());
                return (Tie) tieClass.newInstance();
            } catch (Exception err) {
                tieClass = Utility.loadClassForClass(
                    PackagePrefixChecker.packagePrefix() + className, 
                    Util.getInstance().getCodebase(cls), null, cls, cls.getClassLoader());
                return (Tie) tieClass.newInstance();
            }
        } catch (Exception err) {
            return null;    
        }

    }

    public boolean createsDynamicStubs() 
    {
        return false ;
    }
}
