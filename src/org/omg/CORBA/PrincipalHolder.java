

package org.omg.CORBA;

import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;



@Deprecated
public final class PrincipalHolder implements Streamable {
    
    public Principal value;

    
    public PrincipalHolder() {
    }

    
    public PrincipalHolder(Principal initial) {
        value = initial;
    }

    public void _read(InputStream input) {
        value = input.read_Principal();
    }

    public void _write(OutputStream output) {
        output.write_Principal(value);
    }

    public org.omg.CORBA.TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_Principal);
    }

}
