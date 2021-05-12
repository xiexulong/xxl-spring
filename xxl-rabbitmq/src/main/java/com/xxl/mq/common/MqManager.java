package com.xxl.mq.common;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import com.xxl.util.retry.RetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MqManager {
    private static final Logger logger = LoggerFactory.getLogger(MqManager.class);

    private ExecutorService threadPool;
    
    protected Connection connection;
    protected Channel publishChannel;
    protected MqConfig mqConfig;

    /**
     * create connection via {@code}MqConfig.
     * 
     * @param mqConfig
     *            mq configuration parameter.
     */
    protected void createConnection(MqConfig mqConfig) {
        // update config if you want.
        this.mqConfig = mqConfig;
        
        logger.info("mqConfig {}", mqConfig);

        // init factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(mqConfig.getVhost());
        factory.setUsername(mqConfig.getUser());
        factory.setPassword(mqConfig.getPassword());
        factory.setAutomaticRecoveryEnabled(mqConfig.isAutomaticRecoveryEnabled());
        factory.setTopologyRecoveryEnabled(mqConfig.isTopologyRecoveryEnabled());
        factory.setConnectionTimeout(mqConfig.getConnectionTimeoutMillis());
        factory.setHandshakeTimeout(mqConfig.getHandshakeTimeoutMillis());

        // shuffle addresses
        List<Address> addresses = Arrays.asList(Address.parseAddresses(mqConfig.getAddress()));
        Collections.shuffle(addresses);//对集合进行重新打乱(随机排序)

        // create thread pool
        threadPool = Executors.newFixedThreadPool(mqConfig.getPoolSize(),
                new CustomizableThreadFactory(this.getClass().getSimpleName() + "-thread-"));

        // create connection
        connection = RetryUtil.retry("new mq connection", () -> factory.newConnection(threadPool, addresses));

        // init recovery listener
        if (factory.isAutomaticRecoveryEnabled()) {
            // we want to know recovery event.
            ((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
                private long startTimestamp;

                @Override
                public void handleRecovery(Recoverable recoverable) {
                    logger.warn("connection recovery success! recovery total time: "
                            + (System.currentTimeMillis() - startTimestamp));
                }

                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    logger.warn("connection recovery start!");
                    startTimestamp = System.currentTimeMillis();
                }
            });
        }

        // create producer channel
        boolean durable = true;
        publishChannel = RetryUtil.retry("new channel", () -> connection.createChannel());

        if (mqConfig.isPublisherConfirmEnabled()) {
            // 开启confirm模式
            RetryUtil.retry("confirmSelect", () -> publishChannel.confirmSelect());
        }

        // 声明交换机 直接交换机， 持久化配置
        if (!StringUtils.isEmpty(mqConfig.getPublishExchange())) {
            RetryUtil.retry("declare exchange", () -> publishChannel.exchangeDeclare(mqConfig.getPublishExchange(),
                    BuiltinExchangeType.DIRECT, durable, false, false, null));
        }

        // do other init like creating channels, queues, consumers
        initClient(connection, mqConfig);
    }



    /**
     * we can use the created connection to creating channels, queues,
     * consumers.
     * 
     * @param connection
     *            created connection via mqConfig parameters.
     * @param mqConfig
     *            mq configuration parameters.
     */
    protected abstract void initClient(Connection connection, MqConfig mqConfig);

    /**
     * Publish a message. if {@link MqConfig#isPublisherConfirmEnabled()} is true,
     * wait until the message have been sent to the broker. Note, We need
     * confirm the routine key have been bound a queue to an exchange.
     * 
     * @param exchange the exchange to publish the message to
     * @param routingKey the routing key
     * @param msg the message.
     * @return if true send success. 
     * @throws InterruptedException waitForConfirms throw InterruptedException.
     */
    public boolean basicPublish(String exchange, String routingKey, String msg)
            throws MqException, InterruptedException {
        synchronized (publishChannel) {
            
            try {
                publishChannel.basicPublish(exchange, routingKey, true, false,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
            } catch (Exception e) {
                throw new MqException("basicPublish error", e);
            }

            if (mqConfig.isPublisherConfirmEnabled()) {
                boolean status = publishChannel.waitForConfirms();
                logger.info("basicPublish waitForConfirms, status:{}", status);
                return status;
            }
            return true;
        }
    }

    /**
     * clean all message from queue.
     * 
     * @param queue
     *            queue name.
     */
    public void queuePurge(String queue) {
        synchronized (publishChannel) {
            RetryUtil.retry("clean message from queue", () -> publishChannel.queuePurge(queue));
        }
    }
    
    /**
     * shutdown connection, channel and thread pool.
     */
    public void shutdown() {
        try {
            logger.info("shutdown publishChannel");
            if (publishChannel != null) {
                publishChannel.close();
            }
            
            logger.info("shutdown connection");
            if (connection != null) {
                connection.close();
            }
            
            logger.info("shutdown threadPool");
            if (threadPool != null) {
                threadPool.shutdownNow();
            }
        } catch (Exception ex) {
            logger.warn("shutdown error, message: " + ex.getMessage());
        }
    }
}
