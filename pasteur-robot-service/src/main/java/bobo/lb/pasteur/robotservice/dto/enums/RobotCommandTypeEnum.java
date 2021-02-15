package bobo.lb.pasteur.robotservice.dto.enums;

import java.io.Serializable;

public enum RobotCommandTypeEnum implements Serializable {

    DEFAULT(0, "无操作"),
    SET_TASK(1, "设定任务"),
    URGENT_CHARGE(2, "立刻停止当前作业并补给"),
    DELAYED_CHARGE(3, "完成当前车厢单次工作后开始补给"),
    TERMINATE(-1, "停止作业并返回停机点"),
    SHUTDOWN(-2, "停止作业并原地待命");

    private int commandCode;

    private String description;

    private RobotCommandTypeEnum(int commandCode, String description) {
        this.commandCode = commandCode;
        this.description = description;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public String getDescription() {
        return description;
    }

    public RobotCommandTypeEnum commandOf(int commandCode) {
        for(RobotCommandTypeEnum command : values()) {
            if(command.getCommandCode() == commandCode) {
                return command;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ROBOT COMMAND: " + description;
    }
}
