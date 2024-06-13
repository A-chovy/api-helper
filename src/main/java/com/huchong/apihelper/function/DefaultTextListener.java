package com.huchong.apihelper.function;

import org.apache.http.entity.ContentType;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author huchong
 * @create 2024-06-11 17:59
 * @description
 */
public class DefaultTextListener implements FocusListener {

    private String remindText;

    private JTextComponent component;

    public DefaultTextListener(ContentType defaultText, JTextArea textArea) {
    }

    public DefaultTextListener(String remindText, JTextComponent component) {
        this.remindText = remindText;
        this.component = component;
    }

    public String getRemindText() {
        return remindText;
    }

    public void setRemindText(String remindText) {
        this.remindText = remindText;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (remindText.equals(component.getText()) || remindText.length() > 100) {
            component.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (component.getText().isEmpty()) {
            component.setText(remindText);
        }
    }
}
