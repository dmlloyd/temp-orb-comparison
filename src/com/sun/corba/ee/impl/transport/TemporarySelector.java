

package com.sun.corba.ee.impl.transport;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.sun.corba.ee.spi.transport.TemporarySelectorState;




public class TemporarySelector {
    
    private TemporarySelectorState itsState;
    private Selector itsSelector;

    
    public TemporarySelector(SelectableChannel theSelectableChannel) throws IOException {
        itsSelector = theSelectableChannel.provider().openSelector();
        itsState = new TemporarySelectorStateOpen();
    }
    
    
    synchronized public int select(long theTimeout) throws IOException {
        return itsState.select(itsSelector, theTimeout);
    }
    
    synchronized public SelectionKey registerChannel(SelectableChannel theSelectableChannel, int theOps) throws IOException {
        return itsState.registerChannel(itsSelector, theSelectableChannel, theOps);
    }
 
    
    synchronized public void close() throws IOException {
        itsState = itsState.close(itsSelector);
    }
    
    synchronized public void removeSelectedKey(SelectionKey theSelectionKey) throws IOException {
        itsState = itsState.removeSelectedKey(itsSelector, theSelectionKey);
    }

    synchronized public void cancelAndFlushSelector(SelectionKey theSelectionKey) throws IOException {
        itsState = itsState.cancelKeyAndFlushSelector(itsSelector, theSelectionKey);
    }

}
