


package com.sun.corba.ee.impl.ior.iiop;

import org.omg.IOP.TAG_JAVA_CODEBASE ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.TaggedComponentBase ;

import com.sun.corba.ee.spi.ior.iiop.JavaCodebaseComponent ;


public class JavaCodebaseComponentImpl extends TaggedComponentBase 
    implements JavaCodebaseComponent
{
    private String URLs ;

    public boolean equals( Object obj )
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof JavaCodebaseComponentImpl))
            return false ;

        JavaCodebaseComponentImpl other = (JavaCodebaseComponentImpl)obj ;

        return URLs.equals( other.getURLs() ) ;
    }

    public int hashCode()
    {
        return URLs.hashCode() ;
    }

    public String toString()
    {
        return "JavaCodebaseComponentImpl[URLs=" + URLs + "]" ;
    }

    public String getURLs() 
    {
        return URLs ;
    }

    public JavaCodebaseComponentImpl( String URLs ) 
    {
        this.URLs = URLs ;
    }
    
    public void writeContents(OutputStream os) 
    {
        os.write_string( URLs ) ;
    }
    
    public int getId() 
    {
        return TAG_JAVA_CODEBASE.value ; 
    }
}
