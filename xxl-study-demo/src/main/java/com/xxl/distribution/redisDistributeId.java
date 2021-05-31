package com.xxl.distribution;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import redis.clients.jedis.JedisShardInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class redisDistributeId {

    /**
     * 前缀+当前日期+5位自增ID（这个5位个数根据自己的业务情况设定）
     * key-20210514015826507-5位自增ID
     * 从 00001 开始到 99999结束
     * 表示在相同毫秒情况下，最多只能生成10万-1=99999个订单号，也就是说1秒钟9.9万*1000=9.9千万订单.
     * 而且订单号可以提前生成存放在redis中，因为读的速度肯定是比写的速度更快。
     * @param args
     */
    public static void main(String[] args) {

        for (int i=0; i < 3; i++) {
            generateId();
        }
    }

    public static String generateId() {
        String key = "xxlI2";
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, getFactory());
        // redisAtomicLong.set(1); //设置初始值
        // redisAtomicLong.addAndGet(10);//设置步长为10，默认为1
        long increment = redisAtomicLong.incrementAndGet();
        String id = currentTime() + "-" + String.format("%1$05d", increment);
        System.out.println(id);
        return id;
    }

    public static String currentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
    }

    public static JedisConnectionFactory getFactory() {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1",6379);
        jedisShardInfo.setPassword("xxl-redis-ps");
        return new JedisConnectionFactory(jedisShardInfo);

    }

    public static RedisTemplate getRedisTemplate() {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1",6379);
        jedisShardInfo.setPassword("xxl-redis-ps");

        RedisTemplate<String, Long> template = new RedisTemplate();
        template.setConnectionFactory(new JedisConnectionFactory(jedisShardInfo));
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
