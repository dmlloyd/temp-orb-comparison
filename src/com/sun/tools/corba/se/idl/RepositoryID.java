


package com.sun.tools.corba.se.idl;




public class RepositoryID
{
  public RepositoryID ()
  {
    _id = "";
  } 

  public RepositoryID (String id)
  {
    _id = id;
  } 

  public String ID ()
  {
    return _id;
  } 

  public Object clone ()
  {
    return new RepositoryID (_id);
  } 

  public String toString ()
  {
    return ID ();
  } 

  
  public static boolean hasValidForm (String string)
  {
    return string != null && string.indexOf (':') > 0;
  } 

  private String _id;
} 
