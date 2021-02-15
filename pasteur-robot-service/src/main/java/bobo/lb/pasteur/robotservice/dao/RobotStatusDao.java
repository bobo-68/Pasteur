package bobo.lb.pasteur.robotservice.dao;

import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RobotStatusDao {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void updateCurrentStatus(long robotId, RobotStatus status) {
        String key = "cur-status:" + robotId;
        redisTemplate.opsForValue().set(key, status);
    }

    public void recordStatus(long robotId, RobotStatus status) {
        String key = "status:" + robotId + status.getTimestamp();
        redisTemplate.opsForValue().set(key, status);
    }
}
