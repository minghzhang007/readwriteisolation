package com.lewis.readwriteisolation.core;

public class JdbcContextHolder {

    private static ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static void setJdbcType(String jdbcType) {
        contextHolder.set(jdbcType);
    }

    public static void setSlave() {
        setJdbcType("slave");
    }

    public static void setMaster() {
        setJdbcType("master");
    }

    public static String getJdbcType() {
        return contextHolder.get();
    }

    public static void clearJdbcType() {
        contextHolder.remove();
    }
}
