package com.orchid.exmaples;

import com.google.common.math.LongMath;
import lombok.Data;


/**
 * 令牌桶限流对象
 */
@Data
public class TokenRateLimiter {

    private long maxPermits;//桶的大小

    private long permitsPerMillis;//每毫秒投放令牌数

    private long nextFreeTokenMillis=System.currentTimeMillis();//下一次投放令牌的时间，毫秒

    private long storePermits;//存储的令牌数，初始值为1s内存放的令牌

    private TokenRateLimiter(long maxPermits, long permitsPerMillis) {
        this.maxPermits = maxPermits;
        this.permitsPerMillis = permitsPerMillis;
        this.storePermits=1000*permitsPerMillis;
    }




    public static TokenRateLimiter crate(long maxPermits,long permitsPerSecond){
        return new TokenRateLimiter(maxPermits, permitsPerSecond);
    }


    /**
     * 每次获取令牌之前，更新令牌桶中令牌数量
     *
     * @param now
     */
    private void reSync(long now){
        if(now > nextFreeTokenMillis){
            this.storePermits+=Math.min(maxPermits, this.storePermits+(now-nextFreeTokenMillis)*permitsPerMillis);
            this.nextFreeTokenMillis=now;
        }
    }


    /**
     * 获取n个token,需要等待多长时间(毫秒)
     * @param tokens
     * @return
     */
    private long reserveAndGetWaitMillis(long tokens){
        long now=System.currentTimeMillis();
        this.reSync(now);

        //可以消耗的令牌
        long storePermitsToSpend=Math.min(tokens, this.storePermits);

        //需要等待的令牌
        long freshPermits=tokens-storePermits;

        //需要等待的时间
        long waitMillis=freshPermits*(1/permitsPerMillis);

        return LongMath.saturatedAdd(this.nextFreeTokenMillis-now, waitMillis);
    }


















}
