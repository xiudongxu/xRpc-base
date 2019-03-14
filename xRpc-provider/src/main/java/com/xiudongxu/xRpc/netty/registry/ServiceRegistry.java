package com.xiudongxu.xRpc.netty.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * 将服务注册这步改造成nacos
 * 服务注册的类都是在zk上操作的
 *
 * @author dongxu.xiu
 * @since 2019-03-06 下午7:05
 */
@Component
public class ServiceRegistry {




    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${registry.address}")
    private String registryAddress;

    @Value("${registry.isUseZk}")
    private boolean isZk;

    @Value("${nacos.config.server-addr}")
    private String nacosAddress;

    private NamingService namingService;

    @PostConstruct
    public void init(){
        try {
            namingService = NamingFactory.createNamingService(nacosAddress);
        } catch (NacosException e) {
            e.printStackTrace();
        }

    }
    private static final String ZK_REGISTRY_PATH = "/xRpc";
    private static final String NACOS_SERVICENAME = "nacos.xRpc";

    public void register(String nettyAddress) {
        if (nettyAddress != null) {
            if(isZk){
                ZkClient zkClient = connectZkServer();
                if (zkClient != null) {
                    AddRootNode(zkClient);
                    createNode(zkClient, nettyAddress);
                }
            }else{
                connectRegistryNacosServer(nettyAddress);
            }
        }
    }

    //创建根目录
    private void AddRootNode(ZkClient zkClient) {
        boolean exists = zkClient.exists(ZK_REGISTRY_PATH);
        if (!exists) {
            zkClient.createPersistent(ZK_REGISTRY_PATH);
            LOGGER.info("创建zookeeper主节点, path:{}", ZK_REGISTRY_PATH);
        }
    }

    //在 /rpc根目录下，创建临时顺序子节点
    private void createNode(ZkClient client, String data) {
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        LOGGER.info("创建zookeeper 数据节点 ({} => {})", path, data);
    }

    private ZkClient connectZkServer() {
        ZkClient zkClient = new ZkClient(registryAddress, 20000, 20000);
        return zkClient;
    }

    private void connectRegistryNacosServer(String nettyAddress){
        try {
            String[] ipAndPort = nettyAddress.split(":");
            //注册一个服务
            namingService.registerInstance(NACOS_SERVICENAME, ipAndPort[0], Integer.valueOf(ipAndPort[1]));
        } catch (NacosException e) {
            LOGGER.error("注册服务失败，一会重试");
        }
    }
}
