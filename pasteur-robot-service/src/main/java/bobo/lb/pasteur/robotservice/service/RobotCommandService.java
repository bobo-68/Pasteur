package bobo.lb.pasteur.robotservice.service;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotTask;

public interface RobotCommandService {

    RobotCommand queryCommandFor(long robotId);

    RobotTask setTask(long robotId, RobotTask newTask);

    void urgentCharge(long robotId);

    void delayedCharge(long robotId);

    void terminate(long robotId);

    void shutDown(long robotId);
}
