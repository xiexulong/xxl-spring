package com.xxl.concurrent.distributed.locks.imp;

import java.util.concurrent.TimeUnit;

import com.xxl.concurrent.distributed.locks.DCountDownLatch;
import com.xxl.util.TryCatchUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ZkDCountDownLatch implements DCountDownLatch {

    private static final Logger logger = LoggerFactory.getLogger(ZkDCountDownLatch.class);
    private static final String BOOT_PATH = "/DISTRIBUTED_COUNTDOWNLATCH";

    CuratorFramework client;
    DistributedBarrier distributedBarrier;
    InterProcessMutex interProcessMutex;

    DistributedAtomicLong counter;

    String lockPath;
    String lockCountPath;
    String counterPath;

    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public ZkDCountDownLatch(CuratorFramework cf, String lockPath) {
        this.client = cf;
        this.lockPath = BOOT_PATH + lockPath;
        this.lockCountPath = BOOT_PATH + lockPath + "_count_path";
        this.counterPath = lockCountPath + "_counter";
        distributedBarrier = new DistributedBarrier(cf, lockPath);
        interProcessMutex = new InterProcessMutex(cf, lockCountPath);
        counter = new DistributedAtomicLong(cf, counterPath, new RetryNTimes(10, 10));
    }

    @Override
    public void await() throws Exception {
        startClientIfNecessary();
        if (getCount() == 0) {
            return;
        }
        distributedBarrier.setBarrier();
        distributedBarrier.waitOnBarrier();
    }

    @Override
    public boolean await(long timeout, TimeUnit unit)
            throws Exception {
        startClientIfNecessary();
        if (getCount() == 0) {
            logger.info("await getCount=0");
            return false;
        }
        distributedBarrier.setBarrier();
        return distributedBarrier.waitOnBarrier(timeout, unit);
    }

    @Override
    public  void countDown() throws Exception {
        startClientIfNecessary();
        try {
            interProcessMutex.acquire();

            long count = getCount();
            if (count == 0) {
                return;
            }

            if (count > 1) {
                //count--;
                //setCount(count);

                counter.decrement();
            } else {
                setCount(0);
                distributedBarrier.removeBarrier();
            }
        } finally {
            if (interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
            }
        }
    }

    @Override
    public long getCount() throws Exception {
        startClientIfNecessary();
        return counter.get().preValue();
    }

    @Override
    public boolean trySetCount(long count) throws Exception {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }

        startClientIfNecessary();

        try {
            interProcessMutex.acquire();
            long oldCount = getCount();
            if (oldCount == 0) {

                setCount(count);
                return true;
            }

            return false;
        } finally {
            if (interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
            }
        }
    }

    @Override
    public boolean delete() {
        TryCatchUtil.run("try to delete lockPath", () -> {
            if (client.checkExists().forPath(lockPath) != null) {
                client.delete().guaranteed().forPath(lockPath);
            }
        });

        TryCatchUtil.run("try to delete lockCountPath", () -> {

            if (client.checkExists().forPath(lockCountPath) != null) {
                client.delete().guaranteed().forPath(lockCountPath);
            }

        });

        TryCatchUtil.run("try to delete counterPath", () -> {
            if (client.checkExists().forPath(counterPath) != null) {
                client.delete().guaranteed().forPath(counterPath);
            }
        });
        return true;
    }

    private void setCount(long count) throws Exception {
        counter.forceSet(count);

    }

    private void startClientIfNecessary() {
        synchronized (client) {
            if (client != null && CuratorFrameworkState.STARTED != client.getState()) {
                client.start();
            }
        }
    }

}
