package bobo.lb.pasteur.robotservice.service.impl;

import bobo.lb.pasteur.robotservice.dao.RobotStatusDao;
import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.service.RobotStatusService;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedList;
import java.util.List;

@Service
public class RobotStatusServiceImpl implements RobotStatusService {

    private static final String DISPATCH_SERVICE_NAME = "pasteur-dispatch-service";

//    // 在 RobotServer 中，接受机器人的连接并发起登录，是一个单线程运行的，因此这个 list 本来不存在同步问题，
//    // 但为了应付高并发登录的场景，希望在 list 接收到1个RobotInfo后，等待5秒，积攒足够多的数量，再一次性发送登录请求
//    // 这就导致了该list需要被并发读写
//    private LinkedList<RobotInfo> loginWaitList = new LinkedList<>();

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
        System.out.println(robotInfo.getId() + " logs in.");
//        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(DISPATCH_SERVICE_NAME);
//        String host = serviceInstances.get(0).getHost();
//        int port = serviceInstances.get(0).getPort();
        long robotId = robotInfo.getId();
        String path = String.format(
                "http://%s:%s/robots/%s/login",
                "192.168.64.1", 9090, robotId);

//        WebClient webClient = WebClient.create(path);
//        webClient.post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(robotInfo)
//                .retrieve();
//        System.out.println(robotInfo.getId() + " logs in succeeded.");

        RestTemplate template = new RestTemplate();
        template.postForEntity(path, robotInfo, ResponseEntity.class);
        System.out.println(robotInfo.getId() + " logs in succeeded.");
    }
}
