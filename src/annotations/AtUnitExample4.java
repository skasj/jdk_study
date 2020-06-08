//: annotations/AtUnitExample4.java
package annotations;

import static net.mindview.util.Print.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.mindview.atunit.Test;
import net.mindview.atunit.TestObjectCreate;
import net.mindview.atunit.TestProperty;
import net.mindview.util.OSExecute;

public class AtUnitExample4 {

    static String theory = "All brontosauruses " +
            "are thin at one end, much MUCH thicker in the " +
            "middle, and then thin again at the far end.";

    private String word;

    private Random rand = new Random(); // Time-based seed

    public AtUnitExample4(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public String scrambleWord() {
        List<Character> chars = new ArrayList<Character>();
        for (Character c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, rand);
        StringBuilder result = new StringBuilder();
        for (char ch : chars) {
            result.append(ch);
        }
        return result.toString();
    }

    @TestProperty
    static List<String> input =
            Arrays.asList(theory.split(" "));

    @TestProperty
    static Iterator<String> words = input.iterator();

    @TestObjectCreate
    static AtUnitExample4 create() {
        if (words.hasNext()) {
            return new AtUnitExample4(words.next());
        } else {
            return null;
        }
    }

    @Test
    boolean words() {
        print("'" + getWord() + "'");
        return getWord().equals("All");
    }

    @Test
    boolean scramble1() {
        // Change to a specific seed to get verifiable results:
        rand = new Random(47);
        print("'" + getWord() + "'");
        String scrambled = scrambleWord();
        print(scrambled);
        return scrambled.equals("ntsaueorosurbs");
    }

    @Test
    boolean scramble2() {
        rand = new Random(74);
        print("'" + getWord() + "'");
        String scrambled = scrambleWord();
        print(scrambled);
        return scrambled.equals("are");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("starting");
        System.out.println(Arrays.toString(input.toArray()));
        String path = AtUnitExample4.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // 测试结果受执行顺序影响，不是一个合格的测试方法
        OSExecute.command(
                "java -cp $CLASS_PATH;" + path + ";" + path
                        + "lib/ net.mindview.atunit.AtUnit out/production/jdk_study/annotations/AtUnitExample4");
    }
} /* Output:
starting
annotations.AtUnitExample4
  . scramble1 'All'
lAl

  . scramble2 'brontosauruses'
tsaeborornussu

  . words 'are'

OK (3 tests)
*///:~
