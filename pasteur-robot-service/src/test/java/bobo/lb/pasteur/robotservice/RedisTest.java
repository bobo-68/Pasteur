package bobo.lb.pasteur.robotservice;

import bobo.lb.pasteur.robotservice.dao.RobotStatusDao;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RobotStatusDao robotStatusDao;

    private List<RobotStatus> statusList;

    @BeforeEach
    private void prepareStatuses() {
        statusList = new ArrayList<>(20000);
        for(int i = 0; i < 14000; i++) {
            RobotStatus status = new RobotStatus();
            status.setId(i);
            statusList.add(status);
        }
    }

    @Test
    public void testRedisUpdateAndGet() {
        long start = System.currentTimeMillis();
        for(RobotStatus status : statusList) {
            robotStatusDao.updatestatusAndGetTask(status.getId(), status);
        }
        long end = System.currentTimeMillis();
        System.out.println("Used time: " + (end - start));
        // 大约需要 7.2 s
    }

    @Test
    public void testRedisUpdateAndGetInBatch() {
        long start = System.currentTimeMillis();
        robotStatusDao.updateStatusAndGetTaskInBatch(statusList);
        long end = System.currentTimeMillis();
        System.out.println("Used time: " + (end - start));
    }
}
