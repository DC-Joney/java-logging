package com.nxt.mms;

import com.nxt.mms.logger.CommonLogger;

import java.io.UnsupportedEncodingException;

public class SmsLogMain {
    public static void main(String[] args) throws UnsupportedEncodingException {
        CommonLogger<SmsLogMain> log = new CommonLogger<>(SmsLogMain.class);
        log.debug("2222");
        log.info("111111");
        log.warning("111111");
        log.error("111111");
    }

}
