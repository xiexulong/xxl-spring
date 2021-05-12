package com.xxl.mq.demo2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.helper.BroadcastHelper;
import com.xxl.mq.common.MqConsumer;
import com.xxl.mq.common.message.BroadcastReplyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class BroadcastReplyConsumer implements MqConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BroadcastReplyConsumer.class);

    private final ObjectMapper objectMapper;
    private final BroadcastHelper broadcastHelper;;

    public BroadcastReplyConsumer(BroadcastHelper broadcastHelper) {
        this.broadcastHelper = broadcastHelper;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void consume(String message) {
        logger.info("received broadcast reply message: {}", message);
        int index = message.indexOf(':');
        BroadcastReplyMessage reply;
        try {
            reply = objectMapper.readValue(message.substring(index + 1),
                    (Class<BroadcastReplyMessage>) Class.forName(message.substring(0, index)));
        } catch (Exception e) {
            logger.error("exception happened in broadcast reply deserialization", e); // this should never happen
            return;
        }
        if (reply instanceof BroadcastReplyMessage) {
            broadcastHelper.onReply(reply);
        } else {
            throw new IllegalArgumentException("unknown broadcast reply message " + message);
        }
    }
}
