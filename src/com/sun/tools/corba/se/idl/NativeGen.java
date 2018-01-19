

package com.sun.tools.corba.se.idl;




public interface NativeGen extends Generator
{
  void generate (Hashtable symbolTable, NativeEntry entry, PrintWriter stream);
} 
