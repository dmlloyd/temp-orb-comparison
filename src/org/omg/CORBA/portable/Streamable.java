
package org.omg.CORBA.portable;




public interface Streamable {
    
    void _read(InputStream istream);
    
    void _write(OutputStream ostream);

    
    TypeCode _type();
}
