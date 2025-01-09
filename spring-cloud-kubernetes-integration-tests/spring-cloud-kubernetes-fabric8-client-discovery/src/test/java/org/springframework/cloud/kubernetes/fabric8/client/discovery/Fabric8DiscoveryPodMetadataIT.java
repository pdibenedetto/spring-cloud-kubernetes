/*
 * Copyright 2012-2024 the original author or authors.
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
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.kubernetes.integration.tests.commons.Images;
import org.springframework.cloud.kubernetes.integration.tests.commons.Phase;

import static org.springframework.cloud.kubernetes.fabric8.client.discovery.TestAssertions.assertPodMetadata;

/**
 * @author wind57
 */
class Fabric8DiscoveryPodMetadataIT extends Fabric8DiscoveryBase {

	@Autowired
	private DiscoveryClient discoveryClient;

	@BeforeEach
	void beforeEach() {
		Images.loadBusybox(K3S);
		util.busybox(NAMESPACE, Phase.CREATE);
	}

	@AfterEach
	void afterEach() {
		util.busybox(NAMESPACE, Phase.DELETE);
	}

	/**
	 * <pre>
	 * 		- there is a 'busybox-service' service deployed with two pods
	 * 		- find each of the pod, add annotation to one, and labels to another
	 * 		- call DiscoveryClient::getInstances with this serviceId and assert fields returned
	 * </pre>
	 */
	@Test
	void test() throws Exception {
		String[] busyboxPods = K3S.execInContainer("sh", "-c", "kubectl get pods -l app=busybox -o=name --no-headers")
			.getStdout()
			.split("\n");

		String podOne = busyboxPods[0].split("/")[1];
		String podTwo = busyboxPods[1].split("/")[1];

		K3S.execInContainer("sh", "-c", "kubectl label pods " + podOne + " my-label=my-value");
		K3S.execInContainer("sh", "-c", "kubectl annotate pods " + podTwo + " my-annotation=my-value");

		assertPodMetadata(discoveryClient);
	}

}
