package com.huchong.apihelper;

import com.huchong.apihelper.util.CurlUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

        String curl = CurlUtil.buildCurl(event);
        // 将Curl命令复制到剪贴板
        StringSelection selection = new StringSelection(curl);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        // 显示提示消息
        Notification notification = new Notification("Api-Helper", "Curl Copied", "Curl command has been copied to clipboard", NotificationType.INFORMATION);
        Notifications.Bus.notify(notification);

        // 定时器，3秒后关闭通知
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notification.expire();
                timer.cancel();
            }
        }, 3000); // 3秒后关闭通知
    }
}
