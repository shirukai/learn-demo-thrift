# RPC框架初体验之Thrift

> 版本说明：thrfit 0.12.0

模块说明：

* thrift-demo-java-api: 使用thrift生成Java api
* thrift-demo-java-server: Java 实现Thrift服务端
* thrift-demo-java-client：Java实现Thrift客户端
* thrift-demo-py-api：使用thrift生成Python api
* thrift-demo-py-server：Python实现Thrift服务端
* thrift-demo-py-client：Python实现Thrift客户端

# 1 前言

上一篇文章《RPC框架初体验之Dubbo》，体验了阿里开源的RPC框架，该框架体验还算不错，业界使用也较多。但是仅支持Java语言，不能进行跨语言。这里就体验一款性能不错，评价不错，且支持跨语言的RPC框架thrfit。本篇将分别使用Java和Python实现thrift的服务端和客户端，并进行交叉调用。

# 2 项目准备

## 2.1 thrfit 安装

thrift的安装方式有好多种，像我在mac环境可以使用brew install thrfit的方式进行安装，也可以通过源码编译的方式进行安装。同样在linux环境下，centos可以使用yum，ubantu可以使用apt，当然unix的环境都可以使用源码编译的方式安装。thrfit的也支持window环境的安装，在官网下载exe二进制的安装文件进行安装即可。http://www.apache.org/dyn/closer.cgi?path=/thrift/0.12.0/thrift-0.12.0.exe 。当然，官方也建议我们使用docker去安装thrift环境，关于thrfit的安装步骤这里就不详细介绍。

## 2.2 创建项目

先看一下整体的目录结构

```
learn-demo-thrift/
├── README.md
├── pom.xml
├── thrfit-demo-java-server
├── thrift
├── thrift-demo-java-api
├── thrift-demo-java-client
├── thrift-demo-py-api
├── thrift-demo-py-client
└── thrift-demo-py-server
```

关于几个模块的功能在文章开头已经描述过了，另外包块一个thrift的文件夹，里面用来存放我们的.thrirft生成文件和生成脚本。

### 2.2.1 创建一个Maven项目：learn-demo-thrift

这里直接使用IDEA创建一个Maven项目即可，指定groupId为learn.demo，指定artifactId为thrift，指定version为1.0，项目名称为：learn-demo-thrift

