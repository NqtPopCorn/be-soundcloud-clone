//package com.popcorn.soundcloudclone.service.impl;
//
//import com.popcorn.soundcloudclone.service.PlayCache;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//
//@Component
//public class RedisPlayCache implements PlayCache {
//    private final StringRedisTemplate redisTemplate;
//
//    public RedisPlayCache(StringRedisTemplate redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    @Override
//    public boolean hasPlay(Integer userId, Integer trackId) {
//        String key = key(userId, trackId);
//        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
//    }
//
//    @Override
//    public void savePlay(Integer userId, Integer trackId, Duration ttl) {
//        redisTemplate.opsForValue().set(key(userId, trackId), "1", ttl);
//    }
//
//    private String key(Integer userId, Integer trackId) {
//        return "play:" + userId + ":" + trackId;
//    }
//}
//
