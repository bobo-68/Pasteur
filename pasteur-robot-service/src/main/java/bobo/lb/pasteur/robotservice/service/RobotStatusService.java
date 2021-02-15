package bobo.lb.pasteur.robotservice.service;

import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;

public interface RobotStatusService {

    void writeStatus(RobotStatus status);

    void login(RobotInfo robotInfo);
}
