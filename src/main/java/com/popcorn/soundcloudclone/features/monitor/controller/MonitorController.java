package com.popcorn.soundcloudclone.features.monitor.controller;

import com.popcorn.soundcloudclone.common.response.ApiResponse;
import com.popcorn.soundcloudclone.features.monitor.entity.SystemLog;
import com.popcorn.soundcloudclone.features.monitor.repository.SystemLogRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Measurement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final SystemLogRepository systemLogRepository;
    private final MeterRegistry meterRegistry;

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Memory
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long usedHeap = memoryMXBean.getHeapMemoryUsage().getUsed();
        long maxHeap = memoryMXBean.getHeapMemoryUsage().getMax();
        metrics.put("memoryUsed", usedHeap);
        metrics.put("memoryMax", maxHeap);

        // System Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        metrics.put("uptime", uptime);

        // CPU Usage
        try {
            Double cpuUsage = meterRegistry.get("process.cpu.usage").gauge().value();
            metrics.put("cpuUsage", cpuUsage != null ? cpuUsage : 0.0);
        } catch (Exception e) {
            metrics.put("cpuUsage", 0.0);
        }

        // Active Threads
        try {
            Double activeThreads = meterRegistry.get("jvm.threads.live").gauge().value();
            metrics.put("activeThreads", activeThreads != null ? activeThreads : 0.0);
        } catch (Exception e) {
            metrics.put("activeThreads", 0.0);
        }

        return ApiResponse.<Map<String, Object>>builder()
                .statusCode(200)
                .message("Success")
                .result(metrics)
                .build();
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<SystemLog>> getLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        if ("ALL".equalsIgnoreCase(level) || level != null && level.trim().isEmpty()) {
            level = null;
        }
        
        Page<SystemLog> logs = systemLogRepository.findByFilter(
                level, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
        
        return ApiResponse.<Page<SystemLog>>builder()
                .statusCode(200)
                .message("Success")
                .result(logs)
                .build();
    }
}
