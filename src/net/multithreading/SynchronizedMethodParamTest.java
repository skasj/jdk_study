package net.multithreading;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: jdk_study
 * @description: 测试锁定方法入参对象
 * @author: YeDongYu
 * @create: 2020-04-21 16:00
 */
public class SynchronizedMethodParamTest {

    static class Study {

        String name;

        Study(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class ChangeName implements Runnable {

        private Study study;

        private int no;

        public ChangeName(Study study,int no) {
            this.study = study;
            this.no = no;
        }

        @Override
        public void run() {
            test(study);
        }

        private void test(Study study){
            synchronized (study){
//                System.out.println(study.name);
//                System.out.println(no);
                int tmp = no;
                study.setName(String.valueOf(tmp));
//                System.out.println(study.name.equals(String.valueOf(tmp)));
            }
        }
    }

    public static void main(String[] args) {
        int n = 100;
        ExecutorService executors = Executors.newFixedThreadPool(n);
        Study study = new Study("test");
        while (n>1){
            executors.execute(new ChangeName(study,n));
            n--;
        }
        System.out.println("over");
    }
}
