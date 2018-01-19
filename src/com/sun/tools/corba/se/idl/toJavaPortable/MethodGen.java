


package com.sun.tools.corba.se.idl.toJavaPortable;


















import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.tools.corba.se.idl.EnumEntry;
import com.sun.tools.corba.se.idl.ExceptionEntry;
import com.sun.tools.corba.se.idl.InterfaceEntry;
import com.sun.tools.corba.se.idl.MethodEntry;
import com.sun.tools.corba.se.idl.ParameterEntry;
import com.sun.tools.corba.se.idl.PrimitiveEntry;
import com.sun.tools.corba.se.idl.StringEntry;
import com.sun.tools.corba.se.idl.SymtabEntry;
import com.sun.tools.corba.se.idl.SequenceEntry;
import com.sun.tools.corba.se.idl.ValueEntry;
import com.sun.tools.corba.se.idl.ValueBoxEntry;
import com.sun.tools.corba.se.idl.InterfaceState;
import com.sun.tools.corba.se.idl.TypedefEntry;
import com.sun.tools.corba.se.idl.AttributeEntry;

import com.sun.tools.corba.se.idl.constExpr.Expression;


public class MethodGen implements com.sun.tools.corba.se.idl.MethodGen
{
  private static final String ONE_INDENT   = "    ";
  private static final String TWO_INDENT   = "        ";
  private static final String THREE_INDENT = "            ";
  private static final String FOUR_INDENT  = "                ";
  private static final String FIVE_INDENT  = "                    ";
  
  private static final int ATTRIBUTE_METHOD_PREFIX_LENGTH  = 5;
  
  public MethodGen ()
  {
  } 

  
  public void generate (Hashtable symbolTable, MethodEntry m, PrintWriter stream)
  {
  } 

  
  protected void interfaceMethod (Hashtable symbolTable, MethodEntry m, PrintWriter stream)
  {
    this.symbolTable = symbolTable;
    this.m           = m;
    this.stream      = stream;
    if (m.comment () != null)
      m.comment ().generate ("", stream);
    stream.print ("  ");
    SymtabEntry container = (SymtabEntry)m.container ();
    boolean isAbstract = false;
    boolean valueContainer = false;
    if (container instanceof ValueEntry)
    {
      isAbstract = ((ValueEntry)container).isAbstract ();
      valueContainer = true;
    }
    if (valueContainer && !isAbstract)
      stream.print ("public ");
    writeMethodSignature ();
    if (valueContainer && !isAbstract)
    {
      stream.println ();
      stream.println ("  {");
      stream.println ("  }");
      stream.println ();
    }
    else
      stream.println (";");
  } 

  
  protected void stub (String className, boolean isAbstract,
      Hashtable symbolTable, MethodEntry m, PrintWriter stream, int index)
  {
    localOptimization =
        ((Arguments)Compile.compiler.arguments).LocalOptimization;
    this.isAbstract  = isAbstract;
    this.symbolTable = symbolTable;
    this.m           = m;
    this.stream      = stream;
    this.methodIndex = index;
    if (m.comment () != null)
      m.comment ().generate ("  ", stream);
    stream.print ("  public ");
    writeMethodSignature ();
    stream.println ();
    stream.println ("  {");
    writeStubBody ( className );
    stream.println ("  } // " + m.name ());
    stream.println ();
  } 

  
  protected void localstub (Hashtable symbolTable, MethodEntry m, PrintWriter stream, int index, InterfaceEntry i)
  {
    this.symbolTable = symbolTable;
    this.m           = m;
    this.stream      = stream;
    this.methodIndex = index;
    if (m.comment () != null)
      m.comment ().generate ("  ", stream);
    stream.print ("  public ");
    writeMethodSignature ();
    stream.println ();
    stream.println ("  {");
    writeLocalStubBody (i);
    stream.println ("  } // " + m.name ());
    stream.println ();
  } 
  
  protected void skeleton (Hashtable symbolTable, MethodEntry m, PrintWriter stream, int index)
  {
    this.symbolTable = symbolTable;
    this.m           = m;
    this.stream      = stream;
    this.methodIndex = index;
    if (m.comment () != null)
      m.comment ().generate ("  ", stream);
    stream.print ("  public ");
    writeMethodSignature ();
    stream.println ();
    stream.println ("  {");
    writeSkeletonBody ();
    stream.println ("  } // " + m.name ());
  } 

  
  protected void dispatchSkeleton (Hashtable symbolTable, MethodEntry m, PrintWriter stream, int index)
  {
    this.symbolTable = symbolTable;
    this.m           = m;
    this.stream      = stream;
    this.methodIndex = index;
    if (m.comment () != null)
      m.comment ().generate ("  ", stream);
    writeDispatchCall ();
  } 

  
  
