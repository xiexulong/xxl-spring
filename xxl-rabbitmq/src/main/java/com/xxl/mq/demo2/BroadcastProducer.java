package com.xxl.mq.demo2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.exception.BroadcastException;
import com.xxl.helper.BroadcastHelper;
import com.xxl.mq.demo1.entity.BroadcastResult;
import com.xxl.mq.common.message.BroadcastMessage;
import com.xxl.mq.common.message.BroadcastReplyMessage;
import com.xxl.util.retry.exception.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class BroadcastProducer {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastProducer.class);
    private final ObjectMapper objectMapper;
    private final ComputeMqManager mqManager;

    private final BroadcastHelper broadcastHelper;

    public BroadcastProducer(ObjectMapper objectMapper, ComputeMqManager mqManager,
                             BroadcastHelper broadcastHelper) {
        this.objectMapper = objectMapper;
        this.mqManager = mqManager;
        this.broadcastHelper = broadcastHelper;
    }

    /**
     * Broadcast the message. It will send a message to mq to notify workers and wait all workers reply.
     * Note the message is published with limited retries.
     * @return the reply message map if success, the key is worker host
     * @throws BroadcastException if execution failed
     */
    public Map<String, BroadcastReplyMessage> broadcastAndWaitWorkersReply(BroadcastMessage message,
                                                                           long timeout, TimeUnit unit)
            throws BroadcastException {
        List<String> broadcastAddressList = getConsumerList();
        logger.info("the hosts of workers are {}", broadcastAddressList);

        message.newBroadcastId();

        long broadcastId = message.getBroadcastId();
        BroadcastResult result = broadcastHelper.addBroadcastResult(broadcastId, broadcastAddressList);
        try {
            String messageString = message.getClass().getName() + ":" + objectMapper.writeValueAsString(message);
            logger.info("send broadcast message: {}", messageString);
            mqManager.broadcast(messageString);

            if (!result.await(timeout, unit)) {
                throw new BroadcastException("await broadcast " + broadcastId + " countdown latch timeout");
            }
            if (!result.isSuccessful()) {
                throw new BroadcastException("broadcast " + broadcastId + " execution failed");
            }
            return result.getReplyMessageMap();
        } catch (JsonProcessingException | RetryException e) {
            throw new BroadcastException("broadcast " + broadcastId + " failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BroadcastException("broadcast " + broadcastId + " failed", e);
        } finally {

            broadcastHelper.removeBroadcastResult(broadcastId);
        }
    }



    private List<String> getConsumerList() {

        return Arrays.asList("one","two","three");
    }




}
