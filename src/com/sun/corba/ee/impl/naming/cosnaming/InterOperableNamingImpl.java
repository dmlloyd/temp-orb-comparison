


package com.sun.corba.ee.impl.naming.cosnaming;





public class InterOperableNamingImpl
{
   
    public String convertToString( org.omg.CosNaming.NameComponent[] 
                                   theNameComponents )
    {
        boolean first = true ;
        final StringBuffer sb = new StringBuffer() ;
        for (NameComponent nc : theNameComponents) {
            final String temp = convertNameComponentToString( nc ) ;
            sb.append( temp ) ;
            if (first) {
                first = false ;
            } else {
                sb.append( '/' ) ;
            }
        }

        return sb.toString() ;
    }

    private boolean isEmpty( String str ) {
        return str==null || str.length() == 0 ;
    }

    private boolean contains( String str, char ch ) {
        return str.indexOf( ch ) != -1 ;
    }

   
    private String convertNameComponentToString( 
        org.omg.CosNaming.NameComponent theNameComponent ) 
    {
        final String id = addEscape( theNameComponent.id ) ;
        final String kind = addEscape( theNameComponent.kind ) ;
        final StringBuffer sb = new StringBuffer() ;

        if (!isEmpty(id)) {
            sb.append( id ) ;
        }

        sb.append( '.' ) ;

        if (!isEmpty(kind)) {
            sb.append(kind) ;
        }

        return sb.toString() ;
    }


   
   private String addEscape( String value )
   {
        if ((value != null) && (contains( value, '.' ) ||
            contains( value, '/' ))) {
            final StringBuffer theNewValue = new StringBuffer() ;
            for( int i = 0; i < value.length( ); i++ ) {
                char c = value.charAt( i );
                if ((c == '.') || (c == '/')) {
                    theNewValue.append( '\\' );
                }

                
                theNewValue.append( c );
            }

            return theNewValue.toString() ;
        } else {
            return value;
        }
   } 

   
   public org.omg.CosNaming.NameComponent[] convertToNameComponent( 
       String theStringifiedName )
       throws org.omg.CosNaming.NamingContextPackage.InvalidName
   {
        String[] components = breakStringToNameComponents( theStringifiedName );
        if (( components == null ) || (components.length == 0)) {
            return null;
        } 

        NameComponent[] theNameComponents = new NameComponent[components.length];
        for( int i = 0; i < components.length; i++ ) {
            theNameComponents[i] = createNameComponentFromString( 
                components[i] );
        }

        return theNameComponents;
   }

   
   private String[] breakStringToNameComponents( final String sname ) {
       int[] theIndices = new int[100];
       int theIndicesIndex = 0;

       for(int index = 0; index <= sname.length(); ) {
           theIndices[theIndicesIndex] = sname.indexOf( '/',
                index );
           if( theIndices[theIndicesIndex] == -1 ) {
               
               
               index = sname.length()+1;
           }
           else {
               
               
               
               
               if( (theIndices[theIndicesIndex] > 0 )
               && (sname.charAt(
                   theIndices[theIndicesIndex]-1) == '\\') )
               {
                  index = theIndices[theIndicesIndex] + 1;
                  theIndices[theIndicesIndex] = -1;
               }
               else {
                  index = theIndices[theIndicesIndex] + 1;
                  theIndicesIndex++;
               }
           }
        }
        if( theIndicesIndex == 0 ) {
            String[] tempString = new String[1];
            tempString[0] = sname;
            return tempString;
        }
        if( theIndicesIndex != 0 ) {
            theIndicesIndex++;
        }
        return StringComponentsFromIndices( theIndices, theIndicesIndex, 
                                            sname );
    } 

    
   private String[] StringComponentsFromIndices( int[] theIndices, 
          int indicesCount, String theStringifiedName )
   {
       String[] theStringComponents = new String[indicesCount];
       int firstIndex = 0;
       int lastIndex = theIndices[0];
       for( int i = 0; i < indicesCount; i++ ) {
           theStringComponents[i] = theStringifiedName.substring( firstIndex, 
             lastIndex );
           if( ( theIndices[i] < theStringifiedName.length() - 1 ) 
             &&( theIndices[i] != -1 ) ) 
           {
               firstIndex = theIndices[i]+1;
           }
           else {
               firstIndex = 0;
               i = indicesCount;
           }
           if( (i+1 < theIndices.length) 
            && (theIndices[i+1] < (theStringifiedName.length() - 1)) 
            && (theIndices[i+1] != -1) )
           {
               lastIndex = theIndices[i+1];
           }
           else {
               i = indicesCount;
           }
           
           if( firstIndex != 0 && i == indicesCount ) {
               theStringComponents[indicesCount-1] = 
               theStringifiedName.substring( firstIndex );
           }
       }
       return theStringComponents;
   }

    
   private NameComponent createNameComponentFromString( 
        String theStringifiedNameComponent )
        throws org.omg.CosNaming.NamingContextPackage.InvalidName

