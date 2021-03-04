package bobo.lb.pasteur.robotservice;

import bobo.lb.pasteur.robotservice.dao.ser.RobotStatusSerializer;
import bobo.lb.pasteur.robotservice.dao.ser.RobotTaskSerializer;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RobotStatus> robotStatusRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RobotStatus> robotStatusRedisTemplate =  new RedisTemplate<>();
        robotStatusRedisTemplate.setConnectionFactory(redisConnectionFactory);
        robotStatusRedisTemplate.setKeySerializer(new StringRedisSerializer());
        robotStatusRedisTemplate.setValueSerializer(new RobotStatusSerializer());
        robotStatusRedisTemplate.afterPropertiesSet();
        return robotStatusRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, RobotTask> robotTaskRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RobotTask> robotTaskRedisTemplate =  new RedisTemplate<>();
        robotTaskRedisTemplate.setConnectionFactory(redisConnectionFactory);
        robotTaskRedisTemplate.setKeySerializer(new StringRedisSerializer());
        robotTaskRedisTemplate.setValueSerializer(new RobotTaskSerializer());
        robotTaskRedisTemplate.afterPropertiesSet();
        return robotTaskRedisTemplate;
    }
}
