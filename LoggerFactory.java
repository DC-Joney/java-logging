package com.nxt.mms.logger;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.*;


@SuppressWarnings("unchecked")
public abstract class LoggerFactory {

    private static LogManager logManager;

    private static  Handler defaultHandler;

    private static  Formatter  defaultFormatter = new DefaultLoggingFormatter();

    private static  Filter defaultFilter = new DefaultLoggingFilter();

    private static ResourceBundle resourceBundle ;

    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    private static final Lock lock = new ReentrantLock(true);

    private static ErrorManager errorManager = new DefaultErrorManager();

    private static Level level;

    static {
        if(atomicBoolean.getAndSet(false)){
            init();
        }
    }

     static void init() {
        try {
            logManager = LogManager.getLogManager();
            resourceBundle = ResourceBundle.getBundle(LoggerConfigurer.DEFAULT_CONFIG_PATH, Locale.CHINA);
            defaultHandler = new DefaultConsoleHandler(getLevel(), Charset.forName("GBK"));
            defaultHandler.setFormatter(defaultFormatter);
            defaultHandler.setErrorManager(errorManager);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }



     static <T> Logger getDefaultLogger(Class<T> clazz) {

        lock.tryLock();

        Logger logger = logManager.getLogger(clazz.getName());

        if (logger != null) {
            return logger;
        }

        Logger clazzLogger = LoggerConfigurer.configureLogger(clazz,
                s -> s.handler(defaultHandler)
                        .level(getLevel())
                        .filter(defaultFilter)
                        .useParentHandlers(false)
                        .resourceBundle(resourceBundle)
                        .build());

        logManager.addLogger(clazzLogger);

        lock.unlock();

        return clazzLogger;

    }


    private static Level getLevel(){
        try {
           return Optional.ofNullable(level).orElseGet(LoggerFactory::getDefaultLevel);
        }catch (IllegalArgumentException  | MissingResourceException e){
            e.printStackTrace(System.err);
        }

        return Level.INFO;
    }



    private static Level getDefaultLevel(){
        String levelName = Optional.of(resourceBundle.getString(LoggerConfigurer.DEFAULT_LEVEL_PROPERTIES_NAME))
                .orElse(LoggerConfigurer.DEFAULT_LEVEL_NAME);
        level = Level.parse(levelName.toUpperCase());
        return level;
    }


    public static <T> Logger getLogger(Class<T> clazz, Function<LoggerConfigurer.LoggerBuilder<? super T>, Logger> loggerFunction) {

        lock.tryLock();

        Logger logger = logManager.getLogger(clazz.getName());

        if (logger != null) {
            return logger;
        }

        Logger clazzLogger = LoggerConfigurer.configureLogger(clazz, loggerFunction);

        logManager.addLogger(clazzLogger);

        clazzLogger.setLevel(getLevel());

        lock.unlock();

        return clazzLogger;

    }


}
