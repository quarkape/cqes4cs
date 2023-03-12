package club.hue.config;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

// 设置redis的数据源为db1，目前没有使用
@Component
public class SelectRedisDb1 {

    public static RedisTemplate<String, Serializable> select(RedisTemplate redisTemplate) {
        LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        if (lettuceConnectionFactory != null) {
            lettuceConnectionFactory.setDatabase(1);
            redisTemplate.setConnectionFactory(lettuceConnectionFactory);
            lettuceConnectionFactory.resetConnection();
            lettuceConnectionFactory.afterPropertiesSet();
        }
        return redisTemplate;
    }
}