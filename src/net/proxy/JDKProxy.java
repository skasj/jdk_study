package net.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: jdk_study
 * @description: JDK动态代理类
 * @author: YeDongYu
 * @create: 2020-04-21 10:21
 */
public class JDKProxy implements InvocationHandler {

    // 需要代理的类
    private Object targetObject;

    private Object newProxy(Object targetObject) { //将目标对象传入进行代理
        this.targetObject = targetObject;
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(), targetObject.getClass().getInterfaces(),
                this); // 返回代理对象
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result = null;
        result = method.invoke(targetObject,args);
        after();
        return result;
    }

    private void before() {
        System.out.println("代理方法执行前运行");
    }

    private void after() {
        System.out.println("代理方法执行后运行");
    }
}
