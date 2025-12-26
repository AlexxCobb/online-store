package ru.zinovev.online.store.config.cache;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class MyCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
        log.error("Redis GET error: cache - {}, key - {}, error - {}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCachePutError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key,
                                    Object value) {
        log.error("Redis PUT error: cache={}, key={}, error={}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheEvictError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
        log.error("Redis EVICT error: cache={}, key={}, error={}", cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheClearError(@NonNull RuntimeException e, @NonNull Cache cache) {
        log.error("Redis CLEAR error: cache={}, error={}", cache.getName(), e.getMessage());
    }
}
