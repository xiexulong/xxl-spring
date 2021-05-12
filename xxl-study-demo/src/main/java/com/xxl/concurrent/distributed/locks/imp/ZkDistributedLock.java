package com.xxl.concurrent.distributed.locks.imp;

import java.util.concurrent.TimeUnit;

import com.xxl.concurrent.distributed.locks.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * zookeeper lock.
 */
public class ZkDistributedLock implements DistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(ZkDistributedLock.class);
    private static final String BOOT_PATH = "/DISTRIBUTED_LOCK";
    InterProcessMutex interProcessMutex;
    CuratorFramework client;

    public ZkDistributedLock(CuratorFramework cf, String lockPath) {
        client = cf;
        interProcessMutex = new InterProcessMutex(cf, BOOT_PATH + lockPath);
    }

    @Override
    public void lock() throws Exception {
        startClientIfNecessary();
        interProcessMutex.acquire();
    }


    @Override
    public boolean lock(long time, TimeUnit unit) throws Exception {
        startClientIfNecessary();
        return interProcessMutex.acquire(time, unit);
    }

    @Override
    public void unlock() throws Exception {
        startClientIfNecessary();
        interProcessMutex.release();
    }

    private void startClientIfNecessary() {
        if (client != null && CuratorFrameworkState.STARTED != client.getState()) {
            logger.info("startClientIfNecessary do start");
            client.start();
        }
    }
}
