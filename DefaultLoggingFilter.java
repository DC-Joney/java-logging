package com.nxt.mms.logger;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class DefaultLoggingFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        return true;
    }
}
