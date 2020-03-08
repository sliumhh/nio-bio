package com.sliumhh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author: sliu
 * @date: 2020/3/8 0008 19:18
 **/
public class ClientRequestHandler implements Runnable {
    private Selector selector;

    @Override
    public void run() {
        try {
            for (; ; ) {
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                // 获取可用channel的集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    // selectionKey实例
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    // 根据就绪状态，调用对应方法处理业务逻辑
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 要从 selectionKey 中获取到已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 循环读取服务器端响应信息
        StringBuilder response = new StringBuilder();
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换buffer为读模式
            byteBuffer.flip();
            response.append(StandardCharsets.UTF_8.decode(byteBuffer));
        }
        // channel再次注册到selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        if (response.length() > 0) {
            System.out.println(response);
        }
    }

    public ClientRequestHandler(Selector selector) {
        this.selector = selector;
    }
}
