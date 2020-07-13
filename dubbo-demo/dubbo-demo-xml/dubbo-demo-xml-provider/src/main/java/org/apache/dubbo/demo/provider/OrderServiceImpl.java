package org.apache.dubbo.demo.provider;

import org.apache.dubbo.demo.Order;
import org.apache.dubbo.demo.OrderService;

import java.util.Arrays;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    @Override
    public List<Order> getOrder(Order order) {
        Order order1 = new Order("1", "no-2020-00001", "订单1");
        Order order2 = new Order("2", "no-2020-00002", "订单2");
        return Arrays.asList(order1, order2);
    }

}
