


package xxxx;




public class CorbaExtension extends StandardLogger {
    public static final CorbaExtension self = new CorbaExtension() ;

    private CorbaExtension() {}

    public static final int ORBUtilGroup = 0 ;
    public static final int ActivationGroup = 1 ;
    public static final int NamingGroup = 2 ;
    public static final int InterceptorsGroup = 3 ;
    public static final int POAGroup = 4 ;
    public static final int IORGroup = 5 ;
    public static final int UtilGroup = 6 ;

    private static final Class<?>[] SYS_EX_CLASSES = {
        UNKNOWN.class, BAD_PARAM.class, NO_MEMORY.class, 
        IMP_LIMIT.class, COMM_FAILURE.class, INV_OBJREF.class,
        NO_PERMISSION.class, INTERNAL.class, MARSHAL.class,
        INITIALIZE.class, NO_IMPLEMENT.class, BAD_TYPECODE.class,
        BAD_OPERATION.class, NO_RESOURCES.class, NO_RESPONSE.class, 
        PERSIST_STORE.class, BAD_INV_ORDER.class, TRANSIENT.class,
        FREE_MEM.class, INV_IDENT.class, INV_FLAG.class,
        INTF_REPOS.class, BAD_CONTEXT.class, OBJ_ADAPTER.class,
        DATA_CONVERSION.class, OBJECT_NOT_EXIST.class, TRANSACTION_REQUIRED.class, 
        TRANSACTION_ROLLEDBACK.class, INVALID_TRANSACTION.class, INV_POLICY.class,
        CODESET_INCOMPATIBLE.class, REBIND.class, TIMEOUT.class,
        TRANSACTION_UNAVAILABLE.class, BAD_QOS.class, INVALID_ACTIVITY.class,
        ACTIVITY_COMPLETED.class, ACTIVITY_REQUIRED.class } ;

    @SuppressWarnings("unchecked")
    private static final List<Constructor<SystemException>> SYS_EX_CONSTRUCTORS =
        new ArrayList<Constructor<SystemException>>(
            SYS_EX_CLASSES.length) ;

    static {
        Class<?>[] ptypes = { String.class, int.class,
            CompletionStatus.class } ;

        for (Class<?> cls : SYS_EX_CLASSES) {
                try {
                    @SuppressWarnings("unchecked")
                    final Constructor<SystemException> cons =
                        (Constructor<SystemException>)cls.getDeclaredConstructor(
                            ptypes);
                    SYS_EX_CONSTRUCTORS.add(cons) ;
                } catch (Exception ex) {
                    throw new RuntimeException(
                        "Cound not find constructor for " + cls, ex ) ;
                }


        }
    }

    private int findClass( Class<?> cls ) {
        for (int ctr=0; ctr<SYS_EX_CLASSES.length; ctr++ ) {
            if (cls.equals( SYS_EX_CLASSES[ctr] )) {
                return ctr ;
            }
        }

        throw new RuntimeException(
            cls + " is not a subclass of SystemException" ) ;
    }

    private ORBException getORBException( Method method) {
        final Class<?> cls = method.getDeclaringClass() ;
        final ORBException orbex = cls.getAnnotation( ORBException.class ) ;
        return orbex ;
    }

    private Log getLog( Method method ) {
        Log log = method.getAnnotation( Log.class );
        if (log == null) {
                throw new RuntimeException(
                    "No Log annotation present on " + method ) ;
        } else {
            return log ;
        }
    }

    private int getExceptionId( Method method ) {
        final Class<?> rtype = method.getReturnType() ;
        final int exceptionId = findClass( rtype ) ;
        return exceptionId ;
    }

    private int getMinorCode( ORBException orbex, Log log ) {
        return 200*orbex.group() + log.id() ;
    }

    public int getMinorCode( Method method ) {
        final ORBException orbex = getORBException( method ) ;
        final Log log = getLog( method ) ;
        final int minorCode = getMinorCode( orbex, log ) ;
        final int base = orbex.omgException() ?
            SUNVMCID.value :
            OMGVMCID.value ;
        return base + minorCode ;
    }

    public int getMinorCode( Class<?> cls, String methodName ) {
        Method method = null ;
        for (Method m : cls.getDeclaredMethods()) {
            if (methodName.equals( m.getName())) {
                method = m ;
                break ;
            }
        }

        if (method == null) {
            return -1 ;
        } else {
            return getMinorCode( method ) ;
        }
    }

    
    
    
    
    
    @Override
    public String getLogId( Method method ) {
        final ORBException orbex = getORBException( method ) ;
        final Log log = getLog( method ) ;
        final int minorCode = getMinorCode( orbex, log ) ;
        final int exceptionId = getExceptionId( method ) ;

        final int omgId = orbex.omgException() ? 0 : 1 ;

        final String result = String.format( "%03d%1d%04d",
            exceptionId, omgId, minorCode ) ;

        return result ;
    }

    @Override
    public Exception makeException( String msg, Method method ) {
        try {
            final ORBException orbex = getORBException( method ) ;
            final Log log = getLog( method ) ;
            final int minorCode = getMinorCode( orbex, log ) ;
            final int exceptionId = getExceptionId(method) ;

            final Constructor<SystemException> cons = SYS_EX_CONSTRUCTORS.get(exceptionId) ;

            final CS cs = method.getAnnotation( CS.class ) ;
            final CSValue csv = cs == null ? CSValue.NO : cs.value() ;

            final int base = orbex.omgException() ?
                SUNVMCID.value :
                OMGVMCID.value ;

            SystemException result = cons.newInstance(msg, base + minorCode,
                csv.getCompletionStatus()) ;

            return result ;
        } catch (Exception exc) {
            throw new RuntimeException( exc ) ;
        }
    }

    private static final String CLASS_NAME_SUFFIX = "SystemException" ;

    @Override
    public String getLoggerName( Class<?> cls ) {
        final ExceptionWrapper ew = cls.getAnnotation(
            ExceptionWrapper.class ) ;

        String str = ew.loggerName() ;
        if (str.length() == 0) {
            str = cls.getSimpleName() ;
            if (str.endsWith(CLASS_NAME_SUFFIX)) {
                str = str.substring( 0,
                    str.length() - CLASS_NAME_SUFFIX.length()) ;
            }

            return StandardLogger.CORBA_LOGGER_PREFIX + "." + str ;
        }

        return str ;
    }
}
