

package com.sun.corba.ee.impl.resolver ;

import org.omg.CORBA.portable.InputStream ;
import org.omg.CORBA.portable.OutputStream ;
import org.omg.CORBA.portable.ApplicationException ;
import org.omg.CORBA.portable.RemarshalException ;

import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.ior.IORFactories ;
import com.sun.corba.ee.spi.ior.IORTemplate ;
import com.sun.corba.ee.spi.ior.ObjectKey ;
import com.sun.corba.ee.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate ;
import com.sun.corba.ee.spi.ior.iiop.IIOPFactories ;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.resolver.Resolver ;

import com.sun.corba.ee.impl.ior.ObjectIdImpl;
import com.sun.corba.ee.impl.ior.ObjectKeyImpl;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;
import com.sun.corba.ee.impl.misc.ORBUtility ;
import java.util.Set;

public class BootstrapResolverImpl implements Resolver {
    private org.omg.CORBA.portable.Delegate bootstrapDelegate ;
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public BootstrapResolverImpl(ORB orb, String host, int port) {
        
        byte[] initialKey = "INIT".getBytes();
        ObjectKey okey = new ObjectKeyImpl(orb.getWireObjectKeyTemplate(),
                                           new ObjectIdImpl(initialKey));

        IIOPAddress addr = IIOPFactories.makeIIOPAddress( host, port ) ;
        IIOPProfileTemplate ptemp = IIOPFactories.makeIIOPProfileTemplate(
            orb, GIOPVersion.V1_0, addr);
            
        IORTemplate iortemp = IORFactories.makeIORTemplate( okey.getTemplate() ) ;
        iortemp.add( ptemp ) ;

        IOR initialIOR = iortemp.makeIOR( orb, "", okey.getId() ) ;

        bootstrapDelegate = ORBUtility.makeClientDelegate( initialIOR ) ;       
    }

    
    private InputStream invoke( String operationName, String parameter )
    { 
        boolean remarshal = true;

        

        InputStream inStream = null;

        
        
        
        

        while (remarshal) {
            org.omg.CORBA.Object objref = null ;
            remarshal = false;

            OutputStream os = bootstrapDelegate.request(objref, operationName,
                true);

            if ( parameter != null ) {
                os.write_string( parameter );
            }

            try {
                
                
                
                
                
                

                inStream = bootstrapDelegate.invoke( objref, os);
            } catch (ApplicationException e) {
                throw wrapper.bootstrapApplicationException( e ) ;
            } catch (RemarshalException e) {
                wrapper.bootstrapRemarshalException( e ) ;
                remarshal = true;
            }
        }

        return inStream;
    }

    public org.omg.CORBA.Object resolve( String identifier ) 
    {
        InputStream inStream = null ;
        org.omg.CORBA.Object result = null ;

        try { 
            inStream = invoke( "get", identifier ) ;

            result = inStream.read_Object();

            
            
        } finally {
            bootstrapDelegate.releaseReply( null, inStream ) ;
        }

        return result ;
    }

    public Set<String> list()
    {
        InputStream inStream = null ;
        java.util.Set result = new java.util.HashSet() ;

        try {
            inStream = invoke( "list", null ) ;

            int count = inStream.read_long();
            for (int i=0; i < count; i++) {
                result.add(inStream.read_string());
            }

            
            
        } finally {
            bootstrapDelegate.releaseReply( null, inStream ) ;
        }

        return result ;
    }
}
