/*
 * Copyright 2012-2025 the original author or authors.
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

package org.springframework.cloud.kubernetes.fabric8.client.discovery;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.web.server.test.LocalManagementPort;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.kubernetes.integration.tests.commons.Images;
import org.springframework.cloud.kubernetes.integration.tests.commons.Phase;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.cloud.kubernetes.fabric8.client.discovery.TestAssertions.testReactiveConfiguration;

/**
 * @author wind57
 */
@TestPropertySource(properties = { "logging.level.org.springframework.cloud.kubernetes.commons.discovery=DEBUG",
		"logging.level.org.springframework.cloud.client.discovery.health.reactive=DEBUG",
		"logging.level.org.springframework.cloud.kubernetes.fabric8.discovery.reactive=DEBUG",
		"logging.level.org.springframework.cloud.kubernetes.fabric8.discovery=DEBUG",
		"spring.cloud.discovery.blocking.enabled=false" })
class Fabric8DiscoveryReactiveIT extends Fabric8DiscoveryBase {

	@LocalManagementPort
	private int port;

	@Autowired
	private ReactiveDiscoveryClient discoveryClient;

	@BeforeEach
	void beforeEach() {
		Images.loadBusybox(K3S);
		util.busybox(NAMESPACE, Phase.CREATE);
	}

	@AfterEach
	void afterEach() {
		util.busybox(NAMESPACE, Phase.DELETE);
	}

	@Test
	void test(CapturedOutput output) {
		testReactiveConfiguration(discoveryClient, output, port);
	}

}
