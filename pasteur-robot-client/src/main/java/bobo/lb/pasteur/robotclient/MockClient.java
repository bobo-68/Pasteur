package bobo.lb.pasteur.robotclient;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.enums.RobotCommandTypeEnum;
import bobo.lb.pasteur.robotservice.dto.enums.RobotStateEnum;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MockClient {

    private final String robotServerHost = "192.168.64.1";
    private final int robotServerPort = 8000;
    private final int CONCURRENCY;
    private final Selector selector;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final AtomicInteger count = new AtomicInteger();
    private volatile long start;
    private volatile long end;
    private volatile int connectionNum = 0;
    private int processCount = 0;
    private long totalProcessTime = 0;
    AtomicInteger clientNum = new AtomicInteger();
    private final byte[] mockStatusBytes;
    private final byte[] mockInfoBytes;
    private ThreadLocal<ByteBuffer> readBuffer = new ThreadLocal<>();

    public MockClient(int concurrency) throws IOException {
        this.CONCURRENCY = concurrency;

        selector = Selector.open();
        Random random = new Random();

        // prepare mock status
        long id = random.nextInt(CONCURRENCY);
        int battery = random.nextInt(100);
        int disinfectant = random.nextInt(100);
        int carriage = random.nextInt(16);
        double xPos = random.nextDouble();
        double yPos = random.nextDouble();
        RobotStateEnum state = RobotStateEnum.READY;
        LocalDateTime timestamp = LocalDateTime.now();
        RobotStatus mockStatus = new RobotStatus(id, battery, disinfectant,
                carriage, xPos, yPos,
                state, timestamp);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(mockStatus);
            byte[] mockStatusRealBytes = byteArrayOutputStream.toByteArray();
            mockStatusBytes = wrap(mockStatusRealBytes);
        }

        // prepare mock info

        int id2 = 0;
        String host = "localhost";
        int line = 12;
        String station = "东川路";
        int trainId = 15;
        RobotInfo mockInfo = new RobotInfo(id2, host, line, station, trainId);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(mockInfo);
            byte[] mockInfoRealBytes = byteArrayOutputStream.toByteArray();
            mockInfoBytes = wrap(mockInfoRealBytes);
        }
    }

    public void start() throws IOException, InterruptedException {

        for(int i = 0; i < CONCURRENCY; i++) {
            connect();
        }

        start = System.currentTimeMillis();

        while(true) {

            // connect CONCURRENCY
//            if(connectionNum < 50) {
//                ConcurrentLinkedQueue<SocketChannel> registerQueue = new ConcurrentLinkedQueue<>();
//                CountDownLatch latch0 = new CountDownLatch(CONCURRENCY / 50);
//                for (int i = 0; i < CONCURRENCY / 50; i++) {
//                    threadPool.execute(() -> {
//                        connect(latch0, registerQueue);
//                    });
//                }
//                System.out.println("Connection created " + (connectionNum+1) + "/50");
//                connectionNum++;
//                latch0.await();
//                while(!registerQueue.isEmpty()) {
//                    registerQueue.poll().register(selector, SelectionKey.OP_READ);
//                }
//            }

            if(selector.select() == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            CountDownLatch latch = new CountDownLatch(selectedKeys.size());
//            System.out.println("key num = " + selectedKeys.size());
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isReadable()) {
                    threadPool.execute(new ReadTask(key, latch));
                    key.interestOps(SelectionKey.OP_WRITE);
                }
                else if(key.isWritable()) {
                    threadPool.execute(new WriteTask(key, latch));
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
            latch.await();
            if(count.get() >= CONCURRENCY) {
                end = System.currentTimeMillis();
                count.set(0);
                long pt = end - start;
                System.out.println(CONCURRENCY + " requests used " + pt + "secs.");
                totalProcessTime += pt;
                System.out.println("Average process time is " + (totalProcessTime/(++processCount)));
                pt = System.currentTimeMillis() - start;
                if(pt < 1000) {
                    Thread.sleep(1000 - pt);
                }
                start = System.currentTimeMillis();
            }
//            System.out.println("Round finished.");
        }
    }

//    public void connect(CountDownLatch latch, Queue<SocketChannel> registerQueue) {
//        count.getAndIncrement();
//        try {
//            SocketChannel socketChannel = null;
//            try {
//                socketChannel = SocketChannel.open();
//                socketChannel.connect(new InetSocketAddress(robotServerHost, robotServerPort));
//                socketChannel.configureBlocking(false);
//                socketChannel.write(ByteBuffer.wrap(mockInfoBytes));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            registerQueue.offer(socketChannel);
//            System.out.println("Connect " + clientNum.incrementAndGet());
//        } finally {
//            latch.countDown();
//        }
//    }

    public void connect() {
        count.getAndIncrement();
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(robotServerHost, robotServerPort));
            socketChannel.configureBlocking(false);
            socketChannel.write(ByteBuffer.wrap(mockInfoBytes));
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("Connect " + clientNum.incrementAndGet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ReadTask implements Runnable {
        SelectionKey key;
        private CountDownLatch latch;

        public ReadTask(SelectionKey key, CountDownLatch latch) {
            this.key = key;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = readBuffer.get();
                if(buffer == null) {
                    buffer = ByteBuffer.allocate(1048576);
                    readBuffer.set(buffer);
                }
                ((SocketChannel) key.channel()).read(buffer);
                buffer.clear();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }

    private class WriteTask implements Runnable {
        SelectionKey key;
        private CountDownLatch latch;

        public WriteTask(SelectionKey key, CountDownLatch latch) {
            this.key = key;
            this.latch = latch;
        }

        @Override
        public void run() {
            count.getAndIncrement();
            try {
                ((SocketChannel) key.channel()).write(ByteBuffer.wrap(mockStatusBytes));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }


    private byte[] wrap(byte[] realBytes) {
        byte[] bytes = new byte[realBytes.length + 4];
        int2bytes(bytes, realBytes.length);
        System.arraycopy(realBytes, 0, bytes, 4, realBytes.length);
        return bytes;
    }

    private void int2bytes(byte[] bytes, int x) {
        bytes[0] = (byte) (x >>> 24);
        bytes[1] = (byte) (x >>> 16);
        bytes[2] = (byte) (x >>> 8);
        bytes[3] = (byte) x;
    }
}
