



package xxxx;









public class FVDCodeBaseImpl extends _CodeBaseImplBase
{
    
    private static Map<String,FullValueDescription> fvds = 
        new HashMap<String,FullValueDescription>();

    
    
    private transient ORB orb = null;

    private static final OMGSystemException wrapper =
        OMGSystemException.self ;

    
    
    
    
    private transient ValueHandlerImpl vhandler = null;

    public FVDCodeBaseImpl( ValueHandler vh ) {
        
        this.vhandler = (com.sun.corba.ee.impl.io.ValueHandlerImpl)vh ;  
    }

    
    public com.sun.org.omg.CORBA.Repository get_ir (){
        return null;
    }

    
    public String implementation (String x){
        try{
            
            
            String result = Util.getInstance().getCodebase(
                vhandler.getClassFromType(x));
            if (result == null) {
                return "";
            } else {
                return result;
            }
        } catch(ClassNotFoundException cnfe){
            throw wrapper.missingLocalValueImpl( cnfe ) ;
        }
    }

    public String[] implementations (String[] x){
        String result[] = new String[x.length];

        for (int i = 0; i < x.length; i++) {
            result[i] = implementation(x[i]);
        }

        return result;
    }

    
    public FullValueDescription meta (String x){
        try{
            FullValueDescription result = fvds.get(x);

            if (result == null) {
                try{
                    result = ValueUtility.translate(_orb(), 
                        ObjectStreamClass.lookup(vhandler.getAnyClassFromType(x)), vhandler);
                } catch(Throwable t){
                    if (orb == null) {
                        orb = ORB.init();
                    }

                    result = ValueUtility.translate(orb, 
                        ObjectStreamClass.lookup(vhandler.getAnyClassFromType(x)), vhandler);           
                }

                if (result != null){
                    fvds.put(x, result);
                } else {
                    throw wrapper.missingLocalValueImpl();
                }
            }
                                
            return result;
        } catch(Throwable t){
            throw wrapper.incompatibleValueImpl(t);
        }
    }

    public FullValueDescription[] metas (String[] x){
        FullValueDescription descriptions[] = new FullValueDescription[x.length];

        for (int i = 0; i < x.length; i++) {
            descriptions[i] = meta(x[i]);
        }

        return descriptions;
    }

    
    public String[] bases (String x){
        try {
            Stack<String> repIds = new Stack<String>();
            Class parent = ObjectStreamClass.lookup(
                vhandler.getClassFromType(x)).forClass().getSuperclass();

            while (!parent.equals(java.lang.Object.class)) {
                repIds.push(vhandler.createForAnyType(parent));
                parent = parent.getSuperclass();
            }

            String result[] = new String[repIds.size()];
            for (int i = result.length - 1; i >= 0; i++) {
                result[i] = repIds.pop();
            }

            return result;
        } catch (Throwable t) {
            throw wrapper.missingLocalValueImpl( t );
        }
    }
}
