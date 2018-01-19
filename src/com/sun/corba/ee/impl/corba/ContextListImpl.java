



package com.sun.corba.ee.impl.corba;


public class ContextListImpl extends ContextList 
{
    private final static int     INITIAL_CAPACITY       = 2;

    private org.omg.CORBA.ORB _orb;
    private List<String> _contexts;

    public ContextListImpl(org.omg.CORBA.ORB orb) 
    {
        
        _orb = orb;
        _contexts = new ArrayList<String>(INITIAL_CAPACITY);
    }

    public synchronized int count() 
    {
        return _contexts.size();
    }

    public synchronized void add(String ctxt)
    {
        _contexts.add(ctxt);
    }

    public synchronized String item(int index)
        throws Bounds
    {
        try {
            return _contexts.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }

    public synchronized void remove(int index)
        throws Bounds
    {
        try {
            _contexts.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }
}
