package my;

public class StaticTest {

    private static int result =  test();

    private static int result2 = 3;

    private static int test(){
        System.out.println(result);
        System.out.println(result2);
        return 1;
    }

    public volatile BaseBean baseBean1 = new BaseBean();

    private int result3;

    {
        result3 =2;
    }

    public static void main(String[] args) {
        System.out.println(result);
        System.out.println(result2);
        StaticTest staticTest = new StaticTest();
        System.out.println(staticTest.baseBean1);
        staticTest.baseBean1 = new BaseBean();
        System.out.println(staticTest.baseBean1);
    }
}
