


package com.sun.tools.corba.se.idl;



import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class ExceptionEntry extends StructEntry
{
  protected ExceptionEntry ()
  {
    super ();
  } 

  protected ExceptionEntry (ExceptionEntry that)
  {
    super (that);
  } 

  protected ExceptionEntry (SymtabEntry that, IDLID clone)
  {
    super (that, clone);
   } 

  public Object clone ()
  {
    return new ExceptionEntry (this);
  } 

  
  public void generate (Hashtable symbolTable, PrintWriter stream)
  {
    exceptionGen.generate (symbolTable, this, stream);
  } 

  
  public Generator generator ()
  {
    return exceptionGen;
  } 

  static ExceptionGen exceptionGen;
} 
