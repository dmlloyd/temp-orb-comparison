

package com.sun.corba.ee.spi.transport ;



public interface IORTransformer {
    IOR unmarshal( CDRInputObject io ) ;

    void marshal( CDROutputObject oo, IOR ior ) ;
}
