package com.xxl.concurrent.distributed.locks;



public interface DistributedSemaphore {

    void acquire() throws Exception;

    void acquire(int permits) throws Exception;

    void release();

    void release(int permits);
}