![](https://raw.githubusercontent.com/shirukai/images/master/532984c696fa4701331335e2d56c243f.jpg)

### 2.2.2 创建文件夹thrift

在刚才创建的项目里，创建一个名称为thrift的文件夹，用来保存我们的.thrift生成文件和生成脚本。

### 2.2.3 创建Maven子模块：thrift-demo-java-api

在项目里创建Maven子模块，指定groupId为learn.demo，指定artifactId为thrift-demo-java-api，指定version为1.0，模块名称为：thrift-demo-java-api

### 2.2.4 创建Maven子模块：thrift-demo-java-server

在项目里创建Maven子模块，指定groupId为learn.demo，指定artifactId为thrift-demo-java-server，指定version为1.0，模块名称为：thrift-demo-java-server

### 2.2.5 创建Maven子模块：thrift-demo-java-client

在项目里创建Maven子模块，指定groupId为learn.demo，指定artifactId为thrift-demo-java-client，指定version为1.0，模块名称为：thrift-demo-java-client

### 2.2.6 创建Python子模块：thrift-demo-py-api

在项目里创建Python子模块，Python环境选择2.7，模块名称为：thrift-demo-py-api

### 2.2.7 创建Python子模块：thrift-demo-py-server

在项目里创建Python子模块，Python环境选择2.7，模块名称为：thrift-demo-py-server

### 2.2.8 创建Python子模块：thrift-demo-py-client

在项目里创建Python子模块，Python环境选择2.7，模块名称为：thrift-demo-py-client



![](https://raw.githubusercontent.com/shirukai/images/master/9cd73f82cf66b7228a942c54dfd4128e.jpg)

最终项目框架创建完成。

# 3 Thrift API生成

上文提到，thrirft支持跨语言，它之所以支持跨语言，是因为它的服务是我们根据.thrift文件生成的，我们只需按照固定的格式，定义好一个thrift服务，然后指定服务语言，就可以把代码自动生成。这里就简单定义一个服务和一个数据类型，并分别生成java和python两个语言的API。

## 3.1 定义.thrift文件

在项目下的thrift目录下创建一个名为demo.thrift的文件，该文件包括三部分内容:

* namespace: 用来描述生成的语言，以及包路径，如namespace java learn.demo.thrift.api
* struct: 用来定义数据结构，对应Java里的实体类
* service: 用来定义服务，里面包括定义的抽象方法

如下demo.thrift文件，我们定义了两个namespace，分别为java和python的，并且定义了一个复杂的数据结构，包括一个int类型的id和一个string类型的name以及一个list<\striung\>类型的列表。并且定义了一个service里面包含了两个方法。

```thrift
namespace java learn.demo.thrift.api
namespace py thrift_demo.api

struct DemoInfo{
    1:i32 id,
    2:string name,
    3:list<string> tags
}

service DemoService{

    DemoInfo getDemoById(1:i32 id);

    void createDemo(1:DemoInfo demo)
}
```

## 3.2 创建生成脚本

thrift文件定义好之后，我们就以使用thrift命令进行代码生成，例如

```shell
thrift --gen java -out ../thrift-demo-java-api/src/main/java demo.thrift
```

--gen 指定生成的语言

--out 指定生成路径

为了方便起见，我们直接创建一个名为gen-code.sh的shell脚本，一次性生成java和python的代码，如下所示：

```shell
#!/usr/bin/env bash
thrift --gen java -out ../thrift-demo-java-api/src/main/java demo.thrift

thrift --gen py -out ../thrift-demo-py-api demo.thrift
```

## 3.3 生成代码

执行gen-code.sh脚本，会在thrift-demo-java-api和thrirft-demo-py-api下生成代码。如下图所示，生成的Python代码

![](https://raw.githubusercontent.com/shirukai/images/master/ebd59b411c15bca946abddfb9c2a468a.jpg)

如下图所示，生成的Java代码

![](https://raw.githubusercontent.com/shirukai/images/master/ebacf916b706cd678822e9c82d0ea2be.jpg)

### 3.3.1 thrift-demo-java-api中添加依赖

生成Java代码之后，我们打开代码查看

![](https://raw.githubusercontent.com/shirukai/images/master/26dfbb026763856c5a70f1d9738b8073.jpg)

发现代码飘红，原因是我们没有引入thrift依赖，所以要在pom文件中引入相关依赖。

```python
        <dependency>
            <groupId>org.apache.thrift</groupId>
            <artifactId>libthrift</artifactId>
            <version>0.12.0</version>
        </dependency>
```

### 3.3.2 发布thrift-demo-py-api

上面我们缺少Maven依赖，同理这里缺少Python的包依赖，我们可以使用pip安装thrift包

```shell
pip install thrift
```

这里要注意，Maven模块我们可以在其它模块里直接引入依赖即可，但是Python在不同模块里没法直接使用。所以这里把生成的Python代码进行打包。在thrift-demo-py-api模块下，创建一个setup.py文件用来进行打包安装。内容如下

```python
# encoding: utf-8
from setuptools import setup, find_packages

setup(name="thrift_demo_py_api",
      version="1.0",
      description="The api of thrift demo.",
      author="shirukai",
      author_email="shirukai@hollysys.com",
      url="https://shirukai.github.io",

      packages=find_packages(),
      scripts=[]
      )
```

将此模块以包的形式发布到环境中

```shell
python setup.py install 
```

这样我们在其它的模块里就可以直接引用了。

```
>>> from thrift_demo.api import DemoService
>>> 
```

# 4 Java实现服务端和客户端

上面我们已经在thrift-demo-java-api中生成了thrift服务相关的Java代码，这里我们就要使用Java去实现服务端和客户端，服务端和客户端都与SpringBoot整合实现。

## 5.1 服务端：thrift-demo-java-server

服务端主要实现两个方面：

* 实现抽象接口
* 实现服务暴露

在这之前，我们需要对模块进行稍加改造，因为是springboot项目，所以这里指定项目parent为springboot。

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.0.RELEASE</version>
    </parent>
```

然后引入api依赖和springboot的依赖。

```xml
        <!-- demo api -->
        <dependency>
            <groupId>learn.demo</groupId>
            <artifactId>thrift-demo-java-api</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
        <!-- spring boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
```

### 5.1.1 实现抽象接口

在第三节定义demo.thrift的时候，我们在service里定义了两个抽象方法，thrift会给我生成一个接口类

```java
public class DemoService {

  public interface Iface {

    public DemoInfo getDemoById(int id) throws org.apache.thrift.TException;

    public void createDemo(DemoInfo demo) throws org.apache.thrift.TException;

  }
  //……
 }
```

所以在服务端，我们首先要实现这个接口。在learn.demo.thrift.server.service包下创建一个名为DemoServiceImpl的类。该类继承DemoService.Iface接口，实现里面的两个方法。内容如下

```java
package learn.demo.thrift.server.service;

import learn.demo.thrift.api.DemoInfo;
import learn.demo.thrift.api.DemoService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shirukai on 2019-06-27 09:28
 * Demo Service
 */
@Service
public class DemoServiceImpl implements DemoService.Iface {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceImpl.class);
    private static final Map<Integer, DemoInfo> demoCache = new HashMap<>(16);

    @Override
    public DemoInfo getDemoById(int id) throws TException {
        log.info("The client invoke method: getDemoById");
        if (demoCache.containsKey(id)) {
            return demoCache.get(id);
        }
        return null;
    }

    @Override
    public void createDemo(DemoInfo demo) throws TException {
        log.info("The client invoke method: createDemo");
        demoCache.put(demo.id, demo);
    }
}

```

### 5.1.2 实现服务暴露

这里就需要我们去实现一个thrift的服务暴露，暴露一个端口，使客户端可以进行通讯。在这之前我们使用springboot统一的配置文件指定一下需要暴露的端口。

在resources下创建一个application.properties配置文件，指定thrift服务暴露的端口为7911

```properties
thrift.server.name=thrift-demo-server
thrift.server.port=7911
```

然后像普通的SpringBoot应用一样，创建一个启动类，在learn.demo.thrift.server下创建Application类，用以启动SpringBoot应用。

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
  //……
}
```

Thrift服务实现，大体分一下几步：

1. 创建处理器
2. 设置监听端口
3. 构建服务参数：指定处理器、指定传输方式、指定传输协议
4. 创建服务
5. 启动服务

这里我们使用的是@Configuration的形式，将我们的Thrift服务注入到SpringBoot应用里。在Application里创建静态内部类ThriftServerConfiguration

```java
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
```

## 5.2 客户端：thrift-demo-java-client

客户端同样是与SpringBoot整合

* 实现客户端并以Bean的形式注入Spring
* 实现REST接口，用来演示远程调用

在这之前，我们依然需要对模块进行稍加改造，因为是springboot项目，所以这里指定项目parent为springboot。

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.0.RELEASE</version>
    </parent>
```

然后引入api依赖和springboot的依赖。

```xml
        <!-- demo api -->
        <dependency>
            <groupId>learn.demo</groupId>
            <artifactId>thrift-demo-java-api</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
        <!-- spring boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
```

### 5.2.1 实现客户端并以Bean的形式注入Spring

我们需要从配置文件里获取远程thrift服务的IP地址和端口号，所以需要先创建一个application.properties文件

```properties
thrift.server.name=thrift-demo-client
thrift.server.ip=127.0.0.1
thrift.server.port=8911
```

同样在learn.demo.thrift.client下创建SpringBoot应用启动类

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    // ……
 }
```

接下来是客户端实现的重头戏，大体分以下几步：

1. 创建Socket连接
2. 设置传输方式
3. 设置传输协议
4. 连接服务端
5. 获取客户端实例

同样是以@Configuration的形式，将客户端实例以Bean的形式注入到Spring里。在Application类下创建ThriftClientConfiguration静态内部类

```java
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
            // 传输协议
            TProtocol protocol = new TBinaryProtocol(transport);
            // 创建连接
            transport.open();
            log.info("The application is creating thrift client from address {}:{} ……",serverIp,serverPort);
            return new DemoService.Client(protocol);
        }
    }
```

### 5.2.2 实现REST接口，用来演示远程调用

这个就比较基础了，是SpringBoot Web开发里的内容，上面我们已经将Thrift客户端实例以Bean的形式注入到了Spring里。这里我们可以通过@Autowired直接拿到实例，然后调用其方法。在learn.demo.thrift.client.controller下创建DemoController类，实现两个REST接口。

```java
package learn.demo.thrift.client.controller;

import learn.demo.thrift.client.dto.DemoInfoDTO;
import learn.demo.thrift.api.DemoInfo;
import learn.demo.thrift.api.DemoService;
import org.apache.thrift.TException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by shirukai on 2019-06-27 10:03
 * controller
 */
@RestController
@RequestMapping(value = "/demo")
public class DemoController {
    @Autowired
    private DemoService.Iface demoService;

    @PostMapping
    public String creatDemo(
            @RequestBody() DemoInfoDTO demoInfoDTO
    ) throws TException {
        DemoInfo demoInfo = new DemoInfo();
        BeanUtils.copyProperties(demoInfoDTO, demoInfo);
        demoService.createDemo(demoInfo);
        return "success";
    }

    @GetMapping(value = "/{id}")
    public DemoInfoDTO getDemo(
            @PathVariable("id") Integer id
    ) throws TException {
        DemoInfo demoInfo = demoService.getDemoById(id);
        if (demoInfo != null) {
            DemoInfoDTO demoInfoDTO = new DemoInfoDTO();
            BeanUtils.copyProperties(demoInfo, demoInfoDTO);
            return demoInfoDTO;
        }
        return null;
    }
}
```

# 5 Python实现服务端和客户端

上面我们已经在thrift-demo-py-api中生成了thrift服务相关的Python代码，并将该模块打包发布，这里我们就要使用Python去实现服务端和客户端。

## 5.1 服务端：thrift-demo-py-server

无论是什么语言，对于thrift的服务端和客户端的创建流程都是一样的，这里就不多撰述，服务端依然是两方面实现：

* 实现抽象接口
* 实现服务暴露

Python没有接口的概念，但是Thrift为了统一，依然给我实现了一个抽象“接口”，实际上是一个没有具体实现的类。如下所示：

```python
class Iface(object):
    def getDemoById(self, id):
        """
        Parameters:
         - id

        """
        pass

    def createDemo(self, demo):
        """
        Parameters:
         - demo

        """
        pass
```

所以首先要继承该类，并重写其方法

```python
class DemoServiceHandler(DemoService.Iface):
    """
    继承DemoService.Iface，重写其方法
    """

    def getDemoById(self, id):
        print "The client invoke method: " + "getDemoById."
        if id in demoCache:
            return demoCache[id]
        else:
            return None

    def createDemo(self, demo):
        print "The client invoke method: " + "createDemo."
        demoCache[demo.id] = demo
```

然后是服务端的暴露，老套路

1. 创建处理器
2. 设置监听端口
3. 初始化传输方式
4. 初始化传输协议
5. 创建服务
6. 启动服务

直接上代码

```python
if __name__ == '__main__':
    handler = DemoServiceHandler()

    # 创建处理器
    processor = DemoService.Processor(handler)

    # 监听端口
    transport = TSocket.TServerSocket("127.0.0.1", "8911")

    # 传输方式工厂:TBufferedTransportFactory/TFramedTransportFactory
    # 服务端使用什么传输方式，客户端就需要使用什么传输方式
    tfactory = TTransport.TFramedTransportFactory()

    # 传输协议工厂:TCompactProtocol/TJSONProtocol/TBinaryProtocol
    # 服务端使用什么传输协议，客户端就需要使用什么传输协议
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    # 创建服务:TSimpleServer/TForkingServer/TThreadedServer/TThreadPoolServer
    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print "python thrift server start"
    server.serve()

```

## 5.2 客户端：thrift-demo-py-client

Python客户端没有整合复杂的服务，这里直接创建客户端，然后进行远程调用

```python
# encoding: utf-8
"""
@author : shirukai
@date : 2019-06-26 21:02
thrift consumer
官网：http://thrift.apache.org/tutorial/py#client
"""
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket, TTransport
from thrift_demo.api import DemoService
from thrift_demo.api.ttypes import DemoInfo

if __name__ == '__main__':
    # 建立socket
    transport = TSocket.TSocket('127.0.0.1', 7911)

    # 传输方式，与服务端一致
    transport = TTransport.TFramedTransport(transport)

    # 传输协议，与服务端一致
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    # 创建客户端
    client = DemoService.Client(protocol)

    # 连接服务端
    transport.open()

    # 远程调用
    demo = DemoInfo()
    demo.id = 1
    demo.name = "demo1"
    demo.tags = ['1', '2']
    client.createDemo(demo)

    print client.getDemoById(1)

```

# 6 交叉验证

至此我们就完成了Java版的Thrift服务端和客户端的开发以及Python版的服务端和客户端开发。下面将进行交叉验证，通过以下几种方案来验证我们实现的RPC是否可用。

* Java客户端-Java服务端
* Java客户端-Python服务端
* Python客户端-Python服务端
* Python客户端-Java服务端

## 6.1 Java客户端-Java服务端

1首选我们启动Java服务端，执行thrift-demo-java-server中Application的main方法。暴露Thrift服务端口为7911

![](https://raw.githubusercontent.com/shirukai/images/master/a85ebfeee813eee937f2551caeb9b409.jpg)

在启动Java客户端之前，需要修改配置文件，将需要调用的服务端口改为7911

```properties
thrift.server.port=7911
```

然后启动应用

![](https://raw.githubusercontent.com/shirukai/images/master/96f111535a46793360a51c01401dfc6c.jpg)

启动完成后，我们可以通过REST来进行远程调用。

创建Demo

![](https://raw.githubusercontent.com/shirukai/images/master/76b60b4e0c5111f092cb65ecb4fa80ac.jpg)

服务端打印日志，说明createDemo方法已被调用

```
2019-06-27 15:11:53.395  INFO 43138 --- [       Thread-2] l.d.t.server.service.DemoServiceImpl     : The client invoke method: createDemo
```

获取Demo

![](https://raw.githubusercontent.com/shirukai/images/master/89ae3280b5fce569d8be847596445a8e.jpg)

服务端打印日志，并且得到相应，说明RPC正常。

```
2019-06-27 15:13:27.957  INFO 43138 --- [       Thread-2] l.d.t.server.service.DemoServiceImpl     : The client invoke method: getDemoById
```



## 6.2 Java客户端-Python服务端

现在我们将上面两个服务停掉，将之前的Java服务端，改为Python服务端，启动thrift-demo-py-server模块下server里的main方法。暴露服务端口为8911

![](https://raw.githubusercontent.com/shirukai/images/master/2c43e404f037d4fee7b4afaa18ba7549.jpg)

将Java客户端里需要调用的服务端的端口改为8911

```properties
thrift.server.port=8911
```

然后启动服务。

依然使用PostMan进行REST请求。

创建Demo

![](https://raw.githubusercontent.com/shirukai/images/master/89ae3280b5fce569d8be847596445a8e.jpg)

服务端打印日志

```
The client invoke method: createDemo.
```

获取Demo

![](https://raw.githubusercontent.com/shirukai/images/master/89ae3280b5fce569d8be847596445a8e.jpg)

验证完毕，说明我们的RPC正常。

## 6.3 Python客户端-Python服务端

同理进行Python客户端-Python服务端的验证。

![](https://raw.githubusercontent.com/shirukai/images/master/795beb70286227fe21a336b0bb0b4498.gif)

## 6.4 Python客户端-Java服务端

同理机型Python客户端-Java服务端的验证

![](https://raw.githubusercontent.com/shirukai/images/master/86e7ce8a4b1d614f5168325a860226fe.gif)

# 7 总结

Thrift的初体验至此，相比较Dubbo感觉没有走太多的坑。因为thrift没有注册中心，是通过直连的方式进行通讯，所以配置起来并不复杂。