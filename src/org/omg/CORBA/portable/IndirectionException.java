


package org.omg.CORBA.portable;

import org.omg.CORBA.SystemException;

public class IndirectionException extends SystemException {

    
    public int offset;

    
    public IndirectionException(int offset){
        super("", 0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        this.offset = offset;
    }
}
