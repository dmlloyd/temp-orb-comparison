
package com.sun.corba.se.impl.encoding;


interface RestorableInputStream
{
    Object createStreamMemento();

    void restoreInternalState(Object streamMemento);
}
