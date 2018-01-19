

package xxxx;



public interface IdentifiableFactoryFinder
{
    
    Identifiable create(int id, InputStream is);

    
    void registerFactory( IdentifiableFactory factory ) ;
}
