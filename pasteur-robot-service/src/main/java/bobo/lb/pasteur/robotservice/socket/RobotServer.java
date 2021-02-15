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

@Component
public class RobotServer {

    private static final String DEFAULT_IP = "localhost";

    private static final int DEFAULT_PORT = 8000;

    private static final int ROBOT_STATUS_BUFFER_SIZE = 1024;

    private final String ip;

    private final int port;

    private RobotStatusService robotStatusService;

    private RobotCommandService robotCommandService;


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
            if(selector.select() == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if(key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                else if(key.isReadable()) {
                    updateRobotStatus(key);
                    key.interestOps(SelectionKey.OP_WRITE);
                }
                else if(key.isWritable()) {
                    sendRobotCommand(key);
//                    key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    key.interestOps(SelectionKey.OP_READ);
                }
            }
        }

    }

    private void updateRobotStatus(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(ROBOT_STATUS_BUFFER_SIZE);
        socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);


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
            throw cne;
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

}
