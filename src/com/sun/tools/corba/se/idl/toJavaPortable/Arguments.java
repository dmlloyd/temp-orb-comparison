


package com.sun.tools.corba.se.idl.toJavaPortable;









import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.io.File;

import com.sun.tools.corba.se.idl.InvalidArgument;


public class Arguments extends com.sun.tools.corba.se.idl.Arguments
{
  
  public Arguments ()
  {
    super ();
    corbaLevel = 2.4f;
  } 

  
  protected void parseOtherArgs (String[] args,
    Properties properties) throws InvalidArgument
  {
    String skeletonPattern = null ;
    String tiePattern = null ;

    
    packages.put ("CORBA", "org.omg"); 
    packageFromProps (properties);

    
    
    
    try
    {
      Vector unknownArgs = new Vector ();

      
      for (int i = 0; i < args.length; ++i)
      {
        String lcArg = args[i].toLowerCase ();

        if (lcArg.charAt (0) != '-' && lcArg.charAt (0) != '/')
          throw new InvalidArgument (args[i]);
        if (lcArg.charAt (0) == '-' ) {
            lcArg = lcArg.substring (1);
        }

        
        if (lcArg.startsWith ("f"))
        {
          
          if (lcArg.equals ("f"))
            lcArg = 'f' + args[++i].toLowerCase ();

          
          
          

          if (lcArg.equals ("fclient"))
          {
            emit = ((emit == Server || emit == All) ? All : Client);
          }
          else if (lcArg.equals ("fserver"))
          {
            emit = ((emit == Client || emit == All) ? All : Server);
            TIEServer = false;
          }
          else if (lcArg.equals ("fall"))
          {
            emit = All;
            TIEServer = false;
            
            
          }
          else if (lcArg.equals ("fservertie"))
          {
            emit = ((emit == Client || emit == All) ? All : Server);
            TIEServer = true;
          }
          else if (lcArg.equals ("falltie"))
          {
            emit = All;
            TIEServer = true;
          }
          else
            i = collectUnknownArg (args, i, unknownArgs);
        }
        else if (lcArg.equals ("pkgtranslate"))
        {
          if (i + 2 >= args.length)
            throw new InvalidArgument( args[i] ) ;

          String orig = args[++i] ;
          String trans = args[++i] ;
          checkPackageNameValid( orig ) ;
          checkPackageNameValid( trans ) ;
          if (orig.equals( "org" ) || orig.startsWith( "org.omg" ))
              throw new InvalidArgument( args[i] ) ;
          orig = orig.replace( '.', '/' ) ;
          trans = trans.replace( '.', '/' ) ;
          packageTranslation.put( orig, trans ) ;
        }
        
        else if (lcArg.equals ("pkgprefix"))
        {
          if (i + 2 >= args.length)
            throw new InvalidArgument (args[i]);

          String type = args[++i];
          String pkg = args[++i];
          checkPackageNameValid( type ) ;
          checkPackageNameValid( pkg ) ;
          packages.put (type, pkg);
        }
        
        else if (lcArg.equals ("td"))  
        {
          if (i + 1 >= args.length)
            throw new InvalidArgument (args[i]);
          String trgtDir = args[++i];
          if (trgtDir.charAt (0) == '-')
            throw new InvalidArgument (args[i - 1]);
          else
          {
            targetDir = trgtDir.replace ('/', File.separatorChar);
            if (targetDir.charAt (targetDir.length () - 1) != File.separatorChar)
              targetDir = targetDir + File.separatorChar;
          }
        }
        
        else if (lcArg.equals ("sep"))
        {
          if (i + 1 >= args.length)
            throw new InvalidArgument (args[i]);
          separator = args[++i];
        }
        
        else if (lcArg.equals ("oldimplbase")){
            POAServer = false;
        }
        else if (lcArg.equals("skeletonname")){
          if (i + 1 >= args.length)
            throw new InvalidArgument (args[i]);
          skeletonPattern = args[++i];
        }
        else if (lcArg.equals("tiename")){
          if (i + 1 >= args.length)
            throw new InvalidArgument (args[i]);
          tiePattern = args[++i];
        }
        else if (lcArg.equals("localoptimization")) {
            LocalOptimization = true;
        }
        else i = collectUnknownArg (args, i, unknownArgs);
      }

      
      if (unknownArgs.size () > 0)
      {
        String [] otherArgs = new String [unknownArgs.size ()];
        unknownArgs.copyInto (otherArgs);
        
        super.parseOtherArgs (otherArgs, properties);
      }

      setDefaultEmitter(); 
      setNameModifiers( skeletonPattern, tiePattern ) ;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      
      
      
      throw new InvalidArgument (args[args.length - 1]);
    }
  } 

  
  protected int collectUnknownArg (String[] args, int i, Vector unknownArgs)
  {
    unknownArgs.addElement (args [i]);
    ++i;
    while (i < args.length && args[i].charAt (0) != '-' && args[i].charAt (0) != '/')
      unknownArgs.addElement (args[i++]);
    return --i;
  } 

  
  
  protected void packageFromProps (Properties props) throws InvalidArgument
  {
    Enumeration propsEnum = props.propertyNames ();
    while (propsEnum.hasMoreElements ())
    {
      String prop = (String)propsEnum.nextElement ();
      if (prop.startsWith ("PkgPrefix."))
      {
        String type = prop.substring (10);
        String pkg = props.getProperty (prop);
        checkPackageNameValid( pkg ) ;
        checkPackageNameValid( type ) ;
        packages.put (type, pkg);
      }
    }
  } 

  
  protected void setDefaultEmitter () {
      
      if (emit == None) emit = Client;
  }

  protected void setNameModifiers( String skeletonPattern,
    String tiePattern ) {
    if (emit>Client) {
        String tp ;
        String sp ;

        if (skeletonPattern != null)
            sp = skeletonPattern ;
        else if (POAServer)
            sp = "%POA" ;
        else
            sp = "_%ImplBase" ;

        if (tiePattern != null)
            tp = tiePattern ;
        else if (POAServer)
            tp = "%POATie" ;
        else
            tp = "%_Tie" ;

        skeletonNameModifier = new NameModifierImpl( sp ) ;
        tieNameModifier = new NameModifierImpl( tp ) ;
    }
  }

  
  private void checkPackageNameValid (String name) throws InvalidArgument
  {
    if (name.charAt (0) == '.')
      throw new InvalidArgument (name);
    for (int i = 0; i < name.length ();++i)
      if (name.charAt (i) == '.')
      {
        if (i == name.length () - 1 || !Character.isJavaIdentifierStart (name.charAt (++i)))
          throw new InvalidArgument (name);
      }
      else if (!Character.isJavaIdentifierPart (name.charAt (i)))
        throw new InvalidArgument (name);
  } 

  

  
  
  
  public Hashtable packages         = new Hashtable ();

  public    String separator        = null;

  public static final int
    None   = 0,
    Client = 1,
    Server = 2,
    All    = 3;
  public int       emit              = None;
  public boolean   TIEServer         = false;
  public boolean   POAServer         = true;
  
  
  
  public boolean   LocalOptimization = false;
  public NameModifier skeletonNameModifier   = null ;
  public NameModifier tieNameModifier   = null ;

  
  
  
  public Hashtable packageTranslation = new Hashtable() ;

  public String    targetDir        = "";     
} 
