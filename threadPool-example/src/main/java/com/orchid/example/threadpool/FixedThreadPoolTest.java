package com.orchid.example.threadpool;


import java.util.concurrent.*;

/**
 * @author biejunyang
 * @version 1.0
 * @date 2021/6/30 14:55
 */
public class FixedThreadPoolTest {


    public static void main(String[] args) {

        /**
         * 固定线程数线程池：
         *  线程数量固定，全部是核心线程
         *  使用共享无界队列来运行线程
         */
        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        ExecutorService executorService = new ThreadPoolExecutor(3, 3,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>());
        for (int i = 0; i < 10; i++) {

            int finalI = (i+1);
            executorService.execute(() -> {
                try {
                    // 打印正在执行的缓存线程信息
                    System.out.println(Thread.currentThread().getName()
                            + "正在被执行第"+ finalI +"个任务");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
