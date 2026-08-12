package dev.abykov.streamtemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CloseTrackingInputStream extends ByteArrayInputStream {

    private boolean closed;

    CloseTrackingInputStream(byte[] buffer) {
        super(buffer);
    }

    @Override
    public void close() throws IOException {
        closed = true;
        super.close();
    }

    boolean isClosed() {
        return closed;
    }
}
