package com.orchid.examples.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer2 {


    /**
     * 2、多线程处理并发的请求，主线程监听请求连接。
     * 支持请求并发、一个线程处理一个请求，并发请求过多时，占用内存空间太大。
     * 其他不说，假设有1百万个连接，按照一个连接最少64k来算，64k*1000000 约 61G
     * （关于一个线程需要多少内存，可以看这个启动一个线程所需内存）按这么算，当连接足够多时，服务端啥都不用干，内存就会被撑爆。
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket=new ServerSocket(8088);

        //等待客户端连接
        while (true){
            final Socket socket=serverSocket.accept();//阻塞方法，直到有客户端连接进来
            System.out.println("有连接进入！！！！！！！！！！！！！！");
             new Thread() {
                @Override
                public void run() {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
