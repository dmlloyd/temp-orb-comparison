


package com.sun.tools.corba.se.idl.toJavaPortable;





public class Factories extends com.sun.tools.corba.se.idl.Factories
{
  public com.sun.tools.corba.se.idl.GenFactory genFactory ()
  {
    return new GenFactory ();
  } 

  public com.sun.tools.corba.se.idl.Arguments arguments ()
  {
    return new Arguments ();
  } 

  public String[] languageKeywords ()
  {
  
    return keywords;
  } 

  static String[] keywords =
    {"abstract",   "break",     "byte",
     "catch",      "class",     "continue",
     "do",         "else",      "extends",
     "false",      "final",     "finally",
     "for",        "goto",      "if",
     "implements", "import",    "instanceof",
     "int",        "interface", "native",
     "new",        "null",      "operator",
     "outer",      "package",   "private",
     "protected",  "public",    "return",
     "static",     "super",     "synchronized",
     "this",       "throw",     "throws",
     "transient",  "true",      "try",
     "volatile",   "while",

     "+Helper",    "+Holder",   "+Package",




     "clone",      "equals",       "finalize",
     "getClass",   "hashCode",     "notify",
     "notifyAll",  "toString",     "wait"};

  
  

  private Helper _helper = null;        
  public Helper helper ()
  {
    if (_helper == null)
      if (Util.corbaLevel (2.4f, 99.0f)) 
         _helper = new Helper24 ();     
      else
         _helper = new Helper ();
    return _helper;
  } 

  private ValueFactory _valueFactory = null;        
  public ValueFactory valueFactory ()
  {
    if (_valueFactory == null)
      if (Util.corbaLevel (2.4f, 99.0f)) 
         _valueFactory = new ValueFactory ();     
      
    return _valueFactory;
  } 

  private DefaultFactory _defaultFactory = null;        
  public DefaultFactory defaultFactory ()
  {
    if (_defaultFactory == null)
      if (Util.corbaLevel (2.4f, 99.0f)) 
         _defaultFactory = new DefaultFactory ();     
      
    return _defaultFactory;
  } 

  private Holder _holder = new Holder ();
  public Holder holder ()
  {
    return _holder;
  } 

  private Skeleton _skeleton = new Skeleton ();
  public Skeleton skeleton ()
  {
    return _skeleton;
  } 

  private Stub _stub = new Stub ();
  public Stub stub ()
  {
    return _stub;
  } 

  
  
} 
