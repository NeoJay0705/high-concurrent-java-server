package net.n.example.high_concurrent_java_server.draft.web.opt;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

// @Service
public class CacheService {

    private final RedissonClient redissonClient;
    private final Random random = new Random();

    public CacheService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    // Cache key prefix to avoid collisions
    private static final String CACHE_KEY_PREFIX = "myApp:cache:";

    public <T> T getCachedValue(String key, long ttlSeconds, DataProvider<T> dataProvider) {
        String cacheKey = CACHE_KEY_PREFIX + key;

        try {
            // Attempt to get the value from cache
            String cachedValue = (String) redissonClient.getBucket(cacheKey).get();
            if (cachedValue != null) {
                return dataProvider.codec().decode(cachedValue);
            }

            // Use a Redis lock to avoid Hotspot Invalid and Cache Penetration
            RLock lock = redissonClient.getLock(cacheKey + ":lock");
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (isLocked) {
                try {
                    // Recheck the cache in case another thread has populated it
                    cachedValue = (String) redissonClient.getBucket(cacheKey).get();
                    if (cachedValue != null) {
                        return dataProvider.codec().decode(cachedValue);
                    }

                    // Fetch from database
                    T dbValue = dataProvider.search().call();

                    if (dbValue != null) {
                        // Add a random component to TTL to avoid Cache Avalanche
                        long randomTTL = ttlSeconds + random.nextLong(10);

                        // Update cache with the value and TTL
                        redissonClient.getBucket(cacheKey).set(dataProvider.codec().encode(dbValue),
                                randomTTL, TimeUnit.SECONDS);
                        return dbValue;
                    } else {
                        // To prevent Cache Penetration, cache null values with a short TTL
                        redissonClient.getBucket(cacheKey).set(null, 60, TimeUnit.SECONDS);
                        return null;
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                // Wait and retry fetching from cache to avoid repeated DB hits
                Thread.sleep(100);
                return dataProvider.codec()
                        .decode((String) redissonClient.getBucket(cacheKey).get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error accessing cache", e);
        }
    }

    public interface DataProvider<T> {
        Callable<T> search();

        StringCodec<T> codec();
    }

    public interface DataCodec<T, S> {
        T decode(S value);

        S encode(T value);
    }

    public interface StringCodec<T> extends DataCodec<T, String> {
    }

    public static class JsonStringCodec<T> implements StringCodec<T> {
        @Override
        public String encode(T value) {
            return null;
        }

        @Override
        public T decode(String value) {
            return null;
        }
    }
}
