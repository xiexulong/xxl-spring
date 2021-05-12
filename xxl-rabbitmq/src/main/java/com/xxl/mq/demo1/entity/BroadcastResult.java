package com.xxl.mq.demo1.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import com.xxl.mq.common.message.BroadcastReplyMessage;

public class BroadcastResult {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastResult.class);

    private final long broadcastId;
    private final CountDownLatch latch;
    private final AtomicBoolean successful;
    private final ConcurrentHashMap<String, AtomicBoolean> replyReceivedMap; // <workerHost, received>
    private final ConcurrentHashMap<String, BroadcastReplyMessage> replyMessageMap; // <workerHost, replyMsg>

    public BroadcastResult(long broadcastId, List<String> broadcastAddressList) {
        this.broadcastId = broadcastId;
        this.latch = new CountDownLatch(broadcastAddressList.size());
        this.successful = new AtomicBoolean(true);
        this.replyReceivedMap = new ConcurrentHashMap<>();
        for (String workerHost : broadcastAddressList) {
            this.replyReceivedMap.put(workerHost, new AtomicBoolean(false));
        }
        this.replyMessageMap = new ConcurrentHashMap<>();
    }

    public void onReply(BroadcastReplyMessage replyMessage) {
        onReply(replyMessage.getHost(), replyMessage.isSuccessful(), replyMessage);
    }

    public void onReply(String workerHost, boolean successful, BroadcastReplyMessage replyMessage) {
        AtomicBoolean received = replyReceivedMap.get(workerHost);
        if (received == null) {
            logger.warn("unexpected broadcast reply, broadcast {} worker {}", broadcastId, workerHost);
            return;
        }
        //this.replyReceivedMap.put(workerHost, new AtomicBoolean(false));
        if (received.compareAndSet(false, true)) {
            logger.info("set result, broadcast {} worker {} successful {}", broadcastId, workerHost, successful);
            //successful = new AtomicBoolean(true);
            this.successful.compareAndSet(true, successful);
            if (replyMessage != null) {
                replyMessageMap.put(workerHost, replyMessage);
            }
            this.latch.countDown();
        }
    }

    public void onOffline(String workerHost) {
        if (replyReceivedMap.containsKey(workerHost)) {
            logger.warn("try set result for offline, broadcast {} worker {}", broadcastId, workerHost);
            onReply(workerHost, false, null);
        }
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public boolean isSuccessful() {
        return successful.get();
    }

    public Map<String, BroadcastReplyMessage> getReplyMessageMap() {
        return Collections.unmodifiableMap(replyMessageMap);
    }
}
