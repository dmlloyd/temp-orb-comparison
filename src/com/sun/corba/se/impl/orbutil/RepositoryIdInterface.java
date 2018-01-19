

package com.sun.corba.se.impl.orbutil;

import org.omg.CORBA.ORB;
import java.io.Serializable;
import java.net.MalformedURLException;


public interface RepositoryIdInterface
{
    Class getClassFromType() throws ClassNotFoundException;

    Class getClassFromType(String codebaseURL)
        throws ClassNotFoundException, MalformedURLException;

    Class getClassFromType(Class expectedType,
                           String codebaseURL)
        throws ClassNotFoundException, MalformedURLException;

    String getClassName();
}
