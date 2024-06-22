package com.huchong.apihelper.ui;

import com.huchong.apihelper.util.Constants;
import com.huchong.apihelper.util.CurlUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author huchong
 * @create 2024-06-21 16:48
 * @description
 */
public class SelectUi extends JDialog{
    private JRadioButton localButton;
    private JRadioButton qaButton;
    private JRadioButton preonButton;
    private JRadioButton prodButton;
    private JButton next;
    private JPanel choosePanel;

    public SelectUi() {
    }

    public SelectUi(AnActionEvent event) {

        //定义按钮组
        ButtonGroup group = new ButtonGroup();
        group.add(localButton);
        group.add(qaButton);
        group.add(preonButton);
        group.add(prodButton);

        localButton.addActionListener(click -> {
            Constants.setChooseType("localhost");
        });
        qaButton.addActionListener(click -> {
            Constants.setChooseType("qa");
        });
        preonButton.addActionListener(click -> {
            Constants.setChooseType("pre");
        });
        prodButton.addActionListener(click -> {
            Constants.setChooseType("prod");
        });
        setContentPane(choosePanel);
        setTitle("CURL Generate");
        setVisible(true);
        setSize(300, 100);
        setLocationRelativeTo(null);
        next.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String curl = CurlUtil.buildCurl(event);
                setVisible(false);
                // 将Curl命令复制到剪贴板
                StringSelection selection = new StringSelection(curl);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                // 显示提示消息
                Notification notification = new Notification("Api-Helper", "Curl Copied", "Curl command has been copied to clipboard", NotificationType.INFORMATION);
                Notifications.Bus.notify(notification);
                // 定时器，在 UI 线程上操作通知
                ApplicationManager.getApplication().invokeLater(() -> {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            notification.expire();
                            timer.cancel();
                        }
                    }, 3000); // 3秒后关闭通知
                });
            }
        });

    }
}
