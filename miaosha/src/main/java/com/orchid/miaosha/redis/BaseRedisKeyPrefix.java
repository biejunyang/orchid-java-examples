package com.orchid.miaosha.redis;

public abstract class BaseRedisKeyPrefix implements RedisKeyPrefix {

    private int expireSeconds;

    private String prefix;


    public BaseRedisKeyPrefix(String prefix) {
        this.expireSeconds=0;
        this.prefix = prefix;
    }

    public BaseRedisKeyPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expiredSeconds() {
        return this.expireSeconds;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName()+":"+this.prefix;
    }
}
