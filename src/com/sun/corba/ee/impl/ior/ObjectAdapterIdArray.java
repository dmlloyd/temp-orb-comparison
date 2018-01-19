


package xxxx;


public class ObjectAdapterIdArray extends ObjectAdapterIdBase {
    private final String[] objectAdapterId ;

    @Override
    public boolean equals( Object obj ) {
        
        
        
        
        return super.equals( obj ) ;
    }

    public ObjectAdapterIdArray( String[] objectAdapterId )
    {
        this.objectAdapterId = (String[])objectAdapterId.clone() ;
    }

    
    public ObjectAdapterIdArray( String name1, String name2 ) 
    {
        objectAdapterId = new String[2] ;
        objectAdapterId[0] = name1 ;
        objectAdapterId[1] = name2 ;
    }

    public int getNumLevels()
    {
        return objectAdapterId.length ;
    }

    public Iterator<String> iterator()
    {
        return Arrays.asList( objectAdapterId ).iterator() ;
    }

    public String[] getAdapterName()
    {      
        return (String[])objectAdapterId.clone() ;
    }
}
