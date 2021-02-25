package bobo.lb.pasteur.dispatchservice.web;

import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/robots/{id}/login")
    public ResponseEntity login(@PathVariable("id") long id, @RequestBody RobotInfo robotInfo) throws InterruptedException {
        Thread.sleep(10);
        System.out.println(id + "logs in.");
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
