package com.nxt.mms.logger;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.*;

public class DefaultConsoleHandler extends Handler {

    private OutputStreamWriter streamWriter;

    public DefaultConsoleHandler(Level level,Charset charset) throws UnsupportedEncodingException {
        super();
        streamWriter = new OutputStreamWriter(System.out,charset.name());
        setEncoding(charset.name());
        setLevel(level);
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;
        try {
            msg = getFormatter().format(record);
        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        try {
            streamWriter.write(msg);

        } catch (Exception ex) {
            // We don't want to throw an exception here, but we
            // report the exception to any registered ErrorManager.
            reportError(null, ex, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void close() {
        if (streamWriter != null) {
            try {
                streamWriter.write(getFormatter().getTail(this));
                streamWriter.flush();
                streamWriter.close();
            } catch (Exception ex) {
                // We don't want to throw an exception here, but we
                // report the exception to any registered ErrorManager.
                reportError(null, ex, ErrorManager.CLOSE_FAILURE);
            }
            streamWriter = null;
        }
    }

    @Override
    public synchronized void flush() {
        if (streamWriter != null) {
            try {
                streamWriter.flush();
            } catch (Exception ex) {
                // We don't want to throw an exception here, but we
                // report the exception to any registered ErrorManager.
                reportError(null, ex, ErrorManager.FLUSH_FAILURE);
            }
        }
    }
}
