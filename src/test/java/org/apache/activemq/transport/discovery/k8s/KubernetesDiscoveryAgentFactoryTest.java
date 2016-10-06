package org.apache.activemq.transport.discovery.k8s;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.junit.Test;

public class KubernetesDiscoveryAgentFactoryTest {

    @Test
    public void canCreateKubernetesDiscoveryAgent() throws IOException {
        KubernetesDiscoveryAgentFactory factory = new KubernetesDiscoveryAgentFactory();
        DiscoveryAgent agent = factory.doCreateDiscoveryAgent(URI.create("k8s://default?podLabelKey=app&amp;podLabelValue=activemq"));
        assertTrue(agent instanceof KubernetesDiscoveryAgent);
    }
}
