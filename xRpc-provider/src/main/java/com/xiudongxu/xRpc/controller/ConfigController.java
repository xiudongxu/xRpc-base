package com.xiudongxu.xRpc.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dongxu.xiu
 * @since 2019-03-13 下午2:37
 */

@RestController
public class ConfigController {

    @NacosValue(value = "${useLocalCache:false}", autoRefreshed = true)
    private boolean useLocalCache;

    @GetMapping("get")
    public boolean get(){
        return useLocalCache;
    }


}
