


package com.sun.tools.corba.se.idl.constExpr;



import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class BooleanOr extends BinaryExpr
{
  protected BooleanOr (Expression leftOperand, Expression rightOperand)
  {
    super ("||", leftOperand, rightOperand);
  } 

  public Object evaluate () throws EvaluationException
  {
    try
    {
      Object tmpL = left ().evaluate ();
      Object tmpR = right ().evaluate ();
      Boolean l;
      Boolean r;

      
      
      
      
      if (tmpL instanceof Number)
      {
        if (tmpL instanceof BigInteger)
          l = new Boolean (((BigInteger)tmpL).compareTo (zero) != 0);
        else
          l = new Boolean (((Number)tmpL).longValue () != 0);
      }
      else
        l = (Boolean)tmpL;
      
      
      
      
      if (tmpR instanceof Number)
      {
        if (tmpR instanceof BigInteger)
          r = new Boolean (((BigInteger)tmpR).compareTo (BigInteger.valueOf (0)) != 0);
        else
          r = new Boolean (((Number)tmpR).longValue () != 0);
      }
      else
        r = (Boolean)tmpR;
      value (new Boolean (l.booleanValue () || r.booleanValue ()));
    }
    catch (ClassCastException e)
    {
      String[] parameters = {Util.getMessage ("EvaluationException.booleanOr"), left ().value ().getClass ().getName (), right ().value ().getClass ().getName ()};
      throw new EvaluationException (Util.getMessage ("EvaluationException.1", parameters));
    }
    return value ();
  } 
} 
