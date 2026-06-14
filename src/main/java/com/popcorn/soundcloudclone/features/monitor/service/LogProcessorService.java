package com.popcorn.soundcloudclone.features.monitor.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.popcorn.soundcloudclone.features.monitor.entity.SystemLog;
import com.popcorn.soundcloudclone.features.monitor.logging.MemoryLogQueue;
import com.popcorn.soundcloudclone.features.monitor.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import com.popcorn.soundcloudclone.features.monitor.logging.DatabaseLogAppender;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogProcessorService {

    private final SystemLogRepository systemLogRepository;
    private final Environment environment;

    @PostConstruct
    public void init() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        DatabaseLogAppender appender = new DatabaseLogAppender();
        appender.setContext(context);
        
        // Only log WARN and ERROR to the database
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(Level.WARN.levelStr);
        filter.start();
        appender.addFilter(filter);
        
        appender.start();
        
        ch.qos.logback.classic.Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);
    }

    @Scheduled(fixedDelay = 5000)
    public void processLogs() {
        List<ILoggingEvent> events = MemoryLogQueue.drain(100); // Process 100 max per tick
        
        if (events.isEmpty()) {
            return;
        }

        List<SystemLog> logsToSave = events.stream().map(event -> {
            String exceptionStr = null;
            IThrowableProxy proxy = event.getThrowableProxy();
            if (proxy != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(proxy.getClassName()).append(": ").append(proxy.getMessage()).append("\n");
                for (StackTraceElementProxy step : proxy.getStackTraceElementProxyArray()) {
                    sb.append("\tat ").append(step.getSTEAsString()).append("\n");
                }
                exceptionStr = sb.toString();
            }

            return SystemLog.builder()
                    .level(event.getLevel().toString())
                    .message(event.getFormattedMessage())
                    .loggerName(event.getLoggerName())
                    .exception(exceptionStr)
                    .timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimeStamp()), ZoneId.systemDefault()))
                    .build();
        }).collect(Collectors.toList());

        systemLogRepository.saveAll(logsToSave);
    }

    // Run every day at 2 AM to delete logs older than 7 days
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void deleteOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        systemLogRepository.deleteByTimestampBefore(cutoff);
        log.info("Deleted system logs older than {}", cutoff);
    }
}
