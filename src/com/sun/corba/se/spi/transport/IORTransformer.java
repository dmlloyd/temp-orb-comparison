

package com.sun.corba.se.spi.transport ;

import com.sun.corba.se.spi.ior.IOR ;
import com.sun.corba.se.spi.encoding.CorbaInputObject ;
import com.sun.corba.se.spi.encoding.CorbaOutputObject ;


public interface IORTransformer {
    IOR unmarshal( CorbaInputObject io ) ;

    void marshal( CorbaOutputObject oo, IOR ior ) ;
}
