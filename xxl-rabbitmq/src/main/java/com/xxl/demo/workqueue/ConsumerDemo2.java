package com.xxl.demo.workqueue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class ConsumerDemo2 {

    private static final String HOST = "127.0.0.1";
    private static final String Q = "demo2-queue";

    public static void main(String[] args) throws Exception {
        receive();
    }

    private static void receive() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("admin");
        factory.setUsername("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        boolean durable = true;//队列的持久化
        channel.queueDeclare(Q, durable, false, false, null);
        //使用了channel.basicQos(1)保证在接收端一个消息没有处理完时不会接收另一个消息，即接收端发送了ack后才会接收下一个消息。在这种情况下发送端会尝试把消息发送给下一个not busy的接收端。
        channel.basicQos(1);

        Consumer consumer1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException, IOException {
                try {
                    doWork("Consumer1", body);
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        Consumer consumer2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    doWork("Consumer2", body);
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        //不自动会发ack
        boolean autoAck = false;
        channel.basicConsume(Q, autoAck, consumer1);
        channel.basicConsume(Q, autoAck, consumer2);
    }

    private static void doWork(String name, byte[] body) {
        String message = new String(body);
        System.out.println(name + " - Message received: " + message);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + " - Work done! " + message);
    }
}
