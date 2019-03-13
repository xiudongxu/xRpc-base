package com.xiudongxu.xRpc.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiudongxu.xRpc.entity.Request;
import com.xiudongxu.xRpc.entity.Response;
import com.xiudongxu.xRpc.netty.client.NettyClient;
import com.xiudongxu.xRpc.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午7:45
 */

@Component
public class RpcFactory<T> implements InvocationHandler {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private NettyClient nettyClient;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setId(IdUtil.getId());

        Object result = nettyClient.send(request);
        Class<?> returnType = method.getReturnType();
        Response response = JSON.parseObject(result.toString(), Response.class);
        if(response.getCode() == 1){
            throw new Exception(response.getError_msg());
        }
        //如果是 基本类型 或者是 String类型
        if(returnType.isPrimitive() || String.class.isAssignableFrom(returnType)){
            return response.getData();
        }else if(Collection.class.isAssignableFrom(returnType)){
            return JSONArray.parseArray(response.getData().toString(), Object.class);
        }else if(Map.class.isAssignableFrom(returnType)){
            return JSON.parseObject(response.getData().toString(), Map.class);
        }else{
            Object data = response.getData();
            return JSONObject.parseObject(data.toString(), returnType);
        }
    }
}
