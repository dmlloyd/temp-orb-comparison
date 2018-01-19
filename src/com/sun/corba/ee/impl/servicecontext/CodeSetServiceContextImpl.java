


package com.sun.corba.ee.impl.servicecontext;





public class CodeSetServiceContextImpl extends ServiceContextBase
    implements CodeSetServiceContext 
{
    private CodeSetComponentInfo.CodeSetContext csc ;

    public CodeSetServiceContextImpl( CodeSetComponentInfo.CodeSetContext csc )
    {
        this.csc = csc ;
    }

    public CodeSetServiceContextImpl(InputStream is, GIOPVersion gv)
    {
        super(is) ;
        csc = new CodeSetComponentInfo.CodeSetContext() ;
        csc.read( (MarshalInputStream)in ) ;
    }

    public int getId() 
    { 
        return SERVICE_CONTEXT_ID ; 
    }

    public void writeData( OutputStream os ) 
    {
        csc.write( (MarshalOutputStream)os ) ;
    }
    
    public CodeSetComponentInfo.CodeSetContext getCodeSetContext() 
    {
        return csc ;
    }

    public String toString() 
    {
        return "CodeSetServiceContextImpl[ csc=" + csc + " ]" ;
    }
}
