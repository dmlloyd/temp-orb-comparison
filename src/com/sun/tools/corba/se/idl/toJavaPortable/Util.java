


package com.sun.tools.corba.se.idl.toJavaPortable;































import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import com.sun.tools.corba.se.idl.ConstEntry;
import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.ExceptionEntry;
import com.sun.tools.corba.se.idl.GenFileStream;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.NativeEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.StructEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.UnionBranch;
import com.sun.tools.corba.se.idl.UnionEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.InterfaceState;

import com.sun.tools.corba.se.idl.constExpr.*;


public class Util extends com.sun.tools.corba.se.idl.Util
{
  
  
  public static String getVersion ()
  {
    return com.sun.tools.corba.se.idl.Util.getVersion ("com/sun/tools/corba/se/idl/toJavaPortable/toJavaPortable.prp");
  } 

  
  static void setSymbolTable (Hashtable symtab)
  {
    symbolTable = symtab;
  } 

  public static void setPackageTranslation( Hashtable pkgtrans )
  {
    packageTranslation = pkgtrans ;
  }

  public static boolean isInterface (String name)
  {
    return isInterface (name, symbolTable);
  } 

  static String arrayInfo (Vector arrayInfo)
  {
    int         arrays = arrayInfo.size ();
    String      info   = "";
    Enumeration e      = arrayInfo.elements ();
    while (e.hasMoreElements ())
      info = info + '[' + parseExpression ((Expression)e.nextElement ()) + ']';
    return info;
  } 

  
  public static String sansArrayInfo (Vector arrayInfo)
  {
    int    arrays   = arrayInfo.size ();
    String brackets = "";
    for (int i = 0; i < arrays; ++i)
      brackets = brackets + "[]";
    return brackets;
  } 

  
  static public String sansArrayInfo (String name)
  {
    int index = name.indexOf ('[');
    if (index >= 0)
    {
      String array = name.substring (index);
      name = name.substring (0, index);
      while (!array.equals (""))
      {
        name = name + "[]";
        array = array.substring (array.indexOf (']') + 1);
      }
    }
    return name;
  } 

  
  public static String fileName (SymtabEntry entry, String extension )
  {
    NameModifier nm = new NameModifierImpl() ;
    return fileName( entry, nm, extension ) ;
  } 

  public static String fileName (SymtabEntry entry, NameModifier modifier, String extension )
  {
    
    
    String pkg = containerFullName (entry.container ());
    if (pkg != null && !pkg.equals (""))
      mkdir (pkg);

    String name = entry.name ();
    name = modifier.makeName( name ) + extension ;
    if (pkg != null && !pkg.equals (""))
      name = pkg + '/' + name;

    return name.replace ('/', File.separatorChar);
  } 

  public static GenFileStream stream (SymtabEntry entry, String extension)
  {
    NameModifier nm = new NameModifierImpl() ;
    return stream(entry, nm, extension);
  } 

  public static GenFileStream stream (SymtabEntry entry, NameModifier modifier, String extension )
  {
    return getStream ( fileName (entry,modifier,extension), entry ) ;
  }

  public static GenFileStream getStream (String name, SymtabEntry entry)
  {
    
    String absPathName = ((Arguments)Compile.compiler.arguments).targetDir + name;
    if (Compile.compiler.arguments.keepOldFiles && new File (absPathName).exists ())
      return null;
    else
      
      return new GenFileStream (absPathName);
  } 

  public static String containerFullName( SymtabEntry container)
  {
      String name = doContainerFullName( container ) ;
      if (packageTranslation.size() > 0)
          name = translate( name ) ;
      return name ;
  }

  public static String translate( String name )
  {
      String head = name ;
      String tail = "" ;
      int index ;
      String trname ;

      
      
      do {
          trname = (String)(packageTranslation.get( head )) ;
          if (trname != null)
              return trname + tail ;

          index = head.lastIndexOf( '/' ) ;
          if (index >= 0) {
              tail = head.substring( index ) + tail ;
              head = head.substring( 0, index ) ;
          }
      } while (index >= 0) ;

      return name ;
  }

