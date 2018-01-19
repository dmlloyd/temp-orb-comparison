


package com.sun.corba.ee.impl.interceptors;




public class PICurrent extends org.omg.CORBA.LocalObject
    implements Current
{
    private static final OMGSystemException wrapper =
        OMGSystemException.self ;

    
    private int slotCounter;

    
    private transient ORB myORB;

    
    
    private boolean orbInitializing;

    
    
    private transient ThreadLocal<SlotTableStack> threadLocalSlotTable
        = new ThreadLocal<SlotTableStack>() {
        @Override
            protected SlotTableStack initialValue( ) {
                return new SlotTableStack( myORB, PICurrent.this );
            }
        };

    
    PICurrent( ORB myORB ) {
        this.myORB = myORB;
        this.orbInitializing = true;
        slotCounter = 0;
    }

    @Override
    public org.omg.CORBA.ORB _orb() {
        return myORB;
    }

    synchronized int getTableSize() {
        return slotCounter ;
    }

    
    synchronized int allocateSlotId( ) {
        int slotId = slotCounter;
        slotCounter = slotCounter + 1;
        return slotId;
    }

    
    SlotTable getSlotTable( ) {
        SlotTable table = threadLocalSlotTable.get().peekSlotTable();
        return table;
    }

    
    void pushSlotTable( ) {
        SlotTableStack st = threadLocalSlotTable.get();
        st.pushSlotTable( );
    }


    
    void popSlotTable( ) {
        SlotTableStack st = threadLocalSlotTable.get();
        st.popSlotTable( );
    }

    
    public void set_slot( int id, Any data ) throws InvalidSlot 
    {
        if( orbInitializing ) {
            
            
            
            throw wrapper.invalidPiCall3() ;
        }

        getSlotTable().set_slot( id, data );
    }

    
    public Any get_slot( int id ) throws InvalidSlot 
    {
        if( orbInitializing ) {
            
            
            
            throw wrapper.invalidPiCall4() ;
        }

        return getSlotTable().get_slot( id );
    }

    
    void resetSlotTable( ) {
        getSlotTable().resetSlots();
    }

    
    void setORBInitializing( boolean init ) {
        this.orbInitializing = init;
    }
}


    
