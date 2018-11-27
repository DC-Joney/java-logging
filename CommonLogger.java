package com.nxt.mms.logger;

import org.apache.http.util.Asserts;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CommonLogger<T> {

    private final Logger logger;
    private final int level;

    public CommonLogger(Class<T> clazz) {
        logger = LoggerFactory.getDefaultLogger(clazz);
        level = logger.getLevel().intValue();
    }

    public CommonLogger(Logger logger) {
        this.logger = logger;
        level = logger.getLevel().intValue();
    }

    public void info(Object obj) { log(obj,Level.INFO); }

    public void debug(Object obj) { log(obj,Level.CONFIG); }

    public void error(Object obj) { log(obj,Level.SEVERE); }

    public void warning(Object obj) { log(obj,Level.WARNING); }

    private void log(Object object,Level level){

        Objects.requireNonNull(object);

        LogRecord logRecord = new LogRecord(level,null);

        inferCaller(logRecord);

        Optional.of(object)
                .filter(o -> this.level <= level.intValue())
                .map(Object::toString)
                .ifPresent(s->{
                    logRecord.setMessage(s);
                    logger.log(logRecord);
                });
    }

    private void inferCaller(LogRecord record) {
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();
        int depth = access.getStackTraceDepth(throwable);

        boolean lookingForLogger = true;
        for (int ix = 0; ix < depth; ix++) {
            StackTraceElement frame =
                    access.getStackTraceElement(throwable, ix);
            String cname = frame.getClassName();
            boolean isLoggerImpl = isLoggerImplFrame(cname);
            if (lookingForLogger) {
                if (isLoggerImpl) {
                    lookingForLogger = false;
                }
            } else {
                if (!isLoggerImpl) {
                    if (!cname.startsWith("java.lang.reflect.") && !cname.startsWith("sun.reflect.")) {
                        record.setSourceClassName(cname);
                        record.setSourceMethodName(frame.getMethodName());
                        return;
                    }
                }
            }
        }

    }


    private boolean isLoggerImplFrame(String cname) {
        return cname.equals("com.nxt.mms.logger.CommonLogger");
    }

}

//        switch (level.intValue()){
//            case LoggerConfigurer.DEFAULT_DEBUG_LEVEL:
//                Optional.ofNullable(object)
//                        .filter(o -> this.level <= level.intValue())
//                        .map(Object::toString)
//                        .ifPresent(s-> {
//
//                        });
//                break;
//            case LoggerConfigurer.DEFAULT_INFO_LEVEL:
//                Optional.ofNullable(object)
//                        .filter(o -> this.level <= level.intValue())
//                        .map(Object::toString)
//                        .ifPresent(logger::info);
//                break;
//            case LoggerConfigurer.DEFAULT_WARNING_LEVEL:
//                Optional.ofNullable(object)
//                        .filter(o -> this.level <= level.intValue())
//                        .map(Object::toString)
//                        .ifPresent(logger::warning);
//                break;
//            case LoggerConfigurer.DEFAULT_ERROR_LEVEL:
//                Optional.ofNullable(object)
//                        .filter(o -> this.level <= level.intValue())
//                        .map(Object::toString)
//                        .ifPresent(logger::severe);
//                break;
//        }