//: annotations/AtUnitExample3.java
package annotations;
import net.mindview.atunit.*;
import net.mindview.util.*;

public class AtUnitExample3 {
  private int n;
  public AtUnitExample3(int n) { this.n = n; }
  public int getN() { return n; }
  public String methodOne() {
    return "This is methodOne";
  }
  public int methodTwo() {
    System.out.println("This is methodTwo");
    return 2;
  }
  @TestObjectCreate static AtUnitExample3 create() {
    // 此处限制static是基于方法加载顺序的考虑，必须要先加载create方法。如果改动AtUnit，使之先加载@TestObjectCreate注解的方法，那么可以
    // 不使用static关键字
    return new AtUnitExample3(47);
  }
  @Test boolean initialization() { return n == 47; }
  @Test boolean methodOneTest() {
    return methodOne().equals("This is methodOne");
  }
  @Test boolean m2() { return methodTwo() == 2; }
  public static void main(String[] args) throws Exception {
    String path = AtUnitExample3.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    OSExecute.command(
            "java -cp $CLASS_PATH;"+ path +";"+ path+"lib/ net.mindview.atunit.AtUnit out/production/jdk_study/annotations/AtUnitExample3");
  }
} /* Output:
annotations.AtUnitExample3
  . initialization
  . methodOneTest
  . m2 This is methodTwo

OK (3 tests)
*///:~
