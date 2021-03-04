//package bobo.lb.pasteur.robotservice.socket;
//
//import bobo.lb.pasteur.robotservice.service.RobotCommandService;
//import bobo.lb.pasteur.robotservice.service.RobotStatusService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.*;
//
//@Component
//public class RobotServer0 {
//
//    private ConcurrentHashMap<SelectionKey, byte[]> halfPackMap = new ConcurrentHashMap<>();
//
//    private static final String DEFAULT_IP = "localhost";
//
//    private static final int DEFAULT_PORT = 8000;
//
//    private final String ip;
//
//    private final int port;
//
//    private RobotStatusService robotStatusService;
//
//    private RobotCommandService robotCommandService;
//
//    private ExecutorService threadPool = new ThreadPoolExecutor(
//            24, 24, 60L,
//            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000));
//
//    private ThreadLocal<ByteBuffer> readBuffer = new ThreadLocal<>();
//
//    private Serializer serializer = new Serializer();
//
//    public RobotServer0() {
//        this(DEFAULT_IP, DEFAULT_PORT);
//    }
//
//    public RobotServer0(int port) {
//        this(DEFAULT_IP, port);
//    }
//
//    public RobotServer0(String ip, int port) {
//        this.ip = ip;
//        this.port = port;
//    }
//
//    @Autowired
//    public void setRobotStatusService(RobotStatusService robotStatusService) {
//        this.robotStatusService = robotStatusService;
//    }
//
//    @Autowired
//    public void setRobotCommandService(RobotCommandService robotCommandService) {
//        this.robotCommandService = robotCommandService;
//    }
//
//    /* */
//
//    public void start() throws IOException {
//
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.socket().bind(new InetSocketAddress(ip, port));
//        serverSocketChannel.configureBlocking(false);
//
//        Selector selector = Selector.open();
//        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//        while(true) {
//            if(selector.select() == 0) {
//                continue;
//            }
//
//            Set<SelectionKey> selectedKeys = selector.selectedKeys();
//            CountDownLatch latch = new CountDownLatch(selectedKeys.size());
//            Iterator<SelectionKey> iterator = selectedKeys.iterator();
//            List<SocketChannel> registerList = new LinkedList<>();
//            while(iterator.hasNext()) {
//                SelectionKey key = iterator.next();
//                iterator.remove();
//
//                if(key.isAcceptable()) {
//                    SocketChannel socketChannel = serverSocketChannel.accept();
//                    socketChannel.configureBlocking(false);
//                    registerList.add(socketChannel);
//                    latch.countDown();
//                }
//                else if(key.isReadable()) {
//                    key.interestOps(SelectionKey.OP_WRITE);
//                    threadPool.execute(new ReadTask(key, latch, robotStatusService, halfPackMap, readBuffer, serializer));
//                }
//                else if(key.isWritable()) {
//                    key.interestOps(SelectionKey.OP_READ);
//                    threadPool.execute(new WriteTask(key, latch, robotCommandService));
//                }
//            }
//            try {
//                latch.await();
//                for(SocketChannel channel : registerList) {
//                    channel.register(selector, SelectionKey.OP_READ);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//}
