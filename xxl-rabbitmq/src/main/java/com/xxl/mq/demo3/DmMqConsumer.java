package com.xxl.mq.demo3;

import com.xxl.mq.common.MqConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class DmMqConsumer implements MqConsumer {

    private static final Logger logger  = LoggerFactory.getLogger(DmMqConsumer.class);


    //threadPool to consume message.
    private ExecutorService processMessageThreadPool;


    private static final long REPLY_EXPIRED = 30L * 60 * 1000;// unit millisecond

    private volatile boolean available = true;



    @PostConstruct
    public void init() {
        processMessageThreadPool = Executors.newFixedThreadPool(20,
                new CustomizableThreadFactory("process-message-thread-"));
    }

    @Override
    public void consume(String message) {
        System.out.println("消费消息：" + message);
    }
}
