


package com.sun.tools.corba.se.idl;




public class InvalidArgument extends Exception
{
  
  public InvalidArgument (String arg)
  {
    message = Util.getMessage ("InvalidArgument.1", arg) + "\n\n" + Util.getMessage ("usage");
  } 

  public InvalidArgument ()
  {
    message = Util.getMessage ("InvalidArgument.2") + "\n\n" + Util.getMessage ("usage");
  } 

  public String getMessage ()
  {
    return message;
  } 

  private String message = null;
} 
