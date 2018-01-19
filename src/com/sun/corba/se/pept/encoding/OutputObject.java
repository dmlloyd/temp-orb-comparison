

package com.sun.corba.se.pept.encoding;

import java.io.IOException;

import com.sun.corba.se.pept.protocol.MessageMediator;


public interface OutputObject
{
    public void setMessageMediator(MessageMediator messageMediator);

    public MessageMediator getMessageMediator();

    public void close() throws IOException;
}