  private static String doContainerFullName (SymtabEntry container)
  {
    String name = "";

    if (container == null)
      name = "";
    else
    {
      if (container instanceof InterfaceEntry ||
          container instanceof StructEntry ||
          container instanceof UnionEntry)
        name = container.name () + "Package";
      else
        name = container.name ();

      if (container.container () != null &&
        !container.container ().name ().equals (""))
        name = doContainerFullName (container.container ()) + '/' + name;
    }

    return name;
  } 

  
  public static String javaName (SymtabEntry entry)
  {
    
    String name = "";
    if (entry instanceof TypedefEntry || entry instanceof SequenceEntry)
      try
      {
        name = sansArrayInfo ((String)entry.dynamicVariable (Compile.typedefInfo));
      }
      catch (NoSuchFieldException e)
      {
        name = entry.name ();
      }
    else if (entry instanceof PrimitiveEntry)
      name = javaPrimName (entry.name ());
    else if (entry instanceof StringEntry)
      name = "String";
    else if (entry instanceof NativeEntry)
      name = javaNativeName (entry.name());
    else if (entry instanceof ValueEntry && entry.name ().equals ("ValueBase"))
        name = "java.io.Serializable";
    else if (entry instanceof ValueBoxEntry)
    {
      ValueBoxEntry v = (ValueBoxEntry) entry;
      TypedefEntry member = ((InterfaceState) v.state ().elementAt (0)).entry;
      SymtabEntry mType = member.type ();
      if (mType instanceof PrimitiveEntry)
      {
         name = containerFullName (entry.container ());
         if (!name.equals (""))
           name = name + '.';
         name = name + entry.name ();
      }
      else
         name = javaName (mType);
    }
    else
    {
      name = containerFullName (entry.container ());
      if (name.equals (""))
        name = entry.name ();
      else
        name = name + '.' + entry.name ();
    }

    
    return name.replace ('/', '.');
  } 

  public static String javaPrimName (String name)
  {
    if (name.equals ("long") || name.equals ("unsigned long"))
      name = "int";
    else if (name.equals ("octet"))
      name = "byte";
    
    else if (name.equals ("long long") || name.equals ("unsigned long long"))
      name = "long";
    else if (name.equals ("wchar"))
      name = "char";
    else if (name.equals ("unsigned short"))
      name = "short";
    else if (name.equals ("any"))
      name = "org.omg.CORBA.Any";
    else if (name.equals ("TypeCode"))
      name = "org.omg.CORBA.TypeCode";
    else if (name.equals ("Principal")) 
      name = "org.omg.CORBA.Principal";
    return name;
  } 

  public static String javaNativeName (String name)
  {

    

    if (name.equals ("AbstractBase") || name.equals ("Cookie"))
      name = "java.lang.Object";
    else if (name.equals ("Servant"))
      name = "org.omg.PortableServer.Servant";
    else if (name.equals ("ValueFactory"))
      name = "org.omg.CORBA.portable.ValueFactory";
    return name;
  }


  
  public static String javaQualifiedName (SymtabEntry entry)
  {
    String name = "";
    if (entry instanceof PrimitiveEntry)
      name = javaPrimName (entry.name ());
    else if (entry instanceof StringEntry)
      name = "String";
    else if (entry instanceof ValueEntry && entry.name ().equals ("ValueBase"))
      name = "java.io.Serializable";
    else
    {
      SymtabEntry container = entry.container ();
      if (container != null)
        name = container.name ();
      if (name.equals (""))
        name = entry.name ();
      else
        name = containerFullName (entry.container ()) + '.' + entry.name ();
    }
    return name.replace ('/', '.');
  } 

  
  

  
  public static String collapseName (String name)
  {
    if (name.equals ("unsigned short"))
      name = "ushort";
    else if (name.equals ("unsigned long"))
      name = "ulong";
    else if (name.equals ("unsigned long long"))
      name = "ulonglong";
    else if (name.equals ("long long"))
      name = "longlong";
    return name;
  } 

  
  public static SymtabEntry typeOf (SymtabEntry entry)
  {
    while (entry instanceof TypedefEntry && ((TypedefEntry)entry).arrayInfo ().isEmpty () && !(entry.type () instanceof SequenceEntry))
      entry = entry.type ();
    return entry;
  } 

  
  static void fillInfo (SymtabEntry infoEntry)
  {
    String      arrayInfo   = "";
    SymtabEntry entry       = infoEntry;
    boolean     alreadyHave = false;

    do
    {
      try
      {
        alreadyHave = entry.dynamicVariable (Compile.typedefInfo) != null;
      }
      catch (NoSuchFieldException e)
      {}
      
      
      if (!alreadyHave)
      {
        if (entry instanceof TypedefEntry)
          arrayInfo = arrayInfo + arrayInfo (((TypedefEntry)entry).arrayInfo ());
        else if (entry instanceof SequenceEntry)
        {
          Expression maxSize = ((SequenceEntry)entry).maxSize ();
          if (maxSize == null)
            arrayInfo = arrayInfo + "[]";
          else
            arrayInfo = arrayInfo + '[' + parseExpression (maxSize) + ']';
        }
        if (entry.type () == null)
        {
          
          
          
          
        }
        else
          entry = entry.type ();
      }
    } while (!alreadyHave && entry != null &&
        (entry instanceof TypedefEntry || entry instanceof SequenceEntry));
    
    
    
    if (entry instanceof ValueBoxEntry)
      fillValueBoxInfo ((ValueBoxEntry)entry);
    try
    {
      if (alreadyHave)
        infoEntry.dynamicVariable (Compile.typedefInfo, (String)entry.dynamicVariable (Compile.typedefInfo) + arrayInfo);
      else
        infoEntry.dynamicVariable (Compile.typedefInfo, javaName (entry) + arrayInfo);
    }
    catch (NoSuchFieldException e)
    {}
  } 

  
  
