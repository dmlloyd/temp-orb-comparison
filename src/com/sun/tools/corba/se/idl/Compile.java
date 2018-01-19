


package com.sun.tools.corba.se.idl;








public class Compile
{
  public Compile ()
  {
    noPragma.init (preprocessor);
    preprocessor.registerPragma (noPragma);

    
    
    ParseException.detected  = false;
    SymtabEntry.includeStack = new java.util.Stack ();
    SymtabEntry.setEmit      = true;
    
    Parser.repIDStack        = new java.util.Stack (); 
  } 

  public static void main (String[] args)
  {
    (new Compile ()).start (args);
  } 

  protected Factories factories ()
  {
    return new Factories ();
  } 

  protected void registerPragma (PragmaHandler handler)
  {
    handler.init (preprocessor);
    preprocessor.registerPragma (handler);
  } 

  
  protected void init (String[] args) throws InvalidArgument
  {
    initFactories ();
    arguments.parseArgs (args);
    initGenerators ();
    parser = new Parser (preprocessor, arguments, overrideNames, symbolTable, symtabFactory, exprFactory, keywords);
    preprocessor.init (parser);
    parser.includes = includes;
    parser.includeEntries = includeEntries;
  } 

  
  protected Enumeration parse () throws IOException
  {
    if (arguments.verbose)
      System.out.println (Util.getMessage ("Compile.parsing", arguments.file));
    parser.parse (arguments.file);
    if ( !ParseException.detected )
    {
      parser.forwardEntryCheck();

      
      
    }
    if (arguments.verbose)
      System.out.println (Util.getMessage ("Compile.parseDone", arguments.file));
    if (ParseException.detected)
    {
      symbolTable = null;
      emitList    = null;
    }
    else
    {
      symbolTable = parser.symbolTable;
      emitList    = parser.emitList.elements ();
    }
    return emitList;
  } 

  
  protected void generate () throws IOException
  {
    

    if (ParseException.detected)
      emitList = null;
    else
      emitList = parser.emitList.elements ();
    if (emitList != null)
    {
      
      if (arguments.verbose)
        System.out.println ();
      while (emitList.hasMoreElements ())
      {
        SymtabEntry entry = (SymtabEntry)emitList.nextElement ();
        if (arguments.verbose)
          if (entry.generator () instanceof Noop)
            ; 
          else if (entry.module () . equals (""))
            System.out.println (Util.getMessage ("Compile.generating", entry.name ()));
          else
            System.out.println (Util.getMessage ("Compile.generating", entry.module () + '/' + entry.name ()));
        entry.generate (symbolTable, null);
        if (arguments.verbose)
          if (entry.generator () instanceof Noop)
            ; 
          else if (entry.module () . equals (""))
            System.out.println (Util.getMessage ("Compile.genDone", entry.name ()));
          else
            System.out.println (Util.getMessage ("Compile.genDone", entry.module () + '/' + entry.name ()));
      }
    }
  } 

  
  public void start (String[] args)
  {
    try
    {
      init (args);
      if (arguments.versionRequest) 
        displayVersion ();
      else
      {
        parse ();
        generate ();
      }
    }
    catch (InvalidArgument e)
    {
      System.err.println (e);
    }
    catch (IOException e)
    {
      System.err.println (e);
    }
  } 

  private void initFactories ()
  {
    
    Factories factories = factories ();
    if (factories == null) factories = new Factories ();

    
    Arguments tmpArgs = factories.arguments ();
    if (tmpArgs == null)
      arguments = new Arguments ();
    else
      arguments = tmpArgs;

    
    SymtabFactory tmpSTF = factories.symtabFactory ();
    if (tmpSTF == null)
      symtabFactory = new DefaultSymtabFactory ();
    else
      symtabFactory = tmpSTF;

    
    ExprFactory tmpExpF = factories.exprFactory ();
    if (tmpExpF == null)
      exprFactory = new DefaultExprFactory ();
    else
      exprFactory = tmpExpF;

    
    GenFactory tmpGenF = factories.genFactory ();
    if (tmpGenF == null)
      genFactory = noop;
    else
      genFactory = tmpGenF;

    
    keywords = factories.languageKeywords ();
    if (keywords == null)
      keywords = new String[0];
  } 

