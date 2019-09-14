package com.ss.lark.tools.util;

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * Created by zyl06 on 2016/7/29.
 */
public class PathUtil {


    public static boolean isPathValid(String path) {
        return CommandUtil.isWindows() ?
                isPathValid_Windows(path) :
                !path.isEmpty();
    }

    private static boolean isPathValid_Windows(String path) {
        if (TextUtils.isEmpty(path) || path.length() <= 2) {
            return false;
        }

        return isAlphabet(path.charAt(0)) && (':' == path.charAt(1));
    }

    private static boolean isAlphabet(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    public static String getGitPath() {
        return ConfigUtil.getConfig("git");
    }

    public static void setGitPath(String gitPath) {
        ConfigUtil.setConfig("git", gitPath);
    }

    public static String getAndroidStudioTemplatePath(String asPath) {
        return CommandUtil.isWindows() ?
                asPath + File.separator + PathUtil.get("plugins", "android", "lib", "templates", "activities") :
                asPath + File.separator + PathUtil.get("Contents", "plugins", "android", "lib", "templates", "activities");
    }

    public static String get(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
            sb.append(File.separator);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public static File getTmpFile(Project project, String fileName) {
        String basePath = project.getBasePath();
        String path = basePath + File.separator + ".aosplugin" + File.separator + fileName;
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }
}
