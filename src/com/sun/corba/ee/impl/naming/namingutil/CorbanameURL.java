


package com.sun.corba.ee.impl.naming.namingutil;


public class CorbanameURL extends INSURLBase
{
    
    public CorbanameURL( String aURL ) {
        String url = aURL;
  
        
        try {
            url = Utility.cleanEscapes( url );
        } catch( Exception e ) {
            badAddress( e, aURL );
        }

        int delimiterIndex = url.indexOf( '#' );
        String corbalocString = null;
        if( delimiterIndex != -1 ) {
            corbalocString = "corbaloc:" + url.substring( 0, delimiterIndex ) ;
        } else {
            corbalocString = "corbaloc:" + url ;
        }

        try {
            
            
            INSURL insURL = 
                INSURLHandler.getINSURLHandler().parseURL( corbalocString );
            copyINSURL( insURL );
            
            
            
            
            if((delimiterIndex > -1) &&
               (delimiterIndex < (aURL.length() - 1)))
            {
                int start = delimiterIndex + 1 ;
                String result = url.substring(start) ;
                theStringifiedName = result ;
            } 
        } catch( Exception e ) {
            badAddress( e, aURL );
        }
    }

    
    private void copyINSURL( INSURL url ) {
        rirFlag = url.getRIRFlag( );
        theEndpointInfo = (java.util.ArrayList) url.getEndpointInfo( );
        theKeyString = url.getKeyString( );
        theStringifiedName = url.getStringifiedName( );
    }

    public boolean isCorbanameURL( ) {
        return true;
    }

}
