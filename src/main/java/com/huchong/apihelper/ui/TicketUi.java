package com.huchong.apihelper.ui;

import com.huchong.apihelper.function.DefaultTextListener;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.huchong.apihelper.util.Constants.DEFAULT_TEXT;


/**
 * @author huchong
 * @create 2024-06-11 09:57
 * @description
 */
public class TicketUi extends JDialog{
    private JTextArea textArea;
    private JButton curlButton;
    private JLabel ticket;

    public TicketUi() {
    }

    public TicketUi(AnActionEvent event) {

        // 给输入框默认文案
        textArea.addFocusListener(new DefaultTextListener(DEFAULT_TEXT, textArea));

        curlButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                // 根据选择的类型，生成对应的panel类
                String text = DEFAULT_TEXT.equals(textArea.getText()) ? "" : textArea.getText();
            }
        });
    }
}
