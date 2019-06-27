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
