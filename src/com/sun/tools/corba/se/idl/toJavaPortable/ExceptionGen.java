


package com.sun.tools.corba.se.idl.toJavaPortable;



import java.io.PrintWriter;
import java.util.Hashtable;

import com.sun.tools.corba.se.idl.ExceptionEntry;


public class ExceptionGen extends StructGen implements com.sun.tools.corba.se.idl.ExceptionGen
{
  
  public ExceptionGen ()
  {
    super (true);
  } 

  
  public void generate (Hashtable symbolTable, ExceptionEntry entry, PrintWriter stream)
  {
    super.generate (symbolTable, entry, stream);
  } 
} 
