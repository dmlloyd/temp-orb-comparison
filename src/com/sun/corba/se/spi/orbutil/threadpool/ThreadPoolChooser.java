

package com.sun.corba.se.spi.orbutil.threadpool;


public interface ThreadPoolChooser
{
    
    public ThreadPool getThreadPool();

    
    public ThreadPool getThreadPool(int id);

    
    public String[] getThreadPoolIds();
}
