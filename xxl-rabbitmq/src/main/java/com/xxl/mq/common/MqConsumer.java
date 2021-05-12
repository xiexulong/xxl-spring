package com.xxl.mq.common;

public interface MqConsumer {
    /**
     * consumer mq message.
     * @param message mq message.
     * @throws Exception a exception.
     */
    void consume(String message) throws Exception;
}
