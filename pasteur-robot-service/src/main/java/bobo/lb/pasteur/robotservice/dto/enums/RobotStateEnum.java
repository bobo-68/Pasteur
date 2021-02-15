package bobo.lb.pasteur.robotservice.dto.enums;

import bobo.lb.pasteur.robotservice.dto.RobotTask;

public enum RobotStateEnum {

    READY(0, "就绪"),
    WORKING(1, "运行"),
    CHARGING(2, "补给"),
    FAILURE(-1, "故障");

    private int stateCode;

    private String description;

    private RobotStateEnum(int stateCode, String description) {
        this.stateCode = stateCode;
        this.description = description;
    }

    public int getStateCode() {
        return stateCode;
    }

    public String getDescription() {
        return description;
    }

    public RobotStateEnum stateOf(int stateCode) {
        for(RobotStateEnum state : values()) {
            if(state.getStateCode() == stateCode) {
                return state;
            }
        }
        return null;
    }
}
