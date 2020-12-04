package com.orchid.examples.ratelimiter;


import lombok.Data;


//@Slf4j
@Data
public class TokenRateLimiter {

    private long maxPermits;//桶中最大令牌数量(桶的大小)

    private double permitsPerMillis;//每毫秒投放令牌数

    private long prevFreeTokenMillis=System.currentTimeMillis();//上一次投放令牌的时间，毫秒

    private long storePermits;//存储的令牌数，初始值为1s内存放的令牌

    private TokenRateLimiter(long maxPermits, double permitsPerMillis) {
        this.maxPermits = maxPermits;
        this.permitsPerMillis = permitsPerMillis;
        this.storePermits=(long)(1000 * permitsPerMillis);//初始令牌数为1s内生成的令牌
    }

    /**
     * 创建令牌桶限流对象
     * @param permitsPerSecond：令牌投放速率，单位秒。每秒投放令牌数
     * @param mustBurstSeconds：生成最大令牌数量，为mustBurstSeconds秒内生成的令牌
     * @return
     */
    public static TokenRateLimiter crate(long permitsPerSecond, long mustBurstSeconds){
        return new TokenRateLimiter(mustBurstSeconds * permitsPerSecond, 1.0*permitsPerSecond/1000);
    }


    /**
     * 获取令牌
     * @return
     */
    public synchronized boolean acquire(){
        long now=System.currentTimeMillis();
        reSync(now);//更新令牌数量
        if(this.storePermits>0){
            this.storePermits--;
//            log.info("获取令牌后令牌数量：{}", this.storePermits);
            return true;
        }
        this.storePermits=0;
        return false;
    }


    /**
     * 每次获取令牌之前，更新令牌桶中令牌数量
     *
     * @param now
     */
    private void reSync(long now){
        if(now > prevFreeTokenMillis){
//            log.info("上一次时间：{}", this.prevFreeTokenMillis);
//            log.info("当前的时间：{}，时间间隔：{}", now, now-prevFreeTokenMillis);
            long addTokens=(long)Math.floor((now-prevFreeTokenMillis)*permitsPerMillis);
//            log.info("生成令牌数：{}", addTokens);
            if(addTokens > 0){
                this.prevFreeTokenMillis=now;//更新上一次令牌投放时间
                this.storePermits=(long)Math.floor(Math.min(maxPermits, this.storePermits+addTokens));
            }
//            log.info("更新后令牌数量：{}", this.storePermits);
        }
    }

}
