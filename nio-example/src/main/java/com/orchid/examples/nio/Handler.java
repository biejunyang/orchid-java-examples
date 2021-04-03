package com.orchid.examples.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable {

    private SelectionKey selectionKey;

    public Handler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        System.out.println("current thread:"+Thread.currentThread().getName());

        try {
            System.out.println("start--------------------");
            Thread.sleep(20*1000);

            ByteBuffer buffer = null;
            SocketChannel socketChannel = null;
            try {
                buffer = ByteBuffer.allocate(1024);
                socketChannel = (SocketChannel) selectionKey.channel();
                int count = socketChannel.read(buffer);
                if (count < 0) {
                    socketChannel.close();
                    selectionKey.cancel();
                } else if(count == 0) {
                    buffer.flip();
                    String val=new String(buffer.array(),0,count);
                    buffer.clear();
                    if(val.equals("q")){
                        socketChannel.close();
                        selectionKey.cancel();
                    }else{
                        System.out.println(socketChannel+" Read message "+val);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("end--------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
