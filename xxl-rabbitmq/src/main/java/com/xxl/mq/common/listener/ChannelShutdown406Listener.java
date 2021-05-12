
package com.xxl.mq.common.listener;

import com.xxl.mq.demo1.DefaultConsumerWrapper;

public interface ChannelShutdown406Listener {
    void shutdownCompleted(DefaultConsumerWrapper consumerWrapper);
}
