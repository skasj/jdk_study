package my;

import java.nio.ByteBuffer;

public class DirectByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(10);
        buffer.put((byte)2);
        buffer.put((byte)3);
        System.out.println(buffer.limit());
        System.out.println(buffer.position());
        buffer.flip();
        System.out.println(buffer.limit());
        System.out.println(buffer.position());
    }
}
