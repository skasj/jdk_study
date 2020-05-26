package my;

import net.containers.Test;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-05-26 16:00
 */
public class FinalTest {

    class FinalMethodClass{
        public final void Test(){
            System.out.println("public final method");
        }
    }

    class ExtendFinalMethodClass extends FinalMethodClass{
        // Test方法无法被继承，因为使用了final修饰符
        /*public void Test(){
            System.out.println("public final method");
        }*/
    }
}
