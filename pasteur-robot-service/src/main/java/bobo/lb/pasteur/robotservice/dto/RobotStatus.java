package bobo.lb.pasteur.robotservice.dto;

import bobo.lb.pasteur.robotservice.dto.enums.RobotStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RobotStatus implements Serializable {

    // 机器人 ID
    private long id;
    // 剩余电量
    private int battery;
    // 剩余消毒液
    private int disinfectant;
    // 当前车厢号
    private int carriage;
    // 当前 x 坐标
    private double xPos;
    // 当前 y 坐标
    private double yPos;
    // 当前状态
    private RobotStateEnum state;
    // 当前任务
    private transient RobotTask task;
    // 时间戳
    private LocalDateTime timestamp;

    public RobotStatus() {
    }

    public RobotStatus(long id, int battery, int disinfectant, int carriage, double xPos, double yPos, RobotStateEnum state, LocalDateTime timestamp) {
        this.id = id;
        this.battery = battery;
        this.disinfectant = disinfectant;
        this.carriage = carriage;
        this.xPos = xPos;
        this.yPos = yPos;
        this.state = state;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getDisinfectant() {
        return disinfectant;
    }

    public void setDisinfectant(int disinfectant) {
        this.disinfectant = disinfectant;
    }

    public int getCarriage() {
        return carriage;
    }

    public void setCarriage(int carriage) {
        this.carriage = carriage;
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public RobotStateEnum getState() {
        return state;
    }

    public void setState(RobotStateEnum state) {
        this.state = state;
    }

    public RobotTask getTask() {
        return task;
    }

    public void setTask(RobotTask task) {
        this.task = task;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
