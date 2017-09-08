package com.lewis.readwriteisolation.loadData.core.executor;


/**
 * Created by zhangminghua on 2016/11/11.
 */
public class DefaultTaskProcessorFactory implements TaskProcessFactory {

    private static int defaultCoreSize = Runtime.getRuntime().availableProcessors();

    private static int defaultPoolSize;

    private int coreSize;

    private int poolSize;

    static {
        defaultPoolSize = defaultCoreSize * 2;
    }

    public DefaultTaskProcessorFactory() {
        this.coreSize = defaultCoreSize;
        this.poolSize = defaultPoolSize;
    }

    public TaskProcessor createTaskProcessor(String domain) {
        return createTaskProcessor(domain,coreSize,poolSize);
    }

    public TaskProcessor createTaskProcessor(String domain,int coreSize,int poolSize){
        return new TaskProcessor(domain,coreSize,poolSize);
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
