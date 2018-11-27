package com.nxt.mms.logger;

import java.util.logging.ErrorManager;

public class DefaultErrorManager extends ErrorManager {
    @Override
    public synchronized void error(String msg, Exception ex, int code) {
        super.error(msg, ex, code);
    }
}
