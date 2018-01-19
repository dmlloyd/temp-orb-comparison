

package xxxx;



public interface IORTransformer {
    IOR unmarshal( CDRInputObject io ) ;

    void marshal( CDROutputObject oo, IOR ior ) ;
}
