


package com.sun.corba.ee.impl.naming.cosnaming;









@Naming
public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore
{
    private NamingSystemException wrapper ;

    
    public TransientNamingContext(com.sun.corba.ee.spi.orb.ORB orb, 
        org.omg.CORBA.Object initial,
        POA nsPOA )
        throws java.lang.Exception
    {
        super(orb, nsPOA);
        this.localRoot = initial;
    }

    
    @Naming
    public final void bindImpl(NameComponent n, org.omg.CORBA.Object obj,
                           BindingType bt)
        throws org.omg.CORBA.SystemException
    {
        InternalBindingKey key = new InternalBindingKey(n);
        NameComponent[] name = new NameComponent[1];
        name[0] = n;
        Binding b = new Binding(name,bt);
        InternalBindingValue value = new InternalBindingValue(b,null);
        value.theObjectRef = obj;
        InternalBindingValue oldValue = bindingMap.put(key,value);

        if (oldValue != null) {
            throw wrapper.transNcBindAlreadyBound() ;
        }
    }

    
    @Naming
    public final org.omg.CORBA.Object resolveImpl(NameComponent n,
                                              BindingTypeHolder bth)
        throws org.omg.CORBA.SystemException
    {
        if ((n.id.length() == 0) && (n.kind.length() == 0)) {
            bth.value = BindingType.ncontext;
            return localRoot;
        }
    
        
        InternalBindingKey key = new InternalBindingKey(n);
        InternalBindingValue value = bindingMap.get(key);
        if (value == null) {
            return null;
        }
    
        
        bth.value = value.theBinding.binding_type;
        return value.theObjectRef;
    }

    
    @Naming
    public final org.omg.CORBA.Object unbindImpl(NameComponent n)
        throws org.omg.CORBA.SystemException
    {
        
        InternalBindingKey key = new InternalBindingKey(n);
        InternalBindingValue value = bindingMap.remove(key);

        
        if (value == null) {
            return null;
        } else {
            return value.theObjectRef;
       }
    }  

    
    @Naming
    public final void listImpl(int how_many, BindingListHolder bl,
                           BindingIteratorHolder bi)
        throws org.omg.CORBA.SystemException
    {
        try {
            
            
            
            
            Map<InternalBindingKey,InternalBindingValue> copy =
                new HashMap<InternalBindingKey,InternalBindingValue>( bindingMap ) ;
            TransientBindingIterator bindingIterator =
                new TransientBindingIterator( this.orb, copy, nsPOA);
            
            
            bindingIterator.list(how_many,bl);
            
            byte[] objectId = nsPOA.activate_object( bindingIterator );
            org.omg.CORBA.Object obj = nsPOA.id_to_reference( objectId );
      
            
            org.omg.CosNaming.BindingIterator bindingRef = 
                org.omg.CosNaming.BindingIteratorHelper.narrow( obj );
      
            bi.value = bindingRef;
        } catch (org.omg.CORBA.SystemException e) {
            throw e;
        } catch (Exception e) {
            
            throw wrapper.transNcListGotExc( e ) ;
        }
    }
  
    
    @Naming
    public final org.omg.CosNaming.NamingContext newContextImpl()
        throws org.omg.CORBA.SystemException
    {
        try {
            
            TransientNamingContext transContext =
                new TransientNamingContext(
                (com.sun.corba.ee.spi.orb.ORB) orb,localRoot, nsPOA);

            byte[] objectId = nsPOA.activate_object( transContext );
            org.omg.CORBA.Object obj = nsPOA.id_to_reference( objectId );
            return org.omg.CosNaming.NamingContextHelper.narrow( obj );
        } catch (Exception e) {
            throw wrapper.transNcNewctxGotExc( e ) ;
        }
    }

    
    @Naming
    public final void destroyImpl()
        throws org.omg.CORBA.SystemException
    {
        
        try {
            byte[] objectId = nsPOA.servant_to_id( this );
            if( objectId != null ) {
                nsPOA.deactivate_object( objectId );
            }
        } catch (Exception e) { 
            throw wrapper.transNcDestroyGotExc( e ) ;
        }
    }

    
    private String getName( NameComponent n ) {
        return n.id + "." + n.kind;
    }

    
    public final boolean isEmptyImpl() {
        return bindingMap.isEmpty();
    }

    
    private final Map<InternalBindingKey,InternalBindingValue>  bindingMap = 
        new HashMap<InternalBindingKey,InternalBindingValue>();

    
    public org.omg.CORBA.Object localRoot;
}

