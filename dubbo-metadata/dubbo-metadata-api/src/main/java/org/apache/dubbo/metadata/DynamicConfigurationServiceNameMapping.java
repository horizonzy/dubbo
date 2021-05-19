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
package org.apache.dubbo.metadata;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.CompositeConfiguration;
import org.apache.dubbo.common.config.configcenter.ConfigItem;
import org.apache.dubbo.common.config.configcenter.DynamicConfiguration;
import org.apache.dubbo.common.config.configcenter.wrapper.CompositeDynamicConfiguration;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.apache.dubbo.common.constants.CommonConstants.COMMA_SEPARATOR;
import static org.apache.dubbo.common.utils.CollectionUtils.ofSet;
import static org.apache.dubbo.rpc.model.ApplicationModel.getName;

/**
 * The {@link ServiceNameMapping} implementation based on {@link DynamicConfiguration}
 */
public class DynamicConfigurationServiceNameMapping implements ServiceNameMapping {

    private static final List<String> IGNORED_SERVICE_INTERFACES = Collections.singletonList(MetadataService.class.getName());

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int PUBLISH_CONFIG_RETRY_TIMES = 6;

    @Override
    public void map(URL url) {
        String serviceInterface = url.getServiceInterface();
        if (IGNORED_SERVICE_INTERFACES.contains(serviceInterface)) {
            return;
        }

        DynamicConfiguration dynamicConfiguration = DynamicConfiguration.getDynamicConfiguration();
        if (dynamicConfiguration instanceof CompositeDynamicConfiguration) {
            Set<DynamicConfiguration> configurations = ((CompositeDynamicConfiguration) dynamicConfiguration).getConfigurations();
            for (DynamicConfiguration configuration : configurations) {
                if (configuration.hasSupportCas() && ServiceNameMappingHandler.isBothMapping()) {
                    doCasMap(configuration, url);
                    doNormalMap(configuration, url);
                } else {
                    doNormalMap(configuration, url);
                }
            }

        } else {
            boolean supportCas = dynamicConfiguration.hasSupportCas();
            if (supportCas && ServiceNameMappingHandler.isBothMapping()) {
                doCasMap(dynamicConfiguration, url);
                doNormalMap(dynamicConfiguration, url);
            } else {
                doNormalMap(dynamicConfiguration, url);
            }
        }
    }

    public void doNormalMap(DynamicConfiguration dynamicConfiguration, URL url) {
        if (dynamicConfiguration instanceof CompositeDynamicConfiguration) {
            logger.warn("CompositeDynamicConfiguration can't doNormalMap");
            return;
        }

        String serviceInterface = url.getServiceInterface();

        // the Dubbo Service Key as group
        // the service(application) name as key
        // It does matter whatever the content is, we just need a record
        String application = getName();
        String content = valueOf(System.currentTimeMillis());

        execute(() -> {
            boolean success = dynamicConfiguration.publishConfig(application, ServiceNameMapping.buildGroup(serviceInterface), content);
            if (logger.isDebugEnabled()) {
                if (success) {
                    logger.debug(String.format("doNormalMap succeed: Dubbo service[%s] mapped to interface name[%s].", application, serviceInterface));
                } else {
                    logger.debug(String.format("doNormalMap failed: Dubbo service[%s] mapped to interface name[%s].", application, serviceInterface));
                }
            }
        });
    }

    public void doCasMap(DynamicConfiguration dynamicConfiguration, URL url) {
        if (dynamicConfiguration instanceof CompositeDynamicConfiguration) {
            logger.warn("CompositeDynamicConfiguration can't publish config cas ");
            return;
        }

        String serviceInterface = url.getServiceInterface();

        execute(() -> {
            int currentRetryTimes = 1;
            boolean success;
            String newConfigContent = getName();
            do {
                ConfigItem configItem = dynamicConfiguration.getConfigItem(serviceInterface, DEFAULT_MAPPING_GROUP);
                String oldConfigContent = configItem.getContent();
                if (StringUtils.isNotEmpty(oldConfigContent)) {
                    boolean contains = StringUtils.isContains(oldConfigContent, getName());
                    if (contains) {
                        success = true;
                        break;
                    }
                    newConfigContent = oldConfigContent + COMMA_SEPARATOR + getName();
                }
                success = dynamicConfiguration.publishConfigCas(serviceInterface, DEFAULT_MAPPING_GROUP, newConfigContent, configItem.getStat());
            } while (!success && currentRetryTimes++ <= PUBLISH_CONFIG_RETRY_TIMES);
            if (logger.isDebugEnabled()) {
                if (success) {
                    logger.debug(String.format("doCasMap succeed: Dubbo service[%s] mapped to interface name[%s].", newConfigContent, serviceInterface));
                } else {
                    logger.debug(String.format("doCasMap failed: Dubbo service[%s] mapped to interface name[%s].", newConfigContent, serviceInterface));
                }
            }
        });
    }

    @Override
    public Set<String> getAndListen(URL url, MappingListener mappingListener) {
        String serviceInterface = url.getServiceInterface();
        DynamicConfiguration dynamicConfiguration = DynamicConfiguration.getDynamicConfiguration();

        Set<String> serviceNames = new LinkedHashSet<>();
        execute(() -> {
            Set<String> keys = dynamicConfiguration
                    .getConfigKeys(ServiceNameMapping.buildGroup(serviceInterface));
            serviceNames.addAll(keys);
        });
        return Collections.unmodifiableSet(serviceNames);
    }

    @Override
    public Set<String> getAndListenWithNewStore(URL url, MappingListener mappingListener) {
        String serviceInterface = url.getServiceInterface();
        DynamicConfiguration dynamicConfiguration = DynamicConfiguration.getDynamicConfiguration();
        Set<String> serviceNames = new LinkedHashSet<>();
        execute(() -> {
            String configContent = dynamicConfiguration.getConfig(serviceInterface, DEFAULT_MAPPING_GROUP);
            if (null != configContent) {
                String[] split = StringUtils.split(configContent, CommonConstants.COMMA_SEPARATOR_CHAR);
                serviceNames.addAll(ofSet(split));
            }
        });
        return Collections.unmodifiableSet(serviceNames);
    }


    private void execute(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}
