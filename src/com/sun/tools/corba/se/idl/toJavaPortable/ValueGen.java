


package com.sun.tools.corba.se.idl.toJavaPortable;

















public class ValueGen implements com.sun.tools.corba.se.idl.ValueGen, JavaGenerator
{
  
  public ValueGen ()
  {
  } 

  
  public void generate (Hashtable symbolTable, ValueEntry v, PrintWriter str)
  {
    this.symbolTable = symbolTable;
    this.v = v;
    init ();

    openStream ();
    if (stream == null)
      return;
    generateTie ();
    generateHelper ();
    generateHolder ();
    writeHeading ();
    writeBody ();
    writeClosing ();
    closeStream ();
  } 

  
  protected void init ()
  {
    emit = ((Arguments)Compile.compiler.arguments).emit;
    factories = (Factories)Compile.compiler.factories ();
  } 

  
  protected void openStream ()
  {
    stream = Util.stream (v, ".java");
  } 

  
  protected void generateTie ()
  {
    boolean tie = ((Arguments)Compile.compiler.arguments).TIEServer;
    if (v.supports ().size () > 0  && tie)
    {
      Factories factories = (Factories)Compile.compiler.factories ();
      factories.skeleton ().generate (symbolTable, v);
    }
  } 

  
  protected void generateHelper ()
  {
    ((Factories)Compile.compiler.factories ()).helper ().generate (symbolTable, v);
  } 

  
  protected void generateHolder ()
  {
    ((Factories)Compile.compiler.factories ()).holder ().generate (symbolTable, v);
  } 

  
  protected void writeHeading ()
  {
    Util.writePackage (stream, v);
    Util.writeProlog (stream, ((GenFileStream)stream).name ());

    if (v.comment () != null)
      v.comment ().generate ("", stream);

    if (v.isAbstract ())
    {
      writeAbstract ();
      return;
    }
    else
      stream.print ("public class " + v.name ());

    
    SymtabEntry parent = (SymtabEntry) v.derivedFrom ().elementAt (0);

    
    String parentName = Util.javaName (parent);
    boolean impl = false;

    if (parentName.equals ("java.io.Serializable"))
    {

      stream.print (" implements org.omg.CORBA.portable.ValueBase"); 
      impl = true;
    }
    else if ( !((ValueEntry)parent).isAbstract ())
      stream.print (" extends " + parentName);

    
    for (int i = 0; i < v.derivedFrom ().size (); i++) {
      parent = (SymtabEntry) v.derivedFrom ().elementAt (i);
      if ( ((ValueEntry)parent).isAbstract ())
      {
        if (!impl)
        {
          stream.print (" implements ");
          impl = true;
        }
        else
          stream.print (", ");
        stream.print (Util.javaName (parent));
      }
    }


    if (((ValueEntry)v).supports ().size () > 0) {
      if (!impl)
      {
        stream.print (" implements ");
        impl = true;
      }
      else
        stream.print (", ");

      InterfaceEntry s =(InterfaceEntry)((ValueEntry)v).supports().elementAt(0);
      
      if (s.isAbstract ())
         stream.print (Util.javaName (s));
      else
          stream.print (Util.javaName (s) + "Operations");
      }


    if ( ((ValueEntry)v).isCustom ()) {
      if (!impl)
      {
        stream.print (" implements ");
        impl = true;
      }
      else
        stream.print (", ");

      stream.print ("org.omg.CORBA.CustomMarshal ");
      }

    stream.println ();
    stream.println ("{");
  } 

  
  protected void writeBody ()
  {
    writeMembers ();
    writeInitializers ();
    writeConstructor (); 
    writeTruncatable (); 
    writeMethods ();
  } 

  
  protected void writeClosing ()
  {
   if (v.isAbstract ())
     stream.println ("} // interface " + v.name ());
   else
     stream.println ("} // class " + v.name ());
  } 

  
  protected void closeStream ()
  {
    stream.close ();
  } 

  
  protected void writeConstructor ()
  {
   
   if (!v.isAbstract () && !explicitDefaultInit) { 
        stream.println ("  protected " + v.name () + " () {}");
        stream.println ();
    }
  } 

  
  protected void writeTruncatable () 
  {
   
   if (!v.isAbstract ()) {
        stream.println ("  public String[] _truncatable_ids() {");
        stream.println ("      return " + Util.helperName(v, true) + ".get_instance().get_truncatable_base_ids();"); 
        stream.println ("  }");
        stream.println ();
    }
  } 

  
  protected void writeMembers ()
  {
    
    if (v.state () == null)
      return;

    for (int i = 0; i < v.state ().size (); i ++)
    {
      InterfaceState member = (InterfaceState) v.state ().elementAt (i);
      SymtabEntry entry = (SymtabEntry) member.entry;
      Util.fillInfo (entry);

      if (entry.comment () != null)
        entry.comment ().generate (" ", stream);

      String modifier = "  ";
      if (member.modifier == InterfaceState.Public)
        modifier = "  public ";
      Util.writeInitializer (modifier, entry.name (), "", entry, stream);
    }
  } 

  
  protected void writeInitializers ()
  {
    Vector init = v.initializers ();
    if (init != null)
    {
      stream.println ();
      for (int i = 0; i < init.size (); i++)
      {
        MethodEntry element = (MethodEntry) init.elementAt (i);
        element.valueMethod (true);
        ((MethodGen) element.generator ()). interfaceMethod (symbolTable, element, stream);
        if (element.parameters ().isEmpty ()) 
          explicitDefaultInit = true;
      }
    }
  } 

  
  protected void writeMethods ()
  {
    
    
    
    
    
    
    Enumeration e = v.contained ().elements ();
    while (e.hasMoreElements ())
    {
      SymtabEntry contained = (SymtabEntry)e.nextElement ();
      if (contained instanceof MethodEntry)
      {
        MethodEntry element = (MethodEntry)contained;
        ((MethodGen)element.generator ()).interfaceMethod (symbolTable, element, stream);
      }
      else
      {
        
        if (contained instanceof TypedefEntry)
          contained.type ().generate (symbolTable, stream);

        
        
        contained.generate (symbolTable, stream);
      }
    }

    
    
    
    if (v.isAbstract ())
        return;

    
    

    
    if (v.supports ().size () > 0)
    {
      InterfaceEntry intf = (InterfaceEntry) v.supports ().elementAt (0);
      Enumeration el = intf.allMethods ().elements ();
      while (el.hasMoreElements ())
      {
        MethodEntry m = (MethodEntry) el.nextElement ();
        
        
        
        MethodEntry mClone = (MethodEntry)m.clone ();
        mClone.container (v);
        ((MethodGen)mClone.generator ()).interfaceMethod (symbolTable, mClone, stream);
      }
    }

    
    
    for (int i = 0; i < v.derivedFrom ().size (); i++) {
      ValueEntry parentValue = (ValueEntry) v.derivedFrom ().elementAt (i);
      if (parentValue.isAbstract ())
      {
        Enumeration el = parentValue.allMethods ().elements ();
        while (el.hasMoreElements ())
        {
           MethodEntry m = (MethodEntry) el.nextElement ();
          
          
          
          MethodEntry mClone = (MethodEntry)m.clone ();
          mClone.container (v);
          ((MethodGen)mClone.generator ()).interfaceMethod (symbolTable, mClone, stream);
        }
      }
    }

  
  } 

  
  protected void writeStreamableMethods ()
  {
    stream.println ("  public void _read (org.omg.CORBA.portable.InputStream istream)");
    stream.println ("  {");
    read (0, "    ", "this", v, stream);
    stream.println ("  }");
    stream.println ();
    stream.println ("  public void _write (org.omg.CORBA.portable.OutputStream ostream)");
    stream.println ("  {");
    write (0, "    ", "this", v, stream);
    stream.println ("  }");
    stream.println ();
    stream.println ("  public org.omg.CORBA.TypeCode _type ()");
    stream.println ("  {");
    stream.println ("    return " + Util.helperName (v, false) + ".type ();"); 
    stream.println ("  }");
  } 

  
  

