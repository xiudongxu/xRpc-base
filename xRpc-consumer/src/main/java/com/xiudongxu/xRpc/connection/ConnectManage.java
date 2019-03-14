package com.xiudongxu.xRpc.connection;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xiudongxu.xRpc.netty.client.NettyClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dongxu.xiu
 * @since 2019-03-08 下午5:54
 */

@Component
public class ConnectManage {


    @Resource
    private NettyClient nettyClient;

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    public Channel chooseChannel() {
        if (channels.size() > 0) {
            int size = channels.size();
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return channels.get(index);
        } else {
            return null;
        }
    }

    public synchronized void updateConnectServerNacos(List<Instance> instanceList) {

        HashSet<SocketAddress> newAllServerNodeSet = new HashSet<>();
        for (int i = 0; i < instanceList.size(); i++) {
            String host = instanceList.get(i).getIp();
            int port = instanceList.get(i).getPort();
            SocketAddress remotePeer = new InetSocketAddress(host, port);
            newAllServerNodeSet.add(remotePeer);
        }

        for (SocketAddress socketAddress : newAllServerNodeSet) {
            Channel channel = channelNodes.get(socketAddress);
            if (channel != null && channel.isOpen()) {
                LOGGER.info("当前节点已经存在，无需重新建立连接,地址:{} ", socketAddress);
            } else {
                connectServerNode(socketAddress);
            }
        }
    }

    public synchronized void updateConnectServer(List<String> addressList) {
        if (addressList.size() == 0 || addressList == null) {
            LOGGER.error("没有可用的服务器节点，全部服务节点关闭！");
            for (final Channel channel : channels) {
                SocketAddress remotePeer = channel.remoteAddress();
                Channel handler_node = channelNodes.get(remotePeer);
                handler_node.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }

//        HashSet<SocketAddress> newAllServerNodeSet = new HashSet<>();
//        for (int i = 0; i < addressList.size(); i++) {
//            String[] array = addressList.get(i).split(":");
//            if(array.length == 2){
//                String host = array[0];
//                int port = Integer.parseInt(array[1]);
//                SocketAddress remotePeer = new InetSocketAddress(host, port);
//                newAllServerNodeSet.add(remotePeer);
//            }
//        }
//
//        for(SocketAddress socketAddress : newAllServerNodeSet){
//            Channel channel = channelNodes.get(socketAddress);
//            if(channel != null && channel.isOpen()){
//                LOGGER.info("当前节点已经存在，无需重新建立连接,地址:{} ", socketAddress);
//            }else{
//                connectServerNode(socketAddress);
//            }
//        }
    }

    private void connectServerNode(SocketAddress address) {
        try {
            Channel channel = nettyClient.doConnect(address);
            addChannel(channel, address);
        } catch (InterruptedException e) {
            LOGGER.info("未能成功连接到服务器:{}", address);
        }

    }

    private void addChannel(Channel channel, SocketAddress address) {
        LOGGER.info("加入channel到连接管理器 ,address:{}", address);
        channels.add(channel);
        channelNodes.put(address, channel);
    }

    public void removeChannel(Channel channel) {
        LOGGER.info("从连接管理器中移除失效channel:{}", channel.remoteAddress());
        SocketAddress socketAddress = channel.remoteAddress();
        channelNodes.remove(socketAddress);
        channels.remove(channel);
    }
}
