//package org.apache.dubbo.demo.provider;
//
//import org.apache.dubbo.demo.Order;
//import org.apache.dubbo.rpc.service.GenericException;
//import org.apache.dubbo.rpc.service.GenericService;
//
//import java.util.Arrays;
//
//public class OrderGenericService implements GenericService {
//
//    @Override
//    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
//        Order order = new Order("1", "no-2020-00001", "订单1");
//        Order order1 = new Order("2", "no-2020-00002", "订单2");
//        return Arrays.asList(order, order1);
//    }
//}
