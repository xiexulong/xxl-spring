package com.xxl.demo.pubsub;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Demo3Producer {

    private static final String HOST = "127.0.0.1";

    private static final String EX = "demo3-exchange";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        send();
    }

    private static void send() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("admin");
        factory.setUsername("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //这里声明的是交换机
        channel.exchangeDeclare(EX, BuiltinExchangeType.FANOUT);

        for (int i = 0; i < 10; i++) {
            String message = "Hello World! " + i;
            channel.basicPublish(EX, "", null, message.getBytes());
            System.out.println("Message sent: " + message);
            Thread.sleep(1000);
        }

        channel.close();
        connection.close();
    }
}
