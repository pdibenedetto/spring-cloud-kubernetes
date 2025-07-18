/*
 * Copyright 2013-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.kubernetes.discovery;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.health.DiscoveryClientHealthIndicatorProperties;
import org.springframework.cloud.kubernetes.commons.PodUtils;
import org.springframework.cloud.kubernetes.commons.discovery.ConditionalOnSpringCloudKubernetesBlockingDiscovery;
import org.springframework.cloud.kubernetes.commons.discovery.ConditionalOnSpringCloudKubernetesBlockingDiscoveryHealthInitializer;
import org.springframework.cloud.kubernetes.commons.discovery.KubernetesDiscoveryClientHealthIndicatorInitializer;
import org.springframework.cloud.kubernetes.commons.discovery.KubernetesDiscoveryProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wind57
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnSpringCloudKubernetesBlockingDiscovery
@EnableConfigurationProperties({ DiscoveryClientHealthIndicatorProperties.class, KubernetesDiscoveryProperties.class })
class KubernetesDiscoveryClientBlockingAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder();
	}

	@Bean
	@ConditionalOnMissingBean
	KubernetesDiscoveryClient kubernetesDiscoveryClient(RestTemplateBuilder restTemplateBuilder,
			KubernetesDiscoveryProperties properties) {
		return new KubernetesDiscoveryClient(restTemplateBuilder.build(), properties);
	}

	@Bean
	@ConditionalOnMissingBean
	PodUtils<?> kubernetesDiscoveryPodUtils() {
		return new KubernetesDiscoveryPodUtils();
	}

	@Bean
	@ConditionalOnSpringCloudKubernetesBlockingDiscoveryHealthInitializer
	KubernetesDiscoveryClientHealthIndicatorInitializer indicatorInitializer(PodUtils<?> podUtils,
			ApplicationEventPublisher applicationEventPublisher) {
		return new KubernetesDiscoveryClientHealthIndicatorInitializer(podUtils, applicationEventPublisher);
	}

}
