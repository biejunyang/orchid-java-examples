package com.orchid.example.threadpool;


import java.util.concurrent.*;

/**
 * @author biejunyang
 * @version 1.0
 * @date 2021/6/30 14:55
 */
public class ScheduledThreadPoolTest {


    public static void main(String[] args) {

        /**
         * 定时任务线程池：
         *  使用固定数量的线程来定时后者周期性执行任务
         */
//        ScheduledExecutorService  ScheduledExecutorService  = Executors.newScheduledThreadPool(3);

        ScheduledExecutorService executorService =  new ScheduledThreadPoolExecutor(3);

        System.out.println(System.currentTimeMillis());
        //延迟执行
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis()+Thread.currentThread().getName()
                        + "正在被执行");
            }
        }, 1, TimeUnit.SECONDS);

        //延迟时间后，开始周期性执行
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis()+Thread.currentThread().getName()
                        + "正在执行定时任务");
            }
        },1,3, TimeUnit.SECONDS);
    }

}
