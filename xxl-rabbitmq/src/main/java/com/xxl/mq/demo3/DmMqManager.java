
package com.xxl.mq.demo3;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.xxl.mq.common.MqConfig;
import com.xxl.mq.common.MqManager;
import com.xxl.util.retry.RetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DmMqManager extends MqManager {

    private static final Logger logger = LoggerFactory.getLogger(DmMqManager.class);

    private MqConfig dmMqConfig;

    private DmMqConsumer dmMqConsumer;


    @Autowired
    public DmMqManager(MqConfig dmMqConfig, DmMqConsumer dmMqConsumer) {
        this.dmMqConfig = dmMqConfig;
        this.dmMqConsumer = dmMqConsumer;
    }

    @PostConstruct
    public void init() {
        logger.info("device manager mqConfig: {}", dmMqConfig);
        createConnection(dmMqConfig);
    }

    @Override
    protected void initClient(Connection connection, MqConfig mqConfig) {
        initConsumer();
    }

    public void publish(String queue, String message)  {
        logger.info("publish queue = {}, message = {}", queue, message);
        RetryUtil.retry("send message", () -> super.basicPublish("", queue, message));
    }

    /**
     *  declare consumer for message queue provided by device.
     */
    private void initConsumer() {
        final Channel channel = RetryUtil.retry("declare consume channel", () -> connection
                .createChannel());

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) {
                try {
                    String message = new String(body, "UTF-8");
                    logger.info("receive message: {}", message);
                    dmMqConsumer.consume(message);
                } catch (Exception e) {
                    logger.error("exception happened in mq consumer", e);
                }
            }
        };

        String consumerQueue = "vehicle_queue";
        logger.info("consumer queue: {}", consumerQueue);

        RetryUtil.retry("declare consume queue", () -> channel.queueDeclare(consumerQueue, true, false, false, null));

        RetryUtil.retry("consume message", () -> channel.basicConsume(consumerQueue,
                true, consumer));

        logger.info("init consumer success.");
    }

}
