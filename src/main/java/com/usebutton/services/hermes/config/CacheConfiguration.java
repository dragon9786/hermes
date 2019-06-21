package com.usebutton.services.hermes.config;

import io.prometheus.client.Counter;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Configuration for Caching.
 *
 * We are using Redis for our caching engine and, as a result, this cache configuration class is
 * geared explicitly to Redis. The properties will be set in the application.yml file as usual.
 *
 * Configuration values settable in application.yml (defaults shown):
 * <pre class="code">
 * cache:
 *   hostName: 127.0.0.1
 *   port: 6379
 *   defaultExpirationSeconds: 300
 * </pre>
 *
 * note: The {@code EnableCaching} annotation automatically enables caching across the application.
 * If you'd like to disable caching for a particular profile, add the following to
 * the application.yml file
 *
 * {@code spring.cache.type=none}
 *
 * @author Govind
 */
@Configuration
@ConfigurationProperties("cache")
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {
    private String hostName = "127.0.0.1";
    private int port = 6379;
    private int expirationOffsetSeconds = 60;
    private int defaultExpirationSeconds = 300;
    private boolean useCluster = false;

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setExpirationOffsetSeconds(int expirationOffsetSeconds) {
        this.expirationOffsetSeconds = expirationOffsetSeconds;
    }

    public void setDefaultExpirationSeconds(int defaultExpirationSeconds) {
        this.defaultExpirationSeconds = defaultExpirationSeconds;
    }

    public void setUseCluster(boolean useCluster) {
        this.useCluster = useCluster;
    }

    public int getRandomExpirationSeconds() {
        int offset = ThreadLocalRandom.current().nextInt( -expirationOffsetSeconds, expirationOffsetSeconds + 1);
        return defaultExpirationSeconds + offset;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        LoggerFactory.getLogger(getClass()).info("Building {} cache factory for {}:{}",
                                                 (useCluster ? "clustered" : "standalone"), hostName, port);
        JedisConnectionFactory factory;
        if (useCluster) {
            List<String> clusterNodes = new ArrayList<String>() {{
                add(hostName + ":" + port);
            }};
            factory = new JedisConnectionFactory(
                    new RedisClusterConfiguration(clusterNodes)
            );
        }
        else {
            factory = new JedisConnectionFactory();
            factory.setHostName(hostName);
            factory.setPort(port);
        }
        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> userCircleListRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        int expirationSeconds = getRandomExpirationSeconds();
        LoggerFactory.getLogger(getClass()).info("Building cache manager with expiration of {} seconds", expirationSeconds);
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(expirationSeconds);
        cacheManager.setUsePrefix(true);
        return cacheManager;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        CacheErrorHandler errorHandler = new CacheErrorHandler() {
            private final Counter errorCounter = Counter.build()
                .namespace(Constants.APP_NAME)
                .name("cache_failure_total")
                .labelNames("operation", "cache") // success|fail|error
                .help("Count of cache operation failures")
                .register();

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                handleError("get", cache.getName(), exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                handleError("put", cache.getName(), exception.getMessage());

            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                handleError("evict", cache.getName(), exception.getMessage());

            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                handleError("clear", cache.getName(), exception.getMessage());

            }

            private void handleError(String operationType, String errorMessage, String cacheName) {
                LoggerFactory.getLogger(getClass()).error("Cache operation {} for cache {} failed! Error message: {}",
                        operationType.toUpperCase(), cacheName, errorMessage);
                errorCounter.labels(operationType, cacheName).inc();
            }
        };
        return errorHandler;
    }
}
