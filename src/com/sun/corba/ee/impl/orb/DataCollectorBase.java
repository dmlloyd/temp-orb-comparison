


package com.sun.corba.ee.impl.orb ;

import com.sun.corba.ee.org.omg.CORBA.GetPropertyAction ;

import java.security.PrivilegedAction ;

import java.applet.Applet ;

import java.util.Properties ;
import java.util.Set ;
import java.util.HashSet ;
import java.util.Enumeration ;
import java.util.Iterator ;
import java.util.StringTokenizer ;

import java.net.URL ;

import java.security.AccessController ;

import java.io.File ;
import java.io.FileInputStream ;

import com.sun.corba.ee.spi.orb.DataCollector ;
import com.sun.corba.ee.spi.orb.PropertyParser ;

import com.sun.corba.ee.spi.misc.ORBConstants ;

public abstract class DataCollectorBase implements DataCollector {
    private Set<String> propertyNames ;
    private Set<String> propertyPrefixes ;
    private Set<String> URLPropertyNames ;
    protected String localHostName ;
    protected String configurationHostName ;
    private boolean setParserCalled ;
    private Properties originalProps ;
    private Properties resultProps ;

    public DataCollectorBase( Properties props, String localHostName,
        String configurationHostName )
    {
        URLPropertyNames = new HashSet<String>() ;
        URLPropertyNames.add( ORBConstants.INITIAL_SERVICES_PROPERTY ) ;

        propertyNames = new HashSet<String>() ;

        
        
        
        propertyNames.add( ORBConstants.ORB_INIT_REF_PROPERTY ) ;

        propertyPrefixes = new HashSet<String>() ;

        this.originalProps = props ;
        this.localHostName = localHostName ;
        this.configurationHostName = configurationHostName ;
        setParserCalled = false ;
        resultProps = new Properties() ;
    }





    public boolean initialHostIsLocal()
    {
        checkSetParserCalled() ;
        return localHostName.equals( resultProps.getProperty( 
            ORBConstants.INITIAL_HOST_PROPERTY ) ) ;
    }

    public void setParser( PropertyParser parser )
    {
        Iterator<ParserAction> iter = parser.iterator() ;
        while (iter.hasNext()) {
            ParserAction pa = iter.next() ;
            if (pa.isPrefix()) {
                propertyPrefixes.add(pa.getPropertyName());
            } else {
                propertyNames.add(pa.getPropertyName());
            }
        }

        collect() ;
        setParserCalled = true ;
    }

    public Properties getProperties()
    {
        checkSetParserCalled() ;
        return resultProps ;
    }






    public abstract boolean isApplet() ;





    protected abstract void collect() ;





    protected void checkPropertyDefaults()
    {
        String host =
            resultProps.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ;

        if ((host == null) || (host.equals(""))) {
            setProperty(ORBConstants.INITIAL_HOST_PROPERTY,
                configurationHostName);
        }
    }

    protected void findPropertiesFromArgs( String[] params )
    {
        if (params == null) {
            return;
        }

        
        

        String name ;
        String value ;

        for ( int i=0; i<params.length; i++ ) {
            value = null ;
            name = null ;

            if ( params[i] != null && params[i].startsWith("-ORB") ) {
                String argName = params[i].substring( 1 ) ;
                name = findMatchingPropertyName( propertyNames, argName ) ;

                if (name != null) {
                    if (i + 1 < params.length && params[i + 1] != null) {
                        value = params[++i];
                    }
                }
            }

            if (value != null) {
                setProperty( name, value ) ;
            }
        }
    }

    protected void findPropertiesFromApplet( final Applet app )
    {
        
        
        if (app == null) {
            return;
        }

        PropertyCallback callback = new PropertyCallback() {
            public String get(String name) {
                return app.getParameter(name);
            }
        } ;

        findPropertiesByName( propertyNames.iterator(), callback ) ;
    
        
        
        
        
        
        
        PropertyCallback URLCallback = new PropertyCallback() {
            public String get( String name ) {
                String value = resultProps.getProperty(name);
                if (value == null) {
                    return null;
                }

                try {
                    URL url = new URL( app.getDocumentBase(), value ) ;
                    return url.toExternalForm() ;
                } catch (java.net.MalformedURLException exc) {
                    
                    
                    return value ;
                }
            }
        } ;

        findPropertiesByName( URLPropertyNames.iterator(), 
            URLCallback ) ;
    }

    private void doProperties( final Properties props ) 
    {
        PropertyCallback callback =  new PropertyCallback() {
            public String get(String name) {
                return props.getProperty(name);
            }
        } ;
        
        findPropertiesByName( propertyNames.iterator(), callback ) ;

        findPropertiesByPrefix( propertyPrefixes, 
            makeIterator( props.propertyNames()), callback );
    }

    protected void findPropertiesFromFile()
    {
        final Properties fileProps = getFileProperties() ;
        if (fileProps==null) {
            return;
        }

        doProperties( fileProps ) ;
    }

