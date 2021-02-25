package bobo.lb.pasteur.robotservice.socket;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.service.RobotCommandService;
import bobo.lb.pasteur.robotservice.service.RobotStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class RobotServer {

    private static final String DEFAULT_IP = "192.168.1.103";

    private static final int DEFAULT_PORT = 8000;

    private static final int ROBOT_STATUS_BUFFER_SIZE = 1024;

    private final String ip;

    private final int port;

    private RobotStatusService robotStatusService;

    private RobotCommandService robotCommandService;

    private ExecutorService readThreadPool = new ThreadPoolExecutor(
            4, 6, 60L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000));
    private ExecutorService writeThreadPool = new ThreadPoolExecutor(
            4, 6, 60L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000));

    public RobotServer() {
        this(DEFAULT_IP, DEFAULT_PORT);
    }

    public RobotServer(int port) {
        this(DEFAULT_IP, port);
    }

    public RobotServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Autowired
    public void setRobotStatusService(RobotStatusService robotStatusService) {
        this.robotStatusService = robotStatusService;
    }

    @Autowired
    public void setRobotCommandService(RobotCommandService robotCommandService) {
        this.robotCommandService = robotCommandService;
    }

    public void start() throws IOException, ClassNotFoundException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(ip, port));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {
            int readyKeyNum = selector.select();
            if(readyKeyNum == 0) {
                continue;
            }
            CountDownLatch latch = new CountDownLatch(readyKeyNum);

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    latch.countDown();
                }
                else if(key.isReadable()) {
                    readThreadPool.execute(new ReadTask(key, latch));
                }
                else if(key.isWritable()) {
                    writeThreadPool.execute(new WriteTask(key, latch));
                }
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateRobotStatus(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(ROBOT_STATUS_BUFFER_SIZE);
        socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        buffer.clear();

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream =
                     new ObjectInputStream(byteArrayInputStream);)
        {
            Object message = objectInputStream.readObject();

            if(message instanceof RobotStatus) {
                RobotStatus robotStatus = (RobotStatus) message;
                if(robotStatus.disconnect()) {
                    socketChannel.close();
                }
                key.attach(robotStatus.getId());
                robotStatusService.writeStatus(robotStatus);
            }
            else if(message instanceof RobotInfo) {
                RobotInfo robotInfo = (RobotInfo) message;
                key.attach(robotInfo.getId());
                robotStatusService.login(robotInfo);
            }

        } catch (ClassNotFoundException cne) {
            cne.printStackTrace();
            // TODO
        } catch (IOException ioe) {
            throw ioe;
        }

    }

    private void sendRobotCommand(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();

        long robotId = (Long) key.attachment();
        RobotCommand command = robotCommandService.queryCommandFor(robotId);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
        {
            objectOutputStream.writeObject(command);
            socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            throw e;
        }
    }

    private class ReadTask implements Runnable {

        private SelectionKey key;

        private CountDownLatch latch;

        ReadTask(SelectionKey key, CountDownLatch latch) {
            this.key = key;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                updateRobotStatus(key);
                key.interestOps(SelectionKey.OP_WRITE);
            } catch (IOException e) {
                key.cancel();
            } finally {
                latch.countDown();
            }
        }
    }

    private class WriteTask implements Runnable {

        private SelectionKey key;

        private CountDownLatch latch;

        WriteTask(SelectionKey key, CountDownLatch latch) {
            this.key = key;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                sendRobotCommand(key);
                key.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                key.cancel();
            } finally {
                latch.countDown();
            }
        }
    }

}
