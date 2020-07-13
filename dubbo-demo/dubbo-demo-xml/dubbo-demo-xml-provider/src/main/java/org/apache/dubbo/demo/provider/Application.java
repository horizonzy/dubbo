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
package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-provider.xml");
        context.start();


        ServiceBean bean = context.getBean("org.apache.dubbo.demo.DemoService", ServiceBean.class);
        ServiceBean bean1 = context.getBean("org.apache.dubbo.demo.OrderService", ServiceBean.class);
        System.setProperty("xxxx", "true");
        bean.unexport();
        System.setProperty("xxxx", "false");

        System.setProperty("xxxx", "true");
        bean1.unexport();
        System.setProperty("xxxx", "false");

        bean.export();
        bean1.export();
        System.in.read();
    }
}
