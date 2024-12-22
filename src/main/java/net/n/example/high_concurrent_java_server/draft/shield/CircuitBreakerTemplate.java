package net.n.example.high_concurrent_java_server.draft.shield;

import java.net.SocketTimeoutException;
import java.time.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.functions.CheckedSupplier;
import io.netty.handler.timeout.TimeoutException;
import lombok.SneakyThrows;

/**
 * Circuit Breaker State Transitions:
 *
 * [CLOSED]
 *    |
 *    |--> Failure Rate or Slow Call Rate Exceeded
 *    v
 * [OPEN]
 *    |
 *    |--> waitDurationInOpenState expires
 *    v
 * [HALF-OPEN]
 *    |
 *    |--> Test Calls Succeed -> CLOSED
 *    |--> Test Calls Fail    -> OPEN
 *    v
 * [CLOSED]
 */
public class CircuitBreakerTemplate {
    private CircuitBreaker circuitBreaker;

    public CircuitBreakerTemplate() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom().failureRateThreshold(50) // Open
                                                                                             // circuit
                                                                                             // if
                                                                                             // 50%
                                                                                             // of
                                                                                             // calls
                                                                                             // fail
                .slowCallRateThreshold(70) // Open circuit if 70% of calls are slow
                .slowCallDurationThreshold(Duration.ofSeconds(10)) // Calls longer than 10s are slow
                .minimumNumberOfCalls(50) // Minimum of 50 calls in sliding window
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED) // Use
                                                                                      // time-based
                                                                                      // sliding
                                                                                      // window
                .slidingWindowSize(60) // Sliding window size: 100 s
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait 30s before transitioning to
                                                                 // half-open
                .permittedNumberOfCallsInHalfOpenState(50) // Allow 50 calls in half-open state
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // Auto-transition to half-open
                                                                    // after 30s
                .recordException(throwable -> {
                    // Record network timeout exceptions
                    return throwable instanceof SocketTimeoutException
                            || throwable instanceof TimeoutException;
                })
                // .ignoreException(throwable -> {
                // // Ignore business logic errors
                // return throwable instanceof CustomBusinessException;
                // })
                .build();
        this.circuitBreaker = CircuitBreaker.of("grpcClient", config);
    }

    @SneakyThrows
    public <T> T request(CheckedSupplier<T> callable) {
        return CircuitBreaker.decorateCheckedSupplier(circuitBreaker, callable).get();
    }
}
