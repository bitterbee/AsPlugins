package com.ss.lark.tools.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zyl06 on 2019/9/8.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ImageSuffex {
    String PNG = ".png";
    String JPEG = ".jpeg";
    String JPG = ".jpg";
    String WEBP = ".webp";
    String SVG = ".svg";
}