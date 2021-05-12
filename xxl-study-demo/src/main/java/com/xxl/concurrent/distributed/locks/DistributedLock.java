
package com.xxl.concurrent.distributed.locks;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    void lock() throws Exception;

    boolean lock(long time, TimeUnit unit) throws Exception;

    void unlock() throws Exception;
}
