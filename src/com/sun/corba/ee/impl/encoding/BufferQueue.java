

package com.sun.corba.ee.impl.encoding;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class BufferQueue
{
    private LinkedList<ByteBuffer> list =
        new LinkedList<ByteBuffer>();
    
    public void enqueue(ByteBuffer item)
    {
        list.addLast(item);
    }
    
    public ByteBuffer dequeue() throws NoSuchElementException
    {
        return list.removeFirst();
    }
    
    public int size()
    {
        return list.size();
    }

    
    public void push(ByteBuffer item)
    {
        list.addFirst(item);
    }

    public void clear() {
        list.clear() ;
    }
}
