package com.ss.lark.tools.util;

/**
 * Created by zyl06 on 2019/6/3.
 */
public class TextUtils {

    public static boolean isEmpty(final CharSequence s) {
        if (s == null) {
            return true;
        }
        return s.length() == 0;
    }
}
