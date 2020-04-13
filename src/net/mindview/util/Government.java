package net.mindview.util;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-04-13 20:26
 */
public class Government implements Generator<String> {

    private String[] foundation = ("strange woman lying in ponds distributing "
            + "swords is no basis for a system of government").split(" ");

    private int index;

    @Override
    public String next() {
        return foundation[index++];
    }
}
