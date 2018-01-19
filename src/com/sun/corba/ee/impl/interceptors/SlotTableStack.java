


package xxxx;




public class SlotTableStack
{
    private static final InterceptorsSystemException wrapper =
        InterceptorsSystemException.self ;

    
    
    private java.util.List<SlotTable> tableContainer;

    
    private int currentIndex;
 
    
    private ORB orb;

    private PICurrent current ;

    
    SlotTableStack( ORB orb, PICurrent current ) {
       this.current = current ;
       this.orb = orb;

       currentIndex = 0;
       tableContainer = new java.util.ArrayList<SlotTable>( );
       pushSlotTable() ;
    }

    
    void pushSlotTable( ) {
        SlotTable table = new SlotTable( orb, current.getTableSize() );
        
        
        if (currentIndex == tableContainer.size()) {
            
            tableContainer.add( currentIndex, table );
        } else if (currentIndex > tableContainer.size()) {
            throw wrapper.slotTableInvariant( currentIndex,
                tableContainer.size() ) ;
        } else {
            
            tableContainer.set( currentIndex, table );
        }
        currentIndex++;
    }

    
    void  popSlotTable( ) {
        if(currentIndex == 1) {
            
            
            throw wrapper.cantPopOnlyPicurrent() ;
        }
        currentIndex--;
        SlotTable table = tableContainer.get( currentIndex );
        tableContainer.set( currentIndex, null ); 
        table.resetSlots( );
    }

    
    SlotTable peekSlotTable( ) {
        SlotTable result = tableContainer.get( currentIndex - 1 ) ;
        if (result.getSize() != current.getTableSize()) {
            
            result = new SlotTable( orb, current.getTableSize() ) ;
            tableContainer.set( currentIndex - 1, result ) ;
        }

        return result ;
    }
}
