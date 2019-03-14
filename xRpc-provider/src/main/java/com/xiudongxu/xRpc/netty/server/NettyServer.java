package com.xiudongxu.xRpc.netty.server;

import com.xiudongxu.xRpc.annotation.RpcService;
import com.xiudongxu.xRpc.netty.codec.JSONDecoder;
import com.xiudongxu.xRpc.netty.codec.JSONEncoder;
import com.xiudongxu.xRpc.netty.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @author dongxu.xiu
 * @since 2019-03-06 下午6:12
 */

/**
 * InitializingBean 为bean提供初始化接口 或者实现 init-method 都可以 推荐使用这个
 * ApplicationContextAware 为了让bean 获得所在的容器
 * ApplicationContextAware 的 setApplicationContext的方法执行是在 后置处理器中
 * ApplicationContextAwareProcessor 中处理的 会调用
 * 每一个bean 都会处理到
 *
 *
 */
@Component
public class NettyServer implements ApplicationContextAware,InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    //如果这里不传参数的话就会被设置成cpu核数的两倍
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    //保存所有的service
    private Map<String ,Object> serviceMap = new HashMap<>();

    @Value("${rpc.server.address}")
    private String serviceAddress;

    @Autowired
    ServiceRegistry serviceRegistry;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        LOGGER.info("已经获取到所有的beans :{}",beans.toString());
        for(Object serviceBean : beans.values()){
            Class<?> clazz = serviceBean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for(Class<?> inter : interfaces){
                String interfaceName = inter.getName();
                LOGGER.info("加载服务类：{}", interfaceName);
                serviceMap.put(interfaceName, serviceBean);
            }
        }
        LOGGER.info("已加载全部服务接口：{}", serviceMap);
    }

    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    private void startServer() {
        final NettyServerHandler handler = new NettyServerHandler(serviceMap);
        new Thread(()->{
            //进行netty服务的启动
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup,workerGroup).
                        channel(NioServerSocketChannel.class).
                        option(ChannelOption.SO_BACKLOG,1024).
                        childOption(ChannelOption.SO_KEEPALIVE,true).
                        childOption(ChannelOption.TCP_NODELAY,true).
                        childHandler(new ChannelInitializer<SocketChannel>() {
                            //NIOSocketChannel创建成功后，在进行初始化的时候 将ChannelHandler设置到
                            //ChannelPipeline 中，用于处理网络IO事件
                            protected void initChannel(SocketChannel channel) throws Exception {
                                ChannelPipeline pipeline = channel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0,0,60))
                                        .addLast(new JSONEncoder())
                                        .addLast(new JSONDecoder())
                                        .addLast(handler);
                            }
                        });
                String[] array = serviceAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                //netty监听服务的地址
                ChannelFuture cf = bootstrap.bind(host, port).sync();
                //把netty监听服务的地址注册上去
                serviceRegistry.register(serviceAddress);
                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                 LOGGER.error("exception",e);
                 bossGroup.shutdownGracefully();
                 workerGroup.shutdownGracefully();
            }
        }).start();
    }
}
