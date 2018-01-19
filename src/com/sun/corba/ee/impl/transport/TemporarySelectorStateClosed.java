

package xxxx;







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
