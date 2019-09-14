package com.ss.lark.tools.actions.delete_build;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.ss.lark.tools.util.ProjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/9/9.
 */
public class DelBuildAction extends AnAction {

    private List<String> mModuleNames = new ArrayList<String>() {
        {
            add("app/lark-application");
            add("app/lark-bridge");
        }
    };

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
//        Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        Project project = anActionEvent.getData(DataKeys.PROJECT);
        String basePath = project.getBasePath();

        CleanBuildDialog dialog = new CleanBuildDialog(project);
        dialog.show();

        if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return;
        }

        try {
            List<File> buildFiles = new ArrayList<File>();

            tryAddBuildFile(basePath, buildFiles);

            if (dialog.isIncludeModuleProjects()) {
                List<String> moduleNames = ProjectUtil.findSubProjects(project);
                for (String moduleName : moduleNames) {
                    if (mModuleNames.contains(moduleName)) {
                        String parentPath = basePath + File.separator + moduleName;
                        File module = new File(parentPath);
                        tryAddBuildFile(module.getAbsolutePath(), buildFiles);
                    }
                }
            }

            for (File build : buildFiles) {
                deleteRecursive(build);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
        }
    }

    private void tryAddBuildFile(String parentPath, List<File> buildFiles) {
        if (parentPath == null || parentPath.isEmpty()) {
            return;
        }

        String buildPath = parentPath + File.separator + "build";
        File file = new File(buildPath);

        if (file.exists() && file.isDirectory()) {
            buildFiles.add(file);
        }
    }

    private void deleteRecursive(File file) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File sub : subFiles) {
                deleteRecursive(sub);
            }
        }

        file.delete();
    }
}