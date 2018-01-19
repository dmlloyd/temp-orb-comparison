

package com.sun.corba.se.impl.ior ;

import java.util.Iterator ;
import org.omg.CORBA_2_3.portable.OutputStream ;


public class ObjectAdapterIdNumber extends ObjectAdapterIdArray {
    private int poaid ;

    public ObjectAdapterIdNumber( int poaid )
    {
        super( "OldRootPOA", Integer.toString( poaid ) ) ;
        this.poaid = poaid ;
    }

    public int getOldPOAId()
    {
        return poaid ;
    }
}
