package com.lewis.readwriteisolation.loadData.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class InsertSqlHolder {

    private BlockingQueue<String> insertSqlQueue = new LinkedBlockingDeque<>();

    public String take() throws InterruptedException {
        String insertSql = insertSqlQueue.take();
        return insertSql;
    }

    public void put(String sql) {
        try {
            insertSqlQueue.put(sql);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
