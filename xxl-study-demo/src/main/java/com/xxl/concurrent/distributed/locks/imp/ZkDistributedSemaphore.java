/**
 *******************************************************************************
 *                       RoadDB Confidential
 *                    Copyright (c) RoadDB 2019
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *******************************************************************************
 * @file   ZkDistributedSemaphore.java
 * @brief  The zookeeper distributed semaphore.
 *******************************************************************************
 */

package com.xxl.concurrent.distributed.locks.imp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import com.xxl.concurrent.distributed.locks.DistributedSemaphore;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;


/**
 * Description.
 *
 * @author Wenchao Chen
 * @Date 7/30/19 5:11 PM
 */
public class ZkDistributedSemaphore implements DistributedSemaphore {

    private CuratorFramework client;
    private InterProcessSemaphoreV2 semaphore;
    private Collection<Lease> leaseSet = Collections.synchronizedSet(new HashSet<Lease>());

    private static final String BOOT_PATH = "/DISTRIBUTED_SEMAPHORE";

    public ZkDistributedSemaphore(String semaphoreName, CuratorFramework client, int maxCount) {
        this.client = client;
        this.semaphore = new InterProcessSemaphoreV2(client, BOOT_PATH + semaphoreName, maxCount);
    }

    @Override
    public void acquire() throws Exception {
        startClientIfNecessary();
        Lease lease = semaphore.acquire();
        if (lease != null) {
            leaseSet.add(lease);
        }
    }

    @Override
    public void acquire(int permits) throws Exception {
        startClientIfNecessary();
        Collection<Lease> leases = semaphore.acquire(permits);
        if (leases != null) {
            leaseSet.addAll(leases);
        }
    }

    @Override
    public void release() {
        startClientIfNecessary();
        Iterator<Lease> iterator = leaseSet.iterator();
        if (iterator.hasNext()) {
            Lease lease = iterator.next();
            semaphore.returnLease(lease);
            iterator.remove();
        }
    }

    @Override
    public void release(int permits) {
        startClientIfNecessary();
        if (leaseSet.size() > 0) {
            if (permits >= leaseSet.size()) {
                semaphore.returnAll(leaseSet);
            } else {
                int count = 0;
                Iterator<Lease> iterator = leaseSet.iterator();
                if (iterator.hasNext()) {
                    Lease lease = iterator.next();
                    if (count < permits) {
                        semaphore.returnLease(lease);
                        iterator.remove();
                        count++;
                    }
                }
            }
        }
    }

    private void startClientIfNecessary() {
        synchronized (client) {
            if (client != null && CuratorFrameworkState.STARTED != client.getState()) {
                client.start();
            }
        }
    }
}
