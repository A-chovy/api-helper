package com.huchong.apihelper.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huchong
 * @create 2024-06-11 17:56
 * @description 常量对象
 */
public class Constants {

    public static final String DEFAULT_TEXT = "默认取数据库第一个";

    private static String CHOOSE_TYPE;

    // 环境 -（端，url）
    public static final Map<String, Map<String, String>> URL_MAP = new HashMap<>();

    static {
        // 本地环境
        Map<String, String> localMap = new HashMap<>();
        localMap.put("web", "localhost:9396");
        localMap.put("app", "localhost:9393");
        localMap.put("admin", "localhost:9399");
        URL_MAP.put("localhost", localMap);
        // qa环境
        Map<String, String> qaMap = new HashMap<>();
        qaMap.put("web", "https://hongkong-victoria-web-qa.weizhipin.com");
        qaMap.put("app", "https://hongkong-victoria-app-qa.weizhipin.com");
        qaMap.put("admin", "https://hk-admin-qa.weizhipin.com");
        URL_MAP.put("qa", qaMap);
        // 预发环境
        Map<String, String> preMap = new HashMap<>();
        preMap.put("web", "https://hongkong-victoria-pre.weizhipin.com");
        preMap.put("app", "https://hongkong-victoria-pre.weizhipin.com");
        preMap.put("admin", "https://hk-admin-pre.weizhipin.com");
        URL_MAP.put("pre", preMap);
        // 生产环境
        Map<String, String> prodMap = new HashMap<>();
        prodMap.put("web", "www.offertoday.com");
        prodMap.put("app", "www.offertoday.com");
        prodMap.put("admin", "www.offertoday.com");
        URL_MAP.put("prod", prodMap);
    }

    public synchronized static String getChooseType() {
        return CHOOSE_TYPE;
    }

    public synchronized static void setChooseType(String chooseType) {
        CHOOSE_TYPE = chooseType;
    }
}
