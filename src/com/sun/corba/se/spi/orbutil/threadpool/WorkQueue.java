

package com.sun.corba.se.spi.orbutil.threadpool;

public interface WorkQueue
{

    
    public void addWork(Work aWorkItem);

    
    public String getName();

    
    public long totalWorkItemsAdded();

    
    public int workItemsInQueue();

    
    public long averageTimeInQueue();

    
    public void setThreadPool(ThreadPool aThreadPool);

    
    public ThreadPool getThreadPool();
}


