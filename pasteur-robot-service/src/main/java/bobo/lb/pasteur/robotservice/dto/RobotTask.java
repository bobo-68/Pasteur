package bobo.lb.pasteur.robotservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RobotTask implements Serializable {

    private long robotId;

    private Map<Integer, Integer> taskMap;

    private LocalDateTime timastamp;

    public void setTask(Map<Integer, Integer> task) {
        this.taskMap = task;
    }

    public long getRobotId() {
        return robotId;
    }

    public Map<Integer, Integer> getTask() {
        return taskMap;
    }

    public int putTaskForCarriage(int carriage, int time) {
        int oldTimeValue = taskMap.get(carriage);
        taskMap.put(carriage, time);
        return oldTimeValue;
    }

    public int getTaskForCarriage(int carriage) {
        return taskMap.get(carriage);
    }

}
