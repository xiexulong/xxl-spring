package com.xxl.mq.common.message;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class BroadcastMessage {

    private static final AtomicLong idCounter = new AtomicLong(); // generate broadcast id

    protected long broadcastId;

    /**
     * Generate a unique id with timestamp and id counter.
     */
    public void newBroadcastId() {
        this.broadcastId = Instant.now().getEpochSecond() * 100_000_000 + idCounter.incrementAndGet();
    }

    public long getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(long broadcastId) {
        this.broadcastId = broadcastId;
    }


    @Override
    public String toString() {
        return "BroadcastMessage{"
                + "broadcastId=" + broadcastId
                + '}';
    }
}
