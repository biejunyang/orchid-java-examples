package com.orchid.examples.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioServer {

    private static Map<SocketChannel, LinkedList<String>> dataMap=new HashMap<>();

    private static Processor processor=new Processor();

    /**
     * 一个线程可以处理多个请求连接、但是处理的多个请求之间中并不会受到其中某个请求的IO阻塞的影响、
     * 因为使用的是NIO、非阻塞io处理
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8888));
        serverSocketChannel.configureBlocking(false); //设置服务端操作都是非阻塞的

        Selector selector = Selector.open(); //选择器

        //向Selector选择中注册ServerSocketChannel通道、并且监听该通道的Accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); //对客户端的accept事件关心



        //轮询Selector中注册的所有通道上的事件
        while (true) {

            selector.select(); //会阻塞住，直到有事件触发

            if(selector.selectNow()<0){//不阻塞
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys(); //看下有哪些事件被触发了
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    createChannel(serverSocketChannel, key);
                } else if (key.isReadable()) {
//                    doRead(key);
                    Processor processor = (Processor) key.attachment();
                    processor.process(key);
                    key.cancel();
                }
                else if(key.isWritable()){
                    doWrite(key);
                }
                iterator.remove();
            }


        }
    }


    /**
     * 创建客户端连接通道
     * @param selectionKey
     */
    private static void createChannel(ServerSocketChannel serverSocketChannel, SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel= serverSocketChannel.accept();
        System.out.println("Accepted connection from :"+socketChannel);
        socketChannel.configureBlocking(false);//设置为非阻塞IO
        String content="Welcome:"+socketChannel.getRemoteAddress()+"\nthe Thread assigned to you is :" +Thread.currentThread().getId()+"\n";

        socketChannel.write(ByteBuffer.wrap(content.getBytes()));
        dataMap.put(socketChannel, new LinkedList<>());

        //向Selector中注册客户端SocketChannel通道、并监听其该通道的度事件
        SelectionKey readKey=socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        readKey.attach(processor);

    }


    /**
     * Read 操作
     * @param selectionKey
     */
    private static void doRead(SelectionKey selectionKey) throws IOException {
        System.out.println("Reading...........");
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        int read=socketChannel.read(byteBuffer);
        if(read == -1){
            doClose(socketChannel);
        }else{
            byteBuffer.flip();

            //直接打印
            String val=new String(byteBuffer.array(),0,read);
            byteBuffer.clear();
            if(val.equals("q")){
                doClose(socketChannel);
            }else{
                dataMap.get(socketChannel).add(val);
                System.out.println(val);
                selectionKey.interestOps(SelectionKey.OP_WRITE);//修改监听的事件为write操作
            }
        }
    }


    /**
     * Write 操作
     * @param selectionKey
     */
    private static void doWrite(SelectionKey selectionKey) throws IOException {
        System.out.println("Writing...........");
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
        LinkedList<String> byteBuffers=dataMap.get(socketChannel);
        while(!byteBuffers.isEmpty()){
            String content=byteBuffers.poll();
            socketChannel.write(ByteBuffer.wrap(content.getBytes()));
        }
        selectionKey.interestOps(SelectionKey.OP_READ);//修改监听的事件为Read操作
    }


    /**
     * 关闭通道(连接)
     * @param socketChannel
     */
    private static void doClose(SocketChannel socketChannel) throws IOException {
        dataMap.remove(socketChannel);
        Socket socket=socketChannel.socket();
        System.out.println("Connection closed by clients:"+socket.getRemoteSocketAddress());
        socket.close();
    }



}
