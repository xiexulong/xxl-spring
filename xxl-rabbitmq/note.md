环境搭建：

docker pull rabbitmq:management

docker run -dit --name xxl-rabbitmq -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 rabbitmq:management

http://127.0.0.1:15672/

注：15672是管理界面的端口，5672是服务的端口。这里顺便将管理系统的用户名和密码设置为admin admin


一、交换器类型
在rabbitmq中，生产者的消息都是通过交换器来接收，然后再从交换器分发到不同的队列中去，在分发的过程中交换器类型会影响分发的逻辑。

rabitmq中的交换器有4种类型，分别为fanout、direct、topic、headers四种，

直连型交换机（direct exchange）是根据消息携带的路由键（routing key）将消息投递给对应队列的（消息和队列的路由键必须相同）

主题交换机topic与direct类型不同的是判断routeing的规则是模糊匹配模式。（消息和队列的路由键模糊匹配）

扇型交换机（funout exchange）将消息路由给绑定到它身上的所有队列。不同于直连交换机，路由键在此类型上不启任务作用。如果N个队列绑定到某个扇型交换机上，当有消息发送给此扇型交换机时，交换机会将消息的发送给这所有的N个队列





# demo 中：

1、helloword 单发送单接收， 不需要交换机，是一个消费者消费队列里面的消息

![RUNOOB 图标](https://images0.cnblogs.com/i/434101/201408/171507104838529.png)

2、workqueue 单发送多接收， 不需要交换机，是多个消费者共同消费一个队列里面的消息

![RUNOOB 图标](https://images0.cnblogs.com/i/434101/201408/171513043112193.png)

3、pubsub 发布、订阅模式，发送端发送广播消息，多个接收端接收，是通过交换机将消息放到多个队列，每个消费者消费自己队列里面的消息（相当于一个消息被消费多次）

![RUNOOB 图标](https://images0.cnblogs.com/i/434101/201408/171657207955618.png)

4、routing 发送端按routing key发送消息，不同的接收端按不同的routing key接收消息，使用直接交换机，是生产者给每个消息添加routingKey属性再放到发布到交换机上，消费者根据过滤条件将交换机上的消息放到自己的队列中进行消费 （每个消费者有选着性的消费）

![RUNOOB 图标](https://images0.cnblogs.com/i/434101/201408/171709256231158.png)

在绑定queue和exchange的时候使用了routing key，即从该exchange上只接收routing key指定的消息。


5、topic 使用topic 交换机，每条消息对发给绑定的每一个队列。然后消费者根据自己的过滤条件消费队列上的消息

![RUNOOB 图标](https://images0.cnblogs.com/i/434101/201408/171739210926049.png)

和上一个场景的区别：

1、exchange的type为topic

2、收消息的routing key不是固定的单词，而是匹配字符串，如"*.lu.#"，*匹配一个单词，#匹配0个或多个单词。