  protected boolean isValueInitializer ()
  {
    MethodEntry currentInit = null;
    if ((m.container () instanceof ValueEntry))
    {
      Enumeration e = ((ValueEntry)m.container ()).initializers ().elements ();
      while (currentInit != m && e.hasMoreElements ())
        currentInit = (MethodEntry)e.nextElement ();
    }
    return (currentInit == m) && (null != m);  
  } 

  
  protected void writeMethodSignature ()
  {
    boolean isValueInitializer = isValueInitializer ();  

    
    
    
    
    
    
    
    
    
    
    if (m.type () == null)
    {
      if (!isValueInitializer)
        stream.print ("void");
    }
    else
    {
      
      
      stream.print (Util.javaName (m.type ()));
    }
    
    
    
    
    
    if (isValueInitializer)
      stream.print (' ' + m.container ().name () + " (");
    else
      stream.print (' ' + m.name () + " (");

    
    boolean firstTime = true;
    Enumeration e = m.parameters ().elements ();
    while (e.hasMoreElements ())
    {
      if (firstTime)
        firstTime = false;
      else
        stream.print (", ");
      ParameterEntry parm = (ParameterEntry)e.nextElement ();

      writeParmType (parm.type (), parm.passType ());

      
      stream.print (' ' + parm.name ());
    }

    
    if (m.contexts ().size () > 0)
    {
      if (!firstTime)
        stream.print (", ");
      stream.print ("org.omg.CORBA.Context $context");
    }

    
    if (m.exceptions ().size () > 0)
    {
      stream.print (") throws ");
      e = m.exceptions ().elements ();
      firstTime = true;
      while (e.hasMoreElements ())
      {
        if (firstTime)
          firstTime = false;
        else
          stream.print (", ");
        stream.print (Util.javaName ((SymtabEntry)e.nextElement ()));
      }
    }
    else
      stream.print (')');
  } 

  
  protected void writeParmType (SymtabEntry parm, int passType)
  {
    if (passType != ParameterEntry.In)
    {
      parm = Util.typeOf (parm);
      stream.print (Util.holderName (parm));
    }
    else 
      
      
      stream.print (Util.javaName (parm));
  } 

  
  protected void writeDispatchCall ()
  {
    String indent = "       ";
    String fullMethodName = m.fullName ();
    if (m instanceof AttributeEntry)
    {
      
      int index = fullMethodName.lastIndexOf ('/') + 1;
      if (m.type () == null)          
        fullMethodName = fullMethodName.substring (0, index) + "_set_" + m.name ();
      else
        fullMethodName = fullMethodName.substring (0, index) + "_get_" + m.name ();
    }
    stream.println (indent + "case " + methodIndex + ":  // " + fullMethodName);
    stream.println (indent + "{");
    indent = indent + "  ";
    if (m.exceptions ().size () > 0)
    {
      stream.println (indent + "try {");
      indent = indent + "  ";
    }

    
    SymtabEntry mtype = Util.typeOf (m.type ());
    Enumeration parms = m.parameters ().elements ();
    parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm     = (ParameterEntry) parms.nextElement ();
      String         name     = parm.name ();
      String         anyName  = '_' + name;
      SymtabEntry    type     = parm.type ();
      int            passType = parm.passType ();

      if (passType == ParameterEntry.In)
        Util.writeInitializer (indent, name, "", type, writeInputStreamRead ("in", type), stream);

      else 
      {
        String holderName = Util.holderName (type);
        stream.println (indent + holderName + ' ' + name + " = new " + holderName + " ();");
        if (passType == ParameterEntry.Inout)
        {
          if (type instanceof ValueBoxEntry)
          {
            ValueBoxEntry v = (ValueBoxEntry) type;
            TypedefEntry member = ((InterfaceState) v.state ().elementAt (0)).entry;
            SymtabEntry mType = member.type ();
            if (mType instanceof PrimitiveEntry)
              stream.println (indent + name + ".value = (" + writeInputStreamRead ("in", parm.type ()) + ").value;");
            else
              stream.println (indent + name + ".value = " + writeInputStreamRead ("in", parm.type ()) + ";");
          }
          else
            stream.println (indent +  name  + ".value = " + writeInputStreamRead ("in", parm.type ()) + ";");
        }
      }
    }

    
    if (m.contexts ().size () > 0)
    {
      stream.println (indent + "org.omg.CORBA.Context $context = in.read_Context ();");
    }

    
    if (mtype != null)
      Util.writeInitializer (indent, "$result", "", mtype, stream);

    
    writeMethodCall (indent);

