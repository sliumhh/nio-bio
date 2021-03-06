package com.sliumhh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author: sliu
 * @date: 2020/3/8 0008 15:27
 **/
public class NIOClient {

    /**
     * 启动方法
     * @throws IOException
     */
    public void start(String name) throws IOException {
        // 连接服务器端
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
        System.out.println("client has started successfully!");

        // 接收服务器端响应
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new ClientRequestHandler(selector)).start();
        // 向服务器端发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(StandardCharsets.UTF_8.encode(name + ":" + request));
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        NIOClient nioClient = new NIOClient();
//        nioClient.start();
    }
}