  public int helperType (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream)
  {
    ValueEntry vt = (ValueEntry) entry;
    Vector state = vt.state ();
    int noOfMembers = state == null ? 0 : state.size ();
    String members = "_members" + index++;
    String tcOfMembers = "_tcOf" + members;

    stream.println (indent + "org.omg.CORBA.ValueMember[] "
                    + members + " = new org.omg.CORBA.ValueMember["
                    + noOfMembers
                    + "];");
    stream.println (indent + "org.omg.CORBA.TypeCode " + tcOfMembers + " = null;");
    

    String definedInrepId = "_id";
    String repId, version;

    for (int k=0; k<noOfMembers; k++)
    {
      InterfaceState valueMember = (InterfaceState)state.elementAt (k);
      TypedefEntry member = (TypedefEntry)valueMember.entry;
      SymtabEntry mType = Util.typeOf (member);
      if (hasRepId (member))
      {
        repId = Util.helperName (mType, true) + ".id ()"; 
        if (mType instanceof ValueEntry || mType instanceof ValueBoxEntry)
          
          version = "\"\"";
        else
        {
          String id = mType.repositoryID ().ID ();
          version = '"' + id.substring (id.lastIndexOf (':')+1) + '"';
        }
      }
      else
      {
        repId = "\"\"";
        version = "\"\"";
      }

      
      stream.println (indent + "// ValueMember instance for " + member.name ());
      index = ((JavaGenerator)member.generator ()).type (index, indent, tcoffsets, tcOfMembers, member, stream);
      stream.println (indent + members + "[" + k + "] = new org.omg.CORBA.ValueMember ("  
          + '"' + member.name () + "\", ");                               
      stream.println (indent + "    " + repId + ", ");                    
      stream.println (indent + "    " + definedInrepId + ", ");           
      stream.println (indent + "    " + version + ", ");                  
      stream.println (indent + "    " + tcOfMembers + ", ");              
      stream.println (indent + "    " + "null, ");                        
      stream.println (indent + "    " + "org.omg.CORBA." +
          (valueMember.modifier == InterfaceState.Public ?
              "PUBLIC_MEMBER" : "PRIVATE_MEMBER") + ".value" + ");");     
    } 

    stream.println (indent + name + " = org.omg.CORBA.ORB.init ().create_value_tc ("
                    + "_id, "
                    + '"' + entry.name () + "\", "
                    + getValueModifier (vt) + ", "
                    + getConcreteBaseTypeCode (vt) + ", "
                    + members
                    + ");");

    return index;
  } 

