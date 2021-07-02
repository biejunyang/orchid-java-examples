package com.orchid.example.threadpool;


import java.util.concurrent.*;

/**
 * @author biejunyang
 * @version 1.0
 * @date 2021/6/30 14:55
 */
public class CachedThreadPoolTest {


    public static void main(String[] args) {

        /**
         * 缓存线程池：
         * 没有核心线程，全部线程可以重用和回收，使用同步队列来运行
         * 收到任务后查看是否有可用的线程（没有执行其他任务的队列）,没有的话就创建线程执行
         * 线程默认1分没有任务就回收，默认可以创建无线个线程数，设置最大线程数后，超出数量后拒绝
         * 适合执行耗时短的任务，耗材太长的任务时，会导致线程数量积压，占用过多系统资源
         */
//        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
//        ExecutorService executorService = new ThreadPoolExecutor(0, 3,
//                60L, TimeUnit.SECONDS,
//                new SynchronousQueue<Runnable>());
        for (int i = 0; i < 10; i++) {

            int finalI = (i+1);
            executorService.execute(() -> {
                try {
                    // 打印正在执行的缓存线程信息
                    System.out.println(Thread.currentThread().getName()
                            + "正在被执行第"+ finalI +"个任务");
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName()
                            + "执行完成第"+ finalI +"个任务");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