   {
        String id = null;
        String kind = null;
        if( ( theStringifiedNameComponent == null ) 
         || ( theStringifiedNameComponent.length( ) == 0) 
         || ( theStringifiedNameComponent.endsWith(".") ) )
        {
            
            
            throw new org.omg.CosNaming.NamingContextPackage.InvalidName( );
        }

        int index = theStringifiedNameComponent.indexOf( '.', 0 );
        
        if( index == -1 ) {
            id = theStringifiedNameComponent;
        }
        
        else if( index == 0 ) {
            
            
            if( theStringifiedNameComponent.length( ) != 1 ) {
                kind = theStringifiedNameComponent.substring(1);
            }
        }
        else
        {
            if( theStringifiedNameComponent.charAt(index-1) != '\\' ) {
                id = theStringifiedNameComponent.substring( 0, index);
                kind = theStringifiedNameComponent.substring( index + 1 );
            }
            else {
                boolean kindfound = false;
                while( (index < theStringifiedNameComponent.length() )
                     &&( kindfound != true ) )
                {
                    index = theStringifiedNameComponent.indexOf( '.',index + 1);
                    if( index > 0 ) {
                        if( theStringifiedNameComponent.charAt( 
                                index - 1 ) != '\\' )
                        {
                            kindfound = true;
                        }
                    }
                    else
                    {
                        
                        index = theStringifiedNameComponent.length();
                    }
                }
                if( kindfound == true ) {
                    id = theStringifiedNameComponent.substring( 0, index);
                    kind = theStringifiedNameComponent.substring(index + 1 ); 
                }
                else {
                    id = theStringifiedNameComponent;
                }
            }
        }
        id = cleanEscapeCharacter( id );
        kind = cleanEscapeCharacter( kind );
        if( id == null ) {
                id = "";
        }
        if( kind == null ) {
                kind = "";
        }
        return new NameComponent( id, kind );
   }

  
   
   private String cleanEscapeCharacter( String theString )
   {
        if( ( theString == null ) || (theString.length() == 0 ) ) {
                return theString;
        }
        int index = theString.indexOf( '\\' );
        if( index == 0 ) {
            return theString;
        }
        else {
            StringBuffer src = new StringBuffer( theString );
            StringBuffer dest = new StringBuffer( );
            char c;
            for( int i = 0; i < theString.length( ); i++ ) {
                c = src.charAt( i );
                if( c != '\\' ) {
                    dest.append( c );
                } else {
                    if( i+1 < theString.length() ) {
                        char d = src.charAt( i + 1 );
                        
                        
                        
                        if( Character.isLetterOrDigit(d) ) {
                            dest.append( c );
                        }
                    }
                }
            }
            return new String(dest);
        }
   } 

   
    public String createURLBasedAddress( String address, String name )
        throws InvalidAddress
    {
        String theurl = null;
        if( ( address == null )
          ||( address.length() == 0 ) ) {
            throw new InvalidAddress();
        }
        else {
            theurl = "corbaname:" + address + "#" + encode( name );
        }
        return theurl;
    }

    
    private String encode( String stringToEncode ) {
        StringWriter theStringAfterEscape = new StringWriter();
        int byteCount = 0;
        for( int i = 0; i < stringToEncode.length(); i++ )
        {
            char c = stringToEncode.charAt( i ) ;
            if( Character.isLetterOrDigit( c ) ) {
                theStringAfterEscape.write( c );
            }
            
            
            else if((c == ';') || (c == '/') || (c == '?')
            || (c == ':') || (c == '@') || (c == '&') || (c == '=')
            || (c == '+') || (c == '$') || (c == ';') || (c == '-')
            || (c == '_') || (c == '.') || (c == '!') || (c == '~')
            || (c == '*') || (c == ' ') || (c == '(') || (c == ')') )
            {
                theStringAfterEscape.write( c );
            }
            else {
                
                theStringAfterEscape.write( '%' );
                String hexString = Integer.toHexString( (int) c );  
                theStringAfterEscape.write( hexString ); 
            }
        }
        return theStringAfterEscape.toString();
    }

}

