


package xxxx;





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
