package com.nxt.mms.logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.*;
import java.util.logging.Formatter;


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
        lock.lock();
        if(atomicBoolean.getAndSet(false)){
            init();
        }
        lock.unlock();
    }

     static void init() {
        try {
            logManager = LogManager.getLogManager();
            URI uri = Paths.get(System.getProperty("user.dir"),
                    LoggerConfigurer.DEFAULT_CONFIG_RESOURCE_PATH.replace("#{s}", File.separator)).toUri();

            resourceBundle = ResourceBundle.getBundle(LoggerConfigurer.DEFAULT_CONFIG_FILE_PATH,
                    Locale.CHINA,
                    getClassLoader(uri),
                    ResourceBundle.Control.getControl(Collections.unmodifiableList(Collections.singletonList(LoggerConfigurer.DEFAYLT_RESOURCE_BUNDLE_CONTROL))));
            assert resourceBundle != null;
            defaultHandler = new DefaultConsoleHandler(getLevel(), Charset.forName("GBK"));
            defaultHandler.setFormatter(defaultFormatter);
            defaultHandler.setErrorManager(errorManager);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static ClassLoader getClassLoader(URI uri) throws MalformedURLException {
        return new URLClassLoader(new URL[]{uri.toURL()},Thread.currentThread().getContextClassLoader());
    }



     static <T> Logger getDefaultLogger(Class<T> clazz) {

         lock.lock();

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
