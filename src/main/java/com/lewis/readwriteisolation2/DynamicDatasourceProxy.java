package com.lewis.readwriteisolation2;

import javax.sql.DataSource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/8/28.
 */
public class DynamicDatasourceProxy extends AbstractDynamicDatasourceProxy {
    private AtomicLong counter = new AtomicLong(0);

    private static final Long MAX_POOL = Long.MAX_VALUE;

    private final Lock lock = new ReentrantLock();

    @Override
    protected DataSource loadReadDatasource() {
        int index = 1;

        if (getReadDatasourcePoolPattern() == 1) {
            //轮询方式
            long currValue = counter.incrementAndGet();
            if ((currValue + 1) >= MAX_POOL) {
                try {
                    lock.lock();
                    if ((currValue + 1) >= MAX_POOL) {
                        counter.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            index = (int) (currValue % getReadDsSize());
        } else {
            //随机方式
            index = ThreadLocalRandom.current().nextInt(0, getReadDsSize());
        }
        return getResolvedReadDatasources().get(index);
    }


}
