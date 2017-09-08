package com.lewis.readwriteisolation.loadData.core.executor;

/**
 * Created by zhangminghua on 2016/11/11.
 */
public final class MyExecutor {

    private MyExecutor() {
    }

    public static TaskProcessor getCommonTaskProcessor() {
        return TaskProcessManager.getTaskProcessor();
    }

    public static TaskProcessor getTaskProcessor(String domain) {
        return TaskProcessManager.getTaskProcessor(domain);
    }
}
