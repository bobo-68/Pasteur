package bobo.lb.pasteur.robotservice;

import bobo.lb.pasteur.robotservice.socket.RobotServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@SpringBootApplication
//@EnableEurekaClient
public class PasteurRobotServiceApplication {

    private RobotServer robotServer;

    public static void main(String[] args) {

        SpringApplication.run(PasteurRobotServiceApplication.class, args);

    }

}

@Component
class Assistance {

    private RobotServer robotServer;

    @Autowired
    public Assistance(RobotServer robotServer) {
        this.robotServer = robotServer;
        new Thread(() -> {
            try {
                robotServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
