


package xxxx;















public class TypedefGen implements com.sun.tools.corba.se.idl.TypedefGen, JavaGenerator
{
  
  public TypedefGen ()
  {
  } 

  
  public void generate (Hashtable symbolTable, TypedefEntry t, PrintWriter stream)
  {
    this.symbolTable = symbolTable;
    this.t           = t;

    if (t.arrayInfo ().size () > 0 || t.type () instanceof SequenceEntry)
      generateHolder ();
    generateHelper ();
  } 

  
  protected void generateHolder ()
  {
    ((Factories)Compile.compiler.factories ()).holder ().generate (symbolTable, t);
  }

  
  protected void generateHelper ()
  {
    ((Factories)Compile.compiler.factories ()).helper ().generate (symbolTable, t);
  }

  
  

  private boolean inStruct (TypedefEntry entry)
  {
    boolean inStruct = false;
    if (entry.container () instanceof StructEntry || entry.container () instanceof UnionEntry)
      inStruct = true;
    else if (entry.container () instanceof InterfaceEntry)
    {
      InterfaceEntry i = (InterfaceEntry)entry.container ();
      if (i.state () != null)
      {
        Enumeration e = i.state ().elements ();
        while (e.hasMoreElements ())
          if (((InterfaceState)e.nextElement ()).entry == entry)
          {
            inStruct = true;
            break;
          }
      }
    }
    return inStruct;
  } 

  public int helperType (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream)
  {
    TypedefEntry td = (TypedefEntry)entry;
    boolean inStruct = inStruct (td);
    if (inStruct)
      tcoffsets.setMember (entry);
    else
      tcoffsets.set (entry);

    
    index = ((JavaGenerator)td.type ().generator ()).type (index, indent, tcoffsets, name, td.type (), stream);

    if (inStruct && td.arrayInfo ().size () != 0)
      tcoffsets.bumpCurrentOffset (4); 

    
    int dimensions = td.arrayInfo ().size ();
    for (int i = 0; i < dimensions; ++i)
    {
      String size = Util.parseExpression ((Expression)td.arrayInfo ().elementAt (i));
      stream.println (indent + name + " = org.omg.CORBA.ORB.init ().create_array_tc (" + size + ", " + name + " );");
    }

    
    
    if (!inStruct)
      
      
      stream.println (indent + name + " = org.omg.CORBA.ORB.init ().create_alias_tc (" + Util.helperName (td, true) + ".id (), \"" + Util.stripLeadingUnderscores (td.name ()) + "\", " + name + ");"); 

    return index;
  } 

  public int type (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream)
  {
    
    
    
    

    return helperType( index, indent, tcoffsets, name, entry, stream);
  } 

  public void helperRead (String entryName, SymtabEntry entry, PrintWriter stream)
  {
    Util.writeInitializer ("    ", "value", "", entry, stream);
    read (0, "    ", "value", entry, stream);
    stream.println ("    return value;");
  } 

  public void helperWrite (SymtabEntry entry, PrintWriter stream)
  {
    write (0, "    ", "value", entry, stream);
  } 

