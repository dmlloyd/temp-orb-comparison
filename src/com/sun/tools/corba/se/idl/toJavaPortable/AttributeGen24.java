


package com.sun.tools.corba.se.idl.toJavaPortable;





import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.AttributeEntry;


public class AttributeGen24 extends MethodGenClone24
{
  
  public AttributeGen24 ()
  {
  } 

  
  protected void abstractMethod (Hashtable symbolTable, MethodEntry m, PrintWriter stream)
  {
    AttributeEntry a = (AttributeEntry)m;

    
    super.abstractMethod (symbolTable, a, stream);

    
    if (!a.readOnly ())
    {
      setupForSetMethod ();
      super.abstractMethod (symbolTable, a, stream);
      clear ();
    }
  } 

  
  protected void interfaceMethod (Hashtable symbolTable, MethodEntry m, PrintWriter stream)
  {
    AttributeEntry a = (AttributeEntry)m;

    
    super.interfaceMethod (symbolTable, a, stream);

    
    if (!a.readOnly ())
    {
      setupForSetMethod ();
      super.interfaceMethod (symbolTable, a, stream);
      clear ();
    }
  } 

} 
