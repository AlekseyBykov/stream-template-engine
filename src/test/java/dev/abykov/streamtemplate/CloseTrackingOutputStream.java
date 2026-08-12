package dev.abykov.streamtemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CloseTrackingOutputStream extends ByteArrayOutputStream {

    private boolean closed;

    @Override
    public void close() throws IOException {
        closed = true;
        super.close();
    }

    boolean isClosed() {
        return closed;
    }
}
