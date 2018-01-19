
package xxxx;




public interface Streamable {
    
    void _read(InputStream istream);
    
    void _write(OutputStream ostream);

    
    TypeCode _type();
}
