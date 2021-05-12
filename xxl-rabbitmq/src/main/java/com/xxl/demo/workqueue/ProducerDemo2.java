package com.xxl.demo.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class ProducerDemo2 {
    private static final String HOST = "127.0.0.1";
    private static final String Q = "demo2-queue";

    public static void main(String[] args) throws Exception {
        send();
    }

    private static void send() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("admin");
        factory.setUsername("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        boolean durable = true;//队列的持久化
        //这里声明的是队列
        channel.queueDeclare(Q, durable, false, false, null);

        for (int i = 0; i < 10; i++) {

            String msg = "hello world " + i;
            //basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
            //exchange表示exchange的名称、routingKey表示routingKey的名称、body代表发送的消息体
            //MessageProperties.PERSISTENT_TEXT_PLAIN 消息的持久化
            channel.basicPublish("", Q, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
            System.out.println("Message sent:" + msg);
            Thread.sleep(1000);
        }
        channel.close();
        connection.close();
    }
}
