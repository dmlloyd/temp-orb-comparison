


package xxxx;



public interface ContactInfo<C extends Connection> {
    
    C createConnection() throws IOException ;
}
