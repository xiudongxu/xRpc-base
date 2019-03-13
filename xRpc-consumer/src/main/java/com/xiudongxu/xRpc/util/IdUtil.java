package com.xiudongxu.xRpc.util;

/**
 * @author dongxu.xiu
 * @since 2019-03-09 上午11:52
 */
public class IdUtil {


    private final  static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getId(){
        return String.valueOf(idWorker.nextId());
    }
}
