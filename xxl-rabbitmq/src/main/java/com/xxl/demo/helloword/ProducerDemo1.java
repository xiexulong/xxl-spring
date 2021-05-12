package com.xxl.demo.helloword;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProducerDemo1 {
    private static final String HOST = "127.0.0.1";
    private static final String Q = "demo1-queue";

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
        channel.queueDeclare(Q, false, false, false, null);

        String msg = "hello world";
        channel.basicPublish("", Q, null, msg.getBytes());
        System.out.println("Message sent:" + msg);
        channel.close();
        connection.close();
    }
}
