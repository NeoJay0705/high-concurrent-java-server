package net.n.example.high_concurrent_java_server.draft.web.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {

    private class Bucket {
        private int tokens;
        private long lastRefillTimestamp;
        private final Lock lock = new ReentrantLock();

        public Bucket(int initialTokens, long lastRefillTimestamp) {
            this.tokens = initialTokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }

        public Lock getLock() {
            return lock;
        }

        public int getTokens() {
            return tokens;
        }

        public void setTokens(int tokens) {
            this.tokens = tokens;
        }

        public long getLastRefillTimestamp() {
            return lastRefillTimestamp;
        }

        public void setLastRefillTimestamp(long lastRefillTimestamp) {
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int refillRate; // Tokens added per second

    public RateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    private void refillTokens(Bucket bucket) {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - bucket.getLastRefillTimestamp();
        int tokensToAdd = (int) (elapsedTime / TimeUnit.SECONDS.toNanos(1) * refillRate);

        if (tokensToAdd > 0) {
            bucket.setTokens(Math.min(capacity, bucket.getTokens() + tokensToAdd));
            bucket.setLastRefillTimestamp(currentTime);
        }
    }

    public boolean allowRequest(String id) {
        String key = getKey(id);
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, System.nanoTime()));

        Lock lock = bucket.getLock();
        lock.lock();
        try {
            refillTokens(bucket);
            if (bucket.getTokens() > 0) {
                bucket.setTokens(bucket.getTokens() - 1);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private String getKey(String id) {
        return id;
    }
}
