package com.huchong.apihelper.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huchong
 * @create 2024-06-11 17:43
 * @description curl生成工具
 */
public class CurlUtil {

    private static final String WEB_REQUEST_PREFIX = "/wapi";

    private static final String APP_REQUEST_PREFIX = "/api";

    private static final AtomicReference<Integer> PLAT_FORM = new AtomicReference<>(0);

    // qa环境地址
    private static final String ADDR_WEB = "https://hongkong-victoria-web-qa.weizhipin.com";
    private static final String ADDR_APP = "https://hongkong-victoria-app-qa.weizhipin.com";
    private static final String ADDR_ADMIN = "https://hk-admin-qa.weizhipin.com";

    private static final List<String> NEED_TICKET_LIST = new ArrayList<>();

    static {
        NEED_TICKET_LIST.add("OPTIONAL");
        NEED_TICKET_LIST.add("MUST");
    }

    /**
     * 创建curl文本
     *
     * @param event 事件
     * @return curl文本
     */
    public static String buildCurl(AnActionEvent event) {
        StringBuilder curlText = new StringBuilder();
        StringBuilder bodyText = new StringBuilder();
        StringBuilder ticketText = new StringBuilder();
        StringBuilder jsonText = new StringBuilder();

        // 1、读取路径（类上面的wapi）RequestMapping注解
        StringBuilder requestPath = requestPath(event);
        curlText.append("curl --location '");
        if (requestPath == null) {
            return "";
        }
        curlText.append(requestPath);
        PsiElement element = event.getData(CommonDataKeys.PSI_ELEMENT);
        if (element != null) {
            IElementType elementType = element.getNode().getElementType();
            if ("METHOD".equals(elementType.toString())) {
                // 获取方法
                PsiMethodImpl methodImpl = (PsiMethodImpl) element;
                // 判断noLogin注解和loginApi注解
                PsiAnnotation noLogin = methodImpl.getAnnotation("cn.techwolf.victoria.annotion.NoLogin");
                PsiAnnotation loginApiAnnotation = methodImpl.getModifierList().findAnnotation("cn.techwolf.victoria.annotions.LoginApi");
                if (loginApiAnnotation != null) {
                    PsiAnnotationMemberValue optionValue = loginApiAnnotation.findAttributeValue("option");
                    if (optionValue instanceof PsiReferenceExpression) {
                        PsiElement resolvedElement = ((PsiReferenceExpression) optionValue).resolve();
                        if (resolvedElement instanceof PsiField) {
                            String option = ((PsiField) resolvedElement).getName();
                            if (NEED_TICKET_LIST.contains(option)) {
                                String ticket = MysqlUtil.getTicket(PLAT_FORM.get());
                                if (!"".equals(ticket)) {
                                    ticketText.append("--header 'ticket: ");
                                    ticketText.append(ticket);

                                }
                            }
                        }
                    }
                }
                // 需要补充ticket
                if (loginApiAnnotation == null && noLogin == null) {
                    String ticket = MysqlUtil.getTicket(PLAT_FORM.get());
                    if (!"".equals(ticket)) {
                        ticketText.append("--header 'ticket: ");
                        ticketText.append(ticket);
                    }
                }
                PsiAnnotation postReq = methodImpl.getAnnotation("org.springframework.web.bind.annotation.PostMapping");
                PsiAnnotation getReq = methodImpl.getAnnotation("org.springframework.web.bind.annotation.GetMapping");
                if (postReq != null) {
                    // post 请求
                    buildPostCurl(postReq, bodyText, methodImpl, curlText, jsonText, ticketText);
                } else if (getReq != null) {
                    // getReq 请求
                    buildGetCurl(getReq, methodImpl, curlText, ticketText);
                }
                if (!"".contentEquals(bodyText)) {
                    curlText.append(bodyText);
                }
            }
        }
        return curlText.toString();
    }

    /**
     * 构建get请求
     *
     * @param getReq     get请求参数
     * @param method     方法
     * @param curlText   curl文本
     * @param ticketText ticket文本
     */
    private static void buildGetCurl(PsiAnnotation getReq, PsiMethodImpl method, StringBuilder curlText, StringBuilder ticketText) {
        // get请求
        PsiAnnotationMemberValue getValue = getReq.findDeclaredAttributeValue("value");
        if (getValue instanceof PsiLiteralExpression) {
            String valueString = ((PsiLiteralExpression) getValue).getValue().toString();
            curlText.append(valueString);
        }
        // get请求参数
        PsiParameter[] parameters = method.getParameterList().getParameters();
        List<PsiParameter> parameterList = new LinkedList<>();
        for (PsiParameter parameter : parameters) {
            PsiAnnotation requestParamAnno = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestParam");
            if (requestParamAnno != null) {
                parameterList.add(parameter);
            }
        }
        if (parameterList.size() == 1) {
            curlText.append("?").append(parameterList.get(0).getName()).append("=");
        } else {
            for (int i = 0; i < parameterList.size(); i++) {
                if (i == 0) {
                    curlText.append("?").append(parameterList.get(i).getName()).append("=&");
                } else if (i == parameterList.size() - 1) {
                    curlText.append(parameterList.get(i).getName()).append("=");
                } else {
                    curlText.append(parameterList.get(i).getName()).append("=&");
                }
            }
        }
        curlText.append("' \\").append("\n");
        if (!"".contains(ticketText)) {
            ticketText.append("' \\").append("\n");
            curlText.append(ticketText);
        }
    }