    protected void findPropertiesFromProperties()
    {
        if (originalProps == null) {
            return;
        }

        doProperties( originalProps ) ;
    }

    
    
    
    
    
    
    
    protected void findPropertiesFromSystem()
    {
        Set<String> normalNames = getCORBAPrefixes( propertyNames ) ;
        Set<String> prefixNames = getCORBAPrefixes( propertyPrefixes ) ;

        PropertyCallback callback = new PropertyCallback() {
            public String get(String name) {
                return getSystemProperty(name);
            }
        } ;

        findPropertiesByName( normalNames.iterator(), callback ) ;

        findPropertiesByPrefix( prefixNames,
            getSystemPropertyNames(), callback ) ;
    }





    
    
    
    private void setProperty( String name, String value )
    {
        if( name.equals( ORBConstants.ORB_INIT_REF_PROPERTY ) ) {
            
            StringTokenizer st = new StringTokenizer( value, "=" ) ;
            if (st.countTokens() != 2) {
                throw new IllegalArgumentException();
            }

            String refName = st.nextToken() ;
            String refValue = st.nextToken() ;

            resultProps.setProperty( name + "." + refName, refValue ) ;
        } else {
            resultProps.setProperty( name, value ) ;
        }
    }

    private void checkSetParserCalled()
    {
        if (!setParserCalled) {
            throw new IllegalStateException("setParser not called.");
        }
    }

    
    
    
    private void findPropertiesByPrefix( Set<String> prefixes,
        Iterator<String> propertyNames, PropertyCallback getProperty )
    {
        while (propertyNames.hasNext()) {
            String name = propertyNames.next() ;
            Iterator<String> iter = prefixes.iterator() ;
            while (iter.hasNext()) {
                String prefix = iter.next() ;
                if (name.startsWith( prefix )) {
                    String value = getProperty.get( name ) ;

                    
                    
                    setProperty( name, value ) ;
                }
            }
        }
    }

    
    
    
    private void findPropertiesByName( Iterator<String> names,
        PropertyCallback getProperty )
    {
        while (names.hasNext()) {
            String name = names.next() ;
            String value = getProperty.get( name ) ;
            if (value != null) {
                setProperty(name, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static String getSystemProperty(final String name)
    {
        return (String)AccessController.doPrivileged(
            new GetPropertyAction(name));
    }

    
    
    private String findMatchingPropertyName( Set<String> names,
        String suffix ) 
    {
        Iterator<String> iter = names.iterator() ;
        while (iter.hasNext()) {
            String name = iter.next() ;
            if (name.endsWith( suffix )) {
                return name;
            }
        }

        return null ;
    }

    private static Iterator<String> makeIterator(
        final Enumeration<?> enumeration )
    {
        return new Iterator<String>() {
            public boolean hasNext() { return enumeration.hasMoreElements() ; }
            public String next() { return (String)enumeration.nextElement() ; }
            public void remove() { throw new UnsupportedOperationException() ; }
        } ;
    }

    private static Iterator<String> getSystemPropertyNames()
    {
        
        
        @SuppressWarnings("unchecked")
        Enumeration<String> enumeration =
            (Enumeration<String>)AccessController.doPrivileged(
                new PrivilegedAction<Enumeration<?>>() {
                      public Enumeration<?> run() {
                          return System.getProperties().propertyNames();
                      }
                }
            );

        return makeIterator( enumeration ) ;
    }

    private void getPropertiesFromFile( Properties props, String fileName )
    {
        try {
            File file = new File( fileName ) ;
            if (!file.exists()) {
                return;
            }

            FileInputStream in = new FileInputStream( file ) ;
            
            try {
                props.load( in ) ;
            } finally {
                in.close() ;
            }
        } catch (Exception exc) {
            
                
                    
        }
    }

    private Properties getFileProperties()
    {
        Properties defaults = new Properties() ;

        String javaHome = getSystemProperty( "java.home" ) ;
        String fileName = javaHome + File.separator + "lib" + File.separator +
            "orb.properties" ;

        getPropertiesFromFile( defaults, fileName ) ;

        Properties results = new Properties( defaults ) ;

        String userHome = getSystemProperty( "user.home" ) ;
        fileName = userHome + File.separator + "orb.properties" ;

        getPropertiesFromFile( results, fileName ) ;
        return results ;
    }

    private boolean hasCORBAPrefix( String prefix )
    {
        return prefix.startsWith( ORBConstants.CORBA_PREFIX ) ||
            prefix.startsWith( ORBConstants.SUN_PREFIX ) ;
    }

    
    
    private Set<String> getCORBAPrefixes( final Set<String> prefixes )
    {
        Set<String> result = new HashSet<String>() ;
        Iterator<String> iter = prefixes.iterator() ;
        while (iter.hasNext()) {
            String element = iter.next() ;
            if (hasCORBAPrefix( element )) {
                result.add(element);
            }
        }

        return result ;
    }
}


abstract class PropertyCallback
{
    abstract public String get(String name);
}
