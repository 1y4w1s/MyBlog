package com.my.blog.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCache {
    @Autowired(required = false)
    public RedisTemplate redisTemplate;

    private final Map<String, Object> localCache = new ConcurrentHashMap<>();
    private boolean redisDown = false;

    private <T> T doRedis(java.util.function.Supplier<T> action, T fallback) {
        if (redisDown || redisTemplate == null) return fallback;
        try { return action.get(); } catch (Exception e) { redisDown = true; return fallback; }
    }

    private void doRedisVoid(Runnable action) {
        if (redisDown || redisTemplate == null) return;
        try { action.run(); } catch (Exception e) { redisDown = true; }
    }

    public <T> void setCacheObject(final String key, final T value) {
        localCache.put(key, value);
        doRedisVoid(() -> redisTemplate.opsForValue().set(key, value));
    }

    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        localCache.put(key, value);
        doRedisVoid(() -> redisTemplate.opsForValue().set(key, value, timeout, timeUnit));
    }

    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return doRedis(() -> redisTemplate.expire(key, timeout, unit), true);
    }

    public <T> T getCacheObject(final String key) {
        if (redisTemplate != null && !redisDown) {
            try {
                ValueOperations<String, T> operation = redisTemplate.opsForValue();
                T val = operation.get(key);
                if (val != null) return val;
            } catch (Exception e) { redisDown = true; }
        }
        return (T) localCache.get(key);
    }

    public boolean deleteObject(final String key) {
        localCache.remove(key);
        return doRedis(() -> redisTemplate.delete(key), true);
    }

    public long deleteObject(final Collection collection) {
        collection.forEach(k -> localCache.remove(k));
        return doRedis(() -> redisTemplate.delete(collection), (long) collection.size());
    }

    public <T> long setCacheList(final String key, final List<T> dataList) {
        localCache.put(key, dataList);
        return doRedis(() -> { Long c = redisTemplate.opsForList().rightPushAll(key, dataList); return c == null ? 0L : c; }, (long) dataList.size());
    }

    public <T> List<T> getCacheList(final String key) {
        List<T> r = doRedis(() -> redisTemplate.opsForList().range(key, 0, -1), null);
        return r != null ? r : (List<T>) localCache.get(key);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        localCache.put(key, dataSet);
        return doRedis(() -> { BoundSetOperations<String, T> op = redisTemplate.boundSetOps(key); dataSet.forEach(op::add); return op; }, null);
    }

    public <T> Set<T> getCacheSet(final String key) {
        Set<T> r = doRedis(() -> redisTemplate.boundSetOps(key).members(), null);
        return r != null ? r : (Set<T>) localCache.get(key);
    }

    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        localCache.put(key, dataMap);
        doRedisVoid(() -> redisTemplate.opsForHash().putAll(key, dataMap));
    }

    public <T> Map<String, T> getCacheMap(final String key) {
        Map<String, T> r = doRedis(() -> redisTemplate.opsForHash().entries(key), null);
        return r != null ? r : (Map<String, T>) localCache.get(key);
    }

    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        doRedisVoid(() -> redisTemplate.opsForHash().put(key, hKey, value));
    }

    public <T> T getCacheMapValue(final String key, final String hKey) {
        return doRedis(() -> { HashOperations<String, String, T> op = redisTemplate.opsForHash(); return op.get(key, hKey); }, null);
    }

    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return doRedis(() -> redisTemplate.opsForHash().multiGet(key, hKeys), null);
    }

    public Collection<String> keys(final String pattern) {
        return doRedis(() -> redisTemplate.keys(pattern), localCache.keySet());
    }
}
