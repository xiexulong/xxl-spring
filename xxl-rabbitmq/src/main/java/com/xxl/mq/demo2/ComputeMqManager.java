package com.xxl.mq.demo2;

import com.rabbitmq.client.Connection;
import com.xxl.mq.common.MqConfig;
import com.xxl.mq.demo1.AlgoMqManager;
import com.xxl.mq.demo1.common.AlgoQueues;
import com.xxl.util.retry.RetryUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@ConditionalOnProperty(prefix = "mq", name = "enabled", havingValue = "true")
@Component
public class ComputeMqManager extends AlgoMqManager {

    private final MqConfig algoMqConfig;
    private final AlgoReplyConsumer algoReplyConsumer;
    private final BroadcastReplyConsumer broadcastReplyConsumer;

    public ComputeMqManager(MqConfig algoMqConfig, AlgoReplyConsumer algoReplyConsumer,
                            BroadcastReplyConsumer broadcastReplyConsumer) {
        this.algoMqConfig = algoMqConfig;
        this.algoReplyConsumer = algoReplyConsumer;
        this.broadcastReplyConsumer = broadcastReplyConsumer;
    }

    @PostConstruct
    public void init() {
        this.createConnection(algoMqConfig);
    }

    @Override
    protected void initClient(Connection connection, MqConfig algoMqConfig) {
        super.initClient(connection, algoMqConfig);
        //set consumers to handle reply message.
        for (String q : AlgoQueues.getReplyQueueNames()) {
            addConsumer(q, algoReplyConsumer);
        }

        //add consumer to handle worker broadcast reply.
        addConsumer(broadcastReplyQueue, broadcastReplyConsumer);

        //start to consume message.
        start();
    }

    /**
     * 广播消息
     */
    public boolean broadcast(String msg) {
        return RetryUtil.retry("publish broadcast", () -> super.basicPublish(broadcastExchange, "", msg));
    }


}
