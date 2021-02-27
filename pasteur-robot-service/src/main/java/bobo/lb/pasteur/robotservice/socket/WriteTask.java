package bobo.lb.pasteur.robotservice.socket;

import bobo.lb.pasteur.robotservice.dto.RobotCommand;
import bobo.lb.pasteur.robotservice.service.RobotCommandService;
import bobo.lb.pasteur.robotservice.service.RobotStatusService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class WriteTask implements Runnable {

    private SelectionKey key;

    private CountDownLatch latch;

    private RobotCommandService robotCommandService;

    WriteTask(SelectionKey key,
              CountDownLatch latch,
              RobotCommandService robotCommandService) {
        this.key = key;
        this.latch = latch;
        this.robotCommandService = robotCommandService;
    }

    @Override
    public void run() {
        try {
            sendRobotCommand(key);
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
        } finally {
            latch.countDown();
        }
    }

    private void sendRobotCommand(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        if(key.attachment() == null) {
            return;
        }
        long robotId = (Long) key.attachment();
        RobotCommand command = robotCommandService.queryCommandFor(robotId);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream))
        {
            objectOutputStream.writeObject(command);
            socketChannel.write(ByteBuffer.wrap(wrap(byteArrayOutputStream.toByteArray())));
        } catch (IOException e) {
            throw e;
        }
    }

    private byte[] wrap(byte[] realBytes) {
        byte[] bytes = new byte[realBytes.length + 4];
        int2bytes(bytes, realBytes.length);
        System.arraycopy(realBytes, 0, bytes, 4, realBytes.length);
        return bytes;
    }

    private static void int2bytes(byte[] bytes, int x) {
        bytes[0] = (byte) (x >>> 24);
        bytes[1] = (byte) (x >>> 16);
        bytes[2] = (byte) (x >>> 8);
        bytes[3] = (byte) x;
    }
}
