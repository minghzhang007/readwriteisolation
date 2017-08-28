package com.lewis.readwriteisolation2;

import java.sql.Connection;

/**
 * Created by Administrator on 2017/8/28.
 */
public interface ConnectionProxy extends Connection {

    /**
     * 根据传入的读写分离需要的key路由到正确的connection
     * @param key 数据源标识
     * @return
     */
    Connection getTargetConnection(String key);
}
