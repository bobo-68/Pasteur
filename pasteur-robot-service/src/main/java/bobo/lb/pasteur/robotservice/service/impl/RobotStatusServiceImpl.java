package bobo.lb.pasteur.robotservice.service.impl;

import bobo.lb.pasteur.robotservice.dao.RobotStatusDao;
import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.service.RobotStatusService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class RobotStatusServiceImpl implements RobotStatusService {

    private static final String DISPATCH_SERVICE_NAME = "pasteur-dispatch-service";

    private RobotStatusDao robotStatusDao;

//    private DiscoveryClient discoveryClient;

//    @Autowired
//    public RobotStatusServiceImpl(RobotStatusDao robotStatusDao, DiscoveryClient discoveryClient) {
//        this.robotStatusDao = robotStatusDao;
//        this.discoveryClient = discoveryClient;
//    }

    @Autowired
    public RobotStatusServiceImpl(RobotStatusDao robotStatusDao) {
        this.robotStatusDao = robotStatusDao;
    }

    @Override
    public void writeStatus(RobotStatus status) {
        long id = status.getId();
        robotStatusDao.recordStatus(id, status);
        robotStatusDao.updateCurrentStatus(id, status);
    }

    @Override
    public void login(RobotInfo robotInfo) {
        System.out.println(robotInfo.getId() + " loggs in.");
//        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(DISPATCH_SERVICE_NAME);
//        String host = serviceInstances.get(0).getHost();
//        int port = serviceInstances.get(0).getPort();
//        long robotId = robotInfo.getId();
//        String path = String.format(
//                "http//%s:%s/robots/%s/login",
//                host, port, robotId);
//
//        WebClient webClient = WebClient.create(path);
//        webClient.post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(robotInfo)
//                .retrieve();
    }
}
