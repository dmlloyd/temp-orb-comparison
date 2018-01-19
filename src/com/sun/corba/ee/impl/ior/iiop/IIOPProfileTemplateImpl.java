


package com.sun.corba.ee.impl.ior.iiop;

import java.util.Iterator ;

import org.omg.IOP.TAG_INTERNET_IOP ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.TaggedComponent ;
import com.sun.corba.ee.spi.ior.TaggedProfile ;
import com.sun.corba.ee.spi.ior.TaggedProfileTemplate ;
import com.sun.corba.ee.spi.ior.TaggedProfileTemplateBase ;
import com.sun.corba.ee.spi.ior.ObjectKeyTemplate ;
import com.sun.corba.ee.spi.ior.ObjectId ;

import com.sun.corba.ee.impl.ior.EncapsulationUtility ;

import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate ;
import com.sun.corba.ee.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.ee.spi.ior.iiop.IIOPFactories ;

import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.ee.spi.orb.ORB ;


public class IIOPProfileTemplateImpl extends TaggedProfileTemplateBase 
    implements IIOPProfileTemplate 
{
    private ORB orb ;
    private GIOPVersion giopVersion ;
    private IIOPAddress primary ;
   
    public Iterator<TaggedComponent> getTaggedComponents() {
        return iterator() ;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder() ;
        sb.append( "IIOPProfileTemplateImpl[giopVersion=") ;
        sb.append(giopVersion.getMajor()).append('.').append(giopVersion.getMinor()) ;
        sb.append( " primary=" ) ;
        sb.append(primary.getHost()).append(':').append(primary.getPort()) ;
        sb.append( ']' ) ;
        return sb.toString() ;
    }

    public boolean equals( Object obj )
    {
        if (!(obj instanceof IIOPProfileTemplateImpl))
            return false ;

        IIOPProfileTemplateImpl other = (IIOPProfileTemplateImpl)obj ;

        return super.equals( obj ) && giopVersion.equals( other.giopVersion ) &&
            primary.equals( other.primary ) ;
    }

    public int hashCode()
    {
        return super.hashCode() ^ giopVersion.hashCode() ^ primary.hashCode() ;
    }

    public TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) 
    {
        return IIOPFactories.makeIIOPProfile( orb, oktemp, id, this ) ;
    }

    public GIOPVersion getGIOPVersion()
    {
        return giopVersion ;
    }

    public IIOPAddress getPrimaryAddress() 
    {
        return primary ;
    }

    public IIOPProfileTemplateImpl( ORB orb, GIOPVersion version, IIOPAddress primary ) 
    {
        this.orb = orb ;
        this.giopVersion = version ;
        this.primary = primary ;
        if (giopVersion.getMinor() == 0)
            
            
            makeImmutable() ;
    }

    public IIOPProfileTemplateImpl( InputStream istr )
    {
        byte major = istr.read_octet() ;
        byte minor = istr.read_octet() ;
        giopVersion = GIOPVersion.getInstance( major, minor ) ;
        primary = new IIOPAddressImpl( istr ) ;
        orb = (ORB)(istr.orb()) ;
        
        if (minor > 0) 
            EncapsulationUtility.readIdentifiableSequence(      
                this, orb.getTaggedComponentFactoryFinder(), istr ) ;

        makeImmutable() ;
    }
    
    public void write( ObjectKeyTemplate okeyTemplate, ObjectId id, OutputStream os) 
    {
        giopVersion.write( os ) ;
        primary.write( os ) ;

        
        
        

        
        OutputStream encapsulatedOS = OutputStreamFactory.newEncapsOutputStream( (ORB)os.orb()
        ) ;

        okeyTemplate.write( id, encapsulatedOS ) ;
        EncapsulationUtility.writeOutputStream( encapsulatedOS, os ) ;

        if (giopVersion.getMinor() > 0) 
            EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }
    
    
    public void writeContents( OutputStream os) 
    {
        giopVersion.write( os ) ;
        primary.write( os ) ;

        if (giopVersion.getMinor() > 0) 
            EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }
    
    public int getId() 
    {
        return TAG_INTERNET_IOP.value ;
    }

    public boolean isEquivalent( TaggedProfileTemplate temp )
    {
        if (!(temp instanceof IIOPProfileTemplateImpl))
            return false ;

        IIOPProfileTemplateImpl tempimp = (IIOPProfileTemplateImpl)temp ;

        return primary.equals( tempimp.primary )  ;
    }

}