  public int type (int index, String indent, TCOffsets tcoffsets, String name, SymtabEntry entry, PrintWriter stream) {
    stream.println (indent + name + " = " + Util.helperName (entry, true) + ".type ();"); 
    return index;
  } 

  
  

  private static boolean hasRepId (SymtabEntry member)
  {
    SymtabEntry mType = Util.typeOf (member);
    return !( mType instanceof PrimitiveEntry ||
              mType instanceof StringEntry ||
              ( mType instanceof TypedefEntry &&
                !(((TypedefEntry)mType).arrayInfo ().isEmpty ()) ) ||
              ( mType instanceof TypedefEntry && member.type () instanceof SequenceEntry) );
  } 

  private static String getValueModifier (ValueEntry vt)
  {
    String mod = "NONE";
    if (vt.isCustom ())
      mod = "CUSTOM";
    else if (vt.isAbstract ())
      mod = "ABSTRACT";
    else if (vt.isSafe ())
      mod = "TRUNCATABLE";
    return "org.omg.CORBA.VM_" + mod + ".value";
  } 

  private static String getConcreteBaseTypeCode (ValueEntry vt)
  {
    Vector v = vt.derivedFrom ();
    if (!vt.isAbstract ())
    {
      SymtabEntry base = (SymtabEntry)vt.derivedFrom ().elementAt (0);
      if (!"ValueBase".equals (base.name ()))
        return Util.helperName (base, true) + ".type ()"; 
    }
    return "null";
  } 

  public void helperRead (String entryName, SymtabEntry entry, PrintWriter stream)
  {
  
  

    if (((ValueEntry)entry).isAbstract ())
    {
      stream.println ("    throw new org.omg.CORBA.BAD_OPERATION (\"abstract value cannot be instantiated\");");
    }
    else
    {
    stream.println ("    return (" + entryName +") ((org.omg.CORBA_2_3.portable.InputStream) istream).read_value (get_instance());"); 
    }
    stream.println ("  }");
    stream.println ();

    

    stream.println ("  public java.io.Serializable read_value (org.omg.CORBA.portable.InputStream istream)"); 
    stream.println ("  {");

    
    if (((ValueEntry)entry).isAbstract ())
    {
      stream.println ("    throw new org.omg.CORBA.BAD_OPERATION (\"abstract value cannot be instantiated\");");
    }
    else
      if (((ValueEntry)entry).isCustom ())
      {
        stream.println ("    throw new org.omg.CORBA.BAD_OPERATION (\"custom values should use unmarshal()\");");
      }
      else
      {
        stream.println ("    " + entryName + " value = new " + entryName + " ();");
        read (0, "    ", "value", entry, stream);
        stream.println ("    return value;");
      }
    stream.println ("  }");
    stream.println ();
    

    
    
    

    
    stream.println ("  public static void read (org.omg.CORBA.portable.InputStream istream, " + entryName + " value)");
    stream.println ("  {");
    read (0, "    ", "value", entry, stream);
  } 

