


package xxxx;




public interface SymtabFactory
{
  AttributeEntry attributeEntry ();
  AttributeEntry attributeEntry (InterfaceEntry container, IDLID id);

  ConstEntry constEntry ();
  ConstEntry constEntry (SymtabEntry container, IDLID id);

  NativeEntry nativeEntry ();
  NativeEntry nativeEntry (SymtabEntry container, IDLID id);

  EnumEntry enumEntry ();
  EnumEntry enumEntry (SymtabEntry container, IDLID id);

  ExceptionEntry exceptionEntry ();
  ExceptionEntry exceptionEntry (SymtabEntry container, IDLID id);

  ForwardEntry forwardEntry ();
  ForwardEntry forwardEntry (ModuleEntry container, IDLID id);

  ForwardValueEntry forwardValueEntry ();
  ForwardValueEntry forwardValueEntry (ModuleEntry container, IDLID id);

  IncludeEntry includeEntry ();
  IncludeEntry includeEntry (SymtabEntry container);

  InterfaceEntry interfaceEntry ();
  InterfaceEntry interfaceEntry (ModuleEntry container, IDLID id);

  ValueEntry valueEntry ();
  ValueEntry valueEntry (ModuleEntry container, IDLID id);

  ValueBoxEntry valueBoxEntry ();
  ValueBoxEntry valueBoxEntry (ModuleEntry container, IDLID id);

  MethodEntry methodEntry ();
  MethodEntry methodEntry (InterfaceEntry container, IDLID id);

  ModuleEntry moduleEntry ();
  ModuleEntry moduleEntry (ModuleEntry container, IDLID id);

  ParameterEntry parameterEntry ();
  ParameterEntry parameterEntry (MethodEntry container, IDLID id);

  PragmaEntry pragmaEntry ();
  PragmaEntry pragmaEntry (SymtabEntry container);

  PrimitiveEntry primitiveEntry ();
  
  PrimitiveEntry primitiveEntry (String name);

  SequenceEntry sequenceEntry ();
  SequenceEntry sequenceEntry (SymtabEntry container, IDLID id);

  StringEntry stringEntry ();

  StructEntry structEntry ();
  StructEntry structEntry (SymtabEntry container, IDLID id);

  TypedefEntry typedefEntry ();
  TypedefEntry typedefEntry (SymtabEntry container, IDLID id);

  UnionEntry unionEntry ();
  UnionEntry unionEntry (SymtabEntry container, IDLID id);
} 
