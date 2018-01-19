


package xxxx;




public class GenFactory implements com.sun.tools.corba.se.idl.GenFactory
{

  public com.sun.tools.corba.se.idl.AttributeGen createAttributeGen ()
  {
    if (Util.corbaLevel (2.4f, 99.0f)) 
      return new AttributeGen24 ();
    else
      return new AttributeGen ();
  } 

  public com.sun.tools.corba.se.idl.ConstGen createConstGen ()
  {
    return new ConstGen ();
  } 

  public com.sun.tools.corba.se.idl.NativeGen createNativeGen ()
  {
    return new NativeGen ();
  } 

  public com.sun.tools.corba.se.idl.EnumGen createEnumGen ()
  {
    return new EnumGen ();
  } 

  public com.sun.tools.corba.se.idl.ExceptionGen createExceptionGen ()
  {
    return new ExceptionGen ();
  } 

  public com.sun.tools.corba.se.idl.ForwardGen createForwardGen ()
  {
    return null;
  } 

  public com.sun.tools.corba.se.idl.ForwardValueGen createForwardValueGen ()
  {
    return null;
  } 

  public com.sun.tools.corba.se.idl.IncludeGen createIncludeGen ()
  {
    return null;
  } 

  public com.sun.tools.corba.se.idl.InterfaceGen createInterfaceGen ()
  {
    return new InterfaceGen ();
  } 

  public com.sun.tools.corba.se.idl.ValueGen createValueGen ()
  {
    if (Util.corbaLevel (2.4f, 99.0f)) 
      return new ValueGen24 ();
    else
      return new ValueGen ();
  } 

  public com.sun.tools.corba.se.idl.ValueBoxGen createValueBoxGen ()
  {
    if (Util.corbaLevel (2.4f, 99.0f)) 
      return new ValueBoxGen24 ();
    else
      return new ValueBoxGen ();
  } 

  public com.sun.tools.corba.se.idl.MethodGen createMethodGen ()
  {
    if (Util.corbaLevel (2.4f, 99.0f)) 
      return new MethodGen24 ();
    else
      return new MethodGen ();
  } 

  public com.sun.tools.corba.se.idl.ModuleGen createModuleGen ()
  {
    return new ModuleGen ();
  } 

  public com.sun.tools.corba.se.idl.ParameterGen createParameterGen ()
  {
    return null;
  } 

  public com.sun.tools.corba.se.idl.PragmaGen createPragmaGen ()
  {
    return null;
  } 

  public com.sun.tools.corba.se.idl.PrimitiveGen createPrimitiveGen ()
  {
    return new PrimitiveGen ();
  } 

  public com.sun.tools.corba.se.idl.SequenceGen createSequenceGen ()
  {
    return new SequenceGen ();
  } 

  public com.sun.tools.corba.se.idl.StringGen createStringGen ()
  {
    return new StringGen ();
  } 

  public com.sun.tools.corba.se.idl.StructGen createStructGen ()
  {
    return new StructGen ();
  } 

  public com.sun.tools.corba.se.idl.TypedefGen createTypedefGen ()
  {
    return new TypedefGen ();
  } 

  public com.sun.tools.corba.se.idl.UnionGen createUnionGen ()
  {
    return new UnionGen ();
  } 
} 
