// package com.popcorn.soundcloudclone.domain.service.impl;

// import org.springframework.stereotype.Component;

// import com.popcorn.soundcloudclone.domain.service.PlayCache;

// import java.time.Instant;
// import java.time.Duration;
// import java.util.concurrent.ConcurrentHashMap;

// @Component
// public class InMemoryPlayCache implements PlayCache {
// private final ConcurrentHashMap<String, Instant> cache = new
// ConcurrentHashMap<>();

// @Override
// public boolean hasPlay(Integer userId, Integer trackId) {
// String key = key(userId, trackId);
// Instant expireAt = cache.get(key);
// if (expireAt == null)
// return false;

// // hết hạn thì xóa key rồi return false -> reset count
// if (Instant.now().isAfter(expireAt)) {
// cache.remove(key);
// return false;
// }
// return true;
// }

// @Override
// public void savePlay(Integer userId, Integer trackId, Duration ttl) {
// cache.put(key(userId, trackId), Instant.now().plus(ttl));
// }

// private String key(Integer userId, Integer trackId) {
// return userId + ":" + trackId;
// }
// }
