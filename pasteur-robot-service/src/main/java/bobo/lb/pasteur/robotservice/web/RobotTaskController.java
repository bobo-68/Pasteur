package bobo.lb.pasteur.robotservice.web;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import bobo.lb.pasteur.robotservice.service.RobotCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/robots/tasks")
public class RobotTaskController {

    @Autowired
    private RobotCommandService robotCommandService;

    @PostMapping("/set")
    public ResponseEntity setTasksInBatch(List<RobotTask> tasks) {

        for(RobotTask task : tasks) {
            long id = task.getRobotId();
            robotCommandService.setTask(id, task);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

}
