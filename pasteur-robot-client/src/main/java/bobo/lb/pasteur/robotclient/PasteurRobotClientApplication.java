package bobo.lb.pasteur.robotclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@SpringBootApplication
public class PasteurRobotClientApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(PasteurRobotClientApplication.class, args);
        MockClient mockClient = new MockClient(1000);
        mockClient.start();
    }
}
