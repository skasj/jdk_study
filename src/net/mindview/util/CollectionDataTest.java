package net.mindview.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-04-13 20:30
 */
public class CollectionDataTest {


    public static void main(String[] args) {
        Set<String> set = new LinkedHashSet<>(CollectionData.list(new Government(),15));
        set.addAll(new CollectionData<>(new Government(),15));
        System.out.println(set);
    }
}
