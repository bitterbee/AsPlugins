package com.ss.lark.tools.actions.xlog;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ss.lark.tools.EEConsole;
import com.ss.lark.tools.actions.xlog.dialog.SelectPkgNameDialog;
import com.ss.lark.tools.actions.xlog.dialog.ShowXlogsNameDialog;
import com.ss.lark.tools.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/9/9.
 */
public class ViewXlogAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project project = anActionEvent.getData(DataKeys.PROJECT);

        if (project != null) {
//            if (!project.getName().equals("YanXuan")) {
//                return;
//            }
            ConfigUtil.init(project);

            SelectPkgNameDialog selectPkgDlg = new SelectPkgNameDialog(project);
            selectPkgDlg.show();

            if (selectPkgDlg.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                return;
            }

            try {
                String pkgName = selectPkgDlg.getPackageName();

                List<String> xlogPaths = new ArrayList<>();
                xlogPaths.addAll(listXlogFilePaths(project, pkgName, "databases/sdk_storage/staging/log/xlog"));
                xlogPaths.addAll(listXlogFilePaths(project, pkgName, "databases/sdk_storage/log/xlog"));

                String xlogPath = userSelectXlogFilePath(project, xlogPaths);
                EEConsole.show(project, "user select xlog file path: " + xlogPath);
                if (!TextUtils.isEmpty(xlogPath)) {
                    showXlogContent(project, pkgName, xlogPath);
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
    }

    private List<String> listXlogFilePaths(Project project, String pkgName, String phoneXlogDir) {
        List<String> result = new ArrayList<>();

        String lsCmd = StringUtil.from(Fio.getResourceInputStream(ViewXlogConstants.CMD_LS_PATH));
        if (TextUtils.isEmpty(lsCmd)) {
            Messages.showMessageDialog("read lsCmd error", "Error", Messages.getErrorIcon());
            return result;
        } else {
            lsCmd = lsCmd.replace(ViewXlogConstants.MARK_PACKAGE_NAME, pkgName)
                    .replace(ViewXlogConstants.MARK_PHONE_XLOG_DIR, phoneXlogDir);
        }

        File tmpFile = null;
        String lsCmdPath = null;
        try {
            tmpFile = File.createTempFile("ls_cmd", ".txt");
            tmpFile.deleteOnExit();
            boolean success = Fio.writeToFile(tmpFile.getAbsolutePath(), lsCmd, false);
            if (success) {
                lsCmdPath = tmpFile.getAbsolutePath();
            }
        } catch (IOException e) {
            Messages.showMessageDialog("ls_cmd copy: " + e.toString(), "Error", Messages.getErrorIcon());
            return result;
        }

//        EEConsole.show(project, "ls_cmd: adb shell < " + lsCmdPath);
        String[] cmds = CommandUtil.getSystemCmds( ConfigUtil.getAdbPath() + " shell < " + lsCmdPath);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds, null, new File(project.getBasePath()));
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
            return result;
        }
        if (tmpFile != null && tmpFile.exists()) {
            tmpFile.delete();
        }
        String processOutput = StringUtil.from(process.getInputStream());
        EEConsole.show(project, "ls_cmd result: " + processOutput);
        if (processOutput == null) {
            Messages.showMessageDialog("ls_cmd read error", "Error", Messages.getErrorIcon());
            return result;
        }

        String[] fileNames = processOutput.split("\n");
        for (String fileName : fileNames) {
            result.add(phoneXlogDir + File.separator + fileName);
        }
        return result;
    }

    private String userSelectXlogFilePath(Project project, List<String> xlogPaths) {
        if (CollectionUtil.isEmpty(xlogPaths)) {
            return null;
        }
        List<String> xlogs = new ArrayList<>();
        for (String xlogPath : xlogPaths) {
            if (xlogPath != null && xlogPath.endsWith(ViewXlogConstants.SUFFIX_XLOG)) {
                xlogs.add(xlogPath);
            }
        }
        ShowXlogsNameDialog dlg = new ShowXlogsNameDialog(project, xlogs);
        dlg.show();

        if (dlg.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
            return null;
        }

        return dlg.getXlogPath();
    }

    private void showXlogContent(Project project, String pkgName, String phoneXlogPath) {

        String xlogFileName = new File(phoneXlogPath).getName();
        if (TextUtils.isEmpty(xlogFileName)) {
            xlogFileName = ViewXlogConstants.XLOG_ON_PC_NAME;
        }

        File copyCmdFile = getCopyCmdFile(project, pkgName, phoneXlogPath, xlogFileName);
        if (copyCmdFile == null) {
            return;
        }

        String dirPath = copyCmdFile.getParentFile().getAbsolutePath();
        runCmd(project, ConfigUtil.getAdbPath() + " shell < " + copyCmdFile.getAbsolutePath());
        runCmd(project, ConfigUtil.getAdbPath() + " pull " +
                (ViewXlogConstants.XLOG_TO_PHONE_DIR_PATH + xlogFileName)
                + " "
                + dirPath);
        if (copyCmdFile.exists()) {
            copyCmdFile.delete();
        }

        File xlogDecodeFile = getXlogDecodeFile(project);
        if (xlogDecodeFile == null) {
            return;
        }

        String pcXlogPath = dirPath + File.separator + xlogFileName;
        runCmd(project, "python " + xlogDecodeFile.getAbsolutePath() + " " + pcXlogPath);

        String pcLogPath = pcXlogPath + ViewXlogConstants.SUFFIX_LOG;
        String logContent = StringUtil.from(pcLogPath);
        if (logContent == null) {
            Messages.showMessageDialog("read log file error: " + (pcXlogPath + ViewXlogConstants.SUFFIX_LOG),
                    "Error", Messages.getErrorIcon());
            return;
        }

        File pcLogFile = new File(pcLogPath);
        pcLogFile.deleteOnExit();

        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(pcLogFile);
        virtualFile.refresh(false, true);
        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile), true);
    }

    private File getXlogDecodeFile(Project project) {
        String decodeXlogCmd = StringUtil.from(Fio.getResourceInputStream(ViewXlogConstants.CMD_DECODE_LARK_LOG_PATH));
        if (TextUtils.isEmpty(decodeXlogCmd)) {
            Messages.showMessageDialog("read " + ViewXlogConstants.CMD_DECODE_LARK_LOG_PATH +
                    " error", "Error", Messages.getErrorIcon());
            return null;
        }

        File decodeFile = PathUtil.getTmpFile(project,  "decode_xlog_log.py");
        boolean success = Fio.writeToFile(decodeFile.getAbsolutePath(), decodeXlogCmd, false);
        if (!success) {
            Messages.showMessageDialog("write to file error: " + decodeFile.getAbsolutePath(),
                    "Error", Messages.getErrorIcon());
            decodeFile.delete();
            decodeFile = null;
        }
        return decodeFile;
    }

    private File getCopyCmdFile(Project project, String pkgName, String xlogPath, String xlogName) {
        String copyCmd = StringUtil.from(Fio.getResourceInputStream(ViewXlogConstants.CMD_COPY_PATH));
        if (TextUtils.isEmpty(copyCmd)) {
            Messages.showMessageDialog("read " + ViewXlogConstants.CMD_COPY_PATH +
                    " error", "Error", Messages.getErrorIcon());
            return null;
        }

        copyCmd = copyCmd.replace(ViewXlogConstants.MARK_PACKAGE_NAME, pkgName)
                .replace(ViewXlogConstants.MARK_XLOG_FROM_PATH, xlogPath)
                .replace(ViewXlogConstants.MARK_XLOG_TO_PATH, (ViewXlogConstants.XLOG_TO_PHONE_DIR_PATH + xlogName));

        File cmdFile = null;
        try {
            cmdFile = File.createTempFile("copy_cmd", ".txt");
            cmdFile.deleteOnExit();
            boolean success = Fio.writeToFile(cmdFile.getAbsolutePath(), copyCmd, false);
            if (!success) {
                throw new IOException("write to file error: " + cmdFile.getAbsolutePath());
            }
        } catch (IOException e) {
            if (cmdFile != null) {
                cmdFile.delete();
                cmdFile = null;
            }

            Messages.showMessageDialog("ls_cmd copy error: " + e.toString(), "Error", Messages.getErrorIcon());
        }

        return cmdFile;
    }

    private Process runCmd(Project project, String cmd) {
        String[] cmds = CommandUtil.getSystemCmds(cmd);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds, null, new File(project.getBasePath()));
            process.waitFor();
            return process;
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            EEConsole.show(project, process, cmds.toString());
            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
        }
        return null;
    }
}
