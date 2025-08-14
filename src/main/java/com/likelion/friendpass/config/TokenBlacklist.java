package com.likelion.friendpass.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBlacklist {
    private final ConcurrentMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String token, long expiresAtEpochSec) {
        blacklist.put(token, expiresAtEpochSec);
    }
    public boolean contains(String token) {
        Long exp = blacklist.get(token);
        if (exp == null) return false;
        if (exp < (System.currentTimeMillis() / 1000)) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}