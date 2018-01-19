package com.sun.corba.ee.impl.transport;



public class NioBufferWriter {
    protected TemporarySelector tmpWriteSelector;
    protected final java.lang.Object tmpWriteSelectorLock = new java.lang.Object();

    private SocketChannel socketChannel;
    private TcpTimeouts tcpTimeouts;

    public NioBufferWriter(SocketChannel socketChannel, TcpTimeouts tcpTimeouts) {
        this.socketChannel = socketChannel;
        this.tcpTimeouts = tcpTimeouts;
    }

    void write(ByteBuffer byteBuffer) throws IOException {
        int nbytes = socketChannel.write(byteBuffer);
        if (byteBuffer.hasRemaining()) {
            
            
            
            TcpTimeouts.Waiter waiter = tcpTimeouts.waiter() ;
            SelectionKey sk = null;
            TemporarySelector tmpSelector = null;
            try {
                tmpSelector = getTemporaryWriteSelector(socketChannel);
                sk = tmpSelector.registerChannel(socketChannel,
                                                SelectionKey.OP_WRITE);
                while (byteBuffer.hasRemaining() && !waiter.isExpired()) {
                    int nsel = tmpSelector.select(waiter.getTimeForSleep());
                    if (nsel > 0) {
                        tmpSelector.removeSelectedKey(sk);
                        do {
                            
                            nbytes = socketChannel.write(byteBuffer);
                        } while (nbytes > 0 && byteBuffer.hasRemaining());
                    }
                    
                    if (nsel == 0 || nbytes == 0) {
                        waiter.advance() ;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                throw ConnectionImpl.wrapper.exceptionWhenWritingWithTemporarySelector(ioe,
                        byteBuffer.position(), byteBuffer.limit(),
                        waiter.timeWaiting(), tcpTimeouts.get_max_time_to_wait());
            } finally {
                if (tmpSelector != null) {
                    tmpSelector.cancelAndFlushSelector(sk);
                }
            }
            
            if (byteBuffer.hasRemaining() && waiter.isExpired()) {
                
                throw ConnectionImpl.wrapper.transportWriteTimeoutExceeded(
                        tcpTimeouts.get_max_time_to_wait(), waiter.timeWaiting());
            }
        }
    }

    void closeTemporaryWriteSelector() throws IOException {
        synchronized (tmpWriteSelectorLock) {
            if (tmpWriteSelector != null) {
                try {
                    tmpWriteSelector.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
    }

    TemporarySelector getTemporaryWriteSelector(SocketChannel socketChannel1) throws IOException {
        synchronized (tmpWriteSelectorLock) {
            if (tmpWriteSelector == null) {
                tmpWriteSelector = new TemporarySelector(socketChannel1);
            }
        }
        return tmpWriteSelector;
    }
}