    parms = m.parameters ().elements ();
    boolean firstTime = true;
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      if (firstTime)
        firstTime = false;
      else
        stream.print (", ");
      stream.print (parm.name ());
    }

    
    if (m.contexts ().size () > 0)
    {
      if (!firstTime)
        stream.print (", ");
      stream.print ("$context");
    }

    stream.println (");");

    
    writeCreateReply (indent);

    
    if (mtype != null)
    {
      writeOutputStreamWrite (indent, "out", "$result", mtype, stream);
    }

    
    parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      int passType = parm.passType ();
      if (passType != ParameterEntry.In)
      {
        writeOutputStreamWrite (indent, "out", parm.name () + ".value", parm.type (), stream);
      }
    }

    
    if (m.exceptions ().size () > 0)
    {
      Enumeration exceptions = m.exceptions ().elements ();
      while (exceptions.hasMoreElements ())
      {
        indent = "         ";
        ExceptionEntry exc = (ExceptionEntry) exceptions.nextElement ();
        String fullName = Util.javaQualifiedName (exc);
        stream.println (indent + "} catch (" +  fullName + " $ex) {");
        indent = indent + "  ";
        stream.println (indent + "out = $rh.createExceptionReply ();");
        stream.println (indent + Util.helperName (exc, true) + ".write (out, $ex);"); 
      }

      indent = "         ";
      stream.println (indent + "}");
    }

    stream.println ("         break;");
    stream.println ("       }");
    stream.println ();
  } 

  
  protected void writeStubBody ( String className )
  {
    
    String methodName = Util.stripLeadingUnderscores (m.name ());
    if (m instanceof AttributeEntry)
    {
      if (m.type () == null)          
        methodName = "_set_" + methodName;
      else
        methodName = "_get_" + methodName;
    }
    if( localOptimization && !isAbstract ) {
        stream.println (ONE_INDENT + "while(true) {" );
        stream.println(TWO_INDENT + "if(!this._is_local()) {" );
    }
    stream.println(THREE_INDENT +
        "org.omg.CORBA.portable.InputStream $in = null;");
    stream.println(THREE_INDENT + "try {");
    stream.println(FOUR_INDENT + "org.omg.CORBA.portable.OutputStream $out =" +
        " _request (\"" +  methodName + "\", " + !m.oneway() + ");");

    
    
    Enumeration parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      SymtabEntry parmType = Util.typeOf (parm.type ());
      if (parmType instanceof StringEntry)
        if ((parm.passType () == ParameterEntry.In) ||
            (parm.passType () == ParameterEntry.Inout))
        {
          StringEntry string = (StringEntry)parmType;
          if (string.maxSize () != null)
          {
            stream.print (THREE_INDENT + "if (" + parm.name ());
            if (parm.passType () == ParameterEntry.Inout)
              stream.print (".value"); 
            stream.print (" == null || " + parm.name ());
            if (parm.passType () == ParameterEntry.Inout)
              stream.print (".value"); 
            stream.println (".length () > (" +
                Util.parseExpression (string.maxSize ()) + "))");
            stream.println (THREE_INDENT +
                "throw new org.omg.CORBA.BAD_PARAM (0," +
                " org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
          }
        }
    }
    

    
    parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      if (parm.passType () == ParameterEntry.In)
        writeOutputStreamWrite(FOUR_INDENT, "$out", parm.name (), parm.type (),
            stream);
      else if (parm.passType () == ParameterEntry.Inout)
        writeOutputStreamWrite(FOUR_INDENT, "$out", parm.name () + ".value",
            parm.type (), stream);
    }

    
    if (m.contexts ().size () > 0)
    {
      stream.println(FOUR_INDENT + "org.omg.CORBA.ContextList $contextList =" +
         "_orb ().create_context_list ();");

      for (int cnt = 0; cnt < m.contexts ().size (); cnt++)
      {
          stream.println(FOUR_INDENT +
             "$contextList.add (\"" + m.contexts (). elementAt (cnt) + "\");");
      }
      stream.println(FOUR_INDENT +
          "$out.write_Context ($context, $contextList);");
    }

    
    stream.println (FOUR_INDENT + "$in = _invoke ($out);");

    SymtabEntry mtype = m.type ();
    if (mtype != null)
      Util.writeInitializer (FOUR_INDENT, "$result", "", mtype,
          writeInputStreamRead ("$in", mtype), stream);

    
    parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      if (parm.passType () != ParameterEntry.In)
      {
        if (parm.type () instanceof ValueBoxEntry)
        {
          ValueBoxEntry v = (ValueBoxEntry) parm.type ();
          TypedefEntry member =
              ((InterfaceState) v.state ().elementAt (0)).entry;
          SymtabEntry mType = member.type ();
          if (mType instanceof PrimitiveEntry)
            stream.println(FOUR_INDENT +  parm.name () +
                ".value = (" + writeInputStreamRead ("$in", parm.type ()) +
                ").value;");
          else
            stream.println(FOUR_INDENT +  parm.name () +
                ".value = " + writeInputStreamRead ("$in", parm.type ()) +";");
        }
        else
          stream.println (FOUR_INDENT +  parm.name () + ".value = " +
              writeInputStreamRead ("$in", parm.type ()) + ";");
      }
    }
    
    
    parms = m.parameters ().elements ();
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      SymtabEntry parmType = Util.typeOf (parm.type ());
      if (parmType instanceof StringEntry)
        if ((parm.passType () == ParameterEntry.Out) ||
            (parm.passType () == ParameterEntry.Inout))
        {
          StringEntry string = (StringEntry)parmType;
          if (string.maxSize () != null)
          {
            stream.print (FOUR_INDENT + "if (" + parm.name () +
                ".value.length ()");
            stream.println ("         > (" +
                Util.parseExpression (string.maxSize ()) + "))");
            stream.println (FIVE_INDENT + "throw new org.omg.CORBA.MARSHAL(0,"+
                "org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
          }
        }
    }
    if (mtype instanceof StringEntry)
    {
      StringEntry string = (StringEntry)mtype;
      if (string.maxSize () != null)
      {
        stream.println(FOUR_INDENT + "if ($result.length () > (" +
            Util.parseExpression (string.maxSize ()) + "))");
        stream.println (FIVE_INDENT + "throw new org.omg.CORBA.MARSHAL (0," +
            " org.omg.CORBA.CompletionStatus.COMPLETED_NO);");
      }
    }
    

    
    if (mtype != null) {
      stream.println(FOUR_INDENT + "return $result;");
    } else {
      stream.println(FOUR_INDENT + "return;");
    }

    
    stream.println(THREE_INDENT +
        "} catch (org.omg.CORBA.portable.ApplicationException " + "$ex) {");
    stream.println(FOUR_INDENT + "$in = $ex.getInputStream ();");
    stream.println(FOUR_INDENT + "String _id = $ex.getId ();");

    if (m.exceptions ().size () > 0)
    {
      Enumeration exceptions = m.exceptions ().elements ();
      boolean firstExc = true;
      while (exceptions.hasMoreElements ())
      {
        ExceptionEntry exc = (ExceptionEntry)exceptions.nextElement ();
        if (firstExc)
        {
          stream.print(FOUR_INDENT + "if ");
          firstExc = false;
        }
        else
          stream.print(FOUR_INDENT + "else if ");

        stream.println( "(_id.equals (\"" + exc.repositoryID ().ID () + "\"))");
        stream.println (FIVE_INDENT + "throw " +
            Util.helperName ((SymtabEntry)exc, false) + ".read ($in);");
      }
      stream.println(FOUR_INDENT + "else");
      stream.println(FIVE_INDENT + "throw new org.omg.CORBA.MARSHAL (_id);");
    }
    else
      stream.println(FOUR_INDENT + "throw new org.omg.CORBA.MARSHAL (_id);");

    stream.println(THREE_INDENT +
        "} catch (org.omg.CORBA.portable.RemarshalException $rm) {");
    stream.print( FOUR_INDENT );
    if (m.type () != null) 
      stream.print ("return ");
    stream.print (m.name () + " (");
    {
      
      boolean firstTime = true;
      Enumeration e = m.parameters ().elements ();
      while (e.hasMoreElements ())
      {
        if (firstTime)
          firstTime = false;
        else
          stream.print (", ");
        ParameterEntry parm = (ParameterEntry)e.nextElement ();
        stream.print (parm.name ());
      }
      
      if (m.contexts ().size () > 0)
      {
        if (!firstTime)
          stream.print (", ");
        stream.print ("$context");
      }
    }
    stream.println (TWO_INDENT + ");");
    stream.println (THREE_INDENT + "} finally {");
    stream.println (FOUR_INDENT + "_releaseReply ($in);");
    stream.println (THREE_INDENT + "}");
    if( localOptimization && !isAbstract ) {
        stream.println (TWO_INDENT + "}");
        writeStubBodyForLocalInvocation( className, methodName );
    }

  } 


  
  private void writeStubBodyForLocalInvocation( String className,
      String methodName )
  {
    stream.println (TWO_INDENT + "else {" );
    stream.println (THREE_INDENT +
        "org.omg.CORBA.portable.ServantObject _so =");
    stream.println (FOUR_INDENT + "_servant_preinvoke(\"" + methodName +
                    "\", _opsClass);" );
    stream.println(THREE_INDENT + "if (_so == null ) {");
    stream.println(FOUR_INDENT + "continue;" );
    stream.println(THREE_INDENT + "}");
    stream.println(THREE_INDENT + className + "Operations _self =" );
    stream.println(FOUR_INDENT + "(" + className + "Operations) _so.servant;");
    stream.println(THREE_INDENT + "try {" );
    Enumeration parms = m.parameters ().elements ();
    if (m instanceof AttributeEntry)
    {
        
        
        methodName = methodName.substring( ATTRIBUTE_METHOD_PREFIX_LENGTH );
    }
    boolean voidReturnType = (this.m.type() == null);
    if ( !voidReturnType ) {
        stream.println (FOUR_INDENT + Util.javaName (this.m.type ()) +
            " $result;");
    }
    if( !isValueInitializer() ) {
        if ( voidReturnType ) {
            stream.print(FOUR_INDENT + "_self." + methodName + "( " );
        } else {
            stream.print(FOUR_INDENT + "$result = _self." +
                     methodName + "( " );
        }
        while (parms.hasMoreElements ()) {
            ParameterEntry param = (ParameterEntry)parms.nextElement ();
            if( parms.hasMoreElements( ) ) {
                stream.print( " " + param.name() +  "," );
            } else  {
                stream.print( " " + param.name() );
            }
        }
        stream.print( ");" );
        stream.println( " " );
        if( voidReturnType ) {
            stream.println(FOUR_INDENT + "return;" );
        } else {
            stream.println(FOUR_INDENT + "return $result;" );
        }
    }
    stream.println(" ");
    stream.println (THREE_INDENT + "}" );
    stream.println (THREE_INDENT + "finally {" );
    stream.println (FOUR_INDENT + "_servant_postinvoke(_so);" );
    stream.println (THREE_INDENT + "}" );
    stream.println (TWO_INDENT + "}" );
    stream.println (ONE_INDENT + "}" );
  }


  protected void writeLocalStubBody (InterfaceEntry i)
  {
    
    String methodName = Util.stripLeadingUnderscores (m.name ());
    if (m instanceof AttributeEntry)
    {
      if (m.type () == null)          
        methodName = "_set_" + methodName;
      else
        methodName = "_get_" + methodName;
    }
    
    stream.println ("      org.omg.CORBA.portable.ServantObject $so = " +
                    "_servant_preinvoke (\"" + methodName + "\", " + "_opsClass);");
    
    
    
    String opsName = i.name() + "Operations";
    stream.println ("      " + opsName + "  $self = " + "(" + opsName + ") " + "$so.servant;");
    stream.println ();
    stream.println ("      try {");
    stream.print ("         ");
    if (m.type () != null) 
        stream.print ("return ");
    stream.print ("$self." + m.name () + " (");
    {
        
        boolean firstTime = true;
        Enumeration e = m.parameters ().elements ();
        while (e.hasMoreElements ())
        {
          if (firstTime)
            firstTime = false;
          else
            stream.print (", ");
          ParameterEntry parm = (ParameterEntry)e.nextElement ();
          stream.print (parm.name ());
        }
        
        if (m.contexts ().size () > 0)
        {
          if (!firstTime)
            stream.print (", ");
          stream.print ("$context");
        }
    }
    stream.println (");");
    
    
    stream.println ("      } finally {");
    stream.println ("          _servant_postinvoke ($so);");
    stream.println ("      }");
    

  } 



  
  private void writeInsert (String indent, String target, String source, SymtabEntry type, PrintWriter stream)
  {
    String typeName = type.name ();
    if (type instanceof PrimitiveEntry)
    {
      
      if (typeName.equals ("long long"))
        stream.println (indent + source + ".insert_longlong (" + target + ");");
      else if (typeName.equals ("unsigned short"))
        stream.println (indent + source + ".insert_ushort (" + target + ");");
      else if (typeName.equals ("unsigned long"))
        stream.println (indent + source + ".insert_ulong (" + target + ");");
      else if (typeName.equals ("unsigned long long"))
        stream.println (indent + source + ".insert_ulonglong (" + target + ");");
      else
        stream.println (indent + source + ".insert_" + typeName + " (" + target + ");");
    }
    else if (type instanceof StringEntry)
      stream.println (indent + source + ".insert_" + typeName + " (" + target + ");");
    else
      stream.println (indent + Util.helperName (type, true) + ".insert (" + source + ", " + target + ");"); 
  } 

  
  private void writeType (String indent, String name, SymtabEntry type, PrintWriter stream)
  {
    if (type instanceof PrimitiveEntry)
    {
      
      if (type.name ().equals ("long long"))
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_longlong));");
      else if (type.name ().equals ("unsigned short"))
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ushort));");
      else if (type.name ().equals ("unsigned long"))
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulong));");
      else if (type.name ().equals ("unsigned long long"))
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_ulonglong));");
      else
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_" + type.name () + "));");
    }
    else if (type instanceof StringEntry)
    {
      StringEntry s = (StringEntry)type;
      Expression e  = s.maxSize ();
      if (e == null)
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().create_" + type.name () + "_tc (" + Util.parseExpression (e) + "));");
     else
        stream.println (indent + name + " (org.omg.CORBA.ORB.init ().create_" + type.name () + "_tc (0));");
    }
    else
      stream.println (indent + name + '(' + Util.helperName (type, true) + ".type ());"); 
  } 

  
  private void writeExtract (String indent, String target, String source, SymtabEntry type, PrintWriter stream)
  {
    if (type instanceof PrimitiveEntry)
    {
      if (type.name ().equals ("long long"))
        stream.println (indent + target + " = " + source + ".extract_longlong ();");
      else if (type.name ().equals ("unsigned short"))
        stream.println (indent + target + " = " + source + ".extract_ushort ();");
      else if (type.name ().equals ("unsigned long"))
        stream.println (indent + target + " = " + source + ".extract_ulong ();");
      else if (type.name ().equals ("unsigned long long"))
        stream.println (indent + target + " = " + source + ".extract_ulonglong ();");
      else
        stream.println (indent + target + " = " + source + ".extract_" + type.name () + " ();");
    }
    else if (type instanceof StringEntry)
      stream.println (indent + target + " = " + source + ".extract_" + type.name () + " ();");
    else
      stream.println (indent + target + " = " + Util.helperName (type, true) + ".extract (" + source + ");"); 
  } 

  
  private String writeExtract (String source, SymtabEntry type)
  {
    String extract;
    if (type instanceof PrimitiveEntry)
    {
      if (type.name ().equals ("long long"))
        extract = source + ".extract_longlong ()";
      else if (type.name ().equals ("unsigned short"))
        extract = source + ".extract_ushort ()";
      else if (type.name ().equals ("unsigned long"))
        extract = source + ".extract_ulong ()";
      else if (type.name ().equals ("unsigned long long"))
        extract = source + ".extract_ulonglong ()";
      else
        extract = source + ".extract_" + type.name () + " ()";
    }
    else if (type instanceof StringEntry)
      extract = source + ".extract_" + type.name () + " ()";
    else
      extract = Util.helperName (type, true) + ".extract (" + source + ')'; 
    return extract;
  } 

  
  private void writeSkeletonBody ()
  {
    SymtabEntry mtype = Util.typeOf (m.type ());

    
    stream.print ("    ");
    if (mtype != null)
      stream.print ("return ");
    stream.print ("_impl." + m.name () + '(');

    
    Enumeration parms = m.parameters ().elements ();
    boolean first = true;
    while (parms.hasMoreElements ())
    {
      ParameterEntry parm = (ParameterEntry)parms.nextElement ();
      if (first)
        first = false;
      else
        stream.print (", ");
      stream.print (parm.name ());
    }
    if (m.contexts ().size () != 0)
    {
      if (!first)
        stream.print (", ");
      stream.print ("$context");
    }

    stream.println (");");
  } 

  
  protected String passType (int passType)
  {
    String type;
    switch (passType)
    {
      case ParameterEntry.Inout:
        type = "org.omg.CORBA.ARG_INOUT.value";
        break;
      case ParameterEntry.Out:
        type = "org.omg.CORBA.ARG_OUT.value";
        break;
      case ParameterEntry.In:
      default:
        type = "org.omg.CORBA.ARG_IN.value";
        break;
    }
    return type;
  } 

  
  protected void serverMethodName (String name)
  {
    realName = (name == null) ? "" : name;
  } 

  
  private void writeOutputStreamWrite (String indent, String oStream, String name, SymtabEntry type, PrintWriter stream)
  {
    String typeName = type.name ();
    stream.print (indent);
    if (type instanceof PrimitiveEntry)
    {
      if (typeName.equals ("long long"))
        stream.println (oStream + ".write_longlong (" + name +");");
      else if (typeName.equals ("unsigned short"))
        stream.println (oStream + ".write_ushort (" + name + ");");
      else if (typeName.equals ("unsigned long"))
        stream.println (oStream + ".write_ulong (" + name + ");");
      else if (typeName.equals ("unsigned long long"))
        stream.println (oStream + ".write_ulonglong (" + name + ");");
      else
        stream.println (oStream + ".write_" + typeName + " (" + name + ");");
    }
    else if (type instanceof StringEntry)
      stream.println (oStream + ".write_" + typeName + " (" + name + ");");
    else if (type instanceof SequenceEntry)
      stream.println (oStream + ".write_" + type.type().name() + " (" + name + ");");
    else if (type instanceof ValueBoxEntry)
    {
      ValueBoxEntry v = (ValueBoxEntry) type;
      TypedefEntry member = ((InterfaceState) v.state ().elementAt (0)).entry;
      SymtabEntry mType = member.type ();

      
      if (mType instanceof PrimitiveEntry && name.endsWith (".value"))
        stream.println (Util.helperName (type, true) + ".write (" + oStream + ", "  
        + " new " + Util.javaQualifiedName (type) + " (" + name + "));"); 
      else
        stream.println (Util.helperName (type, true) + ".write (" + oStream + ", " + name + ");"); 
    }
    else if (type instanceof ValueEntry)
        stream.println (Util.helperName (type, true) + ".write (" + oStream + ", " + name + ");"); 
    else
      stream.println (Util.helperName (type, true) + ".write (" + oStream + ", " + name + ");"); 
  } 

  
  private String writeInputStreamRead (String source, SymtabEntry type)
  {
    String read = "";
    if (type instanceof PrimitiveEntry)
    {
      if (type.name ().equals ("long long"))
        read = source + ".read_longlong ()";
      else if (type.name ().equals ("unsigned short"))
        read = source + ".read_ushort ()";
      else if (type.name ().equals ("unsigned long"))
        read = source + ".read_ulong ()";
      else if (type.name ().equals ("unsigned long long"))
        read = source + ".read_ulonglong ()";
      else
        read = source + ".read_" + type.name () + " ()";
    }
    else if (type instanceof StringEntry)
      read = source + ".read_" + type.name () + " ()";
    else
      read = Util.helperName (type, true) + ".read (" + source + ')'; 
    return read;
  } 

  
  protected void writeMethodCall (String indent)
  {
    SymtabEntry mtype = Util.typeOf (m.type ());
    if (mtype == null)
      stream.print (indent + "this." + m.name () + " (");
    else
      stream.print (indent + "$result = this." + m.name () + " (");
  } 

  
  protected void writeCreateReply(String indent){
    stream.println(indent + "out = $rh.createReply();");
  }

  protected int           methodIndex = 0;
  protected String        realName    = "";
  protected Hashtable     symbolTable = null;
  protected MethodEntry   m           = null;
  protected PrintWriter   stream      = null;
  protected boolean localOptimization = false;
  protected boolean isAbstract        = false;
} 
