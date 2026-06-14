package com.popcorn.soundcloudclone.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory factory) {

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL, com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(
                "likedTrackIds",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        configs.put(
                "likedAlbumIds",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        configs.put(
                "likedPlaylistIds",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        configs.put(
                "tracks",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        configs.put(
                "users",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        configs.put(
                "usersByUsername",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        configs.put(
                "playlists",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        configs.put(
                "albums",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
