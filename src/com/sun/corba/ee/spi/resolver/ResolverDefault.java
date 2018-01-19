

package com.sun.corba.ee.spi.resolver ;

import java.io.File ;

import com.sun.corba.ee.impl.resolver.LocalResolverImpl ;
import com.sun.corba.ee.impl.resolver.ORBInitRefResolverImpl ;
import com.sun.corba.ee.impl.resolver.ORBDefaultInitRefResolverImpl ;
import com.sun.corba.ee.impl.resolver.BootstrapResolverImpl ;
import com.sun.corba.ee.impl.resolver.CompositeResolverImpl ;
import com.sun.corba.ee.impl.resolver.INSURLOperationImpl ;
import com.sun.corba.ee.impl.resolver.SplitLocalResolverImpl ;
import com.sun.corba.ee.impl.resolver.FileResolverImpl ;

import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.orb.Operation ;
import org.glassfish.pfl.basic.contain.Pair;


public class ResolverDefault {
    
    public static LocalResolver makeLocalResolver( ) 
    {
        return new LocalResolverImpl() ;
    }

    
    public static Resolver makeORBInitRefResolver( Operation urlOperation,
        Pair<String,String>[] initRefs ) 
    {
        return new ORBInitRefResolverImpl( urlOperation, initRefs ) ;
    }

    public static Resolver makeORBDefaultInitRefResolver( Operation urlOperation,
        String defaultInitRef ) 
    {
        return new ORBDefaultInitRefResolverImpl( urlOperation,
            defaultInitRef ) ;
    }

    
    public static Resolver makeBootstrapResolver( ORB orb, String host, int port ) 
    {
        return new BootstrapResolverImpl( orb, host, port ) ;
    }

    
    public static Resolver makeCompositeResolver( Resolver first, Resolver second ) 
    {
        return new CompositeResolverImpl( first, second ) ;
    }

    public static Operation makeINSURLOperation( ORB orb )
    {
        return new INSURLOperationImpl( orb ) ;
    }

    public static LocalResolver makeSplitLocalResolver( Resolver resolver,
        LocalResolver localResolver ) 
    {
        return new SplitLocalResolverImpl( resolver, localResolver ) ;
    }

    public static Resolver makeFileResolver( ORB orb, File file ) 
    {
        return new FileResolverImpl( orb, file ) ;
    }
}

