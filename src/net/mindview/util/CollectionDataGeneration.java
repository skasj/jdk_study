package net.mindview.util;

import java.util.ArrayList;
import net.mindview.util.RandomGenerator.Integer;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-04-13 20:44
 */
public class CollectionDataGeneration {

    public static void main(String[] args) {
        System.out.println(new ArrayList<>(CollectionData.list(new RandomGenerator.String(9), 15)));
        // 正常调用
        ArrayList<java.lang.Integer> y = new ArrayList<>(CollectionData.list(new RandomGenerator.Integer(), 15));
        System.out.println(y);
        // 使用lambda语法,结果是不同的
        ArrayList<Integer> x = new ArrayList<>(CollectionData.list(Integer::new, 15));
        System.out.println(x);
    }
}
