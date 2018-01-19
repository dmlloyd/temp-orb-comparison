

package com.sun.corba.se.spi.orbutil.threadpool;

public interface Work
{

    
    public void doWork();

    
    public void setEnqueueTime(long timeInMillis);

    
    public long getEnqueueTime();

    
    public String getName();

}


