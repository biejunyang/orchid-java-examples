package com.orchid.examples.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer3 {


    /**
     * 3、使用线程池处理并发的请求
     * 支持请求并发、一个线程处理多个请求。
     * 但是单个线程中处理的多个请求时，是串行处理的，即处理的多个请求中，
     * 上一个请求请求IO阻塞时，下一个请求处理的也会IO阻塞。
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket=new ServerSocket(8088);

        ExecutorService threadPool= Executors.newFixedThreadPool(100);

        //等待客户端连接
        while (true){
            final Socket socket=serverSocket.accept();//阻塞方法，直到有客户端连接进来
            System.out.println("有连接进入！！！！！！！！！！！！！！");
            threadPool.submit(() -> {
                    //请求处理
            });
        }
    }
}
