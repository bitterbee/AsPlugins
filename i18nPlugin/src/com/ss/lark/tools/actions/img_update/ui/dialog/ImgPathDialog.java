package com.ss.lark.tools.actions.img_update.ui.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.ss.lark.tools.util.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zyl06 on 7/25/16.
 */
public class ImgPathDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;
    private TextFieldWithBrowseButton guiTextField;
    private TextFieldWithBrowseButton gitTextField;

    private JTextField moduleFilter;
    private JComboBox moduleCombolBox;
    private String moduleComboBoxText;

    private int mTaskId = 0;
    //    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();

    public ImgPathDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("mipmap_git 目录");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        guiTextField.addBrowseFolderListener("Please choose mipmap_git path", "The path where mipmap_git is cloned", null, descriptor);
        myPreferredFocusedComponent = guiTextField;

        String gitPath = CommandUtil.getGitCommandPath(project);
        if (!TextUtils.isEmpty(gitPath)) {
            gitTextField.setText(gitPath);
        }

        String mipmapGitPath = ConfigUtil.getImageGitPath();
        if (!TextUtils.isEmpty(mipmapGitPath)) {
            guiTextField.setText(mipmapGitPath);
        }

        //====
        List<String> modulePaths = getModuleRelativePaths(null);
        for (String module : modulePaths) {
            moduleCombolBox.addItem(module);
        }
        String modulePath = ConfigUtil.getModulePath();
        if (modulePaths.contains(modulePath)) {
            moduleCombolBox.setSelectedItem(modulePath);
        }
        moduleCombolBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    moduleComboBoxText = (String) e.getItem();
                }
            }
        });
        moduleComboBoxText = (String) moduleCombolBox.getSelectedItem();

        moduleFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                post(moduleFilter.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                post(moduleFilter.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                post(moduleFilter.getText());
            }
        });
        //====

        return contentPane;
    }

    public String getGitPath() {
        return guiTextField.getText().trim();
    }

    public String getGitCmdPath() {
        return gitTextField.getText().trim();
    }

    protected void doOKAction() {
        if (!PathUtil.isPathValid(getGitPath())) {

            DialogEarthquakeShaker.shake((JDialog)ImgPathDialog.this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
    }

    //=====
    public String getModuleRelativePath() {
//        return i18nTextField.getText().trim();
        return moduleComboBoxText;
    }

    private void post(String filter) {
        Task task = new Task(++mTaskId, filter);
        mExecutor.schedule(task, 600, TimeUnit.MILLISECONDS);
    }

    private class Task implements Runnable {
        private int mId;
        private String mFilter;

        public Task(int id, String filter) {
            this.mId = id;
            this.mFilter = filter;
        }

        @Override
        public void run() {
            if (this.mId == mTaskId) {
                updateList(mFilter);
            }
        }
    }

    private void updateList(String filter) {
        moduleCombolBox.removeAllItems();
        for (String yaml : getModuleRelativePaths(filter)) {
            moduleCombolBox.addItem(yaml);
        }
    }

    // 返回相对路径
    private List<String> getModuleRelativePaths(String filter) {
        List<String> modulePaths = new ArrayList<String>();

        List<String> subProjPaths = ProjectUtil.findSubProjects(project);
        for (String modulePath : subProjPaths) {
            if (filter != null && !modulePath.contains(filter)) {
                continue;
            }

            File file = new File(project.getBasePath(), modulePath);
            if (file.exists()) {
                modulePaths.add(modulePath);
            }
        }
        Collections.sort(modulePaths);

        return modulePaths;
    }
}
