package com.huchong.apihelper.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author huchong
 * @create 2024-06-11 20:09
 * @description jdbc mysql工具
 */
public class MysqlUtil {

    private static final String JDBC_URL = "jdbc:mysql://onedb-boss-qa.weizhipin.com:3316/boss_victoria";
    private static final String USERNAME = "j__pro_drlv";
    private static final String PASSWORD = "RFn7QrkcBX8tFD0gah";

    public static String getTicket(Integer platform) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();
            String sql = "select ticket from user_passport_ticket where identity = " +
                    platform + " order by id desc limit 1";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                // Process the query results
                return resultSet.getString("ticket");
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
