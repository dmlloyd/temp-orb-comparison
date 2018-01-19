
package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;



public interface Streamable {
    
    void _read(InputStream istream);
    
    void _write(OutputStream ostream);

    
    TypeCode _type();
}
