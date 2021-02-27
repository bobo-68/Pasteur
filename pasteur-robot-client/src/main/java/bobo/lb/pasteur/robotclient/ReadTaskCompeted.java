package bobo.lb.pasteur.robotclient;

import java.nio.channels.SelectionKey;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadTaskCompeted implements Runnable {
    SelectionKey key;
    private CountDownLatch latch;
    AtomicInteger count;

    public ReadTaskCompeted(SelectionKey key, AtomicInteger count, CountDownLatch latch) {
        this.key = key;
        this.count = count;
        this.latch = latch;
    }

    @Override
    public void run() {
//        if (count.get() >= CONCURRENCY) {
//                end = System.currentTimeMillis();
//                long pt = end - start;
//                if(connectionNum >= 50) {
//                    System.out.println("20000 finished !!! in " + (pt));
//                    totalProcessTime += pt;
//                    processCount++;
//                    System.out.println("Average process time = " + totalProcessTime / processCount);
//                }
//                try {
//                    if(pt < 1000)
//                        Thread.sleep(1000 - pt);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                        start = System.currentTimeMillis();
//                count.set(0);
//                start = System.currentTimeMillis();
//            }
        try {
            count.incrementAndGet();
            //a
//                SocketChannel socketChannel = (SocketChannel) key.channel();
//                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//                try {
//                    socketChannel.read(byteBuffer);
//                } catch (IOException e) {
////                    System.out.println("read error");
//                    key.cancel();
//                    connect(new CountDownLatch(1));
//                    return;
//                }
//                byteBuffer.flip();
            //b
//                byte[] bytes = new byte[byteBuffer.limit()];
//                byteBuffer.get(bytes);

//                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//                     ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
//                    RobotCommand command = (RobotCommand) objectInputStream.readObject();
//
////                        System.out.println(command);
//
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("read ok");
        } finally {
            latch.countDown();
        }
    }
}
