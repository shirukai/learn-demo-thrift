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
