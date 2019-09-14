package com.ss.lark.tools.actions.i18n;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.ss.lark.tools.EEConsole;
import com.ss.lark.tools.actions.i18n.dialog.I18nYamlDialog;
import com.ss.lark.tools.util.CommandUtil;
import com.ss.lark.tools.util.ConfigUtil;
import com.ss.lark.tools.util.TextUtils;

import java.io.File;

/**
 * Created by zyl06 on 2019/6/3.
 */
public class I18nAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project project = anActionEvent.getData(DataKeys.PROJECT);

        if (project != null) {
            ConfigUtil.init(project);

            File i18nTool = new File(project.getBasePath(), "i18n-client");
            if (!i18nTool.exists()) {
                Messages.showMessageDialog("Please download i18n-client in project root path first.\n" +
                        " http://10.8.119.154:8000/i18n-client_mac", "Error", Messages.getErrorIcon());
                return;
            }
            
            I18nYamlDialog dialog = new I18nYamlDialog(project);
            dialog.show();

            if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                return;
            }

            try {
                startUpdateResProcess(project, dialog.getI18nPath());
            } catch (Exception e) {
                e.printStackTrace();
                String error = e.getMessage() + "   \n  " + e.getCause();

                for (StackTraceElement stackTrace : e.getStackTrace()) {
                    error += stackTrace.toString() + "\n";
                }

                Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
            }
        }
    }

    private void startUpdateResProcess(Project project, String i18nPath) {
        if (TextUtils.isEmpty(i18nPath)) {
            Messages.showMessageDialog("wrong i18n.yaml path", "Error", Messages.getErrorIcon());
            return;
        }

        ConfigUtil.setI18nYamlPath(i18nPath);

        Process process = doI18nPluginProcess(project, i18nPath);
        if (process == null) {
            return;
        }

        EEConsole.show(project, process, "");
    }

    private Process doI18nPluginProcess(Project project, String i18nPath) {

        if (TextUtils.isEmpty(i18nPath)) {
            Messages.showMessageDialog("empty i18nPath path", "Error", Messages.getErrorIcon());
            return null;
        }

        //
        String[] cmds = CommandUtil.getSystemCmds("./i18n-client -project Lark -platform Android -configFile " + i18nPath);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds, null, new File(project.getBasePath()));
            EEConsole.show(project, process, cmds.toString());

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
            return null;
        }

        return process;
    }
}
