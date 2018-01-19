


package com.sun.corba.ee.impl.naming.cosnaming;


import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;


import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;




import java.util.Map;
import java.util.Iterator;


public class TransientBindingIterator extends BindingIteratorImpl
{
    
    
    private POA nsPOA;
    
    public TransientBindingIterator(ORB orb, 
        Map<InternalBindingKey,InternalBindingValue> aTable,
        POA thePOA )
        throws java.lang.Exception
    {
        super(orb);
        bindingMap = aTable;
        bindingIterator = aTable.values().iterator() ;
        currentSize = this.bindingMap.size();
        this.nsPOA = thePOA;
    }

    
    final public boolean nextOneImpl(org.omg.CosNaming.BindingHolder b)
    {
        
        boolean hasMore = bindingIterator.hasNext();
        if (hasMore) {
            b.value = bindingIterator.next().theBinding;
            currentSize--;
        } else {
            
            b.value = new Binding(new NameComponent[0],BindingType.nobject);
        }
        return hasMore;
    }

    
    final public void destroyImpl()
    {
        
        try {
            byte[] objectId = nsPOA.servant_to_id( this );
            if( objectId != null ) {
                nsPOA.deactivate_object( objectId );
            }
        } 
        catch( Exception e ) {
            NamingUtils.errprint("BindingIterator.Destroy():caught exception:");
            NamingUtils.printException(e);
        }
    }

    
    public final int remainingElementsImpl() {
        return currentSize;
    }

    private int currentSize;
    private Map<InternalBindingKey,InternalBindingValue> bindingMap;
    private Iterator<InternalBindingValue> bindingIterator;
}
