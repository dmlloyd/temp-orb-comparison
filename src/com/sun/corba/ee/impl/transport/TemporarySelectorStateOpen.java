


package com.sun.corba.ee.impl.transport;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.sun.corba.ee.spi.transport.TemporarySelectorState;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.trace.Transport;




@Transport
public class TemporarySelectorStateOpen implements TemporarySelectorState {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    public TemporarySelectorStateOpen() {
    }

    @Transport
    public int select(Selector theSelector, long theTimeout) throws IOException {
        int result;
        if (theSelector.isOpen()) {
            if (theTimeout > 0) {
                result = theSelector.select(theTimeout);
            } else {
                throw wrapper.temporarySelectorSelectTimeoutLessThanOne(
                    theSelector, theTimeout);
            }
        } else {
            throw new TemporarySelectorClosedException(
                "Selector " + theSelector.toString() + " is closed.");
        }

        return result;
    }

    @Transport
    public SelectionKey registerChannel(Selector theSelector, 
        SelectableChannel theSelectableChannel, int theOps) throws IOException {

        SelectionKey key;
        if (theSelector.isOpen()) {
            key = theSelectableChannel.register(theSelector, theOps);
        } else {
            throw new TemporarySelectorClosedException("Selector " +
                                                        theSelector.toString() +
                                                       " is closed.");
        }
        return key;
    }

    @Transport
    public TemporarySelectorState cancelKeyAndFlushSelector(Selector theSelector,
                              SelectionKey theSelectionKey) throws IOException {

        if (theSelectionKey != null) {
            theSelectionKey.cancel();
        }

        if (theSelector.isOpen()) {
            theSelector.selectNow();
        } else {
            throw new TemporarySelectorClosedException(
                "Selector " + theSelector.toString() + " is closed."); }

        return this;
    }

    @Transport
    public TemporarySelectorState close(Selector theSelector) throws IOException {
        theSelector.close();
        return new TemporarySelectorStateClosed();
    }

    @Transport
    public TemporarySelectorState removeSelectedKey(Selector theSelector,
                              SelectionKey theSelectionKey) throws IOException {
        if (theSelector.isOpen()) {
            theSelector.selectedKeys().remove(theSelectionKey);
        } else {
            throw new TemporarySelectorClosedException("Selector " +
                                                        theSelector.toString() +
                                                       " is closed.");
        }
        return this;
    }
}
