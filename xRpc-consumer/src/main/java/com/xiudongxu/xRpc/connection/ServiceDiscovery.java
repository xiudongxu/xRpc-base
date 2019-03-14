package com.xiudongxu.xRpc.connection;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author dongxu.xiu
 * @since 2019-03-09 下午12:13
 */
@Component
public class ServiceDiscovery {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @NacosInjected
    private NamingService namingService;

    @Resource
    private ConnectManage connectManage;

    private static final String NACOS_SERVICENAME = "nacos.xRpc";


    @PostConstruct
    public void init() {
        updateConnectedServer();
    }

    //连接生产者端服务
    private void updateConnectedServer() {
        //把取到的
        try {
            List<Instance> allInstances = namingService.getAllInstances(NACOS_SERVICENAME);
            connectManage.updateConnectServerNacos(allInstances);
        } catch (NacosException e) {

        }
    }

}
