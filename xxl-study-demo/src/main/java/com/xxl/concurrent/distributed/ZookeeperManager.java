package com.xxl.concurrent.distributed;

import com.xxl.concurrent.distributed.locks.DCountDownLatch;
import com.xxl.concurrent.distributed.locks.DistributedLock;
import com.xxl.concurrent.distributed.locks.DistributedSemaphore;
import com.xxl.concurrent.distributed.locks.imp.ZkDCountDownLatch;
import com.xxl.concurrent.distributed.locks.imp.ZkDistributedLock;
import com.xxl.concurrent.distributed.locks.imp.ZkDistributedSemaphore;
import com.xxl.exception.ZookeeperDisconnectException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.common.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ZookeeperManager implements DistributedManager<CuratorFramework> {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperManager.class);
    private static final String ZK_PATH_SEPARATOR = "/";

    CuratorFramework client;
    static volatile boolean canWork;

    /**
     * Construct ZookeeperManager.
     * @param connectString list of servers to connect to
     */
    public ZookeeperManager(String connectString) {

        this.client = createZKClient(connectString);
        canWork = true;
    }

    @Override
    public DCountDownLatch getCountDownLatch(String name) {
        if (!canWork) {
            throw new ZookeeperDisconnectException(" get ZkDCountDownLatch failed, name: " + name);
        }
        validateName(name);
        unifyName(name);
        return new ZkDCountDownLatch(client,name);
    }

    @Override
    public DistributedLock getLock(String name) {
        if (!canWork) {
            throw new ZookeeperDisconnectException(" get ZkDistributedLock failed, name: " + name);
        }
        validateName(name);
        unifyName(name);
        return new ZkDistributedLock(client, name);
    }

    @Override
    public DistributedSemaphore getSemaphore(String name, int permit) {
        if (!canWork) {
            throw new ZookeeperDisconnectException(" get ZkDistributedSemaphore failed, name: " + name);
        }
        validateName(name);
        unifyName(name);
        return new ZkDistributedSemaphore(name,client, permit);
    }

    @Override
    public CuratorFramework getClient() {
        startClientIfNecessary();
        return client;
    }

    @Override
    public String constructLockName(String applicationName, String originName) {
        return ZK_PATH_SEPARATOR + applicationName + ZK_PATH_SEPARATOR + originName;
    }

    private CuratorFramework createZKClient(String zkAddr) {
        //RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        RetryPolicy retryPolicy = new RetryForever(5000);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(zkAddr)
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .retryPolicy(retryPolicy)
                .build();

        cf.getConnectionStateListenable().addListener((client, newState) -> {
            canWork = (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED);
            logger.info("ZookeeperManager stateChanged: {}, canWork:{}", newState, canWork);
        });

        return  cf;
    }

    private boolean validateName(String name) {
        PathUtils.validatePath(name);
        return true;
    }

    private String unifyName(String name) {
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }

        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        return name;
    }

    private void startClientIfNecessary() {
        if (client != null && CuratorFrameworkState.STARTED != client.getState()) {
            client.start();
        }
    }
}
