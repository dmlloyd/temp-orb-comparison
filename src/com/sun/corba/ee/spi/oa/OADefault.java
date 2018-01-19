

package com.sun.corba.ee.spi.oa ;

import com.sun.corba.ee.impl.oa.poa.POAFactory ;
import com.sun.corba.ee.impl.oa.toa.TOAFactory ;
import com.sun.corba.ee.spi.orb.ORB ;


public class OADefault {
    public static ObjectAdapterFactory makePOAFactory( ORB orb )
    {
        ObjectAdapterFactory oaf = new POAFactory() ;
        oaf.init( orb ) ;
        return oaf ;
    }

    public static ObjectAdapterFactory makeTOAFactory( ORB orb )
    {
        ObjectAdapterFactory oaf = new TOAFactory() ;
        oaf.init( orb ) ;
        return oaf ;
    }
}
