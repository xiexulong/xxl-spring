package com.xxl.concurrent.distributed;

import com.xxl.concurrent.distributed.locks.DCountDownLatch;
import com.xxl.concurrent.distributed.locks.DistributedLock;
import com.xxl.concurrent.distributed.locks.DistributedSemaphore;

public interface DistributedManager<T> {
    /**
     * get DCountDownLatch{@code DCountDownLatch} .
     * @param name .
     * @return
     * */
    DCountDownLatch getCountDownLatch(String name);

    /**
     * get DistributedLock{@code DistributedLock} .
     * @param name lock name, when use zookeeper, the name is a path.
     * @return
     */
    DistributedLock getLock(String name);

    /**
     * get DistributedSemaphore{@code DistributedSemaphore} .
     * @param name Semaphore name, when use zookeeper, the name is a path.
     * @param permits number of permits
     * @return
     */
    DistributedSemaphore getSemaphore(String name, int permits);

    /**
     * get origin client .
     * @return client, if use zookeeper,CuratorFramework will be return. or if use redis RedissonClient will be return.
     */
    T getClient();

    /**
     * construct the lock name.
     * @param applicationName application name, such as "DeviceManager".
     * @param originName origin name of lock.
     * @return specific lock name with different separator. "/" in zookeeper, ":" in redis.
     */
    String constructLockName(String applicationName, String originName);
}
