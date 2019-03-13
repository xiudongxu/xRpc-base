package com.xiudongxu.xRpc.rpc;

import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;

/**
 * @author dongxu.xiu
 * @since 2019-03-10 下午12:54
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcInterface;

    @Resource
    RpcFactory<T> factory;

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    public RpcFactoryBean() {}

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[] { rpcInterface },factory);
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
