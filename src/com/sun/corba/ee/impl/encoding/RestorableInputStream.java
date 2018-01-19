

package com.sun.corba.ee.impl.encoding;


interface RestorableInputStream
{
    Object createStreamMemento();

    void restoreInternalState(Object streamMemento);
}
