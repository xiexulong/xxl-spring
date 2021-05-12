
package com.xxl.mq.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqConfig {

    public static final int MIN_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    @Value("${mq.address:127.0.0.1:5672}")
    private String address;

    //在RabbitMQ中可以虚拟消息服务器VirtualHost，每个VirtualHost相当月一个相对独立的RabbitMQ服务器，每个VirtualHost之间是相互隔离的。exchange、queue、message不能互通
    @Value("${mq.vhost:/}")
    private String vhost;

    @Value("${mq.user:admin}")
    private String user;

    @Value("${mq.password:admin}")
    private String password;
    
    /**
     * Connection Timeout Millis.
     */
    @Value("${mq.connectionTimeoutMillis:60000}")
    private int connectionTimeoutMillis;

    /**
     * getConnectionTimeoutMillis.
     * @return the connectionTimeoutMillis
     */
    public int getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    /**
     * setConnectionTimeoutMillis.
     * @param connectionTimeoutMillis the connectionTimeoutMillis to set
     */
    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    /**
     * getHandshakeTimeoutMillis.
     * @return the handshakeTimeoutMillis
     */
    public int getHandshakeTimeoutMillis() {
        return handshakeTimeoutMillis;
    }

    /**
     * setHandshakeTimeoutMillis.
     * @param handshakeTimeoutMillis the handshakeTimeoutMillis to set
     */
    public void setHandshakeTimeoutMillis(int handshakeTimeoutMillis) {
        this.handshakeTimeoutMillis = handshakeTimeoutMillis;
    }

    /**
     * Handshake Timeout Millis.
     */
    @Value("${mq.handshakeTimeoutMillis:60000}")
    private int handshakeTimeoutMillis;

    private int poolSize;

    @Value("${mq.automaticRecovery.enabled:true}")
    private boolean automaticRecoveryEnabled;

    @Value("${mq.topologyRecovery.enabled:true}")
    private boolean topologyRecoveryEnabled;

    @Value("${mq.publisherConfirm.enabled:true}")
    private boolean publisherConfirmEnabled;

    @Value("${mq.publish.exchange:xxl-exchange-publish}")
    private String publishExchange;    

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPoolSize() {
        return poolSize < MIN_POOL_SIZE ? MIN_POOL_SIZE : poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize < MIN_POOL_SIZE ? MIN_POOL_SIZE : poolSize;
    }

    public boolean isAutomaticRecoveryEnabled() {
        return automaticRecoveryEnabled;
    }

    public void setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
        this.automaticRecoveryEnabled = automaticRecoveryEnabled;
    }

    public boolean isTopologyRecoveryEnabled() {
        return topologyRecoveryEnabled;
    }

    public void setTopologyRecoveryEnabled(boolean topologyRecoveryEnabled) {
        this.topologyRecoveryEnabled = topologyRecoveryEnabled;
    }

    public boolean isPublisherConfirmEnabled() {
        return publisherConfirmEnabled;
    }

    public void setPublisherConfirmEnabled(boolean publisherConfirmEnabled) {
        this.publisherConfirmEnabled = publisherConfirmEnabled;
    }

    public String getPublishExchange() {
        return publishExchange;
    }

    public void setPublishExchange(String publishExchange) {
        this.publishExchange = publishExchange;
    }

    public String getVhost() {
        return vhost;
    }

    public void setVhost(String vhost) {
        this.vhost = vhost;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MqConfig [address=" + address + ", vhost=" + vhost + ", user=" + user + ", password=" + password
                + ", connectionTimeoutMillis=" + connectionTimeoutMillis + ", handshakeTimeoutMillis="
                + handshakeTimeoutMillis + ", poolSize=" + poolSize + ", automaticRecoveryEnabled="
                + automaticRecoveryEnabled + ", topologyRecoveryEnabled=" + topologyRecoveryEnabled
                + ", publisherConfirmEnabled=" + publisherConfirmEnabled + ", publishExchange=" + publishExchange + "]";
    }


}
