

package com.sun.corba.se.spi.copyobject ;


public interface ObjectCopier {
    Object copy( Object obj ) throws ReflectiveCopyException ;
}
