package bobo.lb.pasteur.robotclient;

import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.enums.RobotStateEnum;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Random;

@SpringBootTest
class PasteurRobotClientApplicationTests {

    private static RuntimeSchema<RobotStatus> schema = RuntimeSchema.createFrom(RobotStatus.class);

    @Test
    void contextLoads() {

    }

//    public static void main(String[] args) {
//        Random random = new Random();
//        long id = random.nextInt();
//        int battery = random.nextInt(100);
//        int disinfectant = random.nextInt(100);
//        int carriage = random.nextInt(16);
//        double xPos = random.nextDouble();
//        double yPos = random.nextDouble();
//        RobotStateEnum state = RobotStateEnum.READY;
//        LocalDateTime timestamp = LocalDateTime.now();
//        RobotStatus status = new RobotStatus(id, battery, disinfectant,
//                carriage, xPos, yPos,
//                state, timestamp);
////        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
////        {
////            objectOutputStream.writeObject(status);
////            System.out.println(byteArrayOutputStream.toByteArray().length);
////        } catch (IOException e) {
////        }
//        System.out.println(status);
//        byte[] bytes =
//                ProtostuffIOUtil.toByteArray(status, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
//        System.out.println(bytes.length);
//
//        id = random.nextInt();
//        battery = random.nextInt(100);
//        disinfectant = random.nextInt(100);
//         carriage = random.nextInt(16);
//         xPos = random.nextDouble();
//         yPos = random.nextDouble();
//         state = RobotStateEnum.READY;timestamp = LocalDateTime.now();
//        RobotStatus newStatus = new RobotStatus(id, battery, disinfectant,
//                carriage, xPos, yPos,
//                state, timestamp);
//
//        System.out.println(newStatus);
//
//        ProtostuffIOUtil.mergeFrom(bytes, newStatus, schema);
//
//        System.out.println(newStatus);
//    }

}
