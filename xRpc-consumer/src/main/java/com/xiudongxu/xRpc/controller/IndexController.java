package com.xiudongxu.xRpc.controller;

import com.xiudongxu.xRpc.entity.InfoUser;
import com.xiudongxu.xRpc.service.InfoUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 下午6:10
 */
@RestController
public class IndexController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private InfoUserService infoUserService;

    @GetMapping("index")
    @ResponseBody
    public String index(){
        return new Date().toString();
    }

    @GetMapping("getById")
    @ResponseBody
    public InfoUser getById(String id){
        LOGGER.info("根据ID查询用户信息:{}", id);
        return infoUserService.getInfoUserById(id);
    }

    @GetMapping("insertUser")
    @ResponseBody
    public void insertUser(String id ,String name, String address){
        LOGGER.info("新增用户信息:{}", id);
         infoUserService.insertInfoUser(new InfoUser(id, name, address));
    }

}
