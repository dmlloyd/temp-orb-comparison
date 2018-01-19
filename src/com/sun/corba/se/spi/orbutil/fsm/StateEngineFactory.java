

package com.sun.corba.se.spi.orbutil.fsm;



public class StateEngineFactory {
    private StateEngineFactory() {}

    public static StateEngine create()
    {
        return new StateEngineImpl() ;
    }
}
