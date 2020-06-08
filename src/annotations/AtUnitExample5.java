//: annotations/AtUnitExample5.java
package annotations;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import net.mindview.atunit.Test;
import net.mindview.atunit.TestObjectCleanup;
import net.mindview.atunit.TestObjectCreate;
import net.mindview.atunit.TestProperty;
import net.mindview.util.OSExecute;

public class AtUnitExample5 {

    private String text;

    public AtUnitExample5(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    @TestProperty
    static PrintWriter output;

    @TestProperty
    static int counter;

    @TestProperty
    static String path;

    @TestObjectCreate
    static AtUnitExample5 create() {
        String id = Integer.toString(counter++);
        try {
            path = "src/annotations/Test" + id + ".txt";
            output = new PrintWriter(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new AtUnitExample5(id);
    }

    @TestObjectCleanup
    static void
    cleanup(AtUnitExample5 tobj) {
        System.out.println("Running cleanup");
        output.close();
        new File(path).delete();
    }

    @Test
    boolean test1() {
        output.print("test1");
        return true;
    }

    @Test
    boolean test2() {
        output.print("test2");
        return true;
    }

    @Test
    boolean test3() {
        output.print("test3");
        return true;
    }

    public static void main(String[] args) throws Exception {
        String path = AtUnitExample5.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        OSExecute.command(
                "java -cp $CLASS_PATH;" + path + ";" + path
                        + "lib/ net.mindview.atunit.AtUnit out/production/jdk_study/annotations/AtUnitExample5");
    }
} /* Output:
annotations.AtUnitExample5
  . test1
Running cleanup
  . test2
Running cleanup
  . test3
Running cleanup
OK (3 tests)
*///:~