    /**
     * post请求
     *
     * @param postReq    post请求参数
     * @param bodyText   请求体
     * @param method     方法
     * @param curlText   curl文本
     * @param jsonText   json文本
     * @param ticketText ticket文本
     */
    private static void buildPostCurl(PsiAnnotation postReq, StringBuilder bodyText,
                                      PsiMethodImpl method, StringBuilder curlText, StringBuilder jsonText, StringBuilder ticketText) {
        // post请求
        PsiAnnotationMemberValue postValue = postReq.findDeclaredAttributeValue("value");
        if (postValue instanceof PsiLiteralExpression) {
            String valueString = ((PsiLiteralExpression) postValue).getValue().toString();
            curlText.append(valueString);
        }
        // 处理请求body
        bodyText.append("--data '{").append("\n");
        for (PsiParameter parameter : method.getParameterList().getParameters()) {
            PsiAnnotation requestBodyAnno = parameter.getAnnotation("org.springframework.web.bind.annotation.RequestBody");
            if (requestBodyAnno != null) {
                PsiType type = parameter.getType();
                if (type instanceof PsiClassType) {
                    PsiClass psiClass = ((PsiClassType) type).resolve();
                    if (psiClass != null) {
                        PsiField[] fields = psiClass.getFields();
                        for (int i = 0; i < fields.length; i++) {
                            String fieldName = fields[i].getName();
                            if (i == fields.length - 1) {
                                bodyText.append("\"").append(fieldName).append("\"").append(": ").append("\n");
                            } else {
                                bodyText.append("\"").append(fieldName).append("\"").append(": ").append(",").append("\n");
                            }
                        }
                    }
                }
            }
        }
        bodyText.append("}'");
        curlText.append("' \\").append("\n");
        jsonText.append("--header 'Content-Type: application/json' \\").append("\n");
        if (!"".contains(ticketText)) {
            ticketText.append("' \\").append("\n");
            curlText.append(ticketText);
        }
        curlText.append(jsonText);
    }

    /**
     * 请求路径
     *
     * @param event 事件
     * @return 请求路径
     */
    public static StringBuilder requestPath(AnActionEvent event) {
        PsiClass psiClass = getPsiClassFromEvent(event);
        if (psiClass == null) {
            return null;
        }
        PsiAnnotation requestMappingAnnotation = psiClass.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
        StringBuilder pathRes = new StringBuilder();
        if (requestMappingAnnotation != null) {
            PsiAnnotationMemberValue value = requestMappingAnnotation.findDeclaredAttributeValue("value");
            if (value instanceof PsiExpression) {
                String requestMappingValue = ((PsiExpression) value).getText();
                // 处理获取到的RequestMapping注解值，如"WEB_REQUEST_PREFIX + \"/candidate\""
                if (requestMappingValue.contains("APP_REQUEST_PREFIX")) {
                    pathRes.append(ADDR_APP);
                    PLAT_FORM.set(0);
                } else if (requestMappingValue.contains("WEB_REQUEST_PREFIX")) {
                    pathRes.append(ADDR_WEB);
                    PLAT_FORM.set(1);
                } else {
                    pathRes.append(ADDR_ADMIN);
                    PLAT_FORM.set(1);
                }

                String newRequestMappingValue = requestMappingValue
                        .replace("RequestMappingConstants.ADMIN_REQUEST_PREFIX", APP_REQUEST_PREFIX)
                        .replace("RequestMappingConstants.WEB_REQUEST_PREFIX", WEB_REQUEST_PREFIX)
                        .replace("RequestMappingConstants.APP_REQUEST_PREFIX", APP_REQUEST_PREFIX)
                        .replace("WEB_REQUEST_PREFIX", WEB_REQUEST_PREFIX)
                        .replace("APP_REQUEST_PREFIX", APP_REQUEST_PREFIX)
                        .replace("ADMIN_REQUEST_PREFIX", APP_REQUEST_PREFIX);
                String path = newRequestMappingValue.replace("\"", "");
                String[] splitArr = path.split("\\+");
                for (String word : splitArr) {
                    pathRes.append(word.trim());
                }
            }
        }
        return pathRes;
    }

    /**
     * 获取 PsiClass
     *
     * @param event 事件
     * @return PsiClass
     */
    public static PsiClass getPsiClassFromEvent(AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (psiFile != null) {
            PsiElement element = psiFile.findElementAt(event.getData(PlatformDataKeys.CARET).getOffset());
            if (element != null) {
                return PsiTreeUtil.getParentOfType(element, PsiClass.class);
            }
        }
        return null;
    }
}
