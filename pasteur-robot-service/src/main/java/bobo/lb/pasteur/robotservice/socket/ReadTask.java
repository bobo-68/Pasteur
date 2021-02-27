package bobo.lb.pasteur.robotservice.socket;

import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.service.RobotStatusService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ReadTask implements Runnable {

    private static final int ROBOT_STATUS_BUFFER_SIZE = 1024 * 1024;

    private SelectionKey key;

    private CountDownLatch latch;

    private RobotStatusService robotStatusService;

    private ConcurrentHashMap<SelectionKey, byte[]> halfPackMap;

    private ThreadLocal<ByteBuffer> readBuffer;

    ReadTask(SelectionKey key,
             CountDownLatch latch,
             RobotStatusService robotStatusService,
             ConcurrentHashMap<SelectionKey, byte[]> halfPackMap,
             ThreadLocal<ByteBuffer> readBuffer) {
        this.key = key;
        this.latch = latch;
        this.robotStatusService = robotStatusService;
        this.halfPackMap = halfPackMap;
        this.readBuffer = readBuffer;
    }

    @Override
    public void run() {
        try {
            updateRobotStatus(key);
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void updateRobotStatus(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = readBuffer.get();
        if(buffer == null) {
            buffer = ByteBuffer.allocate(ROBOT_STATUS_BUFFER_SIZE);
            readBuffer.set(buffer);
        }
        socketChannel.read(buffer);
        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        buffer.clear();

        List<byte[]> bytesList = processTcpPack(key, bytes);

        for(byte[] bs : bytesList) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bs);
                 ObjectInputStream objectInputStream =
                         new ObjectInputStream(byteArrayInputStream);) {
                Object message = objectInputStream.readObject();

                if (message instanceof RobotStatus) {
                    RobotStatus robotStatus = (RobotStatus) message;
                    if (robotStatus.disconnect()) {
                        socketChannel.close();
                    }
                    key.attach(robotStatus.getId());
                    robotStatusService.writeStatus(robotStatus);
                } else if (message instanceof RobotInfo) {
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
    }

    private List<byte[]> processTcpPack(SelectionKey key, byte[] bytes) {
        byte[] att;
        if((att = halfPackMap.get(key)) != null) {

            byte[] newBytes = new byte[att.length + bytes.length];
            System.arraycopy(att, 0, newBytes, 0, att.length);
            System.arraycopy(bytes, 0, newBytes, att.length, bytes.length);
            bytes = newBytes;
        }
        int idx = 0;
        List<byte[]> res = new LinkedList<>();
        while(idx <= bytes.length - 4) {
            int length = bytes2int(bytes, idx);
            idx += 4;
            if(bytes.length - idx < length) {
                idx -= 4;
                break;
            }

            byte[] bytes1 = Arrays.copyOfRange(bytes, idx, idx + length);
            res.add(bytes1);
            idx += length;
        }
        if(idx < bytes.length) {
            halfPackMap.put(key, Arrays.copyOfRange(bytes, idx, bytes.length));
        } else {
            halfPackMap.remove(key);
        }
        return res;
    }

    private static int bytes2int(byte[] bytes, int begin) {
        int res = 0;
        res |= (bytes[begin++] & 0xff) << 24;
        res |= (bytes[begin++] & 0xff) << 16;
        res |= (bytes[begin++] & 0xff) << 8;
        res |= (bytes[begin++] & 0xff);
        return res;
    }
}
