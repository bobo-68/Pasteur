package bobo.lb.pasteur.robotservice.dto;

import bobo.lb.pasteur.robotservice.dto.enums.RobotCommandTypeEnum;

import java.io.Serializable;

/**
 * 待发送给机器人的指令
 * 包括几种不同的类型：无操作（目前来看实际上不会发送这种指令）、设定任务、补给（2种）、停机（2种）
 */
public class RobotCommand implements Serializable {

    // 机器人 ID
    private long robotId;

    // 指令类型
    private final RobotCommandTypeEnum type;

    // 任务（仅在指令类型为"设定任务"时有意义，否则为null）
    private RobotTask task;

    public RobotCommand(RobotCommandTypeEnum type) {
        this.type = type;
    }

    public RobotCommand(RobotTask task) {
        this.type = RobotCommandTypeEnum.SET_TASK;
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

    public void setTask(RobotTask task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "RobotCommand{" +
                "type=" + type +
                ", task=" + task +
                '}';
    }
}
