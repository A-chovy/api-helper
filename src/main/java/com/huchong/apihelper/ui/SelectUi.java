package com.huchong.apihelper.ui;

import com.huchong.apihelper.util.Constants;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private ActionListener nextButtonListener;

    public SelectUi() {
    }

    public void setNextButtonListener(ActionListener listener) {
        this.nextButtonListener = listener;
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
                setVisible(false);
                // 在点击 Next 按钮时触发监听器
                if (nextButtonListener != null) {
                    nextButtonListener.actionPerformed(e);
                }
            }
        });
    }
}
