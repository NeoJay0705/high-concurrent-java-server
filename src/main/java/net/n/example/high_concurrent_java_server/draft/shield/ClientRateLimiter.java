package net.n.example.high_concurrent_java_server.draft.shield;

import com.google.common.util.concurrent.RateLimiter;

public class ClientRateLimiter {

    public static RateLimiter getRateLimiter(int qps) {
        return RateLimiter.create(qps);
    }
}
