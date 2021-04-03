package com.orchid.examples.ratelimiter;


import lombok.Data;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;


//@Slf4j
@Data
public class TokenBucketRateLimiter {

    //最大令牌数
    private long maxPermits;

    //存储令牌数
    private long storePermits;

    //生成一个令牌需要的毫秒数
    private long intervalMillis;

    //下一次请求可以获取令牌的起始时间(之前时间生成的令牌已经提前消费了)
    private long nextFreeTicketMillis = System.currentTimeMillis();

    private TokenBucketRateLimiter(long maxPermits, long storePermits, long intervalMillis) {
        this.maxPermits = maxPermits;
        this.storePermits = storePermits;
        this.intervalMillis = intervalMillis;
    }

    /**
     * 创建令牌桶限流对象
     * @param permitsPerSecond：令牌投放速率，单位秒。每秒投放令牌数
     * @param mustBurstSeconds：生成最大令牌数量，为mustBurstSeconds秒内生成的令牌
     * @return
     */
    public static TokenBucketRateLimiter crate(long permitsPerSecond, long mustBurstSeconds){
        return new TokenBucketRateLimiter(mustBurstSeconds * permitsPerSecond, permitsPerSecond, TimeUnit.SECONDS.toMillis(1)/permitsPerSecond);
    }





    /**
     * 获取n个令牌，并等待知道获取到令牌
     * @param permits
     * @return
     */
    public long acquire(long permits) throws InterruptedException {
        long waitMillis=resserve(permits);
        Thread.sleep(waitMillis);
        return waitMillis;
    }







    /**
     * 多线程并发获取令牌时，并发处理;
     * 分布式集群环境下并发控制,可使用分布式锁
     * @param permits
     */
    private synchronized long resserve(long permits){
        checkPermits(permits);
        return reserveAdnGetWaitLength(permits, System.currentTimeMillis());
    }

    /**
     * 获取n个令牌，并返回需要等待的时间
     * @param requirePermits
     * @param nowMillis
     * @return
     */
    private long reserveAdnGetWaitLength(long requirePermits,long nowMillis){
        resync(nowMillis);//更新令牌

        //可以消费的令牌
        long storePermitsToSpend=Math.min(requirePermits, this.storePermits);

        //需要生成的令牌
        long freshPermits=requirePermits-storePermitsToSpend;

        //需要等待的时间
        long waitMillis=requirePermits * this.intervalMillis;

        this.storePermits-=storePermitsToSpend;

        this.nextFreeTicketMillis=this.nextFreeTicketMillis+waitMillis;

        return waitMillis;
    }







    /**
     * 更新令牌数量
     * @param nowMillis
     */
    private void resync(long nowMillis){
        if(nowMillis>nextFreeTicketMillis){
            long newPermits=(nowMillis-nextFreeTicketMillis)/intervalMillis;
            this.storePermits=Math.min(this.maxPermits, this.storePermits+newPermits);
            this.nextFreeTicketMillis=nowMillis;

        }
    }

    private static void checkPermits(long permits) {
        checkArgument(permits > 0, "Requested permits (%s) must be positive", permits);
    }
}
