package my;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceReaderTest {
    public static void main(String[] args) throws IOException {
        ResourceReaderTest test = new ResourceReaderTest();
        Class<? extends ResourceReaderTest> testClass = test.getClass();
//        String fileName = System.getProperty("user.dir")+"/src/io/BufferedInputFile.java";
        String fileName = "application.properties";
        URL resource = testClass.getResource(fileName);
        System.out.println(resource);
        InputStream resourceAsStream = testClass.getResourceAsStream(fileName);
        int byteStr = 0;
        while (byteStr!=-1){
            byteStr = resourceAsStream.read();
            System.out.print((char) byteStr);
        }
    }
}
