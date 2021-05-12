package com.xxl.demo.topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Demo5Producer {

    private static final String HOST = "127.0.0.1";

    private static final String EX = "demo5-exchange";
    private static final String K1 = "demo5.hello.me";
    private static final String K2 = "demo5.big.world";
    private static final String K3 = "demo5.hello.world";

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

        channel.exchangeDeclare(EX, BuiltinExchangeType.TOPIC);

        for (int i = 0; i < 10; i++) {
            String message = "Hello World! " + i;
            if (i % 3 == 0) {
                channel.basicPublish(EX, K1, null, message.getBytes());
                System.out.println(K1 + " Message sent: " + message);
            } else if (i % 3 == 1) {
                channel.basicPublish(EX, K2, null, message.getBytes());
                System.out.println(K2 + " Message sent: " + message);
            } else {
                channel.basicPublish(EX, K3, null, message.getBytes());
                System.out.println(K3 + " Message sent: " + message);
            }
            Thread.sleep(1000);
        }

        channel.close();
        connection.close();
    }
}
