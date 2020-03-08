package main.java.com.sliumhh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author: sliu
 * @date: 2020/3/8 0008 13:59
 **/
public class NIOServer {

    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer();
        nioServer.start();

    }

    /**
     * 启动方法
     * @throws IOException
     */
    public void start() throws IOException {
        // 1.创建Selector
        Selector selector = Selector.open();
        // 2.创建serverSocketChannel 通道;
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3. 为通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        // 4. 设置通道channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 5. 将通道channel注册到selector上，监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server has started successfully!");
        // 6. 循环等待新接入的连接
        for (; ; ) {
            // 获取可用的channel数量
            int readyChannelNum = selector.select();// 阻塞方法
            if (readyChannelNum == 0) {
                continue;
            }
            // 获取可用channel集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // selectionKey实例
                SelectionKey selectionKey = iterator.next();
                // **移除Set中的当前selectionKey**
                iterator.remove();
                // 7. 根据就绪状态，调用不同方法来处理业务
                // 接入事件
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                // 可读事件
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }

    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        // 与客户端创建连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 设非阻塞工作模式
        socketChannel.configureBlocking(false);
        // 将channel注册到selector上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 回复客户端提示消息
        socketChannel.write(StandardCharsets.UTF_8.encode("您已进入聊天室......"));

    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 1. 从selectionKey中获取已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 2. 创建Buffer，读写channel
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder request = new StringBuilder();
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换读模式
            byteBuffer.flip();
            // 读取内容
            request.append(StandardCharsets.UTF_8.decode(byteBuffer));
        }
        // 3. 将channel再次注册到selector上，监听其他可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 4. 将客户端发送的请求信息广播给其他客户端
        broadcast(selector, socketChannel, request.toString());

    }

    /**
     * 广播消息到其他客户端
     */
    private void broadcast(Selector selector, SocketChannel sourceChannel, String request) {
        // 获取到所有已接入的客户端channel
        Set<SelectionKey> selectionKeySet = selector.keys();
        // 循环向所有channel广播信息
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != sourceChannel) {
                try {
                    ((SocketChannel) targetChannel).write(StandardCharsets.UTF_8.encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
