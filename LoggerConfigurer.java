package com.nxt.mms.logger;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface LoggerConfigurer {

    String DEFAULT_CONFIG_PATH = "nxt_log";

    int DEFAULT_ERROR_LEVEL = 1000;

    int DEFAULT_WARNING_LEVEL = 900;

    int DEFAULT_INFO_LEVEL = 800;

    int DEFAULT_DEBUG_LEVEL = 700;

    String DEFAULT_LEVEL_PROPERTIES_NAME = "handler.level";

    String DEFAULT_LEVEL_NAME = "INFO";

    static <T> Logger configureLogger(Class<T> loggerClass, Function<LoggerBuilder<? super T>, Logger> loggerFunction) {
        return loggerFunction.apply(new LoggerBuilder<>(loggerClass));
    }

    class LoggerBuilder<T> {

        private final Logger clazzLogger;

        LoggerBuilder(Class<T> clazz) {
            clazzLogger = Logger.getLogger(clazz.getName());
        }

        public LoggerBuilder<T> level(Level level) {
            clazzLogger.setLevel(level);
            return this;
        }

        LoggerBuilder<T> handler(Handler... handler) {
            Arrays.stream(handler).forEach(clazzLogger::addHandler);
            return this;
        }

        LoggerBuilder<T> filter(Filter filter) {
            clazzLogger.setFilter(filter);
            return this;
        }

        LoggerBuilder<T> useParentHandlers(boolean useParentHandlers) {
            clazzLogger.setUseParentHandlers(useParentHandlers);
            return this;
        }

        Logger build() {
            return clazzLogger;
        }

        LoggerBuilder<T> resourceBundle(ResourceBundle resourceBundle) {
            clazzLogger.setResourceBundle(resourceBundle);
            return this;
        }
    }

}