  private void initGenerators ()
  {
    AttributeGen agen = genFactory.createAttributeGen ();
    AttributeEntry.attributeGen = agen == null ? noop : agen;

    ConstGen cgen = genFactory.createConstGen ();
    ConstEntry.constGen = cgen == null ? noop : cgen;

    EnumGen egen = genFactory.createEnumGen ();
    EnumEntry.enumGen = egen == null ? noop : egen;

    ExceptionGen exgen = genFactory.createExceptionGen ();
    ExceptionEntry.exceptionGen = exgen == null ? noop : exgen;

    ForwardGen fgen = genFactory.createForwardGen ();
    ForwardEntry.forwardGen = fgen == null ? noop : fgen;

    ForwardValueGen fvgen = genFactory.createForwardValueGen ();
    ForwardValueEntry.forwardValueGen = fvgen == null ? noop : fvgen;

    IncludeGen ingen = genFactory.createIncludeGen ();
    IncludeEntry.includeGen = ingen == null ? noop : ingen;

    InterfaceGen igen = genFactory.createInterfaceGen ();
    InterfaceEntry.interfaceGen = igen == null ? noop : igen;

    ValueGen vgen = genFactory.createValueGen ();
    ValueEntry.valueGen = vgen == null ? noop : vgen;

    ValueBoxGen vbgen = genFactory.createValueBoxGen ();
    ValueBoxEntry.valueBoxGen = vbgen == null ? noop : vbgen;

    MethodGen mgen = genFactory.createMethodGen ();
    MethodEntry.methodGen = mgen == null ? noop : mgen;

    ModuleGen modgen = genFactory.createModuleGen ();
    ModuleEntry.moduleGen = modgen == null ? noop : modgen;

    NativeGen ngen = genFactory.createNativeGen ();
    NativeEntry.nativeGen = ngen == null ? noop : ngen;

    ParameterGen pgen = genFactory.createParameterGen ();
    ParameterEntry.parameterGen = pgen == null ? noop : pgen;

    PragmaGen prgen = genFactory.createPragmaGen ();
    PragmaEntry.pragmaGen = prgen == null ? noop : prgen;

    PrimitiveGen primgen = genFactory.createPrimitiveGen ();
    PrimitiveEntry.primitiveGen = primgen == null ? noop : primgen;

    SequenceGen seqgen = genFactory.createSequenceGen ();
    SequenceEntry.sequenceGen = seqgen == null ? noop : seqgen;

    StringGen strgen = genFactory.createStringGen ();
    StringEntry.stringGen = strgen == null ? noop : strgen;

    StructGen sgen = genFactory.createStructGen ();
    StructEntry.structGen = sgen == null ? noop : sgen;

    TypedefGen tgen = genFactory.createTypedefGen ();
    TypedefEntry.typedefGen = tgen == null ? noop : tgen;

    UnionGen ugen = genFactory.createUnionGen ();
    UnionEntry.unionGen = ugen == null ? noop : ugen;
  } 

  
  protected void displayVersion ()
  {
    String message = Util.getMessage ("Version.product", Util.getMessage ("Version.number"));
    System.out.println (message);
  }

  
  public Arguments arguments           = null;
  
  protected Hashtable overrideNames    = new Hashtable ();
  
  protected Hashtable symbolTable      = new Hashtable ();
  
  protected Vector includes            = new Vector ();
  
  protected Vector includeEntries      = new Vector ();
  static  Noop          noop           = new Noop ();
  private GenFactory    genFactory     = null;
  private SymtabFactory symtabFactory  = null;
  private ExprFactory   exprFactory    = null;
  private Parser        parser         = null;
          Preprocessor  preprocessor   = new Preprocessor ();
  private NoPragma      noPragma       = new NoPragma ();
  private Enumeration   emitList       = null;
  private String[]      keywords       = null;
} 
