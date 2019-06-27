package lear.demo.thrift.client;

import learn.demo.thrift.api.DemoService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shirukai on 2019-06-27 09:48
 * 应用启动入口
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    static class ThriftClientConfiguration {
        private static final Logger log = LoggerFactory.getLogger(ThriftClientConfiguration.class);
        @Value("${thrift.server.ip}")
        private String serverIp;
        @Value("${thrift.server.port}")
        private int serverPort;

        @Bean("demoService")
        public DemoService.Iface createThriftClient() throws TTransportException {
            // 创建socket
            TTransport socket = new TSocket(serverIp, serverPort);
            // 传输方式
            TFramedTransport transport = new TFramedTransport(socket);
            transport.open();
            // 传输协议
            TProtocol protocol = new TBinaryProtocol(transport);
            log.info("The application is creating thrift client from address {}:{} ……",serverIp,serverPort);
            return new DemoService.Client(protocol);
        }
    }
}
