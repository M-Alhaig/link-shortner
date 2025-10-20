package com.mordizze.linkshortener.configs;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.mordizze.linkshortener.stats.models.AverageTimeBetweenClicks;
import com.mordizze.linkshortener.stats.models.ClickDistributionResponse;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final int AVERAGE_TIME_BETWEEN_CLICKS_TTL_H = 1;
    private final int CLICK_DISTRIBUTION_TTL_H = 1;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder -> {
            RedisCacheConfiguration averageTimeBetweenClicksCache = RedisCacheConfiguration.defaultCacheConfig()
                                                                    .disableCachingNullValues()
                                                                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                                    .fromSerializer(new Jackson2JsonRedisSerializer<>(AverageTimeBetweenClicks.class)))
                                                                    .entryTtl(Duration.ofHours(AVERAGE_TIME_BETWEEN_CLICKS_TTL_H));

            RedisCacheConfiguration clickDistributionCache = RedisCacheConfiguration.defaultCacheConfig()
                                                                                    .disableCachingNullValues()
                                                                                    .entryTtl(Duration.ofHours(CLICK_DISTRIBUTION_TTL_H))
                                                                                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                                                                        .fromSerializer(new Jackson2JsonRedisSerializer<>(ClickDistributionResponse.class)));

            builder.withCacheConfiguration("TIME_BETWEEN_CLICKS", averageTimeBetweenClicksCache);
            builder.withCacheConfiguration("CLICK_DISTRIBUTION", clickDistributionCache);
        });
    }

}
