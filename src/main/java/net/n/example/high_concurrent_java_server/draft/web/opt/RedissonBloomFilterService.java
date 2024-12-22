package net.n.example.high_concurrent_java_server.draft.web.opt;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

// @Service
public class RedissonBloomFilterService {

    private final RBloomFilter<String> bloomFilter;

    public RedissonBloomFilterService(RedissonClient redissonClient) {
        // Initialize or retrieve the Bloom Filter from Redis
        this.bloomFilter = redissonClient.getBloomFilter("my-bloom-filter");

        // Initialize the Bloom Filter if not already done
        int expectedInsertions = 10000; // Maximum number of elements expected
        double falsePositiveProbability = 0.01; // 1% false positive probability

        bloomFilter.tryInit(expectedInsertions, falsePositiveProbability);
    }

    /**
     * Adds an item to the Bloom Filter.
     *
     * @param value The value to add.
     */
    public void add(String value) {
        bloomFilter.add(value);
    }

    /**
     * Checks if an item might be in the Bloom Filter.
     *
     * @param value The value to check.
     * @return True if the item might exist, false otherwise.
     */
    public boolean mightContain(String value) {
        return bloomFilter.contains(value);
    }

    /**
     * Returns the approximate number of elements in the Bloom Filter.
     *
     * @return The approximate count of elements.
     */
    public long approximateElementCount() {
        return bloomFilter.count();
    }
}
