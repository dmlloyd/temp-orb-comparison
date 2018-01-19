


package com.sun.tools.corba.se.idl;







import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.sun.tools.corba.se.idl.som.cff.FileLocator;

public class Util
{
  
  
  public static String getVersion ()
  {
    return getVersion ("com/sun/tools/corba/se/idl/idl.prp");
  } 

  
  public static String getVersion (String filename)
  {
    String version = "";
    if (messages == null)  
    {
      Vector oldMsgFiles = msgFiles;
      if (filename == null || filename.equals (""))
        filename = "com/sun/tools/corba/se/idl/idl.prp";
      filename = filename.replace ('/', File.separatorChar);
      registerMessageFile (filename);
      version = getMessage ("Version.product", getMessage ("Version.number"));
      msgFiles = oldMsgFiles;
      messages = null;
    }
    else
    {
      version = getMessage ("Version.product", getMessage ("Version.number"));
    }
    return version;
  } 

  public static boolean isAttribute (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof AttributeEntry;
  } 

  public static boolean isConst (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof ConstEntry;
  } 

  public static boolean isEnum (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof EnumEntry;
  } 

  public static boolean isException (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof ExceptionEntry;
  } 

  public static boolean isInterface (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof InterfaceEntry;
  } 

  public static boolean isMethod (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof MethodEntry;
  } 

  public static boolean isModule (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof ModuleEntry;
  } 

  public static boolean isParameter (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof ParameterEntry;
  } 

  public static boolean isPrimitive (String name, Hashtable symbolTable)
  {
    
    
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    if (entry == null)
    {
      
      
      
      int parenIndex = name.indexOf ('(');
      if (parenIndex >= 0)
        entry = (SymtabEntry)symbolTable.get (name.substring (0, parenIndex));
    }
    return entry == null ? false : entry instanceof PrimitiveEntry;
  } 

  public static boolean isSequence (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof SequenceEntry;
  } 

  public static boolean isStruct (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof StructEntry;
  } 

  public static boolean isString (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof StringEntry;
  } 

  public static boolean isTypedef (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof TypedefEntry;
  } 

  public static boolean isUnion (String name, Hashtable symbolTable)
  {
    SymtabEntry entry = (SymtabEntry)symbolTable.get (name);
    return entry == null ? false : entry instanceof UnionEntry;
  } 

  
  

  public static String getMessage (String key)
  {
    if (messages == null)
      readMessages ();
    String message = messages.getProperty (key);
    if (message == null)
      message = getDefaultMessage (key);
    return message;
  } 

  public static String getMessage (String key, String fill)
  {
    if (messages == null)
      readMessages ();
    String message = messages.getProperty (key);
    if (message == null)
      message = getDefaultMessage (key);
    else
    {
      int index = message.indexOf ("%0");
      if (index >= 0)
        message = message.substring (0, index) + fill + message.substring (index + 2);
    }
    return message;
  } 

  public static String getMessage (String key, String[] fill)
  {
    if (messages == null)
      readMessages ();
    String message = messages.getProperty (key);
    if (message == null)
      message = getDefaultMessage (key);
    else
      for (int i = 0; i < fill.length; ++i)
      {
        int index = message.indexOf ("%" + i);
        if (index >= 0)
          message = message.substring (0, index) + fill[i] + message.substring (index + 2);
      }
    return message;
  } 

  private static String getDefaultMessage (String keyNotFound)
  {
    String message = messages.getProperty (defaultKey);
    int index = message.indexOf ("%0");
    if (index > 0)
      message = message.substring (0, index) + keyNotFound;
    return message;
  } 

  



  private static void readMessages ()
  {
    messages = new Properties ();
    Enumeration fileList = msgFiles.elements ();
    DataInputStream stream;
    while (fileList.hasMoreElements ())
      try
      {
        stream = FileLocator.locateLocaleSpecificFileInClassPath ((String)fileList.nextElement ());
        messages.load (stream);
      }
      catch (IOException e)
      {
      }
    if (messages.size () == 0)
      messages.put (defaultKey, "Error reading Messages File.");
  } 

  
  public static void registerMessageFile (String filename)
  {
    if (filename != null)
      if (messages == null)
        msgFiles.addElement (filename);
      else
        try
        {
          DataInputStream stream = FileLocator.locateLocaleSpecificFileInClassPath (filename);
          messages.load (stream);
        }
        catch (IOException e)
        {
        }
  } 

  private static Properties messages   = null;
  private static String     defaultKey = "default";
  private static Vector     msgFiles = new Vector ();
  static
  {
    msgFiles.addElement ("com/sun/tools/corba/se/idl/idl.prp");
  }

  
  

  public static String capitalize (String lc)
  {
    String first = new String (lc.substring (0, 1));
    first = first.toUpperCase ();
    return first + lc.substring (1);
  } 

  
  

  
  public static String getAbsolutePath (String filename, Vector includePaths) throws FileNotFoundException
  {
    String filepath = null;
    File file = new File (filename);
    if (file.canRead ())
      filepath = file.getAbsolutePath ();
    else
    {
      String fullname = null;
      Enumeration pathList = includePaths.elements ();
      while (!file.canRead () && pathList.hasMoreElements ())
      {
        fullname = (String)pathList.nextElement () + File.separatorChar + filename;
        file = new File (fullname);
      }
      if (file.canRead ())
        filepath = file.getPath ();
      else
        throw new FileNotFoundException (filename);
    }
    return filepath;
  } 

  
  

  
  

  
  
  public static float absDelta (float f1, float f2)
  {
    double delta = f1 - f2;
    return (float)((delta < 0) ? delta * -1.0 : delta);
  } 

  
  

  static RepositoryID emptyID = new RepositoryID ();
} 
