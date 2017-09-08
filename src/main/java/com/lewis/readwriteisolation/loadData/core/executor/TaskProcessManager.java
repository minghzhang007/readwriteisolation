package com.lewis.readwriteisolation.loadData.core.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangminghua on 2016/11/11.
 */
public class TaskProcessManager {

    TaskProcessManager(){}
    /**
     * 默认的业务执行域
     */
    private static final String DEFAULT_BUSINESS_DOMAIN = TaskProcessManager.class.getName();

    /**
     * 执行器的容器，根据业务域来区分<br>
     * 每个业务域只有一个执行器
     */
    private static Map<String,TaskProcessor> taskProcessContainer = new ConcurrentHashMap<String, TaskProcessor>();

    /**
     * 默认异步任务执行器工厂
     */
    private static TaskProcessFactory defaultTaskProcessorFactory = new DefaultTaskProcessorFactory();

    /**
     * 获取执行器
     * @param businessDomain 业务域
     * @param taskProcessFactory 任务处理工厂
     * @return 执行器
     */
    public static TaskProcessor getTaskProcessor(String businessDomain,TaskProcessFactory taskProcessFactory){
        if (taskProcessFactory == null) {
            taskProcessFactory = defaultTaskProcessorFactory;
        }
        TaskProcessor taskProcessor = taskProcessContainer.get(businessDomain);
        if (taskProcessor == null) {
            synchronized (TaskProcessManager.class){
                taskProcessor = taskProcessContainer.get(businessDomain);
                if (taskProcessor == null) {
                    taskProcessor = taskProcessFactory.createTaskProcessor(businessDomain);
                    taskProcessContainer.put(businessDomain,taskProcessor);
                }
            }
        }
        return taskProcessor;
    }

    public static TaskProcessor getTaskProcessor(String domain){
        return getTaskProcessor(domain,defaultTaskProcessorFactory);
    }

    public static TaskProcessor getTaskProcessor(){
        return getTaskProcessor(DEFAULT_BUSINESS_DOMAIN);
    }
}
