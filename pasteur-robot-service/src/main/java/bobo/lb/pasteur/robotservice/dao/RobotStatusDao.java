package bobo.lb.pasteur.robotservice.dao;

import bobo.lb.pasteur.robotservice.dao.ser.RobotStatusSerializer;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RobotStatusDao {

    private RedisTemplate<String, RobotStatus> statusRedisTemplate;

    private RedisTemplate<String, RobotTask> taskRedisTemplate;

    @Resource
    public void setStatusRedisTemplate(RedisTemplate<String, RobotStatus> statusRedisTemplate) {
        this.statusRedisTemplate = statusRedisTemplate;
    }

    @Resource
    public void setTaskRedisTemplate(RedisTemplate<String, RobotTask> taskRedisTemplate) {
        this.taskRedisTemplate = taskRedisTemplate;
    }

    /**
     * 将单个机器人状态写入 Redis，然后读取其对应的 Task 并删除
     * @param robotId
     * @param status
     * @return 这个机器人当前的 Task
     */
    public RobotTask updatestatusAndGetTask(long robotId, RobotStatus status) {
        statusRedisTemplate.opsForValue().set("cur-status:" + robotId, status);
        statusRedisTemplate.opsForValue().set("status:" + robotId + status.getTimestamp(), status);
        String taskKey = "cur-task:" + robotId;
        RobotTask task = taskRedisTemplate.opsForValue().get(taskKey);
        return task;
    }

    /**
     * 将状态批量写入 Redis，然后批量读取这些状态对应的 Task 并批量删除
     * @param statuses
     * @return
     */
    public List<RobotTask> updateStatusAndGetTaskInBatch(List<RobotStatus> statuses) {
        Map<String, RobotStatus> newStatusEntries = new HashMap<>(statuses.size());
        List<String> taskKeys = new ArrayList<>(statuses.size());
        for(RobotStatus status : statuses) {
            newStatusEntries.put("cur-status:" + status.getId(), status);
//            newStatusEntries.put("status:" + status.getId() + status.getTimestamp(), status);
            taskKeys.add("cur-task:" + status.getId());
        }
        statusRedisTemplate.opsForValue().multiSet(newStatusEntries);
        List<RobotTask> tasks = taskRedisTemplate.opsForValue().multiGet(taskKeys);
//        taskRedisTemplate.delete(newStatusEntries.keySet());
        return tasks;
    }

    public void recordStatus(long robotId, RobotStatus status) {
        String key = "status:" + robotId + status.getTimestamp();
        statusRedisTemplate.opsForValue().set(key, status);
    }
}
