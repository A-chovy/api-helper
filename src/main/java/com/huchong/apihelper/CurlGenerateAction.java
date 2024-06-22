package com.huchong.apihelper;

import com.huchong.apihelper.ui.SelectUi;
import com.huchong.apihelper.util.CurlUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author huchong
 * @create 2024-06-11 09:55
 * @description curl生成器
 */
public class CurlGenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        SelectUi selectUi = new SelectUi(event);
        selectUi.setNextButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String curl = CurlUtil.buildCurl(event);
                selectUi.setVisible(false);
                // 将Curl命令复制到剪贴板
                StringSelection selection = new StringSelection(curl);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                // 发送通知消息
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Notification notification = new Notification("Api-Helper", "Curl Copied", "Curl command has been copied to clipboard", NotificationType.INFORMATION);
                        Notifications.Bus.notify(notification);
                        // 定时器，3秒后关闭通知
                        java.util.Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                notification.expire();
                                timer.cancel();
                            }
                        }, 3000); // 3秒后关闭通知
                    }
                });
            }
        });
    }
}
