package com.xiudongxu.xRpc.service;

import com.xiudongxu.xRpc.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * @author dongxu.xiu
 * @since 2019-03-06 下午5:48
 */
public interface InfoUserService {

    List<InfoUser> insertInfoUser(InfoUser infoUser);

    InfoUser getInfoUserById(String id);

    void deleteInfoUserById(String id);

    String getNameById(String id);

    Map<String,InfoUser> getAllUser();
}
