package net.mindview.util;

import java.util.ArrayList;

/**
 * @program: jdk_study
 * @description:
 * @author: YeDongYu
 * @create: 2020-04-13 19:21
 */
public class CollectionData<T> extends ArrayList<T> {

    public CollectionData(Generator<T> gen, int quantity) {
        while (quantity>0){
            add(gen.next());
            quantity--;
        }
    }

    public static <T> CollectionData<T> list(Generator<T> gen,int quantity){
        return new CollectionData<>(gen,quantity);
    }
}
