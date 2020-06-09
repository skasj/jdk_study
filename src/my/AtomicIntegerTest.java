package my;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-06-09 14:24
 */
public class AtomicIntegerTest extends Thread {

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    private int id;

    AtomicIntegerTest(int id) {
        super();
        this.id = id;
    }

    @Override
    public void run() {
        int i = 100;
        while (i-- > 0) {
            int i1 = atomicInteger.addAndGet(2);
            if (i1 % 2 != 0) {
                System.out.println(i1 + " is not evenNumbers");
            }
        }
        System.out.println("Thread " + id + " is close");
    }

    public static void main(String[] args) {
        int threadNum = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        while (threadNum-- > 0) {
            executorService.execute(new AtomicIntegerTest(threadNum));
        }
        executorService.shutdown();
    }
}
