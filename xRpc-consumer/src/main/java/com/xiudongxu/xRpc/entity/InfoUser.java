package com.xiudongxu.xRpc.entity;

/**
 * @author dongxu.xiu
 * @since 2019-03-06 下午5:49
 */
public class InfoUser {
    private String id;
    private String name;
    private String address;

    public InfoUser(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
