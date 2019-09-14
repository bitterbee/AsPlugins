package com.ss.lark.tools.actions.xlog.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.ss.lark.tools.util.CollectionUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ShowXlogsNameDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;
    private JList xlogJList;
    private List<String> xlogPaths = new ArrayList<>();

    public ShowXlogsNameDialog(@Nullable Project project, List<String> xlogs) {
        super(project, true);
        this.project = project;
        setTitle("选择 xlog 文件");
        if (xlogs != null) {
            xlogPaths = xlogs;
        }
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

        myPreferredFocusedComponent = xlogJList;
        xlogJList.setListData(xlogPaths.toArray());

        if (!CollectionUtil.isEmpty(xlogPaths)) {
            xlogJList.setSelectedIndex(0);
        }

        return contentPane;
    }

    public String getXlogPath() {
        int index = xlogJList.getSelectedIndex();
        if (index < xlogPaths.size()) {
            return xlogPaths.get(xlogJList.getSelectedIndex());
        } else {
            return null;
        }
    }

    protected void doOKAction() {
        int index = xlogJList.getSelectedIndex();
        if (index < 0 || index >= xlogPaths.size()) {
            DialogEarthquakeShaker.shake(this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
    }
}
