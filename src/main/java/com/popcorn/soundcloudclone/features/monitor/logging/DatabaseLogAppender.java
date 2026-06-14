package com.popcorn.soundcloudclone.features.monitor.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class DatabaseLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {
        // We push to an in-memory queue to avoid blocking the main logging thread
        MemoryLogQueue.add(eventObject);
    }
}
