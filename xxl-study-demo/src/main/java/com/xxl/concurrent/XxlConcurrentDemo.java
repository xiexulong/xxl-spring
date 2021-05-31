package com.xxl.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class XxlConcurrentDemo {

    public static void main(String[] args) {
        AtomicLong atomicLong = new AtomicLong(1);//初始值设为1
        boolean flag = atomicLong.compareAndSet(0,2);// 预期为0，设置为2
        System.out.println(flag);
        System.out.println(atomicLong.get());

        Executors.newCachedThreadPool();


        ReentrantLock lock = new ReentrantLock();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, //核心线程5个
                Runtime.getRuntime().availableProcessors() * 2,// cpu核的个数乘以2
                60,//当线程大于核心线程数时，保持空闲多久回收
                TimeUnit.SECONDS,//单位
                new ArrayBlockingQueue<>(200),//设置有限队列
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return null;//自定义线程工厂
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        //拒绝策略
                    }
                }
        );

    }
}
