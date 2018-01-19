
package org.omg.CORBA.portable;



public interface InvokeHandler {
    

    OutputStream _invoke(String method, InputStream input,
                         ResponseHandler handler)
        throws org.omg.CORBA.SystemException;
}
