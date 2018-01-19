


package xxxx;

public interface ReferenceFactory extends org.omg.CORBA.Object,
    org.omg.CORBA.portable.IDLEntity 
{
    
    org.omg.CORBA.Object createReference( byte[] key ) ;

    
    void destroy() ;
}
