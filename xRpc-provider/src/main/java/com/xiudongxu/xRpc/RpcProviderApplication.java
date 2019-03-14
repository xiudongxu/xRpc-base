package com.xiudongxu.xRpc;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午6:07
 */


@SpringBootApplication
@NacosPropertySource(dataId = "xRpc", autoRefreshed = true)
public class RpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderApplication.class, args);
    }
}
