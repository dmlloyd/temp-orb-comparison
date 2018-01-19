


package com.sun.corba.ee.spi.osgi;

import java.util.Properties ;

import org.glassfish.external.amx.AMXGlassfish ;

import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.orb.ClassCodeBaseHandler ;

import com.sun.corba.ee.impl.orb.ORBImpl ;

import com.sun.corba.ee.impl.osgi.loader.OSGIListener;
import com.sun.corba.ee.spi.misc.ORBConstants;


public class ORBFactory {   
    private ORBFactory() {} 

    public static ORB create( String[] args, Properties props, boolean useOSGi ) {
        ORB result = create() ;
        initialize( result, args, props, useOSGi ) ;
        return result ;
    }

    
    public static ORB create() {
        ORB result = new ORBImpl() ;
        return result ;
    }

    
    @SuppressWarnings("static-access")
    public static void initialize( ORB orb, String[] args, Properties props, boolean useOSGi ) {
        
        
        
        
        props.setProperty( ORBConstants.DISABLE_ORBD_INIT_PROPERTY, "true" ) ;

        if (useOSGi) {
            orb.classNameResolver(
                orb.makeCompositeClassNameResolver(
                    OSGIListener.classNameResolver(),
                    orb.defaultClassNameResolver()
                ) );

            ClassCodeBaseHandler ccbh = OSGIListener.classCodeBaseHandler() ;
            orb.classCodeBaseHandler( ccbh ) ;
        }

        orb.setRootParentObjectName( AMXGlassfish.DEFAULT.serverMonForDAS() ) ;

        orb.setParameters( args, props ) ;
    }
}


