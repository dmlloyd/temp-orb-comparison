

package com.sun.corba.ee.impl.legacy.connection;


@ManagedData
@Description( "An address of a transport endpoint that the ORB "
    + "uses for listening to incoming requests" ) 
public class USLPort
{
    private String type;
    private int    port;

    public USLPort (String type, int port)
    {
        this.type = type;
        this.port = port;
    }

    @ManagedAttribute
    @Description( "The type of the port (e.g. plain text vs. SSL)" )
    public String getType  () { return type; }

    @ManagedAttribute
    @Description( "The TCP port number" ) 
    public int    getPort  () { return port; }
    public String toString () { return type + ":" + port; }
}



