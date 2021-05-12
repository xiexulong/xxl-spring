package com.xxl.mq.demo1;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.xxl.mq.common.MqConsumer;
import com.xxl.mq.common.MqException;
import com.xxl.mq.common.listener.ChannelShutdown406Listener;
import com.xxl.util.retry.RetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultConsumerWrapper {
    private static final Logger logger = LoggerFactory.getLogger(AlgoMqManager.class);
    private static final AtomicInteger tag = new AtomicInteger(0);
    private final Channel channel;
    private final String consumerTag;
    private final String queue;
    private final com.rabbitmq.client.Consumer defaultConsumer;
    private final MqConsumer mqConsumer;
    private volatile Thread consumerThread;
    private volatile boolean isRunning;
    private ChannelShutdown406Listener channelShutdown406Listener;

    /**
     * Wrapper the MQ DefaultConsumer, We can consume message from queue via {@link MqConsumer} interface.
     * @param channel the created mq channel.
     * @param consumer message consumer.
     * @param queue the declared mq queue.
     */
    public DefaultConsumerWrapper(Channel channel, MqConsumer consumer, String queue) {
        if (consumer == null || channel == null || queue == null) {
            throw new IllegalArgumentException("consumer is null or channel is null or queue is null!");
        }

        this.channel = channel;
        this.mqConsumer = consumer;
        this.queue = queue;
        this.consumerTag = queue + "-consumer-tag-" + tag.getAndIncrement();
        this.channel.addShutdownListener(this::handleShutdown);

        defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                boolean consumeOk = false;
                try {
                    consumerThread = Thread.currentThread();
                    String message = new String(body, "UTF-8");

                    mqConsumer.consume(message);
                    consumeOk = true;
                } catch (MqException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("exception happened in mq consumer", e);
                } finally {
                    consumerThread = null;
                }

                if (consumeOk) {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } else {
                    channel.basicReject(envelope.getDeliveryTag(), true);
                }
            }
        };

        // prefetch count =1
        RetryUtil.retry("set channel qps", () -> channel.basicQos(1));
    }

    public void setChannelShutdown406Listener(ChannelShutdown406Listener channelShutdown406Listener){
        this.channelShutdown406Listener = channelShutdown406Listener;
    }

    public MqConsumer getMqConsumer(){
        return mqConsumer;
    }

    public String getQueue() {
        return queue;
    }

    public Channel getChannel() {
        return channel;
    }

    private void handleShutdown(ShutdownSignalException cause) {
        logger.warn("channel shutdown, consumerTag {}", consumerTag);
        if(channelShutdown406Listener != null
                && cause.getMessage() != null
                && cause.getMessage().contains(
                "reply-code=406, reply-text=PRECONDITION_FAILED - unknown delivery tag")){
            channelShutdown406Listener.shutdownCompleted(this);
        }
    }

    /**
     * start consume message.
     */
    public synchronized void start() {
        if (isRunning) {
            logger.info("consumer {} already started", consumerTag);
            return;
        }

        RetryUtil.retry("start consume message", () -> {
            try {
                channel.basicConsume(queue, false, consumerTag, defaultConsumer);
            } catch (IOException e) {
                // consumer already exists in mq
                if (e.getCause() != null
                        && e.getCause() instanceof ShutdownSignalException
                        && e.getCause().getMessage() != null
                        && e.getCause().getMessage().contains(
                        "reply-code=530, reply-text=NOT_ALLOWED - attempt to reuse consumer tag")) {
                    logger.info("start consumer {}, {}", consumerTag, e.toString());
                } else {
                    throw e;
                }
            }
        });
        isRunning = true;
    }

    /**
     * stop consume message, and interrupt consumer's thread.
     */
    public synchronized void stop() {
        if (!isRunning) {
            logger.info("consumer {} already stopped", consumerTag);
            return;
        }

        RetryUtil.retry("Cancel Consume Message", () -> {
            try {
                channel.basicCancel(consumerTag);
            } catch (AlreadyClosedException e) {
                // consumer will be auto deleted in mq when connection closed
                logger.info("stop consumer {}, already closed, {}", consumerTag, e.toString());
            } catch (IOException e) {
                // consumer not exists in mq
                if (e.getMessage() != null && e.getMessage().contains("Unknown consumerTag")) {
                    logger.info("stop consumer {}, unknown tag, {}", consumerTag, e.toString());
                } else {
                    throw e;
                }
            }
        });
        if (consumerThread != null) {
            consumerThread.interrupt();
        }
        isRunning = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultConsumerWrapper)) {
            return false;
        }
        DefaultConsumerWrapper that = (DefaultConsumerWrapper) o;
        return Objects.equals(queue, that.queue)
                && Objects.equals(consumerTag, that.consumerTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queue, consumerTag);
    }
}
