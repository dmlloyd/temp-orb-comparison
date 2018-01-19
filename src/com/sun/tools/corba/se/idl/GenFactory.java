


package com.sun.tools.corba.se.idl;




public interface GenFactory
{
  public AttributeGen    createAttributeGen ();
  public ConstGen        createConstGen ();
  public EnumGen         createEnumGen ();
  public ExceptionGen    createExceptionGen ();
  public ForwardGen      createForwardGen ();
  public ForwardValueGen createForwardValueGen ();
  public IncludeGen      createIncludeGen ();
  public InterfaceGen    createInterfaceGen ();
  public ValueGen        createValueGen ();
  public ValueBoxGen     createValueBoxGen ();
  public MethodGen       createMethodGen ();
  public ModuleGen       createModuleGen ();
  public NativeGen       createNativeGen ();
  public ParameterGen    createParameterGen ();
  public PragmaGen       createPragmaGen ();
  public PrimitiveGen    createPrimitiveGen ();
  public SequenceGen     createSequenceGen ();
  public StringGen       createStringGen ();
  public StructGen       createStructGen ();
  public TypedefGen      createTypedefGen ();
  public UnionGen        createUnionGen ();
} 
