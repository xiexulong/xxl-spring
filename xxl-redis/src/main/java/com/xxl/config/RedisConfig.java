package com.xxl.config;

import com.xxl.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    private String getHost() {
        return host;
    }
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisShardInfo jedisShardInfo =
                new JedisShardInfo(getHost(),6379);
        jedisShardInfo.setPassword("xxl-redis-ps");
        return new JedisConnectionFactory(jedisShardInfo);
//        return new JedisConnectionFactory();   直接使用new JedisConnectionFactory 不能读取配置文件信息
    }

    @Bean
    public RedisTemplate<String, User> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, User> template = new RedisTemplate<String, User>();
        JedisConnectionFactory jedisConnectionFactory = jedisConnectionFactory();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.auth("xxl-redis-ps");//设置密码
        System.out.println("服务正在运行: "+jedis.ping());
        System.out.println(jedis.get("xxl"));
    }
}
