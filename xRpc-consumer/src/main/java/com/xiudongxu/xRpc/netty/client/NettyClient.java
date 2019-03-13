package com.xiudongxu.xRpc.netty.client;

import com.alibaba.fastjson.JSONArray;
import com.xiudongxu.xRpc.connection.ConnectManage;
import com.xiudongxu.xRpc.entity.Request;
import com.xiudongxu.xRpc.entity.Response;
import com.xiudongxu.xRpc.netty.codec.JSONDecoder;
import com.xiudongxu.xRpc.netty.codec.JSONEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午7:46
 */
@Component
public class NettyClient {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private EventLoopGroup group = new NioEventLoopGroup(1);

    private Bootstrap bootstrap = new Bootstrap();

    @Resource
    private NettyClientHandler clientHandler;

    @Resource
    private ConnectManage connectManage;

    public NettyClient(){
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_KEEPALIVE, true).
                handler(new ChannelInitializer<SocketChannel>() {
                    //创建NIOSocketChannel成功后，在进行初始化时，
                    //将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler", clientHandler);
                    }
                });

    }

    @PreDestroy
    public void destroy(){
        LOGGER.info("RPC客户端退出，释放资源！");
        group.shutdownGracefully();
    }

    public Object send(Request request) throws InterruptedException {
        Channel channel = connectManage.chooseChannel();
        if(channel != null && channel.isActive()){
            SynchronousQueue<Object> queue = clientHandler.sendRequest(request, channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        }else{
            Response response = new Response();
            response.setCode(1);
            response.setError_msg("未正确连接到服务器，请检查相关配置信息！");
            return JSONArray.toJSONString(response);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        return future.sync().channel();
    }
}
