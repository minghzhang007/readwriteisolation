package com.lewis.readwriteisolation.loadData.core;

import com.lewis.readwriteisolation.loadData.domain.DatasourceAttribute;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection(DatasourceAttribute source) throws SQLException {
        return DriverManager.getConnection(source.getUrl(), source.getUsername(), source.getPassword());
    }

    public static void close(AutoCloseable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
