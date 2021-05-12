package com.xxl.demo.topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Demo5Consumer {

    private static final String HOST = "127.0.0.1";

    private static final String EX = "demo5-exchange";
    private static final String Q1 = "demo5-queue-1";
    private static final String Q2 = "demo5-queue-2";
    private static final String K1 = "demo5.hello.*";
    private static final String K2 = "demo5.*.world";

    public static void main(String[] args) throws IOException, TimeoutException {
        receive();
    }

    private static void receive() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("admin");
        factory.setUsername("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EX, BuiltinExchangeType.TOPIC);
        channel.queueDeclare(Q1, false, false, false, null);
        channel.queueDeclare(Q2, false, false, false, null);
        channel.queueBind(Q1, EX, K1);
        channel.queueBind(Q2, EX, K2);

        Consumer consumer1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                System.out.println("Consumer1 " + envelope.getRoutingKey() + " Message received: " + new String(body));
            }
        };

        Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                System.out.println("Consumer2 " + envelope.getRoutingKey() + " Message received: " + new String(body));
            }
        };

        channel.basicConsume(Q1, true, consumer1);
        channel.basicConsume(Q2, true, consumer2);
    }
}
