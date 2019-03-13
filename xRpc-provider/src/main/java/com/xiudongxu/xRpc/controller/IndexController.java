package com.xiudongxu.xRpc.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiudongxu.xRpc.entity.InfoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午6:10
 */
@RestController
public class IndexController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @GetMapping("index")
    @ResponseBody
    public String index(){
        InfoUser infoUser = new InfoUser(UUID.randomUUID().toString(), "xdx", "BeiJing");
        String json = JSONObject.toJSONString(infoUser);
        LOGGER.info(json);
        return json;
    }
}
