package com.ride_share.service.impl;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ride_share.exceptions.RateLimitExceededException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

import io.github.bucket4j.Refill;

@Service
public class RateLimitingService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public void checkRateLimit(String apiKey) {
        Bucket bucket = buckets.computeIfAbsent(apiKey, this::createNewBucket);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
        }
    }

    private Bucket createNewBucket(String apiKey) {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }
}
