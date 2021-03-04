package bobo.lb.pasteur.robotservice.service.impl;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import bobo.lb.pasteur.robotservice.dto.enums.RobotCommandTypeEnum;
import bobo.lb.pasteur.robotservice.service.RobotCommandService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RobotCommandServiceImpl implements RobotCommandService {

    private ConcurrentHashMap<Long, RobotCommand> commandMap;

    public RobotCommandServiceImpl() {
        commandMap = new ConcurrentHashMap<>();
    }

    @Override
    public RobotCommand queryCommandFor(long robotId) {
        RobotCommand command = commandMap.get(robotId);
        if(command != null) {
            commandMap.remove(robotId);
            return command;
        } else {
            return new RobotCommand(RobotCommandTypeEnum.DEFAULT);
        }
    }

    @Override
    public RobotTask setTask(long robotId, RobotTask newTask) {
        RobotCommand command = new RobotCommand(newTask);
        return commandMap.put(robotId, command).getTask();
    }

    @Override
    public void urgentCharge(long robotId) {
        commandMap.put(robotId, new RobotCommand(RobotCommandTypeEnum.URGENT_CHARGE));
    }

    @Override
    public void delayedCharge(long robotId) {
        commandMap.put(robotId, new RobotCommand(RobotCommandTypeEnum.DELAYED_CHARGE));
    }

    @Override
    public void terminate(long robotId) {
        commandMap.put(robotId, new RobotCommand(RobotCommandTypeEnum.TERMINATE));
    }

    @Override
    public void shutDown(long robotId) {
        commandMap.put(robotId, new RobotCommand(RobotCommandTypeEnum.SHUTDOWN));
    }
}
