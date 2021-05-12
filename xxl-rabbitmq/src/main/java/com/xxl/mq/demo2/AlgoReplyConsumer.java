package com.xxl.mq.demo2;

import com.xxl.mq.common.MqConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlgoReplyConsumer implements MqConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AlgoReplyConsumer.class);


    @Override
    public void consume(String message) {
        long taskId = Long.valueOf(message);
        logger.info("AlgoReplyConsumer consume message, taskId:{}", taskId);
    }
}
