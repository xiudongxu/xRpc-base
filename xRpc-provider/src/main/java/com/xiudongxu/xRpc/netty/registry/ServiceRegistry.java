package com.xiudongxu.xRpc.netty.registry;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author dongxu.xiu
 * @since 2019-03-06 下午7:05
 */
@Component
public class ServiceRegistry {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${registry.address}")
    private String registryAddress;

    private static final String ZK_REGISTRY_PATH = "/xRpc";

    public void register(String data) {
        if (data != null) {
            ZkClient zkClient = connectServer();
            if (zkClient != null) {
                AddRootNode(zkClient);
                createNode(zkClient, data);
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

    private ZkClient connectServer() {
        ZkClient zkClient = new ZkClient(registryAddress, 20000, 20000);
        return zkClient;
    }
}
