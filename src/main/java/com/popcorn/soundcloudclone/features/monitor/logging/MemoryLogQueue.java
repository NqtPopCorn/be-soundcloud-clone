package com.popcorn.soundcloudclone.features.monitor.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemoryLogQueue {
    private static final Queue<ILoggingEvent> queue = new ConcurrentLinkedQueue<>();
    private static final int MAX_CAPACITY = 5000;

    public static void add(ILoggingEvent event) {
        if (queue.size() < MAX_CAPACITY) {
            queue.offer(event);
        }
    }

    public static List<ILoggingEvent> drain(int maxElements) {
        List<ILoggingEvent> elements = new ArrayList<>();
        int count = 0;
        while (count < maxElements && !queue.isEmpty()) {
            ILoggingEvent event = queue.poll();
            if (event != null) {
                elements.add(event);
                count++;
            }
        }
        return elements;
    }
}
