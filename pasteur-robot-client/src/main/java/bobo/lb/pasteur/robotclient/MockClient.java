package bobo.lb.pasteur.robotclient;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.enums.RobotCommandTypeEnum;
import bobo.lb.pasteur.robotservice.dto.enums.RobotStateEnum;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MockClient {

    private String robotServerHost = "192.168.1.103";

    private int robotServerPort = 8000;

    private final int CONCURRENCY;

    private final int GROUP_SIZE;

    private Selector selector;

    private AtomicInteger clientNum = new AtomicInteger();

    private AtomicInteger count = new AtomicInteger();

    public MockClient(int concurrency) throws IOException {
        this.CONCURRENCY = concurrency;
        this.GROUP_SIZE = concurrency / 100;
        selector = Selector.open();
    }

    public void start() throws IOException, InterruptedException {
        for(int i=0; i<CONCURRENCY; i++) {
            connect();
        }
        while(true) {
            if(selector.select() == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    socketChannel.read(byteBuffer);
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(bytes);
                    key.interestOps(SelectionKey.OP_WRITE);

                    try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
                    {
                        RobotCommand command = (RobotCommand) objectInputStream.readObject();
                        System.out.println(command);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if(key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    RobotStatus robotStatus = mockRobotStatus();

                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                         ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
                    {
                        objectOutputStream.writeObject(robotStatus);
                        socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                    } catch (IOException e) {
                        throw e;
                    }

//                    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    key.interestOps(SelectionKey.OP_READ);
                }

                if(count.get() < GROUP_SIZE) {
                    count.getAndIncrement();
                } else {
                    count.set(0);
                    Thread.sleep(10);
                }
            }
        }
    }

    public void connect() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(robotServerHost, robotServerPort));
        socketChannel.configureBlocking(false);

        RobotInfo robotInfo = mockRobotInfo();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
        {
            objectOutputStream.writeObject(robotInfo);
            socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw e;
        }

        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public void sendMockStatus() {

    }

    private RobotInfo mockRobotInfo() {
        int id = clientNum.getAndIncrement();
        String host = "localhost";
        Random random = new Random();
        int line = random.nextInt(20);
        String station = "东川路";
        int trainId = random.nextInt(7000);
        return new RobotInfo(id, host, line, station, trainId);
    }

    private RobotStatus mockRobotStatus() {
        Random random = new Random();
        long id = random.nextInt(clientNum.get());
        int battery = random.nextInt(100);
        int disinfectant = random.nextInt(100);
        int carriage = random.nextInt(16);
        double xPos = random.nextDouble();
        double yPos = random.nextDouble();
        RobotStateEnum state = RobotStateEnum.READY;
        LocalDateTime timestamp = LocalDateTime.now();
        return new RobotStatus(id, battery, disinfectant,
                carriage, xPos, yPos,
                state, timestamp);
    }
}
