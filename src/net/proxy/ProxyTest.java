package net.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: jdk_study
 * @description: 测试JDK的代理接口
 * @author: YeDongYu
 * @create: 2020-04-20 15:39
 */
public class ProxyTest {

    static interface Subject {

        void sayHi();

        void sayHello();
    }

    static class SubjectImpl implements Subject {

        @Override
        public void sayHi() {
            System.out.println("hi");
        }

        @Override
        public void sayHello() {
            System.out.println("hello");
        }
    }

    /**
     * 实现InvocationHandler
     * 在此处可以实现调用代理接口方法前后的具体逻辑。
     */
    static class ProxyInvocationHandler implements InvocationHandler {

        private Subject target;

        public ProxyInvocationHandler(Subject target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.print("say ");
            return method.invoke(target, args);
        }
    }

    public static void main(String[] args) {
        Subject subject = new SubjectImpl();
        // Proxy.newProxyInstance() 动态的创建了一个代理类
        // newProxyInstance方法执行了以下几种操作:
        // 1.生成一个实现了参数interfaces里所有接口且继承了Proxy的代理类的字节码，然后用参数里的classLoader加载这个代理类。
        // 2.使用代理类父类的构造函数 Proxy(InvocationHandler h)来创造一个代理类的实例，将我们自定义的InvocationHandler的子类传入。
        // 3.返回这个代理类实例。
        Subject subjectProxy = (Subject) Proxy
                .newProxyInstance(subject.getClass().getClassLoader(), subject.getClass().getInterfaces(),
                        new ProxyInvocationHandler(subject));
        subjectProxy.sayHi();
        subjectProxy.sayHello();
    }
}
