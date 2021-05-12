package com.xxl.concurrent.distributed;

import com.xxl.concurrent.distributed.locks.DCountDownLatch;
import com.xxl.concurrent.distributed.locks.DistributedLock;
import com.xxl.concurrent.distributed.locks.DistributedSemaphore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;



public class RedisManager implements DistributedManager<RedissonClient> {

    private static  RedissonClient redissonClient;
    public static final String REDIS_ADDRESS_PREFIX = "redis://";
    public static final String REDIS_PATH_SEPARATOR = ":";

    /**
     * construct with param.
     * @param address redis://127.0.0.1:6379
     */
    public RedisManager(String address) {

        Config config = new Config();
        config.useSingleServer().setAddress(REDIS_ADDRESS_PREFIX + address)
                .setDatabase(0);
        config.setLockWatchdogTimeout(40000);

        redissonClient = Redisson.create(config);
    }

    @Override
    public DCountDownLatch getCountDownLatch(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DistributedLock getLock(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DistributedSemaphore getSemaphore(String name, int permit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RedissonClient getClient() {
        return redissonClient;
    }

    @Override
    public String constructLockName(String applicationName, String originName) {
        return applicationName + REDIS_PATH_SEPARATOR + originName;
    }
}
