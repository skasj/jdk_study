package my;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-06-08 17:23
 */
public class GoToTest {

    public static void main(String[] args) {
        System.out.print("result:");
        retry:// 1（行2）
        for (int i = 0; i < 10; i++) {
//            retry:// 死循环
            while (i == 5) {
//                continue retry; // result:0 1 2 3 4 6 7 8 9
//                continue; // result:0 1 2 3 4
//                break;  // result:0 1 2 3 4 5 6 7 8 9
                break retry;  // result:0 1 2 3 4
            }
            System.out.print(i + " ");
        }
    }

}
