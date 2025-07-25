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

package org.springframework.cloud.kubernetes.k8s.client.discovery;

import java.util.Set;

import io.kubernetes.client.openapi.ApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.web.server.test.LocalManagementPort;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.kubernetes.commons.discovery.KubernetesDiscoveryProperties;
import org.springframework.cloud.kubernetes.integration.tests.commons.Images;
import org.springframework.cloud.kubernetes.integration.tests.commons.Phase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.cloud.kubernetes.k8s.client.discovery.TestAssertions.assertBlockingConfiguration;
import static org.springframework.cloud.kubernetes.k8s.client.discovery.TestAssertions.assertPodMetadata;

/**
 * @author wind57
 */
@SpringBootTest(classes = { DiscoveryApp.class, KubernetesClientBlockingIT.TestConfig.class },
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
		properties = { "spring.cloud.discovery.reactive.enabled=false", "spring.cloud.discovery.blocking.enabled=true",
				"logging.level.org.springframework.cloud.kubernetes.commons.discovery=debug",
				"logging.level.org.springframework.cloud.client.discovery.health=debug",
				"logging.level.org.springframework.cloud.kubernetes.client.discovery=debug" })
class KubernetesClientBlockingIT extends KubernetesClientDiscoveryBase {

	@LocalManagementPort
	private int port;

	@Autowired
	private DiscoveryClient discoveryClient;

	@BeforeEach
	void beforeEach() {
		Images.loadWiremock(K3S);
		util.wiremock(NAMESPACE, Phase.CREATE, true);
	}

	@AfterEach
	void afterEach() {
		util.wiremock(NAMESPACE, Phase.DELETE, true);
	}

	/**
	 * <pre>
	 *
	 *     	Reactive is disabled, only blocking is active. As such,
	 * 	 	We assert for logs and call '/health' endpoint to see that blocking discovery
	 * 	 	client was initialized.
	 *
	 * </pre>
	 */
	@Test
	void test(CapturedOutput output) {
		assertBlockingConfiguration(output, port);
		assertPodMetadata(discoveryClient);
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		@Primary
		ApiClient client() {
			return apiClient();
		}

		@Bean
		@Primary
		KubernetesDiscoveryProperties kubernetesDiscoveryProperties() {
			return discoveryProperties(false, Set.of(NAMESPACE), null);
		}

	}

}
