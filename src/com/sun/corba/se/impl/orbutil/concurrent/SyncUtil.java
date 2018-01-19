

package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.concurrent.Sync ;

public class SyncUtil {
    private SyncUtil() {}

    
    public static void acquire( Sync sync )
    {
        boolean held = false ;
        while (!held) {
            try {
                sync.acquire() ;
                held = true ;
            } catch (InterruptedException exc) {
                held = false ;
            }
        }
    }
}
