import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author: sliu
 * @date: 2020/3/8 0008 16:58
 **/
public class BufferTest {
    public static void main(String[] args) {

        /*
         * 创建一个新的 ByteBuffer
         * position:0
         * limit:10
         * capacity:10
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        /*
         * 向byteBuffer中写入三个字节
         *  position:3
         * limit:10
         * capacity:10
         */
        byteBuffer.put("abc".getBytes(StandardCharsets.UTF_8));

        /*
         * 将byteBuffer从写模式切换到读模式
         * position：0
         * limit：3 最多能读取的数量
         * capacity：10
         */
        byteBuffer.flip();

        // 读取一个字节
        byteBuffer.get();

        // 调用mark方法，记录当前position的位置
        byteBuffer.mark();

        /**
         * 先调用get方法，读取下一个字节，position=2，
         * 调用reset方法，position重置到mark位置
         */
        byteBuffer.get();
        byteBuffer.reset();

        // 将所有属性重置
        byteBuffer.clear();
    }

}
