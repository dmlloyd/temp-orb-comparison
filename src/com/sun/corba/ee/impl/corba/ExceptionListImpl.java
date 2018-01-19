



package com.sun.corba.ee.impl.corba;

import java.util.List;
import java.util.ArrayList;

import org.omg.CORBA.Bounds;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ORB;


public class ExceptionListImpl extends ExceptionList {

    private static final int     INITIAL_CAPACITY       = 2;

    private List<TypeCode> _exceptions;

    public ExceptionListImpl() {
        _exceptions = new ArrayList<TypeCode>(INITIAL_CAPACITY);
    }

    public synchronized int count() 
    {
        return _exceptions.size();
    }

    public synchronized void add(TypeCode tc)
    {
        _exceptions.add(tc);
    }

    public synchronized TypeCode item(int index)
        throws Bounds
    {
        try {
            return _exceptions.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }

    public synchronized void remove(int index)
        throws Bounds
    {
        try {
            _exceptions.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }

}

