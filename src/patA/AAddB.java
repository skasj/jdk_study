package patA;

import java.util.Scanner;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2018-08-23 14:33
 */
public class AAddB {

    public static void main(){
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.printf("%,d",a+b);
    }
}
