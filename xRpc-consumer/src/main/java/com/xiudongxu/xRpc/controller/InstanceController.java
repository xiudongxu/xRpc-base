package com.xiudongxu.xRpc.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dongxu.xiu
 * @since 2019-03-14 下午3:48
 */

@RestController
public class InstanceController {


    @NacosInjected
    private NamingService namingService;

    private static final String NACOS_SERVICENAME = "nacos.xRpc";


    @GetMapping("get")
    @ResponseBody
    public List<Instance> registryService() throws NacosException {
        List<Instance> allInstances = namingService.getAllInstances(NACOS_SERVICENAME);
        return allInstances;
    }
}
