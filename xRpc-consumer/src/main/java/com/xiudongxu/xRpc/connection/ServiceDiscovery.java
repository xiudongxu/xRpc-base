package com.xiudongxu.xRpc.connection;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongxu.xiu
 * @since 2019-03-09 下午12:13
 */
@Component
public class ServiceDiscovery {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${registry.address}")
    private String registryAddress;

    @Resource
    private ConnectManage connectManage;

    private volatile List<String> addressList = new ArrayList<>();

    private static final String ZK_REGISTRY_PATH = "/xRpc";

    private ZkClient client;

    @PostConstruct
    public void init(){
        client = connectServer();
        if(client != null){
            watchNode(client);
        }
    }

    private void watchNode(ZkClient client) {
        List<String> nodeList = client.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
            LOGGER.info("监听到子节点数据变化:{}", JSONObject.toJSONString(nodes));
            addressList.clear();
            getNodeData(nodes);
            updateConnectedServer();
        });
        getNodeData(nodeList);
        LOGGER.info("已发现服务列表:{}", JSONObject.toJSONString(addressList));
        updateConnectedServer();
    }

    //连接生产者端服务
    private void updateConnectedServer() {
        connectManage.updateConnectServer(addressList);
    }

    private void getNodeData(List<String> nodes) {
        LOGGER.info("/rpc子节点数据:{}", JSONObject.toJSONString(nodes));
        for(String node : nodes){
            String address = client.readData(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
        }
    }

    private ZkClient connectServer() {
        return new ZkClient(registryAddress, 30000, 30000);
    }


}
