package com.mordizze.linkshortener.configs;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final int AVERAGE_TIME_BETWEEN_CLICKS_TTL_H = 1;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder -> {
            RedisCacheConfiguration averageTimeBetweenClicksCache = RedisCacheConfiguration.defaultCacheConfig()
                                                                    .disableCachingNullValues()
                                                                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                                    .fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)))
                                                                    .entryTtl(Duration.ofHours(AVERAGE_TIME_BETWEEN_CLICKS_TTL_H));

            builder.withCacheConfiguration("TIME_BETWEEN_CLICKS", averageTimeBetweenClicksCache);
        });
    }

}
