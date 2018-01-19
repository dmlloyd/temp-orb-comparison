



package javax.transaction;


public class InvalidTransactionException extends java.rmi.RemoteException 
{
    public InvalidTransactionException()
    {
        super();
    }

    public InvalidTransactionException(String msg)
    {
        super(msg);
    }
}

