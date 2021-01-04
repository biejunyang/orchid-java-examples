package com.orchid.miaosha.redis;

public class UserRedisKey extends BaseRedisKeyPrefix {
    private UserRedisKey(String prefix) {
        super(prefix);
    }

    private UserRedisKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }



}
