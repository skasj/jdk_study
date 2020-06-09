package concurrency;//: concurrency/Daemons.java
// Daemon threads spawn other daemon threads.
import java.util.concurrent.*;
import static net.mindview.util.Print.*;

class Daemon implements Runnable {
  private Thread[] t = new Thread[10];

  public void setContinuedRun(boolean continuedRun) {
    this.continuedRun = continuedRun;
  }

  volatile boolean continuedRun = true;
  public void run() {
    for(int i = 0; i < t.length; i++) {
      t[i] = new Thread(new DaemonSpawn());
      t[i].start();
      printnb("DaemonSpawn " + i + " started, ");
    }
    for(int i = 0; i < t.length; i++)
      printnb("t[" + i + "].isDaemon() = " +
        t[i].isDaemon() + ", ");
    while(continuedRun)
      Thread.yield();
    DaemonSpawn.setRun(false);
    try {
      TimeUnit.SECONDS.sleep(9);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(Thread.currentThread().getName() + " is close");
  }
}

class DaemonSpawn implements Runnable {

  public static void setRun(boolean run) {
    isRun = run;
  }

  private static boolean isRun = true;

  public void run() {
    while(isRun)
      Thread.yield();
    System.out.println(Thread.currentThread().getName() + " is close");
  }
}

public class Daemons {
  public static void main(String[] args) throws Exception {
    Daemon target = new Daemon();
    Thread d = new Thread(target);
    d.setDaemon(true);
    d.start();
    printnb("d.isDaemon() = " + d.isDaemon() + ", ");
    // Allow the daemon threads to
    // finish their startup processes:
    TimeUnit.SECONDS.sleep(1);
    target.setContinuedRun(false);
    TimeUnit.SECONDS.sleep(10);
  }
} /* Output: (Sample)
d.isDaemon() = true, DaemonSpawn 0 started, DaemonSpawn 1 started, DaemonSpawn 2 started, DaemonSpawn 3 started, DaemonSpawn 4 started, DaemonSpawn 5 started, DaemonSpawn 6 started, DaemonSpawn 7 started, DaemonSpawn 8 started, DaemonSpawn 9 started, t[0].isDaemon() = true, t[1].isDaemon() = true, t[2].isDaemon() = true, t[3].isDaemon() = true, t[4].isDaemon() = true, t[5].isDaemon() = true, t[6].isDaemon() = true, t[7].isDaemon() = true, t[8].isDaemon() = true, t[9].isDaemon() = true,
*///:~
