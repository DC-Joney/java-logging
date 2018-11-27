package com.nxt.mms;

import com.nxt.mms.logger.CommonLogger;

import java.io.UnsupportedEncodingException;

public class SmsLogMain {
    void init() throws UnsupportedEncodingException {
//       Logger logger = Logger.getLogger("@@@");
//       logger.setParent();
//       LogManager.getLogManager().addLogger(logger);
//       ConsoleHandler handler = new ConsoleHandler();
//       handler.setLevel(Level.INFO);
//       handler.setEncoding("GBK");
//       handler.setFormatter(new Formatter() {
//           @Override
//           public String format(LogRecord record) {
//               return "";
//           }
//       });
//        System.out.println(handler.getFormatter());
//       logger.addHandler(handler);
//        logger.info("1111");

        CommonLogger<SmsLogMain> log = new CommonLogger<>(SmsLogMain.class);
//        log.debug(null);
        log.info("111111");
        log.warning("111111");
        log.error("111111");
//        new Thread(()-> defaultLogger.info("111")).start();
    }


    public static void main(String[] args) throws UnsupportedEncodingException {
        new SmsLogMain().init();
    }



}
