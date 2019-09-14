package com.ss.lark.tools.util;

import java.util.*;

/**
 * Created by zyl06 on 2019/9/8.
 */
public class CollectionUtil {

    public static <T> Collection<T> nonNull(Collection<T> in) {
        return (in == null) ? new ArrayList<T>() : in;
    }

    public static <T> Iterable<T> iterable(T[] in) {
        return (in == null) ?
                new ArrayList<T>() :
                Arrays.asList(in);
    }

    public static <T> boolean isEmpty(T[] in) {
        return in == null || in.length == 0;
    }

    public static <T> boolean isEmpty(Collection<T> in) {
        return in == null || in.isEmpty();
    }
}
