


package com.sun.corba.ee.spi.transport;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;




public interface TemporarySelectorState {
    
    public int select(Selector theSelector, long theTimeout) throws IOException;

   
    public SelectionKey registerChannel(Selector theSelector,
                                        SelectableChannel theSelectableChannel,
                                        int theOps) throws IOException;

   
    public TemporarySelectorState cancelKeyAndFlushSelector(Selector theSelector,
                                                            SelectionKey theSelectionKey)
                                                            throws IOException;

   
    public TemporarySelectorState close(Selector theSelector) throws IOException;

   
    public TemporarySelectorState removeSelectedKey(Selector theSelector,
                                                    SelectionKey theSelectionKey)
                                                    throws IOException;
}
