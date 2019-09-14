package com.ss.lark.tools.util;

import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zyl06 on 20/02/2017.
 */
public class ConfigUtil {

    private static String sProject = "";

    public static void init(Project project) {
        sProject = project.getName();
        sConfig = new HashMap<String, String>();
    }

    private static Map<String, String> sConfig = new HashMap<String, String>();

    private static final String KEY_I18N = "i18n.yaml";

    public static String getAdbPath() {
        return getConfig("adb");
    }

    public static void setAdbPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("adb", path);
        }
    }

    public static String getImageGitPath() {
        return getConfig("image_path");
    }

    public static void setImgGitPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("image_path", path);
        }
    }

    public static String getModulePath() {
        return getConfig("module_path");
    }

    public static void setModulePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("module_path", path);
        }
    }

    public static String getGitCmdPath() {
        return getConfig("git");
    }

    public static void setGitCmdPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig("git", path);
        }
    }

    public static String getXlogPkgName() {
        return getConfig("xlog_pkgname");
    }

    public static void setXlogPkgName(String pkgName) {
        if (!TextUtils.isEmpty(pkgName)) {
            setConfig("xlog_pkgname", pkgName);
        }
    }

    public static String getI18nYamlPath() {
        return getConfig(KEY_I18N);
    }

    public static void setI18nYamlPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            setConfig(KEY_I18N, path);
        }
    }

    static String getConfig(String key) {
        if (sConfig.get(key) != null) {
            return sConfig.get(key);
        }
        String result = getConfigFromFile(key);
        if (result != null) {
            sConfig.put(key, result);
        }
        return result;
    }

    static void setConfig(String key, String path) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(path)) {
            return;
        }

        String oldPath = sConfig.get(key);
        if (!path.equals(oldPath)) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, String> entry : sConfig.entrySet()) {
                object.addProperty(entry.getKey(), entry.getValue());
            }
            object.addProperty(key, path);
            Fio.writeToFile(getConfigPath(), object.toString(), false);
            sConfig.put(key, path);
        }
    }

    private static String getConfigFromFile(String name) {
        String strConfig = Fio.readStringFile(getConfigPath());
        if (!TextUtils.isEmpty(strConfig)) {
            sConfig.putAll(GsonUtil.parseData(strConfig));
            return sConfig.get(name);
        }

        return null;
    }

    private static String getConfigPath() {
        return CommandUtil.isWindows() ? getConfigPath_Windows() : getConfigPath_Mac();
    }

    private static String getConfigPath_Windows() {
        return PathUtil.get("c:Users", "Public", sProject + "ee_plugin.config");
    }

    private static String getConfigPath_Mac() {
        return PathUtil.get("~/.config", sProject + "ee_plugin.config");
    }
}
