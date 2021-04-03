package com.orchid.miaosha.redis;

public interface RedisKeyPrefix {
    int expiredSeconds();

    String getPrefix();

}
