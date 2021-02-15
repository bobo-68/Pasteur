package bobo.lb.pasteur.robotservice.dto;

import bobo.lb.pasteur.robotservice.dto.enums.RobotStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RobotStatus implements Serializable {

    private long id;

    private int battery;

    private int disinfectant;

    private int carriage;

    private double xPos;

    private double yPos;

    private RobotStateEnum state;

    private transient RobotTask task;

    private boolean disconnect;

    private LocalDateTime timestamp;

    public long getId() {
        return id;
    }

    public boolean disconnect() {
        return disconnect;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public RobotStatus(long id, int battery, int disinfectant,
                       int carriage, double xPos, double yPos,
                       RobotStateEnum state, LocalDateTime timestamp) {
        this.id = id;
        this.battery = battery;
        this.disinfectant = disinfectant;
        this.carriage = carriage;
        this.xPos = xPos;
        this.yPos = yPos;
        this.state = state;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RobotStatus{" +
                "id=" + id +
                ", battery=" + battery +
                ", disinfectant=" + disinfectant +
                ", carriage=" + carriage +
                ", xPos=" + xPos +
                ", yPos=" + yPos +
                ", state=" + state +
                ", task=" + task +
                ", disconnect=" + disconnect +
                ", timestamp=" + timestamp +
                '}';
    }
}
