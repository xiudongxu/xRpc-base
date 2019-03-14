package com.xiudongxu.xRpc.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dongxu.xiu
 * @since 2019-03-14 下午3:48
 */

@RestController
public class InstanceController {


    @GetMapping("registry")
    public String registryService() throws NacosException {
        NamingService namingService = NamingFactory.createNamingService("127.0.0.1:8848");
        namingService.registerInstance("nacos.test.3" ,"11.11.11.11", 8888);
        return "REGISTRY SERVICE OK";
    }
}
