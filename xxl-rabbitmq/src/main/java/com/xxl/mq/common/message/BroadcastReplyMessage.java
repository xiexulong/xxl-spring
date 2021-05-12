package com.xxl.mq.common.message;

public class BroadcastReplyMessage {

    protected long broadcastId;
    protected String host;
    protected boolean successful;

    public long getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(long broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public String toString() {
        return "BroadcastReplyMessage{"
                + "broadcastId=" + broadcastId
                + ", host='" + host + '\''
                + ", successful=" + successful
                + '}';
    }
}
