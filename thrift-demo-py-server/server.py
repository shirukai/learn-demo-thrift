# encoding: utf-8
"""
@author : shirukai
@date : 2019-06-26 20:53
thrift server
官网地址：http://thrift.apache.org/tutorial/py#server
"""
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
from thrift.transport import TSocket, TTransport
from thrift_demo.api import DemoService

# 缓存DemoInfo
demoCache = {}


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
