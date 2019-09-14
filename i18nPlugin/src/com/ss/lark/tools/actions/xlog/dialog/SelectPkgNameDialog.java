package com.ss.lark.tools.actions.xlog.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogEarthquakeShaker;
import com.intellij.openapi.ui.DialogWrapper;
import com.ss.lark.tools.util.CommandUtil;
import com.ss.lark.tools.util.ConfigUtil;
import com.ss.lark.tools.util.TextUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SelectPkgNameDialog extends DialogWrapper {
    private Project project;
    private JPanel contentPane;

    private JComboBox<String> pkgNameComboBox;
    private JTextField adbTextField;
    private String comboBoxText;

    public SelectPkgNameDialog(@Nullable Project project) {
        super(project, true);
        this.project = project;
        setTitle("选择包名");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        setModal(true);

        myPreferredFocusedComponent = pkgNameComboBox;
        pkgNameComboBox.addItem("com.ss.android.lark.debug");
        pkgNameComboBox.addItem("com.larksuite.suite.debug");
        pkgNameComboBox.addItem("com.ss.android.lark");
        pkgNameComboBox.addItem("com.larksuite.suite");
        comboBoxText = ConfigUtil.getXlogPkgName();
        if (TextUtils.isEmpty(comboBoxText)) {
            comboBoxText = pkgNameComboBox.getItemAt(0);
        }

        pkgNameComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    comboBoxText = (String) e.getItem();
                    ConfigUtil.setXlogPkgName(comboBoxText);
                }
            }
        });
        pkgNameComboBox.setSelectedIndex(0);

        adbTextField.setText(CommandUtil.getAdbCmd(project));

        return contentPane;
    }

    public String getPackageName() {
        return comboBoxText;
    }

    public String getAdbCmdPath() {
        return adbTextField.getText();
    }

    protected void doOKAction() {
        if (TextUtils.isEmpty(comboBoxText) || TextUtils.isEmpty(adbTextField.getText())) {
            DialogEarthquakeShaker.shake(this.getPeer().getWindow());
            return;
        }
        super.doOKAction();
        ConfigUtil.setAdbPath(adbTextField.getText());
    }
}
