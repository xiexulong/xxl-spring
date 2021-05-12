package com.xxl.demo.routing;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Demo4Producer {


    private static final String HOST = "127.0.0.1";

    private static final String EX = "demo4-exchange";
    private static final String K1 = "demo4.key1";
    private static final String K2 = "demo4.key2";

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

        channel.exchangeDeclare(EX, BuiltinExchangeType.DIRECT);

        for (int i = 0; i < 10; i++) {
            String message = "Hello World! " + i;
            if (i % 2 == 0) {
                channel.basicPublish(EX, K1, null, message.getBytes());
                System.out.println(K1 + " Message sent: " + message);
            } else {
                channel.basicPublish(EX, K2, null, message.getBytes());
                System.out.println(K2 + " Message sent: " + message);
            }
            Thread.sleep(1000);
        }

        channel.close();
        connection.close();
    }
}