  public int read (int index, String indent, String name, SymtabEntry entry, PrintWriter stream)
  {
    TypedefEntry td = (TypedefEntry)entry;
    String modifier = Util.arrayInfo (td.arrayInfo ());
    if (!modifier.equals (""))
    {
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      int closingBrackets = 0;
      String loopIndex = "";
      String baseName;
      try
      {
        baseName = (String)td.dynamicVariable (Compile.typedefInfo);
      }
      catch (NoSuchFieldException e)
      {
        baseName = td.name ();
      }
      int startArray = baseName.indexOf ('[');
      String arrayDcl = Util.sansArrayInfo (baseName.substring (startArray)) + "[]"; 
      baseName = baseName.substring (0, startArray);

      
      SymtabEntry baseEntry = (SymtabEntry)Util.symbolTable.get (baseName.replace ('.', '/'));
      if (baseEntry instanceof InterfaceEntry && ((InterfaceEntry)baseEntry).state () != null)
        
        
        baseName = Util.javaName ((InterfaceEntry)baseEntry);

      int end1stArray;
      while (!modifier.equals (""))
      {
        int rbracket = modifier.indexOf (']');
        String size = modifier.substring (1, rbracket);
        end1stArray = arrayDcl.indexOf (']');
        arrayDcl = '[' + size + arrayDcl.substring (end1stArray + 2);
        stream.println (indent + name + " = new " + baseName + arrayDcl + ';');
        loopIndex = "_o" + index++;
        stream.println (indent + "for (int " + loopIndex + " = 0;" + loopIndex + " < (" + size + "); ++" + loopIndex + ')');
        stream.println (indent + '{');
        ++closingBrackets;
        modifier = modifier.substring (rbracket + 1);
        indent = indent + "  ";
        name = name + '[' + loopIndex + ']';
      }
      end1stArray = arrayDcl.indexOf (']');
      if (td.type () instanceof SequenceEntry || td.type () instanceof PrimitiveEntry || td.type () instanceof StringEntry)
        index = ((JavaGenerator)td.type ().generator ()).read (index, indent, name, td.type (), stream);
      else if (td.type () instanceof InterfaceEntry && td.type ().fullName ().equals ("org/omg/CORBA/Object"))
        stream.println (indent + name + " = istream.read_Object ();");
      else
        stream.println (indent + name + " = " + Util.helperName (td.type (), true) + ".read (istream);"); 
      for (int i = 0; i < closingBrackets; ++i)
      {
        indent = indent.substring (2);
        stream.println (indent + '}');
      }
    }
    else
    {
      SymtabEntry tdtype = Util.typeOf (td.type ());
      if (tdtype instanceof SequenceEntry || tdtype instanceof PrimitiveEntry || tdtype instanceof StringEntry)
        index = ((JavaGenerator)tdtype.generator ()).read (index, indent, name, tdtype, stream);
      else if (tdtype instanceof InterfaceEntry && tdtype.fullName ().equals ("org/omg/CORBA/Object"))
        stream.println (indent + name + " = istream.read_Object ();");
      else
        stream.println (indent + name + " = " + Util.helperName (tdtype, true) + ".read (istream);"); 
    }
    return index;
  } 

  public int write (int index, String indent, String name, SymtabEntry entry, PrintWriter stream)
  {
    TypedefEntry td = (TypedefEntry)entry;
    String modifier = Util.arrayInfo (td.arrayInfo ());
    if (!modifier.equals (""))
    {
      int closingBrackets = 0;
      String loopIndex = "";
      while (!modifier.equals (""))
      {
        int rbracket = modifier.indexOf (']');
        String size = modifier.substring (1, rbracket);
        stream.println (indent + "if (" + name + ".length != (" + size + "))");
        stream.println (indent + "  throw new org.omg.CORBA.MARSHAL (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);");
        loopIndex = "_i" + index++;
        stream.println (indent + "for (int " + loopIndex + " = 0;" + loopIndex + " < (" + size + "); ++" + loopIndex + ')');
        stream.println (indent + '{');
        ++closingBrackets;
        modifier = modifier.substring (rbracket + 1);
        indent = indent + "  ";
        name = name + '[' + loopIndex + ']';
      }
      if (td.type () instanceof SequenceEntry || td.type () instanceof PrimitiveEntry || td.type () instanceof StringEntry)
        index = ((JavaGenerator)td.type ().generator ()).write (index, indent, name, td.type (), stream);
      else if (td.type () instanceof InterfaceEntry && td.type ().fullName ().equals ("org/omg/CORBA/Object"))
        stream.println (indent + "ostream.write_Object (" + name + ");");
      else
        stream.println (indent + Util.helperName (td.type (), true) + ".write (ostream, " + name + ");"); 
      for (int i = 0; i < closingBrackets; ++i)
      {
        indent = indent.substring (2);
        stream.println (indent + '}');
      }
    }
    else
    {
      SymtabEntry tdtype = Util.typeOf (td.type ());
      if (tdtype instanceof SequenceEntry || tdtype instanceof PrimitiveEntry || tdtype instanceof StringEntry)
        index = ((JavaGenerator)tdtype.generator ()).write (index, indent, name, tdtype, stream);
      else if (tdtype instanceof InterfaceEntry && tdtype.fullName ().equals ("org/omg/CORBA/Object"))
        stream.println (indent + "ostream.write_Object (" + name + ");");
      else
        stream.println (indent + Util.helperName (tdtype, true) + ".write (ostream, " + name + ");"); 
    }
    return index;
  } 

  
  

  protected Hashtable     symbolTable = null;
  protected TypedefEntry  t           = null;
} 
