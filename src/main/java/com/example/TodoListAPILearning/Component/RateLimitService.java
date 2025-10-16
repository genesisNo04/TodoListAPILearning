package com.example.TodoListAPILearning.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitService {

    //Each user will have their own bucket
    //Use ConcurrentHashMap for thread safe
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String userId) {
        //If user does not have the bucket yet create one
        //otherwise return the existing bucket
        return cache.computeIfAbsent(userId, this::newBucket);
    }

    //
    private Bucket newBucket(String userId) {
        //1 token maximum and refill 1 token every 15 seconds
        //This is the rule for the rate limiter
        //Bandwidth is an object in Bucket4j, an object for creating rate limit config
        //capacity: how many token can be in the bucket
        //refill: how token refill over time
        //type of bandwith: classic -> simple, fixed bucket
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(15)));
        //Bucket keep track of how many tokens are left
        //Create new bucket with builder pattern, allow programmer to config the object step by step instead of using complicated constructor
        //Useful for obj that has many optional parameter
        //Add the limit rule to the bucket
        return Bucket.builder().addLimit(limit).build();
    }
}
