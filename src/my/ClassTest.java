package my;

import java.lang.reflect.Method;
import javax.xml.stream.XMLStreamException;
import typeinfo.pets.Rat;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-05-27 13:53
 */
public class ClassTest {

    public Object c;

    public ClassTest() {
        class ClassA{};
        c = new ClassA();
    }

    public Object classAObject() {
        class ClassA{
        }
        return new ClassA( );
    }

    public Runnable classWithAnonymousClass() {
        return new Runnable() {
            public void run() {
            }
        };
    }

    public static void main(String[] args) throws NoSuchMethodException, XMLStreamException {
        int[] ints = {1,2,3};
        Class<? extends int[]> aClass = ints.getClass();
        System.out.println(aClass.isArray());
        System.out.println(aClass.getComponentType());
        System.out.println(aClass.getModifiers());

        Rat rat = new Rat("test");
        Class<? extends Rat> ratClass = rat.getClass();
        System.out.println(ratClass.getGenericSuperclass());
        System.out.println(ratClass.getSuperclass());
        System.out.println(ratClass.getModifiers());
        for (Method method:ratClass.getMethods()){
            System.out.println("method getName: " + method.getName());
            System.out.println("getDeclaringClass: " + method.getDeclaringClass().getName());
        }

        ClassTest classTest = new ClassTest();
        Class cls = classTest.classAObject().getClass();

        System.out.print("Local class with Method = ");
        System.out.println(cls.getEnclosingMethod());
        // Local class with Method = public java.lang.Object com.my.java.lang.ClassDemo.classAObject()

        System.out.print("Local class with type = ");
        System.out.println(cls.getTypeName());
        // Local class with type = my.ClassTest$2ClassA

        System.out.print("Local class with name = ");
        System.out.println(cls.getName());
        // Local class with name = my.ClassTest$2ClassA

        System.out.print("Anonymous class with Method = ");
        System.out.println(classTest.classWithAnonymousClass().getClass().getEnclosingMethod());
        // Anonymous class with Method = public java.lang.Runnable com.my.java.lang.ClassDemo.classWithAnonymousClass()

        System.out.print("getEnclosingConstructor() = ");
        System.out.println(classTest.c.getClass().getEnclosingConstructor());
        // getEnclosingConstructor() = public my.ClassTest()

        System.out.print("Local class with Class = ");
        System.out.println(classTest.c.getClass().getEnclosingClass());
        // Local class with Class = class my.ClassTest

    }
}
