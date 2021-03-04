package bobo.lb.pasteur.robotservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 机器人任务
 * 内容包括要消毒的车厢号及对应消毒次数，用一个Map存储
 */
public class RobotTask implements Serializable {

    // 机器人 ID
    private long robotId;

    // 任务内容：K：车厢号，V：消毒次数
    private Map<Integer, Integer> taskMap;

    // 任务创建的时间
    private LocalDateTime timestamp;

    public long getRobotId() {
        return robotId;
    }

    public void setRobotId(long robotId) {
        this.robotId = robotId;
    }

    public Map<Integer, Integer> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(Map<Integer, Integer> taskMap) {
        this.taskMap = taskMap;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RobotTask{" +
                "robotId=" + robotId +
                ", taskMap=" + taskMap +
                ", timestamp=" + timestamp +
                '}';
    }
}
