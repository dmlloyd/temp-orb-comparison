

package xxxx;




public abstract class _CodeBaseImplBase extends org.omg.CORBA.portable.ObjectImpl
    implements com.sun.org.omg.SendingContext.CodeBase, org.omg.CORBA.portable.InvokeHandler
{

    
    public _CodeBaseImplBase ()
    {
    }

    private static java.util.Map<String,Integer> _methods = 
        new java.util.HashMap<String,Integer> ();

    static
    {
        _methods.put ("get_ir", 0);
        _methods.put ("implementation", 1 ) ; 
        _methods.put ("implementations", 2 ) ;
        _methods.put ("meta", 3 ) ;
        _methods.put ("metas", 4 ) ;
        _methods.put ("bases", 5 ) ;
    }

    public org.omg.CORBA.portable.OutputStream _invoke (String method,
                                                        org.omg.CORBA.portable.InputStream in,
                                                        org.omg.CORBA.portable.ResponseHandler rh)
    {
        org.omg.CORBA.portable.OutputStream out = rh.createReply();
        java.lang.Integer __method = _methods.get (method);
        if (__method == null)
            throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

        switch (__method.intValue ())
            {

                
            case 0:  
                {
                    com.sun.org.omg.CORBA.Repository __result = null;
                    __result = this.get_ir ();
                    com.sun.org.omg.CORBA.RepositoryHelper.write (out, __result);
                    break;
                }


                
            case 1:  
                {
                    String x = com.sun.org.omg.CORBA.RepositoryIdHelper.read (in);
                    String __result = null;
                    __result = this.implementation (x);
                    out.write_string (__result);
                    break;
                }

            case 2:  
                {
                    String x[] = com.sun.org.omg.CORBA.RepositoryIdSeqHelper.read (in);
                    String __result[] = null;
                    __result = this.implementations (x);
                    com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper.write (out, __result);
                    break;
                }


                
            case 3:  
                {
                    String x = com.sun.org.omg.CORBA.RepositoryIdHelper.read (in);
                    com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription __result = null;
                    __result = this.meta (x);
                    com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper.write (out, __result);
                    break;
                }

            case 4:  
                {
                    String x[] = com.sun.org.omg.CORBA.RepositoryIdSeqHelper.read (in);
                    com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription __result[] = null;
                    __result = this.metas (x);
                    com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper.write (out, __result);
                    break;
                }


                
            case 5:  
                {
                    String x = com.sun.org.omg.CORBA.RepositoryIdHelper.read (in);
                    String __result[] = null;
                    __result = this.bases (x);
                    com.sun.org.omg.CORBA.RepositoryIdSeqHelper.write (out, __result);
                    break;
                }

            default:
                throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            }

        return out;
    } 

    
    private final static String[] __ids = {
        "IDL:omg.org/SendingContext/CodeBase:1.0", 
        "IDL:omg.org/SendingContext/RunTime:1.0"};

    public String[] _ids ()
    {
        return __ids.clone();
    }


} 
