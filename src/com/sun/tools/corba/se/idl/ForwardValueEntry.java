


package com.sun.tools.corba.se.idl;



import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class ForwardValueEntry extends ForwardEntry
{
  protected ForwardValueEntry ()
  {
    super ();
  } 

  protected ForwardValueEntry (ForwardValueEntry that)
  {
    super (that);
  } 

  protected ForwardValueEntry (SymtabEntry that, IDLID clone)
  {
    super (that, clone);
  } 

  public Object clone ()
  {
    return new ForwardValueEntry (this);
  } 

  
  public void generate (Hashtable symbolTable, PrintWriter stream)
  {
    forwardValueGen.generate (symbolTable, this, stream);
  } 

  
  public Generator generator ()
  {
     return forwardValueGen;
  } 

  static ForwardValueGen forwardValueGen;
} 
