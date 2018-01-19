

package com.sun.corba.ee.impl.transport;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.sun.corba.ee.spi.transport.TemporarySelectorState;

import com.sun.corba.ee.spi.trace.Transport;




@Transport
public class TemporarySelectorStateClosed implements TemporarySelectorState {

    
    public TemporarySelectorStateClosed() {
    }

    public int select(Selector theSelector, long theTimeout) throws IOException {
        String selectorToString = getSelectorToString(theSelector);
        throw new TemporarySelectorClosedException("Temporary Selector " +
                                                    selectorToString +
                                                   " closed");
    }

    public SelectionKey registerChannel(Selector theSelector,
                                        SelectableChannel theSelectableChannel,
                                        int theOps) throws IOException {
        String selectorToString = getSelectorToString(theSelector);
        throw new TemporarySelectorClosedException("Temporary Selector " +
                                                    selectorToString +
                                                   " closed");
    }

    public TemporarySelectorState cancelKeyAndFlushSelector(Selector theSelector,
                              SelectionKey theSelectionKey) throws IOException {
        String selectorToString = getSelectorToString(theSelector);
        throw new TemporarySelectorClosedException("Temporary Selector " +
                                                    selectorToString +
                                                   " closed");
    }

    @Transport
    public TemporarySelectorState close(Selector theSelector) throws IOException {
        if (theSelector != null && theSelector.isOpen()) {
            theSelector.close();
        }
        return this;
    }

    public TemporarySelectorState removeSelectedKey(Selector theSelector,
                              SelectionKey theSelectionKey) throws IOException {
        String selectorToString = getSelectorToString(theSelector);
        throw new TemporarySelectorClosedException("Temporary Selector " +
                                                    selectorToString +
                                                   " closed");
    }

    private String getSelectorToString(Selector theSelector) {
        String selectorToString = "(null)";
        if (theSelector != null) {
            selectorToString = theSelector.toString();
        }
        return selectorToString;
    }
}
