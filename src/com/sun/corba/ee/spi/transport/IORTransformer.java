

package com.sun.corba.ee.spi.transport ;

import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.impl.encoding.CDRInputObject ;
import com.sun.corba.ee.impl.encoding.CDROutputObject ;


public interface IORTransformer {
    IOR unmarshal( CDRInputObject io ) ;

    void marshal( CDROutputObject oo, IOR ior ) ;
}
