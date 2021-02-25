package bobo.lb.pasteur.robotservice.dto;

import bobo.lb.pasteur.robotservice.dto.enums.RobotCommandTypeEnum;

import java.io.Serializable;

public class RobotCommand implements Serializable {

    private long robotId;

    private final RobotCommandTypeEnum type;

    private RobotTask task;

    public RobotCommand(RobotCommandTypeEnum type) {
        this.type = type;
    }

    public RobotCommand(RobotCommandTypeEnum type, RobotTask task) {
        this.type = type;
        this.task = task;
    }

    public long getRobotId() {
        return robotId;
    }

    public void setRobotId(long robotId) {
        this.robotId = robotId;
    }

    public RobotCommandTypeEnum getType() {
        return type;
    }

    public RobotTask getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "RobotCommand{" +
                "type=" + type +
                ", task=" + task +
                '}';
    }
}
