

package com.sun.jndi.cosnaming;

import javax.naming.spi.InitialContextFactory;
import javax.naming.*;

import java.util.Hashtable;



public class CNCtxFactory implements InitialContextFactory {

  

  public Context getInitialContext(Hashtable<?,?> env) throws NamingException {
      return new CNCtx(env);
  }
}
