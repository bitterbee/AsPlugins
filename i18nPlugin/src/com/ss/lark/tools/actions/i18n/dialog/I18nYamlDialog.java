package com.ss.lark.tools.actions.i18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.ss.lark.tools.Constants;
import com.ss.lark.tools.util.PathUtil;
import com.ss.lark.tools.util.ProjectUtil;
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

public class I18nYamlDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;
//    private TextFieldWithBrowseButton i18nTextField;

    private JComboBox<String> i18nComboBox;
    private JTextField filterTextField;
    private String comboBoxText;

    private int mTaskId = 0;
//    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();

    public I18nYamlDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("选择 i18n.yaml");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

//        String i18nYamlPath = ConfigUtil.getI18nYamlPath();
//        if (!TextUtils.isEmpty(i18nYamlPath)) {
//            i18nTextField.setText(i18nYamlPath);
//        } else {
//            i18nTextField.setText(project.getBasePath());
//        }
//
//        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
//        i18nTextField.addBrowseFolderListener("Please choose i18n.yaml", "The path of i18n.yaml", null, descriptor);
//        myPreferredFocusedComponent = i18nTextField;


        myPreferredFocusedComponent = i18nComboBox;
        for (String yaml : getYamlRelativePaths(null)) {
            i18nComboBox.addItem(yaml);
        }
        i18nComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    comboBoxText = (String) e.getItem();
                }
            }
        });

        filterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                post(filterTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                post(filterTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                post(filterTextField.getText());
            }
        });

        return contentPane;
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
        i18nComboBox.removeAllItems();
        for (String yaml : getYamlRelativePaths(filter)) {
            i18nComboBox.addItem(yaml);
        }
    }

    public String getI18nPath() {
//        return i18nTextField.getText().trim();
        return project.getBasePath() + "/" + comboBoxText;
    }

    protected void doOKAction() {
        if (!PathUtil.isPathValid(getI18nPath())) {
            DialogEarthquakeShaker.shake(this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
    }

    // 返回相对路径
    private List<String> getYamlRelativePaths(String filter) {
        List<String> yamls = new ArrayList<String>();

        List<String> subProjPaths = ProjectUtil.findSubProjects(project);
        for (String projPath : subProjPaths) {
            String relativePath = projPath + "/" + Constants.I18N_NAME;
            if (filter != null && !relativePath.contains(filter)) {
                continue;
            }

            File file = new File(project.getBasePath(), relativePath);
            if (file.exists()) {
                yamls.add(relativePath);
            }
        }
        Collections.sort(yamls);

        return yamls;
    }
}
