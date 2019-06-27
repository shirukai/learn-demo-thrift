package learn.demo.thrift.server;

import learn.demo.thrift.api.DemoService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by shirukai on 2019-06-27 09:25
 * 应用启动类
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    static class ThriftServerConfiguration {
        private static final Logger log = LoggerFactory.getLogger(ThriftServerConfiguration.class);
        @Value(("${thrift.server.port}"))
        private int serverPort;
        @Autowired
        private DemoService.Iface demoService;

        @PostConstruct
        public void startThriftServer() throws TTransportException {
            // 创建处理器
            TProcessor processor = new DemoService.Processor<>(demoService);

            // 监听端口
            TNonblockingServerSocket socket = new TNonblockingServerSocket(serverPort);

            // 构建服务参数
            TNonblockingServer.Args args = new TNonblockingServer.Args(socket);

            // 设置处理器
            args.processor(processor);
            // 设置传输方式
            args.transportFactory(new TFastFramedTransport.Factory());
            // 设置传输协议
            args.protocolFactory(new TBinaryProtocol.Factory());

            // 创建服务
            TServer server = new TNonblockingServer(args);
            log.info("The application is starting thrift server on address 0.0.0.0/0.0.0.0:{}",serverPort);
            // 启动服务
            server.serve();
        }
    }
}

