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

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.spring.ReferenceBean;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.Order;
import org.apache.dubbo.demo.OrderService;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Application {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
//        ReferenceBean demoReferenceBean = context.getBean("&demoService", ReferenceBean.class);
//        ReferenceBean orderReferenceBean = context.getBean("&orderService", ReferenceBean.class);

//        DemoService demoService = context.getBean("demoService", DemoService.class);
//        GenericService genericService = context.getBean("demoService", GenericService.class);
//        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
//        System.out.println(result);

        DemoService demoService = context.getBean("demoService", DemoService.class);
        while (true) {
            System.in.read();
            try {
                System.out.println(demoService.sayHello("world"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        System.out.println(demoService.sayHello("world"));

//        DemoService demoService = context.getBean("demoService", DemoService.class);
//        System.out.println(demoService.sayHello("world"));
//        GenericService genericService = context.getBean("demoService", GenericService.class);
//        Object sayHello = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
//        System.out.println(sayHello);

//        demoReferenceBean.reRefer();
//        demoReferenceBean.reRefer();

    }
}
