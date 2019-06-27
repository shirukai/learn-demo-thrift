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
