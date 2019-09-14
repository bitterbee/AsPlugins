package com.ss.lark.tools;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * Created by zyl06 on 2019/6/3.
 */
public class EEConsole {
    private static final String ID = "EEConsole";

    private static ConsoleView sConsoleView;
    private static ToolWindow sToolWindow;

    public static void show(Project project, Process process, String commandLine) {
        init(project);

        OSProcessHandler handler = new OSProcessHandler(process, commandLine);
        sConsoleView.attachToProcess(handler);
        handler.startNotify();
    }

    public static void show(Project project, String message) {
        init(project);

        sToolWindow.show(new Runnable() {
            @Override
            public void run() {
                sConsoleView.print(message + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            }
        });
    }

    private static void init(Project project) {
        if (sConsoleView == null) {
            TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
            TextConsoleBuilder builder = factory.createBuilder(project);
            sConsoleView = builder.getConsole();
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        sToolWindow = manager.getToolWindow(ID);
        if (sToolWindow == null) {
            sToolWindow = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);
            try {
                sToolWindow.getComponent().add(sConsoleView.getComponent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
