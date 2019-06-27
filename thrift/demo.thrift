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