  static void fillValueBoxInfo (ValueBoxEntry vb)
  {
    SymtabEntry stateMember = (((InterfaceState) vb.state ().elementAt (0)).entry);
    if (stateMember.type() != null)
      Util.fillInfo (stateMember.type ());
    Util.fillInfo (stateMember);
  } 

  
  public static String holderName (SymtabEntry entry)
  {
    String name;
    if (entry instanceof PrimitiveEntry)
      if (entry.name ().equals ("any"))
        name = "org.omg.CORBA.AnyHolder";
      else if (entry.name ().equals ("TypeCode"))
        name = "org.omg.CORBA.TypeCodeHolder";
      else if (entry.name ().equals ("Principal")) 
        name = "org.omg.CORBA.PrincipalHolder";
      else
        name = "org.omg.CORBA." + capitalize (javaQualifiedName (entry)) + "Holder";
    else if (entry instanceof TypedefEntry)
    {
      TypedefEntry td = (TypedefEntry)entry;
      if (!td.arrayInfo ().isEmpty () || td.type () instanceof SequenceEntry)
        name = javaQualifiedName (entry) + "Holder";
      else
        name = holderName (entry.type ());
    }
    else if (entry instanceof StringEntry)
      name = "org.omg.CORBA.StringHolder";
    else if (entry instanceof ValueEntry)
    {
      if (entry.name ().equals ("ValueBase"))
          name = "org.omg.CORBA.ValueBaseHolder"; 
      else
          name = javaName (entry) + "Holder";
    } else if (entry instanceof NativeEntry) {
      
      
      
      name = javaQualifiedName(entry) + "Holder";
    }
    else
      name = javaName (entry) + "Holder";
    return name;
  } 

  
  public static String helperName (SymtabEntry entry, boolean qualifiedName)
  {
    if (entry instanceof ValueEntry)
      if (entry.name ().equals ("ValueBase"))
          return "org.omg.CORBA.ValueBaseHelper";

    if (qualifiedName)
      return javaQualifiedName (entry) + "Helper";
    else
      return javaName (entry) + "Helper";
  } 