  public int read (int index, String indent, String name, SymtabEntry entry, PrintWriter stream)
  {
    
    Vector vParents = ((ValueEntry) entry).derivedFrom ();
    if (vParents != null && vParents.size() != 0)
    {
      ValueEntry parent = (ValueEntry) vParents.elementAt (0);
      if (parent == null)
        return index;
      
      if (! Util.javaQualifiedName(parent).equals ("java.io.Serializable")) 
          stream.println(indent + Util.helperName (parent, true) + ".read (istream, value);"); 
    }

    Vector vMembers = ((ValueEntry) entry).state ();
    int noOfMembers = vMembers == null ? 0 : vMembers.size ();

    for (int k = 0; k < noOfMembers; k++)
    {
      TypedefEntry member = (TypedefEntry)((InterfaceState)vMembers.elementAt (k)).entry;
      String memberName = member.name ();
      SymtabEntry mType = member.type ();

      if (mType instanceof PrimitiveEntry ||
          mType instanceof TypedefEntry   ||
          mType instanceof SequenceEntry  ||
          mType instanceof StringEntry    ||
          !member.arrayInfo ().isEmpty ())
        index = ((JavaGenerator)member.generator ()).read (index, indent, name + '.' + memberName, member, stream);
      else if (mType instanceof ValueEntry)
      {
        String returnType = Util.javaQualifiedName (mType);
        if (mType instanceof ValueBoxEntry)
          
          
          returnType = Util.javaName (mType);
        stream.println ("    " + name + '.' + memberName + " = (" + returnType +
                        ") ((org.omg.CORBA_2_3.portable.InputStream)istream).read_value (" + Util.helperName (mType, true) +  
                        ".get_instance ());"); 
      }
      else
        stream.println (indent + name + '.' + memberName + " = " +
                        Util.helperName (mType, true) + ".read (istream);"); 
    }

    return index;
  } 

  public void helperWrite (SymtabEntry entry, PrintWriter stream)
  {
    
    
    stream.println ("    ((org.omg.CORBA_2_3.portable.OutputStream) ostream).write_value (value, get_instance());"); 
    stream.println ("  }");
    stream.println ();

    
    
    
    if (!((ValueEntry)entry).isCustom ())
    {
       stream.println ("  public static void _write (org.omg.CORBA.portable.OutputStream ostream, " + Util.javaName (entry) + " value)");
       stream.println ("  {");
       write (0, "    ", "value", entry, stream);
       stream.println ("  }");
       stream.println ();
    }

    
    stream.println ("  public void write_value (org.omg.CORBA.portable.OutputStream ostream, java.io.Serializable obj)"); 
    stream.println ("  {");

    
    if (((ValueEntry)entry).isCustom ())
    {
      stream.println ("    throw new org.omg.CORBA.BAD_OPERATION (\"custom values should use marshal()\");");
    }
    else {
      String entryName = Util.javaName(entry);
      stream.println ("    _write (ostream, (" + entryName + ") obj);"); 

    }
  } 

  public int write (int index, String indent, String name, SymtabEntry entry, PrintWriter stream)
  {
    
    Vector vParents = ((ValueEntry)entry).derivedFrom ();
    if (vParents != null && vParents.size () != 0)
    {
      ValueEntry parent = (ValueEntry)vParents.elementAt (0);
      if (parent == null)
        return index;
      
      
      if (! Util.javaQualifiedName(parent).equals ("java.io.Serializable")) 
          stream.println(indent + Util.helperName (parent, true) + "._write (ostream, value);"); 
    }

    Vector vMembers = ((ValueEntry) entry ).state ();
    int noOfMembers = vMembers == null ? 0 : vMembers.size ();
    for (int k = 0; k < noOfMembers; k++)
    {
      TypedefEntry member = (TypedefEntry)((InterfaceState)vMembers.elementAt (k)).entry;
      String memberName = member.name ();
      SymtabEntry mType = member.type ();

      if (mType instanceof PrimitiveEntry ||
          mType instanceof TypedefEntry   ||
          mType instanceof SequenceEntry  ||
          mType instanceof StringEntry    ||
          !member.arrayInfo ().isEmpty ())
        index = ((JavaGenerator)member.generator ()).write (index, indent, name + '.' + memberName, member, stream);
      else
        stream.println (indent + Util.helperName (mType, true) + 
                              ".write (ostream, " + name + '.' + memberName + ");");
    }

    return index;
  } 

  
  protected void writeAbstract ()
  {
    stream.print ("public interface " + v.name ());

    
    
    if (v.derivedFrom ().size () == 0)
      stream.print (" extends org.omg.CORBA.portable.ValueBase"); 
    else
    {
      SymtabEntry parent;
      
      for (int i = 0; i < v.derivedFrom ().size (); i++)
      {
        if (i == 0)
           stream.print (" extends ");
        else
           stream.print (", ");
        parent = (SymtabEntry) v.derivedFrom ().elementAt (i);
        stream.print (Util.javaName (parent));
      }
    }

    
    if (v.supports ().size () > 0)
    {
      stream.print (", ");
      SymtabEntry intf = (SymtabEntry) v.supports ().elementAt (0);
      stream.print (Util.javaName (intf));
    }
    stream.println ();
    stream.println ("{");
  }

  protected int emit = 0;
  protected Factories factories   = null;
  protected Hashtable  symbolTable = null;
  protected ValueEntry v = null;
  protected PrintWriter stream = null;
  protected boolean explicitDefaultInit = false; 
} 
