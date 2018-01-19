


package xxxx;







public class ExceptionHandlerImpl implements ExceptionHandler 
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self;

    private ExceptionRW[] rws ;






    public interface ExceptionRW
    {
        Class getExceptionClass() ;

        String getId() ;

        void write( OutputStream os, Exception ex ) ;

        Exception read( InputStream is ) ;
    }

    public abstract class ExceptionRWBase implements ExceptionRW
    {
        private Class cls ;
        private String id ;

        public ExceptionRWBase( Class cls ) 
        {
            this.cls = cls ;
        }

        public Class getExceptionClass() 
        {
            return cls ;
        }

        public String getId()
        {
            return id ;
        }

        void setId( String id )
        {
            this.id = id ;
        }
    }

    public class ExceptionRWIDLImpl extends ExceptionRWBase
    {
        private Method readMethod ;
        private Method writeMethod ;

        public ExceptionRWIDLImpl( Class cls ) 
        {
            super( cls ) ;

            String helperName = cls.getName() + "Helper" ;
            ClassLoader loader = cls.getClassLoader() ;
            Class helperClass ;

            try {
                helperClass = Class.forName( helperName, true, loader ) ;
                Method idMethod = helperClass.getDeclaredMethod( "id" ) ;
                setId( (String)idMethod.invoke( null ) ) ;
            } catch (Exception ex) {
                throw wrapper.badHelperIdMethod( ex, helperName ) ;
            }

            try {
                writeMethod = helperClass.getDeclaredMethod( "write", 
                    org.omg.CORBA.portable.OutputStream.class, cls ) ;
            } catch (Exception ex) {
                throw wrapper.badHelperWriteMethod( ex, helperName ) ;
            }

            try {
                readMethod = helperClass.getDeclaredMethod( "read", 
                    org.omg.CORBA.portable.InputStream.class ) ;
            } catch (Exception ex) {
                throw wrapper.badHelperReadMethod( ex, helperName ) ;
            }
        }

        public void write( OutputStream os, Exception ex ) 
        {
            try {
                writeMethod.invoke( null, os, ex ) ;
            } catch (Exception exc) {
                throw wrapper.badHelperWriteMethod( exc, 
                    writeMethod.getDeclaringClass().getName() ) ;
            }
        }

        public Exception read( InputStream is ) 
        {
            try {
                return (Exception)readMethod.invoke( null, is ) ;
            } catch (Exception ex) {
                throw wrapper.badHelperReadMethod( ex, 
                    readMethod.getDeclaringClass().getName() ) ;
            }
        }
    }

    public class ExceptionRWRMIImpl extends ExceptionRWBase
    {
        public ExceptionRWRMIImpl( Class cls ) 
        {
            super( cls ) ;
            setId( IDLNameTranslatorImpl.getExceptionId( cls ) ) ;
        }

        public void write( OutputStream os, Exception ex ) 
        {
            os.write_string( getId() ) ;
            os.write_value( ex, getExceptionClass() ) ;
        }

        public Exception read( InputStream is ) 
        {
            is.read_string() ; 
            return (Exception)is.read_value( getExceptionClass() ) ;
        }
    }



    public ExceptionHandlerImpl( Class[] exceptions )
    {
        int count = 0 ;
        for (int ctr=0; ctr<exceptions.length; ctr++) {
            Class cls = exceptions[ctr] ;
            if (!ClassInfoCache.get(cls).isARemoteException(cls))
                count++ ;
        }

        rws = new ExceptionRW[count] ;

        int index = 0 ;
        for (int ctr=0; ctr<exceptions.length; ctr++) {
            Class cls = exceptions[ctr] ;
            ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cls ) ;
            if (!cinfo.isARemoteException(cls)) {
                ExceptionRW erw = null ;
                if (cinfo.isAUserException(cls))
                    erw = new ExceptionRWIDLImpl( cls ) ;
                else
                    erw = new ExceptionRWRMIImpl( cls ) ;

                


                rws[index++] = erw ;
            }
        }
    }

    private int findDeclaredException( Class cls ) 
    {
        for (int ctr = 0; ctr < rws.length; ctr++) {
            Class next = rws[ctr].getExceptionClass() ;
            if (next.isAssignableFrom(cls))
                return ctr ;
        }

        return -1 ;
    }

    private int findDeclaredException( String repositoryId )
    {
        for (int ctr=0; ctr<rws.length; ctr++) {
            
            
            if (rws[ctr]==null)
                return -1 ;

            String rid = rws[ctr].getId() ;
            if (repositoryId.equals( rid )) 
                return ctr ;
        }

        return -1 ;
    }

    public boolean isDeclaredException( Class cls ) 
    {
        return findDeclaredException( cls ) >= 0 ;
    }

    public void writeException( OutputStream os, Exception ex ) 
    {
        int index = findDeclaredException( ex.getClass() ) ;
        if (index < 0)
            throw wrapper.writeUndeclaredException( ex,
                ex.getClass().getName() ) ;

        rws[index].write( os, ex ) ;
    }

    public Exception readException( ApplicationException ae ) 
    {
        
        
        
        
        InputStream is = (InputStream)ae.getInputStream() ;
        String excName = ae.getId() ;
        int index = findDeclaredException( excName ) ;
        if (index < 0) {
            excName = is.read_string() ;
            Exception res = new UnexpectedException( excName ) ;
            res.initCause( ae ) ;
            return res ;
        }

        return rws[index].read( is ) ;
    }

    
    public ExceptionRW getRMIExceptionRW( Class cls )
    {
        return new ExceptionRWRMIImpl( cls ) ;
    }
}

