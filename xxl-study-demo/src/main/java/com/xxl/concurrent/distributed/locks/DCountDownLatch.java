package com.xxl.concurrent.distributed.locks;

import java.util.concurrent.TimeUnit;

public interface DCountDownLatch {

    void await() throws Exception;

    boolean await(long timeout, TimeUnit unit) throws Exception;

    void countDown() throws Exception;

    /**
     * Returns the current count.
     *
     * <p>This method is typically used for debugging and testing purposes.
     *
     * @return the current count
     */
    long getCount() throws Exception;

    /**
     * Sets new count value only if previous count already has reached zero
     * or is not set at all.
     *
     * @param count - number of times {@link #countDown} must be invoked
     *        before threads can pass through {@link #await}
     * @return <code>true</code> if new count setted
     *         <code>false</code> if previous count has not reached zero
     */
    boolean trySetCount(long count) throws Exception;

    /**
     * delete lock.
     * @return
     */
    boolean delete();
}
