

package com.sun.corba.se.spi.oa ;

import com.sun.corba.se.impl.oa.poa.POAFactory ;
import com.sun.corba.se.impl.oa.toa.TOAFactory ;
import com.sun.corba.se.spi.orb.ORB ;


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
