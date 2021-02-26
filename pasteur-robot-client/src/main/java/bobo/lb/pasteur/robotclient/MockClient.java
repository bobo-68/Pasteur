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

//    private final int GROUP_SIZE;

    private Selector selector;

    private AtomicInteger clientNum = new AtomicInteger();

    private AtomicInteger count = new AtomicInteger();

    private long start;

    private long end;

    private int requestCount = 0;

    private long totalWaitTime = 0;

    public MockClient(int concurrency) throws IOException {
        this.CONCURRENCY = concurrency;
//        this.GROUP_SIZE = concurrency / 100;
        selector = Selector.open();
    }

    public void start() throws IOException, InterruptedException {
        for(int i=0; i<CONCURRENCY; i++) {
            connect();
        }
        start = System.currentTimeMillis();
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
                    totalWaitTime += System.currentTimeMillis() - (Long) key.attachment();
                    ++requestCount;
                    count.incrementAndGet();
                    if(count.get() >= CONCURRENCY) {
                        end = System.currentTimeMillis();
                        System.out.println("20000 finished !!! in " + (end - start));
                        System.out.println("Average wait time = " + totalWaitTime / requestCount);
                        Thread.sleep(Math.max(1, 1000 - end + start));
                        start = System.currentTimeMillis();
                        count.set(0);
                        start = System.currentTimeMillis();
                    }

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    try {
                        socketChannel.read(byteBuffer);
                    } catch (IOException e) {
                        key.cancel();
                        connect();
                        continue;
                    }
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(bytes);
                    key.interestOps(SelectionKey.OP_WRITE);

                    try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream))
                    {
                        RobotCommand command = (RobotCommand) objectInputStream.readObject();

//                        System.out.println(command);

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
                        key.attach(System.currentTimeMillis());
                    } catch (IOException e) {
                        key.cancel();
                        connect();
                        continue;
                    }

//                    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    key.interestOps(SelectionKey.OP_READ);
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

        socketChannel.register(selector, SelectionKey.OP_READ).attach(System.currentTimeMillis());
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
