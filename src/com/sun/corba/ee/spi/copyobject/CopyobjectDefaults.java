


package com.sun.corba.ee.spi.copyobject ;



public abstract class CopyobjectDefaults
{
    private CopyobjectDefaults() { }

    
    public static ObjectCopierFactory makeORBStreamObjectCopierFactory( final ORB orb ) 
    {
        return new ObjectCopierFactory() {
            public ObjectCopier make( )
            {
                return new ORBStreamObjectCopierImpl( orb ) ;
            }
        } ;
    }

    public static ObjectCopierFactory makeJavaStreamObjectCopierFactory( final ORB orb ) 
    {
        return new ObjectCopierFactory() {
            public ObjectCopier make( )
            {
                return new JavaStreamORBObjectCopierImpl( orb ) ;
            }
        } ;
    }

    private static final ObjectCopier referenceObjectCopier = new ReferenceObjectCopierImpl() ;

    private static ObjectCopierFactory referenceObjectCopierFactory = 
        new ObjectCopierFactory() {
            public ObjectCopier make() 
            {
                return referenceObjectCopier ;
            }
        } ;

    
    public static ObjectCopierFactory getReferenceObjectCopierFactory()
    {
        return referenceObjectCopierFactory ;
    }

    
    public static ObjectCopierFactory makeFallbackObjectCopierFactory( 
        final ObjectCopierFactory f1, final ObjectCopierFactory f2 )
    {
        return new ObjectCopierFactory() {
            public ObjectCopier make() 
            {
                ObjectCopier c1 = f1.make() ;
                ObjectCopier c2 = f2.make() ;
                return new FallbackObjectCopierImpl( c1, c2 ) ;
            }
        } ;
    }

    
    public static ObjectCopierFactory makeOldReflectObjectCopierFactory( final ORB orb ) 
    {
        return new ObjectCopierFactory() {
            public ObjectCopier make()
            {
                return new OldReflectObjectCopierImpl( orb ) ;
            }
        } ;
    }

    
    public static ObjectCopierFactory makeReflectObjectCopierFactory( final ORB orb ) 
    {
        return new ObjectCopierFactory() {
            public ObjectCopier make( )
            {
                return new ReflectObjectCopierImpl( orb ) ;
            }
        } ;
    }
}
