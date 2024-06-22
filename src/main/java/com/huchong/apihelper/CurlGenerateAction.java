package com.huchong.apihelper;

import com.huchong.apihelper.ui.SelectUi;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author huchong
 * @create 2024-06-11 09:55
 * @description curl生成器
 */
public class CurlGenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        new SelectUi(event);
    }
}
