package com.popcorn.soundcloudclone.features.track.service.impl;

import com.popcorn.soundcloudclone.features.track.repository.TrackRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Batch worker that drains the Redis play queue and persists play events to
 * MySQL.
 * Runs every TTL via @Scheduled.
 *
 * Flow:
 * 1. LPOP items from "track:plays:queue" in batches
 * 2. Group by trackId to get aggregate counts
 * 3. Bulk UPDATE Track.playCount for each track
 * 4. Batch INSERT TrackPlay history logs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrackPlaySyncService {

    private final StringRedisTemplate redisTemplate;
    private final TrackRepository trackRepository;

    private static final String PLAY_QUEUE_KEY = "track:plays:queue";
    private static final int BATCH_SIZE = 500;

    /**
     * Scheduled task: drain Redis play queue and sync to MySQL.
     */
    @Scheduled(fixedDelay = 30_000) // 30s
    @Transactional
    public void syncPlayCountsToDatabase() {
        List<String> batch = drainQueue(BATCH_SIZE);

        if (batch.isEmpty()) {
            return;
        }

        log.info("Processing {} play events from Redis queue", batch.size());

        // Group by trackId -> list of userIds
        Map<Integer, List<Integer>> trackUserMap = new HashMap<>();
        for (String entry : batch) {
            String[] parts = entry.split(":");
            if (parts.length != 2) {
                log.warn("Invalid queue entry: {}", entry);
                continue;
            }
            try {
                int trackId = Integer.parseInt(parts[0]);
                int userId = Integer.parseInt(parts[1]);
                trackUserMap.computeIfAbsent(trackId, k -> new ArrayList<>()).add(userId);
            } catch (NumberFormatException e) {
                log.warn("Invalid queue entry (parse error): {}", entry);
            }
        }

        // Bulk update play counts per track
        for (Map.Entry<Integer, List<Integer>> entry : trackUserMap.entrySet()) {
            int trackId = entry.getKey();
            int count = entry.getValue().size();

            // Update Track.playCount in DB
            trackRepository.increasePlayCountBy(trackId, count);
        }

        log.info("Synced {} play events across {} tracks to MySQL",
                batch.size(), trackUserMap.size());
    }

    /**
     * Drain up to maxItems from the Redis list via LPOP.
     */
    private List<String> drainQueue(int maxItems) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < maxItems; i++) {
            String item = redisTemplate.opsForList().leftPop(PLAY_QUEUE_KEY);
            if (item == null) {
                break; // queue is empty
            }
            items.add(item);
        }
        return items;
    }
}
