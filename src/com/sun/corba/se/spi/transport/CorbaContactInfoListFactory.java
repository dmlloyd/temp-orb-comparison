

package xxxx;




public interface CorbaContactInfoListFactory {
    
    public void setORB(ORB orb);

    public CorbaContactInfoList create( IOR ior ) ;
}
