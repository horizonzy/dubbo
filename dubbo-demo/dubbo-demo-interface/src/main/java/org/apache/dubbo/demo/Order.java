package org.apache.dubbo.demo;

import java.io.Serializable;

public class Order implements Serializable {


    private String id;

    private String orderNo;

    private String msg;

    public Order() {
    }

    public Order(String id, String orderNo, String msg) {
        this.id = id;
        this.orderNo = orderNo;
        this.msg = msg;
    }


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
