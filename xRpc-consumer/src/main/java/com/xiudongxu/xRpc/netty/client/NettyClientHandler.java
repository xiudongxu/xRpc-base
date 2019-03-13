package com.xiudongxu.xRpc.netty.client;

import com.alibaba.fastjson.JSON;
import com.xiudongxu.xRpc.connection.ConnectManage;
import com.xiudongxu.xRpc.entity.Request;
import com.xiudongxu.xRpc.entity.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午7:49
 */
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    private ConcurrentHashMap<String,SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    @Resource
    private ConnectManage connectManage;

    public void channelActive(ChannelHandlerContext ctx)   {
        LOGGER.info("已连接到RPC服务器.{}",ctx.channel().remoteAddress());
    }

    public void channelInactive(ChannelHandlerContext ctx)   {
        InetSocketAddress address =(InetSocketAddress) ctx.channel().remoteAddress();
        LOGGER.info("与RPC服务器断开连接：{}", address);
        ctx.channel().close();
        connectManage.removeChannel(ctx.channel());
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        Response response = JSON.parseObject(msg.toString(),Response.class);
        String requestId = response.getRequestId();
        SynchronousQueue<Object> queue = queueMap.get(requestId);
        queue.put(response);
        queueMap.remove(requestId);
    }

    public SynchronousQueue<Object> sendRequest(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception {
        LOGGER.info("已超过30秒未与RPC服务器进行读写操作!将发送心跳消息...");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.ALL_IDLE){
                Request request = new Request();
                request.setMethodName("heartBeat");
                ctx.channel().writeAndFlush(request);
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        LOGGER.info("RPC通信服务器发生异常.{}",cause);
        ctx.channel().close();
    }
}
