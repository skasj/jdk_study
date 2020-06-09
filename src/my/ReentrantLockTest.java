package my;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest implements Runnable {

    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread() + "加锁");
            lock.lock();
            System.out.println(Thread.currentThread() + "加锁成功");
            condition.await();
            System.out.println("满足条件" + Thread.currentThread());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println(Thread.currentThread() + "解锁");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockTest test = new ReentrantLockTest();
        Thread thread = new Thread(test);
        thread.setName("T1");
        thread.start();
        Thread.sleep(2000);
        System.out.println("通知T1条件满足");
        lock.lock();
        condition.signal();
        lock.unlock();
        System.out.println(Thread.currentThread() + "解锁");
    }
}