  public static final short
      TypeFile   = 0,
      StubFile   = 1,
      HelperFile = 2,
      HolderFile = 3,
      StateFile  = 4;

  
  public static void writePackage (PrintWriter stream, SymtabEntry entry)
  {
    writePackage (stream, entry, TypeFile);
  } 

  
  public static void writePackage (PrintWriter stream, SymtabEntry entry, String name, short type)
  {
    if (name != null && !name.equals (""))
    {
      stream.println ("package " + name.replace ('/', '.') + ';');

      
      
      
      if (!Compile.compiler.importTypes.isEmpty ())
      {
        stream.println ();
        Vector v = addImportLines (entry, Compile.compiler.importTypes, type);
        printImports (v, stream);
      }
    }
  } 

  
  public static void writePackage (PrintWriter stream, SymtabEntry entry, short type)
  {
    String fullName = containerFullName (entry.container ());
    if (fullName != null && !fullName.equals (""))
    {
      stream.println ("package " + fullName.replace ('/', '.') + ';');
       
      
      
      if ((type != HolderFile || entry instanceof TypedefEntry) && !Compile.compiler.importTypes.isEmpty ())
      {
        stream.println ();
        Vector v = addImportLines (entry, Compile.compiler.importTypes, type);
        printImports (v, stream);
      }
      



    }
  } 

  
  static private void printImports (Vector importList, PrintWriter stream)
  {
    Enumeration e = importList.elements ();
    while (e.hasMoreElements ())
      stream.println ("import " + (String)e.nextElement () + ';');
  } 

  
  static private void addTo (Vector importList, String name)
  {
    
    if (name.startsWith ("ValueBase"))  
      if ((name.compareTo ("ValueBase") == 0) ||
          (name.compareTo ("ValueBaseHolder") == 0) ||
              (name.compareTo ("ValueBaseHelper") == 0))
        return;
    if (!importList.contains (name))
      importList.addElement (name);
  } 

  
  static private Vector addImportLines (SymtabEntry entry, Vector importTypes, short type)
  {
    Vector importList = new Vector ();
    if (entry instanceof ConstEntry)
    {
      ConstEntry c      = (ConstEntry)entry;
      Object     cvalue = c.value ().value ();
      if (cvalue instanceof ConstEntry && importTypes.contains (cvalue))
        addTo (importList, ((ConstEntry)cvalue).name ());
    }
    else if (entry instanceof ValueEntry && type == HelperFile) 
    {
      
      
      if (((ValueEntry)entry).derivedFrom ().size () > 0) 
      {
        ValueEntry base = (ValueEntry)((ValueEntry)entry).derivedFrom ().elementAt (0);
        String baseName = base.name ();
        if (!"ValueBase".equals (baseName))
          if (importTypes.contains (base))
            addTo (importList, baseName + "Helper");
      }
    }
    else if (entry instanceof InterfaceEntry && (type == TypeFile || type == StubFile))
    {
      InterfaceEntry i = (InterfaceEntry)entry;

      if (i instanceof ValueEntry) 
      {
        
        Enumeration e = ((ValueEntry)i).supports ().elements ();
        while (e.hasMoreElements ())
        {
          SymtabEntry parent = (SymtabEntry)e.nextElement ();
          if (importTypes.contains (parent))
          {
            addTo (importList, parent.name () + "Operations");
          }
          
          if (type == StubFile)
          {
            if (importTypes.contains (parent))
              addTo (importList, parent.name ());
            Vector subImportList = addImportLines (parent, importTypes, StubFile);
            Enumeration en = subImportList.elements ();
            while (en.hasMoreElements ())
            {
              addTo (importList, (String)en.nextElement ());
            }
          }
        }
      }
      
      
      Enumeration e = i.derivedFrom ().elements ();
      while (e.hasMoreElements ())
      {
        SymtabEntry parent = (SymtabEntry)e.nextElement ();
        if (importTypes.contains (parent))
        {
          addTo (importList, parent.name ());
          
          
          if (!(parent instanceof ValueEntry)) 
            addTo (importList, parent.name () + "Operations");
        }
        
        if (type == StubFile)
        {
          Vector subImportList = addImportLines (parent, importTypes, StubFile);
          Enumeration en = subImportList.elements ();
          while (en.hasMoreElements ())
          {
            addTo (importList, (String)en.nextElement ());
          }
        }
      }
      
      e = i.methods ().elements ();
      while (e.hasMoreElements ())
      {
        MethodEntry m = (MethodEntry)e.nextElement ();

        
        SymtabEntry mtype = typeOf (m.type ());
        if (mtype != null && importTypes.contains (mtype))
          if (type == TypeFile || type == StubFile)
          {
            addTo (importList, mtype.name ());
            addTo (importList, mtype.name () + "Holder");
            if (type == StubFile)
              addTo (importList, mtype.name () + "Helper");
          }
        checkForArrays (mtype, importTypes, importList);
        
        
        if (type == StubFile)
          checkForBounds (mtype, importTypes, importList);

        
        Enumeration exEnum = m.exceptions ().elements ();
        while (exEnum.hasMoreElements ())
        {
          ExceptionEntry ex = (ExceptionEntry)exEnum.nextElement ();
          if (importTypes.contains (ex))
          {
            addTo (importList, ex.name ());
            addTo (importList, ex.name () + "Helper"); 
          }
        }

        
        Enumeration parms = m.parameters ().elements ();
        while (parms.hasMoreElements ())
        {
          ParameterEntry parm = (ParameterEntry)parms.nextElement ();
          SymtabEntry parmType = typeOf (parm.type ());
          if (importTypes.contains (parmType))
          {
            
            if (type == StubFile)
              addTo (importList, parmType.name () + "Helper");
            if (parm.passType () == ParameterEntry.In)
              addTo (importList, parmType.name ());
            else
              addTo (importList, parmType.name () + "Holder");
          }
          checkForArrays (parmType, importTypes, importList);
          
          if (type == StubFile)
            checkForBounds (parmType, importTypes, importList);
        }
      }
    }
    else if (entry instanceof StructEntry)
    {
      StructEntry s = (StructEntry)entry;

      
      Enumeration members = s.members ().elements ();
      while (members.hasMoreElements ())
      {
        SymtabEntry member = (TypedefEntry)members.nextElement ();
        
        
        SymtabEntry memberType = member.type ();
        member = typeOf (member);
        if (importTypes.contains (member))
        {
          
          
          
          if (!(member instanceof TypedefEntry) && !(member instanceof ValueBoxEntry))
            addTo (importList, member.name ());
          
          
          
          if (type == HelperFile)
          {
            addTo (importList, member.name () + "Helper");
            if (memberType instanceof TypedefEntry)
              addTo (importList, memberType.name () + "Helper");
          }
        }
        checkForArrays (member, importTypes, importList);
        checkForBounds (member, importTypes, importList);
      }
    }
    else if (entry instanceof TypedefEntry)
    {
      TypedefEntry t = (TypedefEntry)entry;
      String arrays = checkForArrayBase (t, importTypes, importList);
      if (type == HelperFile)
      {
        checkForArrayDimensions (arrays, importTypes, importList);
        try
        {
          String name = (String)t.dynamicVariable (Compile.typedefInfo);
          int index = name.indexOf ('[');
          if (index >= 0)
            name = name.substring (0, index);
          
          SymtabEntry typeEntry = (SymtabEntry)symbolTable.get (name);
          if (typeEntry != null && importTypes.contains (typeEntry))
            addTo (importList, typeEntry.name () + "Helper");
        }
        catch (NoSuchFieldException e)
        {}

        
        
        checkForBounds (typeOf (t), importTypes, importList);
      }
      Vector subImportList = addImportLines (t.type (), importTypes, type);
      Enumeration e = subImportList.elements ();
      while (e.hasMoreElements ())
        addTo (importList, (String)e.nextElement ());
    }
    else if (entry instanceof UnionEntry)
    {
      UnionEntry u = (UnionEntry)entry;

      
      SymtabEntry utype = typeOf (u.type ());
      if (utype instanceof EnumEntry && importTypes.contains (utype))
        addTo (importList, utype.name ());

      
      Enumeration branches = u.branches ().elements ();
      while (branches.hasMoreElements ())
      {
        UnionBranch branch = (UnionBranch)branches.nextElement ();
        SymtabEntry branchEntry = typeOf (branch.typedef);
        if (importTypes.contains (branchEntry))
        {
          addTo (importList, branchEntry.name ());
          if (type == HelperFile)
            addTo (importList, branchEntry.name () + "Helper");
        }
        checkForArrays (branchEntry, importTypes, importList);
        
        checkForBounds (branchEntry, importTypes, importList);
      }
    }

    
    
    
    Enumeration en = importList.elements ();
    while (en.hasMoreElements ())
    {
      String name = (String)en.nextElement ();
      SymtabEntry e = (SymtabEntry)symbolTable.get (name);
      if (e != null && e instanceof TypedefEntry)
      {
        TypedefEntry t = (TypedefEntry)e;
        if (t.arrayInfo ().size () == 0 || !(t.type () instanceof SequenceEntry))
          importList.removeElement (name);
      }
    }
    return importList;
  } 

  
  static private void checkForArrays (SymtabEntry entry, Vector importTypes, Vector importList)
  {
    if (entry instanceof TypedefEntry)
    {
      TypedefEntry t = (TypedefEntry)entry;
      String arrays = checkForArrayBase (t, importTypes, importList);
      checkForArrayDimensions (arrays, importTypes, importList);
    }
  } 

  
  static private String checkForArrayBase (TypedefEntry t, Vector importTypes, Vector importList)
  {
    String arrays = "";
    try
    {
      String name = (String)t.dynamicVariable (Compile.typedefInfo);
      int index = name.indexOf ('[');
      if (index >= 0)
      {
        arrays = name.substring (index);
        name = name.substring (0, index);
      }

      
      SymtabEntry typeEntry = (SymtabEntry)symbolTable.get (name);
      if (typeEntry != null && importTypes.contains (typeEntry))
        addTo (importList, typeEntry.name ());
    }
    catch (NoSuchFieldException e)
    {}
    return arrays;
  } 

  
  static private void checkForArrayDimensions (String arrays, Vector importTypes, Vector importList)
  {
    
    
    while (!arrays.equals (""))
    {
      int index = arrays.indexOf (']');
      String dim = arrays.substring (1, index);
      arrays = arrays.substring (index + 1);
      SymtabEntry constant = (SymtabEntry)symbolTable.get (dim);
      if (constant == null)
      {
        
        
        int i = dim.lastIndexOf ('.');
        if (i >= 0)
          constant = (SymtabEntry)symbolTable.get (dim.substring (0, i));
      }
      if (constant != null && importTypes.contains (constant))
        addTo (importList, constant.name ());
    }
  } 

  
  
  

  
  static private void checkForBounds (SymtabEntry entry, Vector importTypes, Vector importList)
  {
    
    SymtabEntry entryType = entry;
    while (entryType instanceof TypedefEntry)
      entryType = entryType.type ();

    if (entryType instanceof StringEntry && ((StringEntry)entryType).maxSize () != null)
      checkForGlobalConstants (((StringEntry)entryType).maxSize ().rep (), importTypes, importList);
    else
      if (entryType instanceof SequenceEntry && ((SequenceEntry)entryType).maxSize () != null)
        checkForGlobalConstants (((SequenceEntry)entryType).maxSize ().rep (), importTypes, importList);
  } 

  
  static private void checkForGlobalConstants (String exprRep, Vector importTypes, Vector importList)
  {
    
    
    
    java.util.StringTokenizer st = new java.util.StringTokenizer (exprRep, " +-*()~&|^%<>");
    while (st.hasMoreTokens ())
    {
      String token = st.nextToken ();
      
      
      
      if (!token.equals ("/"))
      {
        SymtabEntry typeEntry = (SymtabEntry)symbolTable.get (token);
        if (typeEntry instanceof ConstEntry)
        {
          int slashIdx = token.indexOf ('/');
          if (slashIdx < 0)  
          {
            if (importTypes.contains (typeEntry))
              addTo (importList, typeEntry.name ());
          }
          else  
          {
            SymtabEntry constContainer = (SymtabEntry)symbolTable.get (token.substring (0, slashIdx));
            if (constContainer instanceof InterfaceEntry && importTypes.contains (constContainer))
              addTo (importList, constContainer.name ());
          }
        }
      }
    }
  } 

  
  public static void writeInitializer (String indent, String name, String arrayDcl, SymtabEntry entry, PrintWriter stream)
  {
    if (entry instanceof TypedefEntry)
    {
      TypedefEntry td = (TypedefEntry)entry;
      writeInitializer (indent, name, arrayDcl + sansArrayInfo (td.arrayInfo ()), td.type (), stream);
    }
    else if (entry instanceof SequenceEntry)
      writeInitializer (indent, name, arrayDcl + "[]", entry.type (), stream);
    else if (entry instanceof EnumEntry)
      if (arrayDcl.length () > 0)
        stream.println (indent + javaName (entry) + ' ' + name + arrayDcl + " = null;");
      else
        stream.println (indent + javaName (entry) + ' ' + name + " = null;");
    else if (entry instanceof PrimitiveEntry)
    {
      boolean array = arrayDcl.length () > 0;
      String tname = javaPrimName (entry.name ());
      if (tname.equals ("boolean"))
        stream.println (indent + "boolean " + name + arrayDcl + " = " + (array ? "null;" : "false;"));
      else if (tname.equals ("org.omg.CORBA.TypeCode"))
        stream.println (indent + "org.omg.CORBA.TypeCode " + name + arrayDcl + " = null;");
      else if (tname.equals ("org.omg.CORBA.Any"))
        stream.println (indent + "org.omg.CORBA.Any " + name + arrayDcl + " = null;");
      else if (tname.equals ("org.omg.CORBA.Principal")) 
        stream.println (indent + "org.omg.CORBA.Principal " + name + arrayDcl + " = null;");
      else
        stream.println (indent + tname + ' ' + name + arrayDcl + " = " + (array ? "null;" : '(' + tname + ")0;"));
    }
    
    
    
    
    else
      stream.println (indent + javaName (entry) + ' ' + name + arrayDcl + " = null;");
  } 

  
  public static void writeInitializer (String indent, String name, String arrayDcl, SymtabEntry entry, String initializer, PrintWriter stream)
  {
    if (entry instanceof TypedefEntry)
    {
      TypedefEntry td = (TypedefEntry)entry;
      writeInitializer (indent, name, arrayDcl + sansArrayInfo (td.arrayInfo ()), td.type (), initializer, stream);
    }
    else if (entry instanceof SequenceEntry)
      writeInitializer (indent, name, arrayDcl + "[]", entry.type (), initializer, stream);
    else if (entry instanceof EnumEntry)
      if (arrayDcl.length () > 0)
        stream.println (indent + javaName (entry) + ' ' + name + arrayDcl + " = " + initializer + ';');
      else
        stream.println (indent + javaName (entry) + ' ' + name + " = " + initializer + ';');
    else if (entry instanceof PrimitiveEntry)
    {
      boolean array = arrayDcl.length () > 0;
      String tname = javaPrimName (entry.name ());
      if (tname.equals ("boolean"))
        stream.println (indent + "boolean " + name + arrayDcl + " = " + initializer + ';');
      else if (tname.equals ("org.omg.CORBA.TypeCode"))
        stream.println (indent + "org.omg.CORBA.TypeCode " + name + arrayDcl + " = " + initializer + ';');
      else if (tname.equals ("org.omg.CORBA.Any"))
        stream.println (indent + "org.omg.CORBA.Any " + name + arrayDcl + " = " + initializer + ';');
      else if (tname.equals ("org.omg.CORBA.Principal")) 
        stream.println (indent + "org.omg.CORBA.Principal " + name + arrayDcl + " = " + initializer + ';');
      else
        stream.println (indent + tname + ' ' + name + arrayDcl + " = " + initializer + ';');
    }
    
    
    
    
    else
      stream.println (indent + javaName (entry) + ' ' + name + arrayDcl + " = " + initializer + ';');
  } 

  
  public static void mkdir (String name)
  {
    String targetDir = ((Arguments)Compile.compiler.arguments).targetDir; 
    name = (targetDir + name).replace ('/', File.separatorChar); 
    File pkg = new File (name);
    if (!pkg.exists ())
      if (!pkg.mkdirs ())
        System.err.println (getMessage ("Util.cantCreatePkg", name));
  } 

  
  public static void writeProlog (PrintWriter stream, String filename)
  {
    
    String targetDir = ((Arguments)Compile.compiler.arguments).targetDir;
    if (targetDir != null)
      filename = filename.substring (targetDir.length ());
    stream.println ();
    stream.println ("/**");
    stream.println ("* " + filename.replace (File.separatorChar, '/') +
        " .");
    stream.println ("* " + Util.getMessage ("toJavaProlog1",
        Util.getMessage ("Version.product", Util.getMessage ("Version.number"))));
    
    
    stream.println ("* " + Util.getMessage ("toJavaProlog2", Compile.compiler.arguments.file.replace (File.separatorChar, '/')));

    
    
    
    

    DateFormat formatter = DateFormat.getDateTimeInstance (DateFormat.FULL, DateFormat.FULL, Locale.getDefault ());

    
    

    if (Locale.getDefault () == Locale.JAPAN)
      formatter.setTimeZone (java.util.TimeZone.getTimeZone ("JST"));
    else
      formatter.setTimeZone (java.util.TimeZone.getDefault ());

    stream.println ("* " + formatter.format (new Date ()));

    
    

    stream.println ("*/");
    stream.println ();
  } 

  
  
  
  

  
  public static String stripLeadingUnderscores (String string)
  {
    while (string.startsWith ("_"))
      string = string.substring (1);
    return string;
  } 

  
  public static String stripLeadingUnderscoresFromID (String string)
  {
    String stringPrefix = "";
    int slashIndex = string.indexOf (':');
    if (slashIndex >= 0)
      do
      {
        stringPrefix = stringPrefix + string.substring (0, slashIndex + 1);
        string = string.substring (slashIndex + 1);
        while (string.startsWith ("_"))
          string = string.substring (1);
        slashIndex = string.indexOf ('/');
      } while (slashIndex >= 0);
    return stringPrefix + string;
  } 

  
  public static String parseExpression (Expression e)
  {
    if (e instanceof Terminal)
      return parseTerminal ((Terminal)e);
    else if (e instanceof BinaryExpr)
      return parseBinary ((BinaryExpr)e);
    else if (e instanceof UnaryExpr)
      return parseUnary ((UnaryExpr)e);
    else
      return "(UNKNOWN_VALUE)"; 
                                
  } 

  
  static String parseTerminal (Terminal e)
  {
    if (e.value () instanceof ConstEntry)
    {
      ConstEntry c = (ConstEntry)e.value ();
      if (c.container () instanceof InterfaceEntry)
        return javaQualifiedName (c.container ()) + '.' + c.name ();
      else
        return javaQualifiedName (c) + ".value";
    }
    else if (e.value () instanceof Expression)
      return '(' + parseExpression ((Expression)e.value ()) + ')';
    else if (e.value () instanceof Character)
    {
      if (((Character)e.value ()).charValue () == '\013')
        
        return "'\\013'";
      else if (((Character)e.value ()).charValue () == '\007')
        
        return "'\\007'";
      else if (e.rep ().startsWith ("'\\x"))
        return hexToOctal (e.rep ());
      else if (e.rep ().equals ("'\\?'"))
        return "'?'";
      else
        return e.rep ();
    }
    else if (e.value () instanceof Boolean)
      return e.value ().toString ();

    
    
    
    



    else if (e.value () instanceof BigInteger)
    {
      
      
      
      

      
      SymtabEntry typeEntry = (SymtabEntry) symbolTable.get(e.type());

      
      while (typeEntry.type() != null) {
          typeEntry = typeEntry.type();
      }
      String type = typeEntry.name();

      if (type.equals("unsigned long long") &&
          ((BigInteger)e.value ()).compareTo (Expression.llMax) > 0) 
      {
        
        BigInteger v = (BigInteger)e.value ();
        v = v.subtract (Expression.twoPow64);
        int index = e.rep ().indexOf (')');
        if (index < 0)
          return v.toString () + 'L';
        else
          return '(' + v.toString () + 'L' + ')';
      }
      else if ( type.indexOf("long long") >= 0 || type.equals("unsigned long") )
      {
        String rep   = e.rep ();
        int    index = rep.indexOf (')');
        if (index < 0)
          return rep + 'L';
        else
          return rep.substring (0, index) + 'L' + rep.substring (index);
      }
      else
        return e.rep ();
    } 
    else
      return e.rep ();
  } 

  
  static String hexToOctal (String hex)
  {
    
    
    hex = hex.substring (3, hex.length () - 1);
    return "'\\" + Integer.toString (Integer.parseInt (hex, 16), 8) + "'";
  } 

  
  static String parseBinary (BinaryExpr e)
  {
    String castString = "";
    if (e.value () instanceof Float || e.value () instanceof Double)
    {
      castString = "(double)";
      if (!(e instanceof Plus || e instanceof Minus ||
            e instanceof Times || e instanceof Divide))
        System.err.println ("Operator " + e.op () + " is invalid on floating point numbers");
    }
    else if (e.value () instanceof Number)
    {
      if (e.type (). indexOf ("long long") >= 0)
        castString = "(long)";
      else
        castString = "(int)";
    }
    else
    {
      castString = "";
      System.err.println ("Unknown type in constant expression");
    }

    
    
    
    

    
    if (e.type ().equals ("unsigned long long"))
    {
      BigInteger value = (BigInteger)e.value ();
      if (value.compareTo (Expression.llMax) > 0) 
        value = value.subtract (Expression.twoPow64); 
      return castString + '(' + value.toString () + 'L' + ')';
    }
    else
      return castString + '(' + parseExpression (e.left ()) + ' ' + e.op () + ' ' + parseExpression (e.right ()) + ')';
    
  } 

  
  static String parseUnary (UnaryExpr e)
  {
    if (!(e.value () instanceof Number))
      return "(UNKNOWN_VALUE)"; 
    else if ((e.value () instanceof Float || e.value () instanceof Double) && e instanceof Not)
      return "(UNKNOWN_VALUE)"; 
    else
    {
      String castString = "";
      if (e.operand ().value () instanceof Float ||
          e.operand ().value () instanceof Double)
        castString = "(double)";
      
      
      
      else if (e.type (). indexOf ("long long") >= 0)
        castString = "(long)";
      else
        castString = "(int)";

      
      
      
      

      
      if (e.type ().equals ("unsigned long long"))
      {
        BigInteger value = (BigInteger)e.value ();
        if (value.compareTo (Expression.llMax) > 0) 
          value = value.subtract (Expression.twoPow64); 
        return castString + '(' + value.toString () + 'L' + ')';
      }
      else
        return castString + e.op () + parseExpression (e.operand ());
      
    }
  } 

  
  public static boolean IDLEntity (SymtabEntry entry)
  {
    boolean rc = true;
    if (entry instanceof PrimitiveEntry || entry instanceof StringEntry)
       rc = false;
    else if (entry instanceof TypedefEntry)
       rc = IDLEntity (entry.type ());
    return rc;
  } 

  
  
  public static boolean corbaLevel (float min, float max)
  {
    float level = Compile.compiler.arguments.corbaLevel;
    float delta = 0.001f;
    if ((level - min + delta >= 0.0f) && (max - level + delta >= 0.0f))
        return true;
    else
        return false;
  } 

  static Hashtable symbolTable = new Hashtable ();
  static Hashtable packageTranslation = new Hashtable() ;
} 
