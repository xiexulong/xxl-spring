package com.xxl.mq.demo1;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.xxl.mq.common.MqConfig;
import com.xxl.mq.common.MqConsumer;
import com.xxl.mq.common.MqException;
import com.xxl.mq.common.MqManager;
import com.xxl.mq.demo1.common.AlgoQueues;
import com.xxl.util.retry.RetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class AlgoMqManager extends MqManager {
    private static final Logger logger = LoggerFactory.getLogger(AlgoMqManager.class);

    protected String broadcastExchange = "rdb-server-exchange-broadcast";
    protected String broadcastReplyExchange = "rdb-server-exchange-broadcast-reply";
    protected String broadcastReplyQueue = "rdb-server-queue-broadcast-reply";
    public static final String BIND_QUEUE = "bind queue ";

    private final ArrayList<DefaultConsumerWrapper> consumers = new ArrayList<>();

    @Override
    protected void initClient(Connection connection, MqConfig algoMqConfig) {
        String exchanger = algoMqConfig.getPublishExchange();//得到交换机的名字
        boolean durable = true;
        //声明队列并与交换机绑定
        for (String q : AlgoQueues.getAllQueueNames()) {
            RetryUtil.retry("queue declare " + q, () -> publishChannel.queueDeclare(q, durable, false, false, null));
            if (!StringUtils.isEmpty(exchanger)) {
                RetryUtil.retry(BIND_QUEUE + q, () -> publishChannel.queueBind(q, exchanger, q));
            }
        }

        // 声明一个扇形交换机，用于广播消息
        RetryUtil.retry("declare exchange ", () -> publishChannel.exchangeDeclare(broadcastExchange,
                BuiltinExchangeType.FANOUT, true, false, false, null));

        //初始化广播回复的交换机和队列
        initBroadcastReply(durable);
    }

    /**
     * 初始化广播回复的交换机和队列.
     */
    private void initBroadcastReply(boolean durable) {
        // 声明一个广播的回复队列 broadcastReplyQueue
        RetryUtil.retry("queue declare " + broadcastReplyQueue, () ->
                publishChannel.queueDeclare(broadcastReplyQueue, durable, false, false, null));

        // 声明一个直接交换机用于回复消息
        RetryUtil.retry("declare exchange ", () -> publishChannel.exchangeDeclare(broadcastReplyExchange,
                BuiltinExchangeType.DIRECT, true, false, false, null));
        //绑定回复交换机和队列根据路由键
        RetryUtil.retry(BIND_QUEUE + broadcastReplyQueue, () ->
                publishChannel.queueBind(broadcastReplyQueue, broadcastReplyExchange, broadcastReplyQueue));
    }

    /**
     * Publish a message via an default exchange which set in
     * {@link MqConfig#setPublishExchange(String)}.
     *
     * @param routingKey
     *            the key for bing to queue.
     * @param msg
     *            send message.
     * @return if true, send message successful.
     */
    public boolean basicPublish(String routingKey, String msg) throws MqException, InterruptedException {
        return super.basicPublish(mqConfig.getPublishExchange(), routingKey, msg);
    }

    /**
     * add consumer to handle sending to queueName's message with a new channel.
     *
     * @param queueName queue name.
     * @param consumer
     * the message consumer. see {@link MqConsumer}
     */
    protected DefaultConsumerWrapper addConsumer(String queueName, MqConsumer consumer) {
        logger.info("+addConsumer+ queueName:{}", queueName);
        final Channel channel = RetryUtil.retry("new channel", () -> connection.createChannel());
        DefaultConsumerWrapper defaultConsumerWrapper = new DefaultConsumerWrapper(channel, consumer, queueName);

        //handle if channel shutdown (code=406)
        defaultConsumerWrapper.setChannelShutdown406Listener(consumerWrapper -> {
            synchronized (consumers) {
                logger.info("reCreate Consumer");
                consumers.remove(consumerWrapper);
                new Thread(() -> {
                    DefaultConsumerWrapper newConsumerWrapper = addConsumer(consumerWrapper.getQueue(), consumerWrapper.getMqConsumer());
                    newConsumerWrapper.start();
                }, "reCreate-consumer-thread").start();

            }
        });

        consumers.add(defaultConsumerWrapper);

        return defaultConsumerWrapper;
    }

    /**
     * add consumer to handle sending to exchange's message with a new channel.
     *
     * @param consumer
     *            the message consumer. see {@link MqConsumer}
     */
    protected DefaultConsumerWrapper addBroadcastConsumer(MqConsumer consumer) {
        logger.info("+addBroadcastConsumer+");
        final Channel channel = RetryUtil.retry("new channel", () -> connection.createChannel());

        //create tmp queue.
        String queue = RetryUtil.retry("declare queue", () -> channel.queueDeclare().getQueue());

        //bind tmp queue to broadcastExchange
        RetryUtil.retry(BIND_QUEUE + queue, () -> channel.queueBind(queue, broadcastExchange, queue));

        //add consumer
        DefaultConsumerWrapper defaultConsumerWrapper = new DefaultConsumerWrapper(channel, consumer, queue);

        //handle if channel shutdown (code=406)
        defaultConsumerWrapper.setChannelShutdown406Listener(consumerWrapper -> {
            synchronized (consumers){
                logger.info("reCreate BroadcastConsumer");
                consumers.remove(consumerWrapper);
                new Thread(() -> {
                    DefaultConsumerWrapper newConsumerWrapper = addBroadcastConsumer(consumerWrapper.getMqConsumer());
                    newConsumerWrapper.start();
                }, "reCreate-broadcastConsumer-thread").start();
            }
        });

        consumers.add(defaultConsumerWrapper);
        logger.info("-addBroadcastConsumer-");
        return defaultConsumerWrapper;
    }

    /**
     * stop all consumers to consume the queueName' messages.
     *
     * @param queueName
     *            algorithms queue name.
     */
    public void stop(String queueName) {
        consumers.stream().filter(consumer -> consumer.getQueue().equals(queueName)).
                forEach((consumer) -> consumer.stop());
    }

    /**
     * stop all consumers.
     */
    public void stop() {
        logger.info("stop all consumers");
        consumers.forEach((consumer) -> consumer.stop());
    }

    /**
     * start the consumer to consume the {@code queueName}' messages.
     *
     * @param queueName
     *            algorithms queue name.
     */
    public void start(String queueName) {
        consumers.stream().filter(consumer -> consumer.getQueue().equals(queueName)).
                forEach((consumer) -> consumer.start());
    }

    /**
     * start all consumers to consume messages.
     */
    public void start() {
        logger.info("start all consumers");
        consumers.forEach((consumer) -> consumer.start());
    }

    /**
     * clean all messages from algorithms queue.
     */
    public void queuePurge() {
        logger.info("purge all queues");
        for (String q : AlgoQueues.getAllQueueNames()) {
            queuePurge(q);
        }
    }

    /**
     * shutdown all consumers to consume messages, close channels, connection,
     * shutdown thread pool.
     */
    @Override
    public void shutdown() {
        logger.info("shutdown all consumers");
        consumers.forEach((consumer) -> {
            consumer.stop();

            // close channel
            try {
                consumer.getChannel().close();
            } catch (Exception e) {
                logger.error("shutdown error.", e);
            }
        });
        super.shutdown();
    }
}
