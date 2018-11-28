package com.nxt.mms.logger;


import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class DefaultLoggingFormatter extends SimpleFormatter {
//2018-11-27 11:47:41.507  INFO 3320 --- [           main] ConfigServletWebServerApplicationContext : Refreshing org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@36ebc363: startup date [Tue Nov 27 11:47:41 CST 2018];
//11:59:36.057 [ThreadPoolExecutorEventTask-1] INFO com.example.webflux.pool.ThreadPoolTest - null

    private final Map<String, String> levels = new ConcurrentHashMap<>(8);

    DefaultLoggingFormatter() {
        levels.put("CONFIG", "DEBUG");
        levels.put("SEVERE", "ERROR");
    }


    @Override
    public synchronized String format(LogRecord record) {
        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS"));
        String levelName = record.getLevel().getName().toUpperCase();
        long threadID = Thread.currentThread().getId();
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        String threadName = Thread.currentThread().getName();
        String sourceClassName = record.getSourceClassName();
        String sourceMethodName = record.getSourceMethodName();
        String className = sourceClassName + " ( " + sourceMethodName + " )";
        int pid = Optional.ofNullable(mxBean.getName())
                .map(s -> Integer.valueOf(s.split("@")[0]))
                .orElse(0);
        String replaceLevelName = Optional.ofNullable(levels.get(levelName)).orElse(levelName);

        return String.format("%s  [ %s ] £ü%d£ü  %s   %s   - %s\n", formatDate
                , threadName, pid, replaceLevelName, className, record.getMessage());
    }
}
