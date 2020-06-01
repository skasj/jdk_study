//: net/mindview/util/BinaryFile.java
// Utility for reading files in binary form.
package net.mindview.util;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BinaryFile {
  public static byte[] read(File bFile) throws IOException{
    BufferedInputStream bf = new BufferedInputStream(
      new FileInputStream(bFile));
    try {
      byte[] data = new byte[bf.available()];
      bf.read(data);
      return data;
    } finally {
      bf.close();
    }
  }
  public static byte[]
  read(String bFile) throws IOException {
    return read(new File(bFile).getAbsoluteFile());
  }

  public static void main(String[] args) throws IOException {
    byte[] read = BinaryFile.read(new File("src/net/mindview/util/BinaryFile.java"));
    Map<Byte,Integer> map = new HashMap<>();
    Integer count;
    for (byte b : read){
      count = map.get(b);
      count = count == null ? 1: ++count;
      map.put(b,count);
    }
    for (Entry entry:map.entrySet()){
        System.out.println((char)Integer.valueOf(entry.getKey().toString()).intValue() + ":" + entry.getValue());
    }
  }
} ///:~
