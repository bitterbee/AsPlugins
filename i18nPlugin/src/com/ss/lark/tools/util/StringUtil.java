package com.ss.lark.tools.util;

import java.io.*;

/**
 * Created by zyl06 on 2019/9/13.
 */
public class StringUtil {
    public static String from(String path) {
        String result = null;
        try {
            if (!TextUtils.isEmpty(path)) {
                result = from(new FileInputStream(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String from(InputStream is) {
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream(is.available() + 16);
            int length;
            byte[] bytes = new byte[1024];
            while ((length = is.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }

            return new String(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            Fio.safeClose(is, os);
        }
    }
}
