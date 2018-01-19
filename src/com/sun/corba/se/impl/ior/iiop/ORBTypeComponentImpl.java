

package com.sun.corba.se.impl.ior.iiop;

import org.omg.IOP.TAG_ORB_TYPE ;

import com.sun.corba.se.spi.ior.TaggedComponentBase ;

import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent ;

import org.omg.CORBA_2_3.portable.OutputStream ;


public class ORBTypeComponentImpl extends TaggedComponentBase
    implements ORBTypeComponent
{
    private int ORBType;

    public boolean equals( Object obj )
    {
        if (!(obj instanceof ORBTypeComponentImpl))
            return false ;

        ORBTypeComponentImpl other = (ORBTypeComponentImpl)obj ;

        return ORBType == other.ORBType ;
    }

    public int hashCode()
    {
        return ORBType ;
    }

    public String toString()
    {
        return "ORBTypeComponentImpl[ORBType=" + ORBType + "]" ;
    }

    public ORBTypeComponentImpl(int ORBType)
    {
        this.ORBType = ORBType ;
    }

    public int getId()
    {
        return TAG_ORB_TYPE.value ; 
    }

    public int getORBType()
    {
        return ORBType ;
    }

    public void writeContents(OutputStream os)
    {
        os.write_ulong( ORBType ) ;
    }
}
