

package xxxx;





public class ReaderThreadImpl
    implements
        ReaderThread,
        Work
{
    private ORB orb;
    private Connection connection;
    private Selector selector;
    private boolean keepRunning;
    private long enqueueTime;

    public ReaderThreadImpl(ORB orb,
                            Connection connection, Selector selector)
    {
        this.orb = orb;
        this.connection = connection;
        this.selector = selector;
        keepRunning = true;
    }

    
    
    
    

    public Connection getConnection()
    {
        return connection;
    }

    public void close()
    {
        if (orb.transportDebugFlag) {
            dprint(".close: " + connection);
        }

        keepRunning = false;
    }

    
    
    
    

    
    public void doWork()
    {
        try {
            if (orb.transportDebugFlag) {
                dprint(".doWork: Start ReaderThread: " + connection);
            }
            while (keepRunning) {
                try {

                    if (orb.transportDebugFlag) {
                        dprint(".doWork: Start ReaderThread cycle: "
                               + connection);
                    }

                    if (connection.read()) {
                        
                        return;
                    }

                    if (orb.transportDebugFlag) {
                        dprint(".doWork: End ReaderThread cycle: "
                               + connection);
                    }

                } catch (Throwable t) {
                    if (orb.transportDebugFlag) {
                        dprint(".doWork: exception in read: " + connection,t);
                    }
                    orb.getTransportManager().getSelector(0)
                        .unregisterForEvent(getConnection().getEventHandler());
                    getConnection().close();
                }
            }
        } finally {
            if (orb.transportDebugFlag) {
                dprint(".doWork: Terminated ReaderThread: " + connection);
            }
        }
    }

    public void setEnqueueTime(long timeInMillis)
    {
        enqueueTime = timeInMillis;
    }

    public long getEnqueueTime()
    {
        return enqueueTime;
    }

    public String getName() { return "ReaderThread"; }

    
    
    
    

    private void dprint(String msg)
    {
        ORBUtility.dprint("ReaderThreadImpl", msg);
    }

    protected void dprint(String msg, Throwable t)
    {
        dprint(msg);
        t.printStackTrace(System.out);
    }
}


