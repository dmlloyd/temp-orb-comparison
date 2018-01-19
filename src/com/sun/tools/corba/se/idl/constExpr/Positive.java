


package com.sun.tools.corba.se.idl.constExpr;




public class Positive extends UnaryExpr
{
  protected Positive (Expression operand)
  {
    super ("+", operand);
  } 

  public Object evaluate () throws EvaluationException
  {
    try
    {
      Number op = (Number)operand ().evaluate ();

      if (op instanceof Float || op instanceof Double)
        value (new Double (+op.doubleValue ()));
      else
      {
        
        
        value (((BigInteger)op).multiply (BigInteger.valueOf (((BigInteger)op).signum ())));
        
      }
    }
    catch (ClassCastException e)
    {
      String[] parameters = {Util.getMessage ("EvaluationException.pos"), operand ().value ().getClass ().getName ()};
      throw new EvaluationException (Util.getMessage ("EvaluationException.2", parameters));
    }
    return value ();
  } 
} 
