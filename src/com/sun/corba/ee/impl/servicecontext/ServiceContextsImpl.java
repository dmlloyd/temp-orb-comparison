


package com.sun.corba.ee.impl.servicecontext;








@TraceServiceContext
public class ServiceContextsImpl implements ServiceContexts 
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private static final AtomicInteger creationCount = new AtomicInteger(0) ;

    private final ORB orb ;

    
    private final Map<Integer,Object> scMap;

    private CodeBase codeBase;
    private GIOPVersion giopVersion;

    private String getValidSCIds() {
        StringBuilder sb = new StringBuilder() ;
        sb.append( "(" ) ;
        boolean first = true ;
        for (int id : scMap.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }

            sb.append( id ) ;
        }
        sb.append( ")" ) ;
        return sb.toString() ;
    }

    @InfoMethod
    private void numberValid( int num ) { }

    @InfoMethod
    private void readingServiceContextId( int id ) { }

    @InfoMethod
    private void serviceContextLength( int len ) { }

    
    @TraceServiceContext
    private void createMapFromInputStream(InputStream is) {
        int numValid = is.read_long() ;
        numberValid( numValid ) ;

        for (int ctr = 0; ctr < numValid; ctr++) {
            int scId = is.read_long();
            readingServiceContextId(scId);

            byte[] data = OctetSeqHelper.read(is);
            serviceContextLength(data.length);

            scMap.put(scId, data);
        }
    }

    public ServiceContextsImpl( ORB orb ) {
        this.orb = orb ;

        scMap = new HashMap<Integer,Object>();

        
        
        
        giopVersion = orb.getORBData().getGIOPVersion();
        codeBase = null ;
    }

    
    public ServiceContextsImpl(InputStream s) {
        this( (ORB)(s.orb()) ) ;

        
        
        
        
        codeBase = ((CDRInputObject)s).getCodeBase();


        createMapFromInputStream(s);

        
        giopVersion = ((CDRInputObject)s).getGIOPVersion();
    }

    @InfoMethod
    private void couldNotFindServiceContextFactory( int scid ) { }

    @InfoMethod
    private void foundServiceContextFactory( int scid ) { }

    
    @TraceServiceContext
    private ServiceContext unmarshal(int scId, byte[] data) {
        ServiceContextFactoryRegistry scr = 
            orb.getServiceContextFactoryRegistry();

        ServiceContext.Factory factory = scr.find(scId);
        ServiceContext sc = null;

        if (factory == null) {
            couldNotFindServiceContextFactory(scId);
            sc = ServiceContextDefaults.makeUnknownServiceContext(scId, data);
        } else {
            foundServiceContextFactory(scId);

            
            
            
            
            
            
            
            
            
            
            
            
            EncapsInputStream eis = EncapsInputStreamFactory.newEncapsInputStream(orb, data, data.length, 
                giopVersion, codeBase);

            try {
                eis.consumeEndian();
                
                
                
                
                
                sc =factory.create(eis, giopVersion);
            } finally {
                try {
                    eis.close();
                } catch (java.io.IOException e) {
                    wrapper.ioexceptionDuringStreamClose(e);
                }
            }

            if (sc == null) {
                throw wrapper.svcctxUnmarshalError();
            }
        }

        return sc;
    }

    
    @TraceServiceContext
    public void write(OutputStream os, GIOPVersion gv) {
        int numsc = scMap.size();
        os.write_long( numsc ) ;

        writeServiceContextsInOrder(os, gv);
    }

    
    @TraceServiceContext
    private void writeServiceContextsInOrder(OutputStream os, GIOPVersion gv) {
        int ueid = UEInfoServiceContext.SERVICE_CONTEXT_ID ;

        for (int i : scMap.keySet() ) {
            if (i != ueid) {
                writeMapEntry(os, i, scMap.get(i), gv);
            }
        }

        
        
        Object uesc = scMap.get(ueid) ;
        if (uesc != null) {
            writeMapEntry(os, ueid, uesc, gv);
        }
    }

    @InfoMethod
    private void writingServiceContextBytesFor( int id ) { }

    @InfoMethod
    private void writingServiceContext( ServiceContext sc ) { }

    
    @TraceServiceContext
    private void writeMapEntry(OutputStream os, int id, Object scObj, 
        GIOPVersion gv) {
        if (scObj instanceof byte[]) {
            
            
            
            byte[] sc = (byte[])scObj ;

            writingServiceContextBytesFor(id);
            OctetSeqHelper.write(os, sc);
        } else if (scObj instanceof ServiceContext) {
            
            
            ServiceContext sc = (ServiceContext)scObj;

            writingServiceContext(sc);
            sc.write(os, gv);
        } else {
            wrapper.errorInServiceContextMap() ;
        }
    }

    @TraceServiceContext
    public void put( ServiceContext sc ) 
    {
        scMap.put(sc.getId(), sc);
    }

    @TraceServiceContext
    public void delete( int scId ) 
    {
        scMap.remove(scId);
    }

    @InfoMethod
    private void serviceContextIdFound( int id ) { }

    @InfoMethod
    private void serviceContextIdNotFound( int id ) { }

    @InfoMethod
    private void unmarshallingServiceContext( int id ) {  }

    @TraceServiceContext
    public ServiceContext get(int id) {
        Object result = scMap.get(id);
        if (result == null) {
            serviceContextIdNotFound(id);
            return null ;
        }

        serviceContextIdFound(id);
        
        
        if (result instanceof byte[]) {
            unmarshallingServiceContext(id) ;

            ServiceContext sc = unmarshal(id, (byte[])result);

            scMap.put(id, sc);

            return sc;
        } else {
            return (ServiceContext)result;
        }
    }

    private ServiceContextsImpl(  ServiceContextsImpl scimpl ) {
        this( scimpl.orb ) ;

        this.codeBase = scimpl.codeBase ;
        this.giopVersion = scimpl.giopVersion ;
        for (Map.Entry<Integer,Object> entry : scimpl.scMap.entrySet() ) {
            this.scMap.put( entry.getKey(), entry.getValue() ) ;
        }
    }

    
    @TraceServiceContext
    public ServiceContexts copy() {
        ServiceContexts result = new ServiceContextsImpl( this ) ;
        return result;
    }
}
