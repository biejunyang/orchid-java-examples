package com.orchid.examples.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {


    /**
     * 1、整个接收请求和处理请求都是在同一个线程里（本示例是主线程）当处理客户端请求这一步发生了阻塞，或者说慢了，后来的所有连接请求都会被阻塞住,并且只能串行处理请求。
     * 请求串行处理、不能并发
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket=new ServerSocket(8088);

        //等待客户端连接
        while (true){
            Socket socket=serverSocket.accept();//阻塞方法，直到有客户端连接进来
            System.out.println("有连接进入！！！！！！！！！！！！！！");

            OutputStream os=socket.getOutputStream();
            os.write("hello, world".getBytes("utf-8"));
            os.close();
        }
    }
}
