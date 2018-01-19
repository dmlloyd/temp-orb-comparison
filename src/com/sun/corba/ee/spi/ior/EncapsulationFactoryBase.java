


package xxxx;




public abstract class EncapsulationFactoryBase<E extends Identifiable> 
    implements IdentifiableFactory<E> {

    private int id ;

    public int getId() 
    {
        return id ;
    }

    public EncapsulationFactoryBase( int id )
    {
        this.id = id ;
    }

    public final E create( ORB orb, InputStream in ) 
    {
        InputStream is = EncapsulationUtility.getEncapsulationStream( orb, in ) ;
        return readContents( is ) ;
    }

    protected abstract E readContents( InputStream is ) ;
}
