package io;//: io/FileLocking.java
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.io.*;

public class FileLocking {
  public static void main(String[] args) throws Exception {
    Thread firstLockThread = new Thread(() -> {
      try(FileOutputStream fos= new FileOutputStream("src/io/file.txt")){
        FileLock fl = fos.getChannel().tryLock(); // write 使用独占锁
        if(fl != null) {
          fos.getChannel().write(ByteBuffer.wrap(
                  "Writer ".getBytes(StandardCharsets.UTF_8)));
          System.out.println("Writer Locked File");
          TimeUnit.MILLISECONDS.sleep(3000);
          fl.release();
          System.out.println("Writer Released Lock");
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    });



    Runnable runnable = () -> {
      ByteBuffer buffer = ByteBuffer.allocate(60); // More than needed
      try (FileInputStream fis = new FileInputStream("src/io/file.txt")) {
        FileLock fl = fis.getChannel().tryLock(0,Long.MAX_VALUE,true); // read 使用共享锁
        if (fl != null) {
          fis.getChannel().read(buffer);
          buffer.flip();
          System.out.println(Charset.forName("UTF-8").decode(buffer));
          System.out.println("Reader Locked File");
          fl.release();
          System.out.println("Reader Released Lock");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
    Thread secondLockThread = new Thread(runnable);

    firstLockThread.start();
    TimeUnit.MILLISECONDS.sleep(1000);
    secondLockThread.start();
    TimeUnit.MILLISECONDS.sleep(3000);
    secondLockThread = new Thread(runnable);
    secondLockThread.start();
  }
} /* Output:
Locked File
Released Lock
*///:~
