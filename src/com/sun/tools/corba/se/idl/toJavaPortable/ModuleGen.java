


package com.sun.tools.corba.se.idl.toJavaPortable;



import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.tools.corba.se.idl.ModuleEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;


public class ModuleGen implements com.sun.tools.corba.se.idl.ModuleGen
{
  
  public ModuleGen ()
  {
  } 

  
  public void generate (Hashtable symbolTable, ModuleEntry entry, PrintWriter stream)
  {
    
    String name = Util.containerFullName( entry ) ;
    Util.mkdir (name);

    
    Enumeration e = entry.contained ().elements ();
    while (e.hasMoreElements ())
    {
      SymtabEntry element = (SymtabEntry)e.nextElement ();
      if (element.emit ())
        element.generate (symbolTable, stream);
    }
  } 
} 
