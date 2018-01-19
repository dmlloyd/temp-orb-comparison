


package com.sun.corba.ee.impl.protocol ;


import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import com.sun.corba.ee.spi.oa.ObjectAdapter;

import com.sun.corba.ee.spi.protocol.MessageMediator;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;

import com.sun.corba.ee.spi.oa.NullServant ;

public abstract class SpecialMethod {
    static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public abstract boolean isNonExistentMethod() ;
    public abstract String getName();
    public abstract MessageMediator invoke(java.lang.Object servant,
                                                MessageMediator request,
                                                byte[] objectId,
                                                ObjectAdapter objectAdapter);

    public static final SpecialMethod getSpecialMethod(String operation) {
        for(int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(operation)) {
                return methods[i];
            }
        }
        return null;
    }

    static SpecialMethod[] methods = {
        new IsA(),
        new GetInterface(),
        new NonExistent(),
        new NotExistent()
    };
}

class NonExistent extends SpecialMethod {
    public boolean isNonExistentMethod() 
    {
        return true ;
    }

    public String getName() {           
        return "_non_existent";
    }

    public MessageMediator invoke(java.lang.Object servant,
                                       MessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        boolean result = (servant == null) || (servant instanceof NullServant) ;
        MessageMediator response =
            request.getProtocolHandler().createResponse(request, null);
        ((OutputStream)response.getOutputObject()).write_boolean(result);
        return response;
    }
}

class NotExistent extends NonExistent {
    @Override
    public String getName() {           
        return "_not_existent";
    }
}

class IsA extends SpecialMethod  {      
    public boolean isNonExistentMethod() 
    {
        return false ;
    }

    public String getName() {
        return "_is_a";
    }
    public MessageMediator invoke(java.lang.Object servant,
                                       MessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        if ((servant == null) || (servant instanceof NullServant)) {
            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.badSkeleton(), null);
        }
        
        String[] ids = objectAdapter.getInterfaces( servant, objectId );
        String clientId = 
            ((InputStream)request.getInputObject()).read_string();
        boolean answer = false;
        for(int i = 0; i < ids.length; i++) {
            if (ids[i].equals(clientId)) {
                answer = true;
                break;
            }
        }
            
        MessageMediator response =
            request.getProtocolHandler().createResponse(request, null);
        ((OutputStream)response.getOutputObject()).write_boolean(answer);
        return response;
    }
}

class GetInterface extends SpecialMethod  {     
    public boolean isNonExistentMethod() 
    {
        return false ;
    }

    public String getName() {
        return "_interface";
    }
    public MessageMediator invoke(java.lang.Object servant,
                                       MessageMediator request,
                                       byte[] objectId,
                                       ObjectAdapter objectAdapter)
    {
        if ((servant == null) || (servant instanceof NullServant)) {
            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.badSkeleton(), null);
        } else {
            return request.getProtocolHandler().createSystemExceptionResponse(
                request, wrapper.getinterfaceNotImplemented(), null);
        }
    }
}



