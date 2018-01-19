



package com.sun.corba.ee.impl.oa.toa;

import com.sun.corba.ee.impl.misc.ORBUtility ;
import com.sun.corba.ee.spi.orb.ORB ;
import org.glassfish.gmbal.Description;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@com.sun.corba.ee.spi.trace.TransientObjectManager
@ManagedData
@Description( "Maintains mapping from Object ID to servant")
public final class TransientObjectManager {
    private ORB orb ;
    private int maxSize = 128;
    private Element[] elementArray; 
    private Element freeList;

    @ManagedAttribute() 
    @Description( "The element array mapping indices into servants" ) 
    private synchronized Element[] getElements() {
        return elementArray.clone() ;
    }

    public TransientObjectManager( ORB orb )
    {
        this.orb = orb ;

        elementArray = new Element[maxSize];
        elementArray[maxSize-1] = new Element(maxSize-1,null);
        for ( int i=maxSize-2; i>=0; i-- ) 
            elementArray[i] = new Element(i,elementArray[i+1]);
        freeList = elementArray[0];
    }

    @com.sun.corba.ee.spi.trace.TransientObjectManager
    public synchronized byte[] storeServant(java.lang.Object servant, java.lang.Object servantData)
    {
        if ( freeList == null ) 
            doubleSize();

        Element elem = freeList;
        freeList = (Element)freeList.servant;
        
        byte[] result = elem.getKey(servant, servantData);
        return result ;
    }

    @com.sun.corba.ee.spi.trace.TransientObjectManager
    public synchronized java.lang.Object lookupServant(byte transientKey[]) 
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        int counter = ORBUtility.bytesToInt(transientKey,4);

        if (elementArray[index].counter == counter &&
            elementArray[index].valid ) {
            return elementArray[index].servant;
        }

        
        return null;
    }

    @com.sun.corba.ee.spi.trace.TransientObjectManager
    public synchronized java.lang.Object lookupServantData(byte transientKey[])
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        int counter = ORBUtility.bytesToInt(transientKey,4);

        if (elementArray[index].counter == counter &&
            elementArray[index].valid ) {
            return elementArray[index].servantData;
        }

        
        return null;
    }

    @InfoMethod
    private void deleteAtIndex( int index ) { }

    @com.sun.corba.ee.spi.trace.TransientObjectManager
    public synchronized void deleteServant(byte transientKey[])
    {
        int index = ORBUtility.bytesToInt(transientKey,0);
        deleteAtIndex(index);

        elementArray[index].delete(freeList);
        freeList = elementArray[index];
    }

    public synchronized byte[] getKey(java.lang.Object servant)
    {
        for ( int i=0; i<maxSize; i++ )
            if ( elementArray[i].valid && 
                 elementArray[i].servant == servant )
                return elementArray[i].toBytes();

        
        return null;
    }

    private void doubleSize()
    {
        

        Element old[] = elementArray;
        int oldSize = maxSize;
        maxSize *= 2;
        elementArray = new Element[maxSize];

        for ( int i=0; i<oldSize; i++ )
            elementArray[i] = old[i];    

        elementArray[maxSize-1] = new Element(maxSize-1,null);
        for ( int i=maxSize-2; i>=oldSize; i-- ) 
            elementArray[i] = new Element(i,elementArray[i+1]);
        freeList = elementArray[oldSize];
    }
}


@ManagedData
@Description( "A single element mapping one ObjectId to a Servant")
final class Element {
    java.lang.Object servant=null;     
    java.lang.Object servantData=null;    
    int index=-1;
    int counter=0; 
    boolean valid=false; 
    

    @ManagedAttribute
    @Description( "The servant" )
    private synchronized Object getServant() {
        return servant ;
    }

    @ManagedAttribute
    @Description( "The servant data" )
    private synchronized Object getServantData() {
        return servantData ;
    }

    @ManagedAttribute
    @Description( "The reuse counter")
    private synchronized int getReuseCounter() {
        return counter ;
    }

    @ManagedAttribute
    @Description( "The index of this entry")
    private synchronized int getIndex() {
        return index ;
    }

    Element(int i, java.lang.Object next)
    {
        servant = next;
        index = i;
    }

    byte[] getKey(java.lang.Object servant, java.lang.Object servantData)
    {
        this.servant = servant;
        this.servantData = servantData;
        this.valid = true;

        return toBytes();
    }

    byte[] toBytes()
    {    
        

        byte key[] = new byte[8];
        ORBUtility.intToBytes(index, key, 0);
        ORBUtility.intToBytes(counter, key, 4);

        return key;
    }

    void delete(Element freeList)
    {
        if ( !valid )    
            return;
        counter++;
        servantData = null;
        valid = false;

        
        servant = freeList;
    }

    @Override
    public String toString() 
    {
        return "Element[" + index + ", " + counter + "]" ;
    }
}

