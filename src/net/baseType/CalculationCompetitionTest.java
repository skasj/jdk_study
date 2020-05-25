package net.baseType;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-04-22 14:55
 */
public class CalculationCompetitionTest {

    public static void main(String[] args) {
        testIntCalcu();
        testShortCalcu();
    }

    private static void testIntCalcu() {
        int m = 0;
        long t1 = System.currentTimeMillis();
        for (int i = 10000; i > 0; i--) {
            m = 100;
            int tarInt = 0;
            while (m > 0) {
                int n = 100;
                while (n > 0) {
                    tarInt = tarInt + 1;
                    n--;
                }
                tarInt = tarInt + 1;
                m--;
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("int 加1操作1一亿次" + (t2 - t1)+"毫秒");
    }

    private static void testShortCalcu() {
        int m = 0;
        long t1 = System.currentTimeMillis();
        for (int i = 10000; i > 0; i--) {
            m = 100;
            short tarShort = 0;
            while (m > 0) {
                short n = 100;
                while (n > 0) {
                    tarShort = (short) (tarShort + 1);
                    n--;
                }
                tarShort = (short) (tarShort + 1);
                m--;
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("short 加1操作1一亿次" + (t2 - t1)+"毫秒");
    }
}
