


package com.sun.tools.corba.se.idl.toJavaPortable;



import java.io.PrintWriter;

import com.sun.tools.corba.se.idl.SymtabEntry;


public interface JavaGenerator
{
  
  

  int helperType (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream);

  void helperRead (String entryName, SymtabEntry entry, PrintWriter stream);

  void helperWrite (SymtabEntry entry, PrintWriter stream);

  
  
  

  int read (int index, String indent, String name, SymtabEntry entry, PrintWriter stream);

  int write (int index, String indent, String name, SymtabEntry entry, PrintWriter stream);

  int type (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream);

} 
