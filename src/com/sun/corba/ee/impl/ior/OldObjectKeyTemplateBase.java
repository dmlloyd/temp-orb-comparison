


package com.sun.corba.ee.impl.ior;

import com.sun.corba.ee.spi.ior.ObjectAdapterId ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.orb.ORBVersion ;
import com.sun.corba.ee.spi.orb.ORBVersionFactory ;

import com.sun.corba.ee.impl.ior.ObjectKeyFactoryImpl ;


public abstract class OldObjectKeyTemplateBase extends ObjectKeyTemplateBase 
{
    public OldObjectKeyTemplateBase( ORB orb, int magic, int scid, int serverid,
        String orbid, ObjectAdapterId oaid ) 
    {
        super( orb, magic, scid, serverid, orbid, oaid ) ;

        
        if (magic == ObjectKeyFactoryImpl.JAVAMAGIC_OLD)
            setORBVersion( ORBVersionFactory.getOLD() ) ;
        else if (magic == ObjectKeyFactoryImpl.JAVAMAGIC_NEW)
            setORBVersion( ORBVersionFactory.getNEW() ) ;
        else 
            throw wrapper.badMagic( Integer.valueOf( magic ) ) ;
    }
}
