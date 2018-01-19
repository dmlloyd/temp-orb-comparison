
package xxxx;



public interface ResponseHandler {
    
    OutputStream createReply();

    
    OutputStream createExceptionReply();
}
