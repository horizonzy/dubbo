/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.common.utils.PojoUtils;
import org.apache.dubbo.demo.DemoService;

import org.apache.dubbo.demo.Order;
import org.apache.dubbo.demo.OrderService;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Application {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
//        GenericService demoService = context.getBean("orderService", GenericService.class);
//        List<Object> results = (List<Object>) demoService.$invoke("getOrder", new String[]{"org.apache.dubbo.demo.Order"}, new Object[]{new Order()});
//        OrderService orderService = context.getBean("orderService", OrderService.class);
//        List<Order> order = orderService.getOrder(new Order());
        DemoService demoService = context.getBean("demoService", DemoService.class);
        System.out.println(demoService.sayHello("world"));

//        while (true) {
//            System.in.read();
//            try {
//                String world = demoService.sayHello("world");
//                System.out.println(world);
//            } catch (RuntimeException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
