package com.xxl.helper;

import com.xxl.mq.demo1.entity.BroadcastResult;
import com.xxl.mq.common.message.BroadcastReplyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BroadcastHelper {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastHelper.class);
    private final ConcurrentHashMap<Long, BroadcastResult> resultMap; // <broadcastId, result>

    public BroadcastHelper() {
        this.resultMap = new ConcurrentHashMap<>();
    }


    public BroadcastResult addBroadcastResult(long broadcastId, List<String> broadcastAddressList) {
        BroadcastResult result = new BroadcastResult(broadcastId, broadcastAddressList);
        resultMap.put(broadcastId, result);
        return result;
    }

    public void removeBroadcastResult(long broadcastId) {
        resultMap.remove(broadcastId);
    }



    public void onReply(BroadcastReplyMessage replyMessage) {
        long broadcastId = replyMessage.getBroadcastId();
        BroadcastResult result = resultMap.get(broadcastId);
        if (result == null) {
            logger.warn("broadcast reply outdated, message {}", replyMessage);
            return;
        }
        result.onReply(replyMessage);
    }



}
